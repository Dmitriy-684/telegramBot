package com.example.telegrambot.enums;

import lombok.Getter;

@Getter
public enum LoggingMessage {

    RECEIVED_HELP_COMMAND("Received help command for chatId: {}"),

    RECEIVED_START_COMMAND("Received start command for chatId: {}"),

    RECEIVED_UNRECOGNIZED_COMMAND("Received unrecognized command for chatId: {}"),

    SENDING_MESSAGE_ERROR("Error sending message: {}"),

    SETTING_COMMANDS_ERROR("Error setting commands: {}"),

    ERROR("Error occurred: {}");

    private final String logMessage;

    LoggingMessage(String logMessage){
        this.logMessage = logMessage;
    }
}
