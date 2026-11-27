let csrfToken;

async function refreshCsrfToken() {
  const response = await fetch('/api/auth/csrf', { credentials: 'include' });
  if (!response.ok) throw new Error('No se pudo obtener el token de seguridad de sesión.');
  const body = await response.json();
  csrfToken = body.data?.token || body.token;
}

const api = async (url, options = {}) => {
  const method = (options.method || 'GET').toUpperCase();
  const writes = !['GET', 'HEAD', 'OPTIONS'].includes(method);
  if (writes && !csrfToken) await refreshCsrfToken();
  const response = await fetch(url, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(writes ? { 'X-XSRF-TOKEN': csrfToken } : {}),
      ...(options.headers || {})
    },
    ...options
  });
  let payload = null;
  if (response.status !== 204) {
    try { payload = await response.json(); } catch (_) {}
  }
  if (!response.ok) {
    const error = new Error(payload?.message || 'La operación no pudo completarse');
    error.details = payload?.details || [];
    error.validationErrors = payload?.validationErrors || {};
    throw error;
  }
  if (response.status === 204) return null;
  return payload?.success === true && Object.prototype.hasOwnProperty.call(payload, 'data') ? payload.data : payload;
};

let areaChart;
let productChart;
let currentUser;
let areasCatalog = [];
let warehousesCatalog = [];
let productsCatalog = [];
let requirementCart = [];
let activeDispatch;
let warehouseProductIds = [];
let inventoryByWarehouse = new Map();

const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, character => ({
  '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
}[character]));

const notifyError = error => {
  const validation = Object.entries(error.validationErrors || {}).map(([field, message]) => `${field}: ${message}`);
  const details = [...(error.details || []), ...validation].filter(Boolean).join(' · ');
  return NotificationService.error(details ? `${error.message}. ${details}` : error.message);
};

const formatQuantity = value => Number(value || 0).toLocaleString('es-PE', { maximumFractionDigits: 3 });
const getCollection = response => response.content || response;

document.getElementById('loginForm').addEventListener('submit', async event => {
  event.preventDefault();
  await NotificationService.withButtonLoading(event.submitter, 'Validando…', async () => {
    try {
      currentUser = await api('/api/auth/login', { method: 'POST', body: JSON.stringify({
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
      }) });
      document.getElementById('loginScreen').classList.add('hidden');
      document.getElementById('app').classList.remove('hidden');
      document.getElementById('userName').textContent = currentUser.nombres;
      document.getElementById('userInitial').textContent = currentUser.nombres.charAt(0).toUpperCase();
      applyRoleVisibility();
      await loadDashboard();
      await NotificationService.success('Sesión iniciada correctamente.');
      lucide.createIcons();
    } catch (error) { await notifyError(error); }
  });
});

window.addEventListener('DOMContentLoaded', () => lucide.createIcons());

document.getElementById('logoutButton').addEventListener('click', async () => {
  await NotificationService.withButtonLoading(document.getElementById('logoutButton'), 'Cerrando…', async () => {
    try {
      await api('/api/auth/logout', { method: 'POST', body: '{}' });
      location.reload();
    } catch (error) { await notifyError(error); }
  });
});

const titles = { dashboard: 'Dashboard', requerimientos: 'Requerimientos', despachos: 'Despachos', inventario: 'Inventario' };
function applyRoleVisibility() {
  const viewsByRole = {
    SOLICITANTE: ['dashboard', 'requerimientos'],
    ALMACENERO: ['dashboard', 'despachos', 'inventario'],
    SUPERVISOR: ['dashboard', 'requerimientos', 'despachos', 'inventario'],
    ADMINISTRADOR: ['dashboard', 'requerimientos', 'despachos', 'inventario']
  };
  const allowedViews = viewsByRole[currentUser?.rol] || ['dashboard'];
  document.querySelectorAll('.nav-button[data-view]').forEach(button => {
    button.classList.toggle('hidden', !allowedViews.includes(button.dataset.view));
  });
  document.getElementById('newRequirementButton').classList.toggle('hidden', !['SOLICITANTE', 'SUPERVISOR', 'ADMINISTRADOR'].includes(currentUser?.rol));
}

