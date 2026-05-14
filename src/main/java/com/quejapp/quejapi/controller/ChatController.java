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
    public Map<String, String> chat(
            @RequestBody Map<String, Object> request
    ) {

        String message =
                request.get("message").toString();

        String respuesta =
                chatService.preguntar(message);

        return Map.of(
                "response",
                respuesta
        );
    }
}