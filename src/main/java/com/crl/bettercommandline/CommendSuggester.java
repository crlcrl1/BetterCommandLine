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
    private int commandHistorySize = -1;
    private String chatLastMessage = "";
    private String typingSuggestion = "";

    public void clearHistory() {
        commandHistorySize = -1;
        chatLastMessage = "";
    }

    public void suggestCommandFromHistory(MinecraftClient client, String command,
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

    /*
     * parameter useOriginalText is used to determine whether to use the original text
     * I don't know a better way to show suggestion when the chat screen opens.
     * */
    public void showSuggestionWhenTyping(MinecraftClient client, String command,
                                         ChatScreen chatScreen, boolean useOriginalText) {
        if (client == null || chatScreen == null) {
            return;
        }

        if (useOriginalText) {
            command = ((ChatScreenAccessor) chatScreen).getChatField().getText();
        }

        String finalCommand = command;
        ArrayList<String> history = new ArrayList<>(client.inGameHud
                .getChatHud()
                .getMessageHistory()
                .stream()
                .filter(s -> s.startsWith(finalCommand))
                .toList());
        Collections.reverse(history);
        String suggestion = !history.isEmpty() ? history.get(0) : "";
        if (suggestion.length() <= command.length()) {
            return;
        }
        suggestion = suggestion.substring(command.length());
        typingSuggestion = suggestion;
        TextFieldWidget chatField = ((ChatScreenAccessor) chatScreen).getChatField();
        chatField.setSuggestion(suggestion);
    }

    public void acceptTypingSuggestion(ChatScreen chatScreen) {
        if (chatScreen == null) {
            return;
        }
        TextFieldWidget chatField = ((ChatScreenAccessor) chatScreen).getChatField();
        chatField.setText(chatField.getText() + typingSuggestion);
        chatField.setCursor(chatField.getText().length());
    }
}

