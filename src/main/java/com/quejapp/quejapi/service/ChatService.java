package com.quejapp.quejapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatService {

    private final String OLLAMA_URL = "http://localhost:11434/api/chat";

    public String preguntar(String mensaje) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> request = Map.of(
                    "model", "queja-bot",
                    "messages", new Object[] {
                            Map.of("role", "user", "content", mensaje)
                    },
                    "stream", false
            );

            Map response = restTemplate.postForObject(OLLAMA_URL, request, Map.class);

            if (response != null && response.get("message") != null) {
                Map messageMap = (Map) response.get("message");
                return messageMap.get("content").toString();
            }
            else {
                return "No pude generar una respuesta 🤖";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al conectar con la IA 🤖";
        }
    }
}