package com.quejapp.quejapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatService {

    private final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public String preguntar(String mensaje) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> request = Map.of(
                    "model", "llama3",
                    "prompt", mensaje,
                    "stream", false
            );

            Map response = restTemplate.postForObject(OLLAMA_URL, request, Map.class);

            // 🔥 Validación segura
            if (response != null && response.get("response") != null) {
                return response.get("response").toString();
            } else {
                return "No pude generar una respuesta 🤖";
            }

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 CLAVE
            return "Error al conectar con la IA 🤖";
        }
    }
}