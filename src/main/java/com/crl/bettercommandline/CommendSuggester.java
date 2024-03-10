package com.crl.bettercommandline;

import com.crl.bettercommandline.mixin.ChatScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

import java.util.List;
import java.util.Objects;

public class CommendSuggester {
    private int commandHistorySize = 0;
    private String chatLastMessage = "";

    public void suggestCommandFromHistory(MinecraftClient client, String command,
                                          int offset, ChatScreen chatScreen) {
        if (client == null || chatScreen == null) {
            return;
        }
        if (!Objects.equals(command, chatLastMessage)) {
            commandHistorySize = -1;
            chatLastMessage = command;
        }

        List<String> suggestions = client.inGameHud
                .getChatHud()
                .getMessageHistory()
                .stream()
                .filter(s -> s.startsWith(command))
                .toList();
        int index = commandHistorySize + offset;
        if (index < 0) {
            index = -1;
        } else if (index >= suggestions.size()) {
            index = suggestions.size() - 1;
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
            ((ChatScreenAccessor) chatScreen).getChatInputSuggestor().setWindowActive(true);
            commandHistorySize = -1;
        }
    }
}

