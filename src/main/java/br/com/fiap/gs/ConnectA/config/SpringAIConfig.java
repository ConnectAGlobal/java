package br.com.fiap.gs.ConnectA.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    public ChatModel chatModel() {
        OpenAiApi openAiApi = new OpenAiApi(apiKey);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel("gpt-4")
                .withTemperature(0.7)
                .build();

        return new OpenAiChatModel(openAiApi, options);
    }
}