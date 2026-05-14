package com.quejapp.quejapi.service;

import com.quejapp.quejapi.model.Role;
import com.quejapp.quejapi.model.User;
import com.quejapp.quejapi.repository.UserRepository;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AiToolsService {

    private final CsvService csvService;
    private final UserRepository userRepository;

    public AiToolsService(
            CsvService csvService,
            UserRepository userRepository
    ) {

        this.csvService = csvService;
        this.userRepository = userRepository;
    }

    // VALIDAR ADMIN
    private boolean esAdmin() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = auth.getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if(user == null) {
            return false;
        }

        return user.getRole() == Role.ADMINISTRATOR;
    }

    // EXPORTAR CSV
    @Tool(
            description =
                    "Exporta todos los usuarios en un archivo CSV"
    )
    public String exportarUsuariosCSV() {

        if(!esAdmin()) {
            return "No autorizado";
        }

        return csvService.exportarUsuariosCSV();
    }

    // CREAR USUARIO
    @Tool(
            description =
                    "Crear un nuevo usuario"
    )
    public String crearUsuario(
            String nombre,
            String apellido,
            String correo
    ) {

        if(!esAdmin()) {
            return "No autorizado";
        }

        Optional<User> existente =
                userRepository.findByEmail(correo);

        if(existente.isPresent()) {
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
    @Tool(
            description =
                    "Eliminar un usuario por correo"
    )
    public String eliminarUsuario(
            String correo
    ) {

        if(!esAdmin()) {
            return "No autorizado";
        }

        Optional<User> usuario =
                userRepository.findByEmail(correo);

        if(usuario.isEmpty()) {
            return "Usuario no encontrado";
        }

        userRepository.delete(usuario.get());

        return "Usuario eliminado correctamente";
    }

    // ACTUALIZAR USUARIO
    @Tool(
            description =
                    "Actualizar nombre de usuario"
    )
    public String actualizarUsuario(
            String correo,
            String nuevoNombre
    ) {

        if(!esAdmin()) {
            return "No autorizado";
        }

        Optional<User> usuario =
                userRepository.findByEmail(correo);

        if(usuario.isEmpty()) {
            return "Usuario no encontrado";
        }

        User user = usuario.get();

        user.setFirstname(nuevoNombre);

        userRepository.save(user);

        return "Usuario actualizado correctamente";
    }
}