document.querySelectorAll('.nav-button[data-view]').forEach(button => button.addEventListener('click', async () => {
  if (button.disabled) return;
  button.disabled = true;
  document.querySelectorAll('.nav-button').forEach(item => item.classList.remove('active'));
  document.querySelectorAll('.view').forEach(item => item.classList.remove('active'));
  button.classList.add('active');
  document.getElementById(button.dataset.view + 'View').classList.add('active');
  document.getElementById('pageTitle').textContent = titles[button.dataset.view];
  try {
    if (button.dataset.view === 'dashboard') await loadDashboard();
    if (button.dataset.view === 'requerimientos') await loadRequirements();
    if (button.dataset.view === 'despachos') await loadDispatches();
    if (button.dataset.view === 'inventario') await loadInventory();
  } catch (error) { await notifyError(error); }
  finally {
    button.disabled = false;
    lucide.createIcons();
  }
}));

async function loadDashboard() {
  const [dashboard, areas, products] = await Promise.all([
    api('/api/dashboard'), api('/api/reportes/consumo-mensual'), api('/api/reportes/productos-mas-solicitados')
  ]);
  document.getElementById('pendingMetric').textContent = dashboard.requerimientosPendientes;
  document.getElementById('partialMetric').textContent = dashboard.requerimientosParciales;
  document.getElementById('dispatchMetric').textContent = dashboard.despachosIniciados;
  document.getElementById('stockMetric').textContent = dashboard.productosStockBajo;
  if (areaChart) areaChart.destroy();
  areaChart = new Chart(document.getElementById('areaChart'), { type: 'bar', data: {
    labels: areas.map(item => item.etiqueta),
    datasets: [{ label: 'Cantidad despachada', data: areas.map(item => item.cantidad), backgroundColor: '#087a5a', borderRadius: 4 }]
  }, options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } } });
  if (productChart) productChart.destroy();
  productChart = new Chart(document.getElementById('productChart'), { type: 'doughnut', data: {
    labels: products.slice(0, 6).map(item => item.etiqueta),
    datasets: [{ data: products.slice(0, 6).map(item => item.cantidad), backgroundColor: ['#087a5a', '#2563eb', '#d97706', '#b42318', '#7c3aed', '#475569'] }]
  }, options: { responsive: true, maintainAspectRatio: false, cutout: '64%', plugins: { legend: { position: 'bottom' } } } });
}

async function loadCatalogs() {
  const [areas, warehouses, products] = await Promise.all([
    api('/api/requerimientos/areas-permitidas'), api('/api/almacenes'), api('/api/productos?size=500&sort=nombre,asc')
  ]);
  areasCatalog = getCollection(areas);
  warehousesCatalog = getCollection(warehouses);
  productsCatalog = getCollection(products);
  fillSelect('areaSelect', [['', 'Selecciona un área'], ...areasCatalog.map(area => [area.id, area.nombre])]);
  if (areasCatalog.length === 1) document.getElementById('areaSelect').value = areasCatalog[0].id;
  if (!areasCatalog.length) NotificationService.warning('Tu usuario no tiene áreas asignadas. Contacta al administrador.');
  fillSelect('warehouseSelect', [['', 'Selecciona un almacén'], ...warehousesCatalog.map(warehouse => [warehouse.id, warehouse.nombre])]);
  const categories = [...new Set(productsCatalog.map(product => product.categoria))].sort((a, b) => a.localeCompare(b, 'es'));
  fillSelect('categoryFilter', [['', 'Todas las categorías'], ...categories.map(category => [category, category])]);
  renderProductOptions();
  updateRequestHeader();
}

function renderAssignedAreas() {
  const el = document.getElementById('assignedAreas');
  if (!el) return;
  if (currentUser?.rol === 'SOLICITANTE') {
    const names = areasCatalog.map(area => area.nombre).join(', ');
    el.textContent = `${areasCatalog.length > 1 ? 'Áreas asignadas' : 'Área asignada'}: ${names || 'ninguna'}`;
  } else {
    el.textContent = 'Áreas: todas';
  }
}

