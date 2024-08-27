package com.example.telegrambot.services;

import com.example.telegrambot.config.BotConfig;
import com.example.telegrambot.enums.CommandBody;
import com.example.telegrambot.enums.CommandDescription;
import com.example.telegrambot.enums.CommandResponse;
import com.example.telegrambot.enums.LoggingMessage;
import com.example.telegrambot.services.func_interfaces.CommandHandler;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
@EqualsAndHashCode(callSuper = true)
@Service
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();

    public Bot(BotConfig config) {
        this.config = config;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        List<BotCommand> listOfCommands = List.of(
                new BotCommand(CommandBody.HELP.getBody(), CommandDescription.HELP.getDescription()),
                new BotCommand(CommandBody.START.getBody(), CommandDescription.START.getDescription())
        );

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(LoggingMessage.SETTING_COMMANDS_ERROR.getLogMessage(), e.getMessage(), e);
        }

        commandHandlers.put(CommandBody.HELP.getBody(), this::handleHelpCommand);
        commandHandlers.put(CommandBody.START.getBody(), this::handleStartCommand);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            commandHandlers.getOrDefault(message.getText(), this::handleUnrecognizedCommand).handle(chatId);
        }
    }

    private void handleStartCommand(Long chatId) {
        log.info(LoggingMessage.RECEIVED_START_COMMAND.getLogMessage(), chatId);
        sendResponse(chatId, CommandResponse.START.getResponse());
    }

    private void handleHelpCommand(Long chatId) {
        log.info(LoggingMessage.RECEIVED_HELP_COMMAND.getLogMessage(), chatId);
        sendResponse(chatId, CommandResponse.HELP.getResponse());
    }

    private void handleUnrecognizedCommand(Long chatId) {
        log.info(LoggingMessage.RECEIVED_UNRECOGNIZED_COMMAND.getLogMessage(), chatId);
        sendResponse(chatId, CommandResponse.UNRECOGNIZED.getResponse());
    }

    private void sendResponse(long chatId, String response) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(response)
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(LoggingMessage.SENDING_MESSAGE_ERROR.getLogMessage(), e.getMessage(), e);
        }
    }

    @Override
    public String getBotToken() {
        return config.token;
    }

    @Override
    public String getBotUsername() {
        return config.name;
    }
}
