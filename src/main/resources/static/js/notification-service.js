(() => {
	const fallback = message => {
		const element = document.getElementById('toast');
		if (!element) return;
		element.textContent = message;
		element.classList.remove('hidden');
		window.clearTimeout(fallback.timeout);
		fallback.timeout = window.setTimeout(() => element.classList.add('hidden'), 3600);
	};

	const show = (icon, title, text = '') => {
		if (window.Swal) return window.Swal.fire({ icon, title, text, confirmButtonColor: '#087a5a' });
		fallback(text || title);
		return Promise.resolve({ isConfirmed: true });
	};

	window.NotificationService = {
		success: (message, title = 'Listo') => show('success', title, message),
		error: (message, title = 'No se pudo completar') => show('error', title, message),
		warning: (message, title = 'Revisa la información') => show('warning', title, message),
		info: (message, title = 'Información') => show('info', title, message),
		confirm: async (message, title = '¿Confirmas la operación?', confirmText = 'Confirmar') => {
			if (!window.Swal) return window.confirm(`${title}\n${message}`);
			const result = await window.Swal.fire({
				icon: 'question', title, text: message, showCancelButton: true,
				confirmButtonText: confirmText, cancelButtonText: 'Volver', confirmButtonColor: '#087a5a'
			});
			return result.isConfirmed;
		},
		loading: message => {
			if (!window.Swal) {
				fallback(message || 'Procesando…');
				return { close() {} };
			}
			window.Swal.fire({
				title: message || 'Procesando…', allowOutsideClick: false, allowEscapeKey: false,
				didOpen: () => window.Swal.showLoading()
			});
			return { close: () => window.Swal.close() };
		},
		withButtonLoading: async (button, label, operation) => {
			if (!button) return operation();
			const originalHtml = button.innerHTML;
			button.disabled = true;
			button.setAttribute('aria-busy', 'true');
			button.textContent = label;
			try {
				return await operation();
			} finally {
				button.disabled = false;
				button.removeAttribute('aria-busy');
				button.innerHTML = originalHtml;
				if (window.lucide) window.lucide.createIcons();
			}
		}
	};
})();