async function showRequirementDetail(id) {
  const requirement = await api(`/api/requerimientos/${id}`);
  const rows = requirement.detalles.map(detail => `
    <tr>
      <td style="text-align:left">${escapeHtml(detail.producto)}</td>
      <td>${escapeHtml(detail.unidad)}</td>
      <td>${formatQuantity(detail.cantidadSolicitada)}</td>
      <td>${formatQuantity(detail.cantidadDespachada)}</td>
      <td>${formatQuantity(detail.cantidadPendiente)}</td>
    </tr>`).join('');
  const html = `
    <p style="margin:0 0 12px;text-align:left">
      <b>Área:</b> ${escapeHtml(requirement.area)} · <b>Almacén:</b> ${escapeHtml(requirement.almacen)} · <b>Estado:</b> ${escapeHtml(requirement.estado)}
    </p>
    <table style="width:100%;border-collapse:collapse;font-size:14px">
      <thead><tr><th style="text-align:left">Producto</th><th>Unidad</th><th>Pedido</th><th>Entregado</th><th>Pendiente</th></tr></thead>
      <tbody>${rows}</tbody>
    </table>`;
  if (window.Swal) {
    await Swal.fire({ title: `Requerimiento ${requirement.numero}`, html, width: 720, confirmButtonText: 'Cerrar' });
  } else {
    await NotificationService.info(`${requirement.numero}: ${requirement.detalles.length} producto(s)`);
  }
}

async function loadRequirements() {
  const requirements = await api('/api/requerimientos');
  document.getElementById('requirementsBody').innerHTML = requirements.map(requirement => `
    <tr><td>${escapeHtml(requirement.numero)}</td><td>${escapeHtml(requirement.area)}</td><td>${escapeHtml(requirement.almacen)}</td>
    <td><span class="status ${escapeHtml(requirement.estado.toLowerCase())}">${escapeHtml(requirement.estado)}</span></td>
    <td>${new Date(requirement.fechaSolicitud).toLocaleString('es-PE')}</td><td><button class="btn" data-open-dispatch="${requirement.id}">Ver detalle</button></td></tr>`).join('')
    || '<tr><td colspan="6">No hay requerimientos abiertos</td></tr>';
  document.querySelectorAll('[data-open-dispatch]').forEach(button => button.addEventListener('click', async () => {
    const id = Number(button.dataset.openDispatch);
    if (currentUser?.rol === 'SOLICITANTE') {
      try { await showRequirementDetail(id); } catch (error) { await notifyError(error); }
      return;
    }
    showView('despachos');
    await openDispatch(id);
  }));
  await loadCatalogs();
  renderAssignedAreas();
}

function showView(name) {
  const button = document.querySelector(`.nav-button[data-view="${name}"]`);
  button?.click();
}


async function loadWarehouseProducts(warehouseId) {
  warehouseProductIds = [];

  if (!warehouseId) {
    renderProductOptions();
    return;
  }

  try {
    const ids = await api(`/api/almacenes/${warehouseId}/productos`);

    warehouseProductIds = [...new Set(ids.map(Number))];

    const warehouseProducts = productsCatalog.filter(product =>
      product.activo &&
      warehouseProductIds.includes(Number(product.id))
    );

    const categories = [
      ...new Set(
        warehouseProducts
          .map(product => product.categoria)
          .filter(Boolean)
      )
    ].sort((a, b) => a.localeCompare(b, 'es'));

    fillSelect('categoryFilter', [
      ['', 'Todas las categorías'],
      ...categories.map(category => [category, category])
    ]);

    renderProductOptions();

  } catch (error) {
    warehouseProductIds = [];
    renderProductOptions();
    console.error('Error cargando productos:', error);
    await notifyError(error);
  }
}


function renderProductOptions() {
  const warehouseId = document.getElementById('warehouseSelect').value;
  const category = document.getElementById('categoryFilter').value;
  const query = document.getElementById('productSearch').value.trim().toLocaleLowerCase('es');

  const filtered = productsCatalog.filter(product => product.activo
    && warehouseProductIds.includes(Number(product.id))
    && (!category || product.categoria === category)
    && (!query || product.nombre.toLocaleLowerCase('es').includes(query)));

  fillSelect('requirementProduct', [
    ['', !warehouseId
      ? 'Selecciona un almacén primero'
      : filtered.length
        ? 'Selecciona un producto'
        : 'No hay productos en este almacén'],
    ...filtered.map(product => [product.id, product.nombre])
  ]);

  updateSelectedProductUnit();
}


function updateSelectedProductUnit() {
  const product = productsCatalog.find(item => item.id === Number(document.getElementById('requirementProduct').value));
  document.getElementById('productUnit').value = product?.unidadMedida || '';
}

