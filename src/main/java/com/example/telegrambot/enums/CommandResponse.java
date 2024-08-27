package com.example.telegrambot.enums;

import lombok.Getter;

@Getter
public enum CommandResponse {

    HELP("Help command"),

    START("Start command"),

    UNRECOGNIZED("Unrecognized command");

    private final String response;

    CommandResponse(String response){
        this.response = response;
    }
}
