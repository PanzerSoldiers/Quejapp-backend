package com.quejapp.quejapi.controller;

import com.quejapp.quejapi.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, Object> request) {

        String message = request.get("message").toString();
        String msg = message.toLowerCase();

// 🔒 VALIDACIÓN MÁS FLEXIBLE
        boolean esTemaQueja =
                msg.contains("queja") ||
                        msg.contains("reclamo") ||
                        msg.contains("problema") ||
                        msg.contains("estado") ||
                        msg.contains("reporte") ||
                        msg.contains("denuncia");

        boolean esIdentidadBot =
                msg.contains("quien") ||
                        msg.contains("eres") ||
                        msg.contains("sirves") ||
                        msg.contains("haces") ||
                        msg.contains("asistente")||
                        msg.contains("hola");

        boolean esSistema =
                msg.contains("app") ||
                        msg.contains("sistema") ||
                        msg.contains("plataforma");

        if (!(esTemaQueja || esIdentidadBot || esSistema)) {
            return Map.of("response",
                    "Lo siento, solo puedo ayudarte con temas relacionados con la plataforma QuejaAPP.");
        }

        String respuesta = chatService.preguntar(message);

        return Map.of("response", respuesta);
    }
}