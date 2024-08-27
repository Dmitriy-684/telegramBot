package com.example.telegrambot.enums;

import lombok.Getter;

@Getter
public enum CommandBody {

    HELP("/help"),

    START("/start");

    private final String body;

    CommandBody(String body){
        this.body = body;
    }
}
