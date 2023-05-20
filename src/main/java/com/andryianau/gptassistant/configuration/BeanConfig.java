package com.andryianau.gptassistant.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.impl.FileApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Value("${app.bot.token}")
    private String token;

    @Value("${chatgpt.key}")
    private String openaiApiKey;

    @Bean
    @Qualifier("openaiRestTemplate")
    public RestTemplate openaiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }

    @Bean
    TelegramBot bot() {
        return new TelegramBot(token);
    }

    @Bean
    FileApi fileApi() { return new FileApi(token); }

}
