package com.crl.bettercommandline

import com.crl.bettercommandline.mixin.accessor.ChatInputSuggestorAccessor
import com.crl.bettercommandline.mixin.accessor.ChatScreenAccessor
import io.netty.util.internal.StringUtil
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.widget.TextFieldWidget
import java.util.*

/**
 * Suggests commands from the command history.
 */
class CommendSuggester {
    companion object {
        val instance = CommendSuggester()
    }

    private var commandHistorySize = -1
    private var chatLastMessage = ""
    private var typingSuggestion: String = ""
    var chatField: TextFieldWidget? = null

    fun clearHistory() {
        commandHistorySize = -1
        chatLastMessage = ""
    }

    /**
     * Suggests a command from the command history.
     *
     * @param command    The command typed by the user
     * @param offset     The offset to help selecting the command from the history
     * @param chatScreen The chat screen opened by the user
     */
    fun suggestCommandFromHistory(command: String, offset: Int, chatScreen: ChatScreen?) {
        if (chatScreen == null) {
            return
        }
        if (command != chatLastMessage) {
            commandHistorySize = -1
            chatLastMessage = command
        }

        val suggestions = ArrayList(HistoryManager
            .instance
            .history
            .stream()
            .filter { s: String -> s.startsWith(command) }
            .toList())
        suggestions.reverse()
        var index = commandHistorySize + offset
        if (index < 0) {
            index = -1
        } else if (index >= suggestions.size) {
            return
        }
        if (index >= 0) {
            val suggestion = suggestions[index]
            (chatScreen as ChatScreenAccessor).invokeSetText(suggestion)
            (chatScreen as ChatScreenAccessor).chatField.cursor = command.length
            (chatScreen as ChatScreenAccessor).chatInputSuggestor.setWindowActive(false)
            commandHistorySize = index
        } else {
            (chatScreen as ChatScreenAccessor).invokeSetText(command)
            (chatScreen as ChatScreenAccessor).chatField.cursor = command.length
            val suggestor = (chatScreen as ChatScreenAccessor).chatInputSuggestor
            if (!(suggestor as ChatInputSuggestorAccessor).isWindowActive) {
                (chatScreen as ChatScreenAccessor).chatInputSuggestor.setWindowActive(true)
            }
            commandHistorySize = -1
        }
    }

    /**
     * Shows the suggestion when the user is typing.
     *
     * @param command The command typed by the user
     */
    fun showSuggestionWhenTyping(command: String) {
        if (chatField == null || StringUtil.isNullOrEmpty(command)) {
            return
        }

        var suggestion = getSuggestion(command)
        if (suggestion!!.length >= command.length) {
            suggestion = suggestion.substring(command.length)
        }
        typingSuggestion = suggestion
        chatField!!.setSuggestion(suggestion)
    }

    /**
     * Accepts the typing suggestion on the chat field.
     */
    fun acceptTypingSuggestion() {
        chatField!!.text += typingSuggestion
        typingSuggestion = ""
        chatField!!.cursor = chatField!!.text.length
    }

    /**
     * Get the typing suggestion.
     *
     * @param command The command typed by the user
     * @return The typing suggestion
     */
    fun getTypingSuggestion(command: String): String {
        if (command.isEmpty()) {
            return ""
        }
        val suggestion = getSuggestion(command)
        if (suggestion!!.length <= command.length) {
            typingSuggestion = ""
            return ""
        }
        return suggestion.substring(command.length).also {
            typingSuggestion = it
        }
    }

    /**
     * Get the suggestion from the command history.
     *
     * @param command The command typed by the user
     * @return The suggestion from the command history
     */
    private fun getSuggestion(command: String): String? {
        val history = ArrayList(HistoryManager
            .instance
            .history
            .stream()
            .filter { s: String -> s.startsWith(command) }
            .toList()
        )
        history.remove(command)
        history.reverse()
        return if (history.isNotEmpty()) history[0] else ""
    }

    /**
     * Suggests one word to the user, select the next word shown in the suggestion window.
     *
     * @param command    The command typed by the user
     * @param chatScreen The chat screen opened by the user
     */
    fun suggestOneWord(command: String, chatScreen: ChatScreen?) {
        if (chatScreen == null || command.isEmpty()) {
            return
        }
        val suggestions = getTypingSuggestion(command)
            .split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (suggestions.isEmpty()) {
            return
        }
        val suggestion = suggestions[0]
        if (suggestion.isEmpty()) {
            return
        }
        (chatScreen as ChatScreenAccessor).invokeSetText("$command$suggestion ")
        (chatScreen as ChatScreenAccessor).chatField.cursor = command.length
    }
}