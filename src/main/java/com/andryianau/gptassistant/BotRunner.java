package com.andryianau.gptassistant;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class BotRunner {

    private final TelegramBot bot;
    private final UpdatesListener updatesListener;

    @PostConstruct
    void run() {
        bot.setUpdatesListener(updatesListener);
    }
}
