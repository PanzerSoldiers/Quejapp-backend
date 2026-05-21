package com.quejapp.quejapi.service;

import com.quejapp.quejapi.model.Role;
import com.quejapp.quejapi.model.User;
import com.quejapp.quejapi.repository.UserRepository;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.quejapp.quejapi.configuration.UserContextHolder;

import java.util.Optional;

@Service
public class AiToolsService {

    private final CsvService csvService;
    private final UserRepository userRepository;
    private final PendingActionService pendingActionService;

    public AiToolsService(
            CsvService csvService,
            UserRepository userRepository,
            PendingActionService pendingActionService
    ) {

        this.csvService = csvService;
        this.userRepository = userRepository;
        this.pendingActionService = pendingActionService;
    }

    // VALIDAR ADMIN
    private boolean esAdmin() {

        String email = UserContextHolder.get();

        if (email == null) return false;

        return userRepository.findByEmail(email)
                .map(u -> u.getRole() == Role.ADMINISTRATOR)
                .orElse(false);
    }

    // EXPORTAR CSV
    @Tool(description = "Exportar usuarios a CSV")
    public String exportarUsuariosCSV() {

        if (!esAdmin()) {
            return "No autorizado";
        }

        return csvService.exportarUsuariosCSV();
    }

    // CREAR USUARIO
    @Tool(description = "Crear usuario en el sistema")
    public String crearUsuario(String nombre, String apellido, String correo) {

        if (!esAdmin()) {
            return "No autorizado";
        }

        if (userRepository.findByEmail(correo).isPresent()) {
            return "El usuario ya existe";
        }

        User user = new User();
        user.setFirstname(nombre);
        user.setLastname(apellido);
        user.setEmail(correo);

        userRepository.save(user);

        return "Usuario creado correctamente";
    }
    // ELIMINAR USUARIO
    @Tool(description = "Eliminar usuario por correo")
    public String eliminarUsuario(String correo) {

        if (!esAdmin()) {
            return "No autorizado";
        }

        Optional<User> usuario = userRepository.findByEmail(correo);

        if (usuario.isEmpty()) {
            return "Usuario no encontrado";
        }

        String adminEmail = UserContextHolder.get();

        if (pendingActionService.tieneAccion(adminEmail)) {
            return "Ya tienes una acción pendiente. Escribe CONFIRMAR.";
        }

        pendingActionService.guardarAccion(
                adminEmail,
                () -> userRepository.delete(usuario.get())
        );

        return "⚠️ Escribe CONFIRMAR para eliminar el usuario.";
    }

    // ACTUALIZAR USUARIO
    @Tool(description = "Actualizar nombre de usuario")
    public String actualizarUsuario(String correo, String nuevoNombre) {

        if (!esAdmin()) {
            return "No autorizado";
        }

        Optional<User> usuario = userRepository.findByEmail(correo);

        if (usuario.isEmpty()) {
            return "Usuario no encontrado";
        }

        User user = usuario.get();
        user.setFirstname(nuevoNombre);

        userRepository.save(user);

        return "Usuario actualizado correctamente";
    }
}