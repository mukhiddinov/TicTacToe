package com.xm.tictactoebot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Scanner;

@SpringBootApplication
public class TicTacToeBotApplication {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Bot Tokenni kiriting: ");
        String botToken = scanner.nextLine();

        System.out.print("Bot Usernameni kiriting: ");
        String botUsername = scanner.nextLine();

        scanner.close();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TicTacToeBot(botToken, botUsername));
            System.out.println("Bot started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
