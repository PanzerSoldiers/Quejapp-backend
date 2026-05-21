package com.quejapp.quejapi.service;

import com.quejapp.quejapi.model.Role;
import com.quejapp.quejapi.repository.UserRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ai.tool.ToolCallback;
import com.quejapp.quejapi.configuration.UserContextHolder;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final PendingActionService pendingActionService;
    private final ToolCallback[] toolCallbacks;
    private final UserRepository userRepository;

    public ChatService(
            ChatClient chatClient,
            PendingActionService pendingActionService,
            ToolCallback[] toolCallbacks,
            UserRepository userRepository
    ) {
        this.chatClient = chatClient;
        this.pendingActionService = pendingActionService;
        this.toolCallbacks = toolCallbacks;
        this.userRepository = userRepository;
    }

    private boolean esAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == Role.ADMINISTRATOR)
                .orElse(false);
    }

    public String preguntar(String mensaje) {

        String usuario = obtenerUsuarioActual();

        UserContextHolder.set(usuario);

        if (mensaje.equalsIgnoreCase("CONFIRMAR")) {
            return pendingActionService.confirmar(usuario);
        }

        var response = chatClient.prompt()
                .system("""
                Eres el asistente oficial de QuejaAPP.
                Usa herramientas cuando sea necesario.
                Nunca simules resultados
          
                Responde preguntas sobre:
                - gestión de quejas
                - soporte al usuario
                - funcionamiento de la plataforma
                - ayuda general
                - No te salgas del contexto de quejapp
                 Sé amable y profesional.
                                
            """)
                .user(mensaje)
                .toolCallbacks(toolCallbacks)
                .call()
                .chatResponse();

        UserContextHolder.clear();

        return response.getResult().getOutput().getText();
    }

    private String obtenerUsuarioActual() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        return auth.getName();
    }
}