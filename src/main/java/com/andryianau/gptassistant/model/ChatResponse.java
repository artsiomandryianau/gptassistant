package com.andryianau.gptassistant.model;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponse {

    private List<Choice> choices;

    // constructors, getters and setters
    @Data
    public static class Choice {

        private int index;
        private Message message;

        // constructors, getters and setters
    }
}