function fillSelect(id, rows) {
  const select = document.getElementById(id);
  const selected = select.value;
  select.innerHTML = rows.map(([value, label]) => `<option value="${escapeHtml(value)}">${escapeHtml(label)}</option>`).join('');
  if (rows.some(([value]) => String(value) === selected)) select.value = selected;
}

function updateRequestHeader() {
  const area = areasCatalog.find(item => item.id === Number(document.getElementById('areaSelect').value));
  const areaName = area?.nombre || '';
  document.getElementById('shiftDisplay').value = /noche/i.test(areaName) ? 'Noche' : /mañana|manana/i.test(areaName) ? 'Mañana' : 'Según área';
  document.getElementById('areaResponsible').value = currentUser?.nombres || '';
  document.getElementById('requestUser').value = currentUser?.username || '';
  const now = new Date();
  document.getElementById('requestDate').value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
}

function renderRequirementCart() {
  const body = document.getElementById('requirementLines');
  if (!requirementCart.length) {
    body.innerHTML = '<tr class="empty-row"><td colspan="5">Agrega productos para completar la hoja.</td></tr>';
    return;
  }
  const groups = new Map();
  requirementCart.forEach(line => {
    if (!groups.has(line.categoria)) groups.set(line.categoria, []);
    groups.get(line.categoria).push(line);
  });
  body.innerHTML = [...groups.entries()].map(([category, lines]) => lines.map((line, index) => `
    <tr data-cart-product="${line.productoId}">
      ${index === 0 ? `<th class="category-cell" scope="rowgroup" rowspan="${lines.length}">${escapeHtml(category)}</th>` : ''}
      <td class="product-name-cell">${escapeHtml(line.producto)}<button class="icon-button" type="button" data-remove-product="${line.productoId}" aria-label="Quitar ${escapeHtml(line.producto)}" title="Quitar producto"><i data-lucide="x"></i></button></td><td>${escapeHtml(line.unidad)}</td>
      <td><input class="line-quantity" data-quantity-product="${line.productoId}" aria-label="Cantidad solicitada de ${escapeHtml(line.producto)}" type="number" min="0.001" step="0.001" value="${line.cantidad}"></td>
      <td class="dispatch-empty" aria-label="Se completa durante el despacho"></td>
    </tr>`).join('')).join('');
  body.querySelectorAll('[data-remove-product]').forEach(button => button.addEventListener('click', async () => {
    const line = requirementCart.find(item => item.productoId === Number(button.dataset.removeProduct));
    if (!await NotificationService.confirm(`¿Quitar ${line?.producto || 'este producto'} de la solicitud?`, 'Quitar producto', 'Quitar')) return;
    requirementCart = requirementCart.filter(line => line.productoId !== Number(button.dataset.removeProduct));
    renderRequirementCart();
  }));
  body.querySelectorAll('[data-quantity-product]').forEach(input => input.addEventListener('change', () => {
    const line = requirementCart.find(item => item.productoId === Number(input.dataset.quantityProduct));
    const quantity = Number(input.value);
    if (!line || !Number.isFinite(quantity) || quantity <= 0) {
      input.value = line?.cantidad || '';
      NotificationService.warning('La cantidad solicitada debe ser mayor que cero.');
      return;
    }
    line.cantidad = quantity;
  }));
  lucide.createIcons();
}

function addProductToRequirement() {
  const product = productsCatalog.find(item => item.id === Number(document.getElementById('requirementProduct').value));
  const quantity = Number(document.getElementById('requirementQuantity').value);
  if (!product) { NotificationService.warning('Selecciona un producto activo.'); return; }
  if (!Number.isFinite(quantity) || quantity <= 0) { NotificationService.warning('Ingresa una cantidad mayor que cero.'); return; }
  if (requirementCart.some(line => line.productoId === product.id)) {
    NotificationService.warning('Ese producto ya está en la solicitud. Modifica su cantidad en la tabla.');
    return;
  }
requirementCart.push({
    productoId: product.id,
    producto: product.nombre,
    categoria: product.categoria,
    unidad: product.unidadMedida,
    cantidad: quantity
});  document.getElementById('requirementQuantity').value = '';
  renderRequirementCart();
}

