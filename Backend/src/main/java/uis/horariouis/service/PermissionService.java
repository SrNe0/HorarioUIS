package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.RolAccion;
import uis.horariouis.model.Usuario;
import uis.horariouis.repository.RolAccionRepository;
import uis.horariouis.repository.UsuarioRepository;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private RolAccionRepository rolAccionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean hasPermission(Long userId, String actionName) {
        Long roleId = getRoleIdFromUserId(userId);

        List<RolAccion> rolAcciones = rolAccionRepository.findByRolIdRol(roleId);
        for (RolAccion rolAccion : rolAcciones) {
            if (rolAccion.getAccion().getNombreAccion().equals(actionName)) {
                return true;
            }
        }
        return false;
    }

    private Long getRoleIdFromUserId(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getRol().getIdRol();
    }
}
