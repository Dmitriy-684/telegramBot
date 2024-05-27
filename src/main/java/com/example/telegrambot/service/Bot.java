package com.example.telegrambot.service;

import com.example.telegrambot.config.BotConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Service
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotConfig config;

    public Bot(BotConfig config){
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "help"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            Message message = update.getMessage();
            Chat chat = message.getChat();
            String messageText = message.getText();
            long chatId = message.getChatId();
            switch (messageText){
                case "/start" -> startCommandReceived(chatId, chat.getFirstName());
                default -> sendMessage(chatId, "Unrecognized command");
            }
        }
    }

    private void startCommandReceived(long chatId, String userName){
        String text = "Hi, " + userName + "!";
        sendMessage(chatId, text);
        log.info("Replied to user " + userName);
    }

    private void sendMessage(long chatId, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    @Override
    public String getBotToken(){
        return config.token;
    }

    @Override
    public String getBotUsername() {
        return config.name;
    }

}
