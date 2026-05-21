package com.quejapp.quejapi.configuration;

import com.quejapp.quejapi.service.AiToolsService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiToolsConfig {

    @Bean
    public ToolCallback[] toolCallbacks(AiToolsService aiToolsService) {

        return MethodToolCallbackProvider
                .builder()
                .toolObjects(aiToolsService)
                .build()
                .getToolCallbacks();
    }
}