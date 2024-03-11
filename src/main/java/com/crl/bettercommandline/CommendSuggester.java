package com.crl.bettercommandline;

import com.crl.bettercommandline.mixin.ChatInputSuggestorAccessor;
import com.crl.bettercommandline.mixin.ChatScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommendSuggester {
    private int commandHistorySize = -1;
    private String chatLastMessage = "";

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
}