async function loadDispatches() {
  const requirements = await api('/api/requerimientos/para-despacho');
  document.getElementById('dispatchesBody').innerHTML = requirements.map(requirement => `
    <tr><td>${escapeHtml(requirement.numero)}</td><td>${escapeHtml(requirement.area)}</td><td>${escapeHtml(requirement.almacen)}</td>
    <td>${new Date(requirement.fechaSolicitud).toLocaleString('es-PE')}</td>
    <td><span class="status ${escapeHtml(requirement.estado.toLowerCase())}">${escapeHtml(requirement.estado)}</span></td>
    <td><button class="btn btn-primary" type="button" data-select-requirement="${requirement.id}"><i data-lucide="package-open"></i>Preparar entrega</button></td></tr>`).join('')
    || '<tr><td colspan="6">Sin requerimientos pendientes de atención</td></tr>';
  document.querySelectorAll('[data-select-requirement]').forEach(button => button.addEventListener('click', () =>
    NotificationService.withButtonLoading(button, 'Cargando…', () => openDispatch(Number(button.dataset.selectRequirement)))));
  lucide.createIcons();
}

async function openDispatch(requirementId) {
  try {
    const requirement = await api(`/api/requerimientos/${requirementId}`);
    const inventory = await api(`/api/inventario/almacen/${requirement.almacenId}`);
    const warehouseInventory = inventory.filter(item => item.almacenId === requirement.almacenId);
    activeDispatch = { requirementId, requirement, inventory: warehouseInventory };
    renderDispatchDetail();
    document.getElementById('dispatchDetail').classList.remove('hidden');
    document.getElementById('dispatchDetail').scrollIntoView({ behavior: 'smooth', block: 'start' });
  } catch (error) { await notifyError(error); }
}

function renderDispatchDetail() {
  const { requirement, inventory } = activeDispatch;
  document.getElementById('dispatchTitle').textContent = `Requerimiento ${requirement.numero}`;
  document.getElementById('dispatchState').textContent = requirement.estado;
  document.getElementById('dispatchMeta').innerHTML = `
    <span><b>Área</b>${escapeHtml(requirement.area)}</span>
    <span><b>Almacén</b>${escapeHtml(requirement.almacen)}</span>
    <span><b>Solicitó</b>${escapeHtml(requirement.solicitante)}</span>
    <span><b>Fecha</b>${new Date(requirement.fechaSolicitud).toLocaleString('es-PE')}</span>`;
  const groups = new Map();
  requirement.detalles.forEach(detail => {
    if (!groups.has(detail.categoria)) groups.set(detail.categoria, []);
    const stock = inventory.find(item => item.productoId === detail.productoId)?.existencia || 0;
    groups.get(detail.categoria).push({ ...detail, stock });
  });
  document.getElementById('dispatchLines').innerHTML = [...groups.entries()].map(([category, lines]) => lines.map((line, index) => {
    const maximum = Math.min(Number(line.cantidadPendiente), Number(line.stock));
    return `<tr>
      ${index === 0 ? `<th class="category-cell" scope="rowgroup" rowspan="${lines.length}">${escapeHtml(category)}</th>` : ''}
      <td>${escapeHtml(line.producto)}</td><td>${escapeHtml(line.unidad)}</td>
      <td>${formatQuantity(line.cantidadSolicitada)}</td><td>${formatQuantity(line.cantidadDespachada)}</td>
      <td>${formatQuantity(line.cantidadPendiente)}</td><td>${formatQuantity(line.stock)}</td>
      <td><input class="dispatch-quantity" data-detail-id="${line.id}" aria-label="Entrega actual de ${escapeHtml(line.producto)}" type="number" min="0" max="${maximum}" step="0.001" value="0" ${maximum <= 0 ? 'disabled' : ''}></td>
    </tr>`;
  }).join('')).join('');
  lucide.createIcons();
}

