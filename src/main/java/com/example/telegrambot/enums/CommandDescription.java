package com.example.telegrambot.enums;

import lombok.Getter;

@Getter
public enum CommandDescription {

    HELP("Get help"),

    START("Get a welcome message");

    private final String description;

    CommandDescription(String description){
        this.description = description;
    }

}
