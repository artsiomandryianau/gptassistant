package com.andryianau.gptassistant;

import com.andryianau.gptassistant.model.ChatRequest;
import com.andryianau.gptassistant.model.ChatResponse;
import com.andryianau.gptassistant.service.VoiceToTextConverterService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.impl.FileApi;
import com.pengrad.telegrambot.model.Audio;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Voice;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateListenerImpl implements UpdatesListener {

    private final TelegramBot bot;

    private final FileApi fileApi;

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${chatgpt.model}")
    private String model;

    @Value("${chatgpt.url}")
    private String apiUrl;

    @Autowired
    private VoiceToTextConverterService converter;

    @Override
    public int process(final List<Update> updates) {
        for (Update update : updates) {
            int lastSuccessfullyProcessedUpdateId = update.updateId();
            try {
                String message = "";
                if (update.message() == null && update.message().voice() != null) {
//                    Voice voice = update.message().voice();
//                    String fileId = voice.fileId();
//
//                    // Download the voice message file
//                    GetFile getFile = new GetFile(fileId);
//                    GetFileResponse telegramFile = bot.execute(getFile);
//
//                    File file = telegramFile.file();
//                    // Convert voice message to text using Google Cloud Speech-to-Text
//                    System.out.println(fileApi.getFullFilePath(file.filePath()));
//
//                    String transcript = converter.convertVoiceMessageToText(getFileBytes(file));

                    // Reply to the user with the transcribed text
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Buy premium version of Telegram and recognise your" +
                            "wonderful voice to text, than copy.");
                    SendResponse response1 = bot.execute(sendMessage);

                } else if (update.message().text() != null) {
                    ChatRequest request = new ChatRequest(model, update.message().text());

                    // call the API
                    ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

                    if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                        message = "No response";
                    }

                    // return the first response
                    message = response.getChoices().get(0).getMessage().getContent();
                }

                SendMessage sendMessage = new SendMessage(update.message().chat().id(), message);
                SendResponse response1 = bot.execute(sendMessage);
            } catch (Exception exception) {
                // todo write as json
                log.error("{}", update, exception);
                return lastSuccessfullyProcessedUpdateId;
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private byte[] getFileBytes(File file) {
        try {
            URL url = new URL(fileApi.getFullFilePath(file.filePath())); // Replace getBotToken() with your actual bot token
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