async function confirmDispatch() {
  if (!activeDispatch) return;
  const entregas = [...document.querySelectorAll('.dispatch-quantity')]
    .map(input => ({ detalleRequerimientoId: Number(input.dataset.detailId), cantidadEntregada: Number(input.value) }))
    .filter(delivery => delivery.cantidadEntregada > 0);
  if (!entregas.length) { NotificationService.warning('Ingresa al menos una cantidad para entregar.'); return; }
  const button = document.getElementById('confirmDispatch');
  if (!await NotificationService.confirm('Se descontará el inventario de las cantidades indicadas.', 'Confirmar despacho', 'Confirmar entrega')) return;
  await NotificationService.withButtonLoading(button, 'Guardando despacho…', async () => {
    try {
      await api('/api/despachos/confirmar', { method: 'POST', body: JSON.stringify({
        requerimientoId: activeDispatch.requirementId,
        entregas
      }) });
      document.getElementById('dispatchDetail').classList.add('hidden');
      activeDispatch = null;
      await NotificationService.success('La entrega se registró correctamente.');
      await loadDispatches();
    } catch (error) { await notifyError(error); }
  });
}


async function loadInventory() {
  const [warehousesResponse, inventory] = await Promise.all([
    api('/api/almacenes'),
    api('/api/inventario')
  ]);

  warehousesCatalog = getCollection(warehousesResponse);

  inventoryByWarehouse = new Map();

  inventory.forEach(item => {
    const warehouseId = Number(item.almacenId);

    if (!inventoryByWarehouse.has(warehouseId)) {
      inventoryByWarehouse.set(warehouseId, []);
    }

    inventoryByWarehouse.get(warehouseId).push(item);
  });

  renderWarehouseCards();
  showWarehouseCards();
}

function renderWarehouseCards() {
  const container = document.getElementById('warehouseCards');

  container.innerHTML = warehousesCatalog.map(warehouse => {
    const items = inventoryByWarehouse.get(Number(warehouse.id)) || [];

    const totalProducts = items.length;
    const totalUnits = items.reduce(
      (total, item) => total + Number(item.existencia || 0),
      0
    );

    const lowStock = items.filter(item => item.stockBajo).length;

    return `
      <article class="warehouse-card">
        <div class="warehouse-card-header">
          <div class="warehouse-card-icon">
            <i data-lucide="warehouse"></i>
          </div>
          <div>
            <h3>${escapeHtml(warehouse.nombre)}</h3>
          </div>
        </div>

        <div class="warehouse-card-stats">
          <div class="warehouse-stat">
            <span>Productos registrados</span>
            <strong>${totalProducts}</strong>
          </div>

          <div class="warehouse-stat">
            <span>Unidades en existencia</span>
            <strong>${formatQuantity(totalUnits)}</strong>
          </div>

          <div class="warehouse-stat warning">
            <span>Productos con stock bajo</span>
            <strong>${lowStock}</strong>
          </div>
        </div>

        <button class="btn" type="button"
                data-open-warehouse="${Number(warehouse.id)}">
          <i data-lucide="eye"></i>
          Ver productos
        </button>
      </article>
    `;
  }).join('') || `
    <div class="inventory-empty">
      No hay almacenes registrados.
    </div>
  `;

  container.querySelectorAll('[data-open-warehouse]').forEach(button => {
    button.addEventListener('click', () => {
      openWarehouseInventory(Number(button.dataset.openWarehouse));
    });
  });

  if (window.lucide) lucide.createIcons();
}

function openWarehouseInventory(warehouseId) {
  const warehouse = warehousesCatalog.find(
    item => Number(item.id) === Number(warehouseId)
  );

  if (!warehouse) return;

  const items = inventoryByWarehouse.get(Number(warehouseId)) || [];

  document.getElementById('inventoryWarehouseTitle').textContent =
    warehouse.nombre;

  document.getElementById('inventoryWarehouseDescription').textContent =
    'Productos registrados en este almacén';

  const totalUnits = items.reduce(
    (total, item) => total + Number(item.existencia || 0),
    0
  );

  const lowStock = items.filter(item => item.stockBajo).length;

  document.getElementById('warehouseInventorySummary').innerHTML = `
    <div class="inventory-summary-item">
      <span>Productos registrados</span>
      <strong>${items.length}</strong>
    </div>

    <div class="inventory-summary-item">
      <span>Unidades en existencia</span>
      <strong>${formatQuantity(totalUnits)}</strong>
    </div>

    <div class="inventory-summary-item">
      <span>Stock bajo</span>
      <strong>${lowStock}</strong>
    </div>
  `;

  document.getElementById('warehouseInventoryBody').innerHTML =
    items.map(item => `
      <tr>
        <td>${escapeHtml(item.producto)}</td>
        <td>${escapeHtml(item.unidad)}</td>
        <td>${formatQuantity(item.existencia)}</td>
        <td>${formatQuantity(item.stockMinimo)}</td>
        <td>
          <span class="status ${item.stockBajo ? 'pendiente' : 'atendido'}">
            ${item.stockBajo ? 'Stock bajo' : 'Disponible'}
          </span>
        </td>
      </tr>
    `).join('') || `
      <tr>
        <td colspan="5">No hay productos registrados en este almacén.</td>
      </tr>
    `;

  document.getElementById('inventoryWarehousesView').classList.add('hidden');
  document.getElementById('warehouseInventoryDetail').classList.remove('hidden');

  if (window.lucide) lucide.createIcons();
}

