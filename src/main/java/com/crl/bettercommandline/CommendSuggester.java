package com.crl.bettercommandline;

import com.crl.bettercommandline.mixin.ChatInputSuggestorAccessor;
import com.crl.bettercommandline.mixin.ChatScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommendSuggester {
    private static int commandHistorySize = -1;
    private static String chatLastMessage = "";
    private static String typingSuggestion = "";
    private static TextFieldWidget chatField;
    private static MinecraftClient client;

    public static void clearHistory() {
        commandHistorySize = -1;
        chatLastMessage = "";
    }

    public static void suggestCommandFromHistory(String command,
                                                 int offset, ChatScreen chatScreen) {
        if (client == null || chatScreen == null) {
            return;
        }
        if (!Objects.equals(command, chatLastMessage)) {
            commandHistorySize = -1;
            chatLastMessage = command;
        }

        List<String> suggestions = new ArrayList<>(client.inGameHud
                .getChatHud()
                .getMessageHistory()
                .stream()
                .filter(s -> s.startsWith(command))
                .distinct()
                .toList());
        Collections.reverse(suggestions);
        int index = commandHistorySize + offset;
        if (index < 0) {
            index = -1;
        } else if (index >= suggestions.size()) {
            return;
        }
        if (index >= 0) {
            String suggestion = suggestions.get(index);
            ((ChatScreenAccessor) chatScreen).invokeSetText(suggestion);
            ((ChatScreenAccessor) chatScreen).getChatField().setCursor(command.length());
            ((ChatScreenAccessor) chatScreen).getChatInputSuggestor().setWindowActive(false);
            commandHistorySize = index;
        } else {
            ((ChatScreenAccessor) chatScreen).invokeSetText(command);
            ((ChatScreenAccessor) chatScreen).getChatField().setCursor(command.length());
            ChatInputSuggestor suggestor = ((ChatScreenAccessor) chatScreen).getChatInputSuggestor();
            if (!((ChatInputSuggestorAccessor) suggestor).isWindowActive()) {
                ((ChatScreenAccessor) chatScreen).getChatInputSuggestor().setWindowActive(true);
            }
            commandHistorySize = -1;
        }
    }

    public static void showSuggestionWhenTyping(String command) {
        if (client == null || chatField == null) {
            return;
        }

        if (command.isEmpty()) {
            return;
        }

        ArrayList<String> history = new ArrayList<>(client.inGameHud
                .getChatHud()
                .getMessageHistory()
                .stream()
                .filter(s -> s.startsWith(command))
                .toList());
        Collections.reverse(history);
        String suggestion = !history.isEmpty() ? history.get(0) : "";
        if (suggestion.length() >= command.length()) {
            suggestion = suggestion.substring(command.length());
        }
        typingSuggestion = suggestion;
        chatField.setSuggestion(suggestion);
    }

    public static void acceptTypingSuggestion() {
        chatField.setText(chatField.getText() + typingSuggestion);
        typingSuggestion = "";
        chatField.setCursor(chatField.getText().length());
    }

    public static String getTypingSuggestion(String command) {
        if (command.isEmpty() || client == null) {
            return "";
        }
        ArrayList<String> history = new ArrayList<>(client.inGameHud
                .getChatHud()
                .getMessageHistory()
                .stream()
                .filter(s -> s.startsWith(command))
                .toList());
        Collections.reverse(history);
        String suggestion = !history.isEmpty() ? history.get(0) : "";
        if (suggestion.length() <= command.length()) {
            typingSuggestion = "";
            return "";
        }
        return typingSuggestion = suggestion.substring(command.length());
    }

    public static void setChatField(TextFieldWidget chatField) {
        CommendSuggester.chatField = chatField;
    }

    public static TextFieldWidget getChatField() {
        return CommendSuggester.chatField;
    }

    public static void setClient(MinecraftClient client) {
        CommendSuggester.client = client;
    }

    public static MinecraftClient getClient() {
        return CommendSuggester.client;
    }
}

