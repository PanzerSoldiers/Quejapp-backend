package com.quejapp.quejapi.controller;

import com.quejapp.quejapi.service.AiToolsService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tools")
@CrossOrigin(origins = "*")
public class AiToolsTestController {

    private final AiToolsService aiToolsService;

    public AiToolsTestController(
            AiToolsService aiToolsService
    ) {
        this.aiToolsService = aiToolsService;
    }

    // EXPORTAR CSV
    @GetMapping("/csv")
    public String exportarCsv() {

        return aiToolsService
                .exportarUsuariosCSV();
    }

    // CREAR USUARIO
    @PostMapping("/crear")
    public String crearUsuario() {

        return aiToolsService.crearUsuario(
                "Juan",
                "Perez",
                "juan@gmail.com"
        );
    }

    // ELIMINAR
    @DeleteMapping("/eliminar")
    public String eliminarUsuario() {

        return aiToolsService.eliminarUsuario(
                "juan@gmail.com"
        );
    }

    // ACTUALIZAR
    @PutMapping("/actualizar")
    public String actualizarUsuario() {

        return aiToolsService.actualizarUsuario(
                "juan@gmail.com",
                "Carlos"
        );
    }
}