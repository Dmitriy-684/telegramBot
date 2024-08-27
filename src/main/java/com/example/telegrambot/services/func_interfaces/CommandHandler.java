package com.example.telegrambot.services.func_interfaces;

@FunctionalInterface
public interface CommandHandler {
    void handle(Long chatId);
}
