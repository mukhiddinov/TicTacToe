package com.xm.tictactoebot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class TicTacToeBot extends TelegramLongPollingBot {
    private final String BOT_TOKEN = "7908074921:AAEr6VuaFOWBbYowey2K3DNCiJW7PYjqGio";
    private final String BOT_USERNAME = "@tictac_toe_kresiki_noliki_bot";

    private Map<Long, TicTacToeGame> games = new HashMap<>();
    private Map<Long, Integer> messageIds = new HashMap<>();

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId;

        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();


            if (messageText.equals("/start")) {
                sendMessage(chatId, "Tic Tac Toe o‘yiniga xush kelibsiz! Boshlash uchun /play buyrug‘ini yuboring.");
            } else if (messageText.equals("/play")) {

                getUserInfo(update.getMessage());
                startNewGame(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();

            if (data.equals("new_game")) {
                startNewGame(chatId);
            } else if (data.startsWith("move_")) {
                int messageId = update.getCallbackQuery().getMessage().getMessageId();
                String[] parts = data.split("_");
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);

                if (games.containsKey(chatId)) {
                    TicTacToeGame game = games.get(chatId);

                    if (game.makeMove(row, col)) {
                        if (game.checkWinner()) {
                            editBoard(chatId, messageId, game, "O'yin yakunlandi!");
                            sendGameOverMessage(chatId, "Tabriklaymiz 🎉 g‘olib: " + game.getCurrentPlayer() + "!");
                            games.remove(chatId);
                        } else if (game.isDraw()) {
                            editBoard(chatId, messageId, game, "O'yin yakunlandi!");
                            sendGameOverMessage(chatId, "Ajoyib, natija 🤝 Durang!");
                            games.remove(chatId);
                        } else {
                            game.switchPlayer();
                            editBoard(chatId, messageId, game, "Navbat: " + game.getCurrentPlayer());
                        }
                    }
                }
            }
        }
    }

    private void startNewGame(long chatId) {
        TicTacToeGame game = new TicTacToeGame();
        games.put(chatId, game);
        int messageId = sendBoard(chatId, game);
        messageIds.put(chatId, messageId);
    }


    private void sendGameOverMessage(long chatId, String resultText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(resultText);
        message.setReplyMarkup(createNewGameButton());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createNewGameButton() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton newGameButton = new InlineKeyboardButton();
        newGameButton.setText("🔄 Yangi o‘yin boshlash");
        newGameButton.setCallbackData("new_game");

        row.add(newGameButton);
        keyboard.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }


    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private int sendBoard(long chatId, TicTacToeGame game) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("O‘yin boshlandi! \nNavbat: " + game.getCurrentPlayer());
        message.setReplyMarkup(createBoardMarkup(game));

        try {
            return execute(message).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void editBoard(long chatId, int messageId, TicTacToeGame game, String newText) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);
        editMessage.setReplyMarkup(createBoardMarkup(game));

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createBoardMarkup(TicTacToeGame game) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                char cell = game.getCell(i, j);
                button.setText(cell == '-' ? "\uD83D\uDE35" : String.valueOf(cell));
                button.setCallbackData("move_" + i + "_" + j);
                row.add(button);
            }
            keyboard.add(row);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    private void getUserInfo(Message message) {
        User user = message.getFrom();
        Long chatId = message.getChatId();
        String username = user.getUserName();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        Boolean isPremium = user.getIsPremium();

        System.out.println(String.format("""
            \tUser Info
            Username: %s
            FirstName: %s
            LastName: %s
            IsPremium: %s
            """, username, firstName, lastName, isPremium));
    }

}