function showWarehouseCards() {
  document.getElementById('warehouseInventoryDetail').classList.add('hidden');
  document.getElementById('inventoryWarehousesView').classList.remove('hidden');
}


document.getElementById('newRequirementButton').addEventListener('click', async event => {
  await NotificationService.withButtonLoading(event.currentTarget, 'Cargando catálogo…', async () => {
    try {
      await loadCatalogs();
      requirementCart = [];
      renderRequirementCart();
      document.getElementById('requirementNumber').textContent = 'Correlativo al registrar';
      document.getElementById('requirementObservation').value = '';
      updateRequestHeader();
      document.getElementById('requirementForm').classList.remove('hidden');
      document.getElementById('requirementForm').scrollIntoView({ behavior: 'smooth', block: 'start' });
    } catch (error) { await notifyError(error); }
  });
});

document.getElementById('backToWarehouses')
  .addEventListener('click', showWarehouseCards);

document.getElementById('refreshInventory')
  .addEventListener('click', async () => {
    try {
      await loadInventory();
      NotificationService.success('Inventario actualizado');
    } catch (error) {
      await notifyError(error);
    }
  });

document.getElementById('cancelRequirement').addEventListener('click', async () => {
  if (requirementCart.length && !await NotificationService.confirm('Se perderán los productos agregados que aún no se registraron.', 'Cancelar requerimiento', 'Descartar')) return;
  requirementCart = [];
  document.getElementById('requirementForm').classList.add('hidden');
});
document.getElementById('addRequirementProduct').addEventListener('click', addProductToRequirement);
document.getElementById('warehouseSelect').addEventListener('change', async () => {
  requirementCart = [];
  renderRequirementCart();

  document.getElementById('productSearch').value = '';
  document.getElementById('categoryFilter').value = '';

  await loadWarehouseProducts(document.getElementById('warehouseSelect').value);
});
document.getElementById('categoryFilter').addEventListener('change', renderProductOptions);
document.getElementById('productSearch').addEventListener('input', renderProductOptions);
document.getElementById('requirementProduct').addEventListener('change', updateSelectedProductUnit);
document.getElementById('areaSelect').addEventListener('change', updateRequestHeader);
document.getElementById('refreshDispatches').addEventListener('click', event =>
  NotificationService.withButtonLoading(event.currentTarget, 'Cargando…', async () => {
    try { await loadDispatches(); } catch (error) { await notifyError(error); }
  }));
document.getElementById('confirmDispatch').addEventListener('click', confirmDispatch);
document.getElementById('cancelDispatch').addEventListener('click', () => {
  activeDispatch = null;
  document.getElementById('dispatchDetail').classList.add('hidden');
});

document.getElementById('createRequirementForm').addEventListener('submit', async event => {
  event.preventDefault();
  if (!requirementCart.length) { NotificationService.warning('Agrega al menos un producto antes de registrar.'); return; }
  const body = {
    areaId: Number(document.getElementById('areaSelect').value),
    almacenId: Number(document.getElementById('warehouseSelect').value),
    observaciones: document.getElementById('requirementObservation').value,
    detalles: requirementCart.map(line => ({ productoId: line.productoId, cantidad: line.cantidad }))
  };
  await NotificationService.withButtonLoading(event.submitter, 'Guardando…', async () => {
    try {
      const created = await api('/api/requerimientos', { method: 'POST', body: JSON.stringify(body) });
      document.getElementById('requirementNumber').textContent = created.numero;
      document.getElementById('requirementForm').classList.add('hidden');
      requirementCart = [];
      await NotificationService.success(`Requerimiento ${created.numero} registrado correctamente.`);
      await loadRequirements();
    } catch (error) { await notifyError(error); }
  });
});
