package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.UsuarioRequest;
import com.gestionalmacen.dto.response.UsuarioResponse;
import com.gestionalmacen.entity.*;
import com.gestionalmacen.enums.NombreRol;
import com.gestionalmacen.exception.BusinessRuleException;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.*;
import com.gestionalmacen.service.interfaces.UsuarioService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SedeRepository sedeRepository;
    private final AreaRepository areaRepository;
    private final UsuarioAreaRepository usuarioAreaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            SedeRepository sedeRepository, AreaRepository areaRepository,
            UsuarioAreaRepository usuarioAreaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.sedeRepository = sedeRepository;
        this.areaRepository = areaRepository;
        this.usuarioAreaRepository = usuarioAreaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        Usuario usuario = Usuario.builder()
                .nombres(request.nombres().trim())
                .username(request.username().trim())
                .email(request.email().trim())
                .passwordHash(passwordEncoder.encode(request.password()))
                .rol(buscarRol(request.rolId()))
                .sede(request.sedeId() == null ? null : buscarSede(request.sedeId()))
                .activo(true)
                .build();
        usuario = usuarioRepository.save(usuario);
        asignarAreas(usuario, request.areaIds());
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscar(id);
        Rol nuevoRol = buscarRol(request.rolId());
        if (usuario.getActivo() && usuario.getRol().getNombre() == NombreRol.ADMINISTRADOR
                && nuevoRol.getNombre() != NombreRol.ADMINISTRADOR
                && usuarioRepository.countActivosPorRol(NombreRol.ADMINISTRADOR) <= 1) {
            throw new BusinessRuleException("No puedes quitar el rol de administrador al último administrador activo.");
        }
        usuario.setNombres(request.nombres().trim());
        usuario.setUsername(request.username().trim());
        usuario.setEmail(request.email().trim());
        usuario.setRol(nuevoRol);
        usuario.setSede(request.sedeId() == null ? null : buscarSede(request.sedeId()));
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        usuario = usuarioRepository.save(usuario);
        asignarAreas(usuario, request.areaIds());
        return toResponse(usuario);
    }

    @Override
    public void desactivar(Long id) {
        Usuario usuario = buscar(id);
        if (usuario.getActivo() && usuario.getRol().getNombre() == NombreRol.ADMINISTRADOR
                && usuarioRepository.countActivosPorRol(NombreRol.ADMINISTRADOR) <= 1) {
            throw new BusinessRuleException(
                    "No puedes desactivar al último administrador activo. Activa otro administrador primero.");
        }
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private void asignarAreas(Usuario usuario, List<Long> areaIds) {
        if (areaIds == null)
            return;
        usuarioAreaRepository.deleteByUsuarioId(usuario.getId());
        Set<Long> ids = new HashSet<>(areaIds);
        for (Long areaId : ids) {
            usuarioAreaRepository.save(UsuarioArea.builder()
                    .usuario(usuario)
                    .area(buscarArea(areaId))
                    .build());
        }
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    private Rol buscarRol(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + id));
    }

    private Sede buscarSede(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada: " + id));
    }

    private Area buscarArea(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada: " + id));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        Long sedeId = usuario.getSede() == null ? null : usuario.getSede().getId();
        return new UsuarioResponse(usuario.getId(), usuario.getNombres(), usuario.getUsername(),
                usuario.getEmail(), usuario.getRol().getId(), usuario.getRol().getNombre().name(), sedeId,
                usuario.getActivo());
    }
}
