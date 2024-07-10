package com.crl.bettercommandline.inject

import com.crl.bettercommandline.CommandSuggester
import com.crl.bettercommandline.CommandSuggester.acceptTypingSuggestion
import com.crl.bettercommandline.CommandSuggester.clearHistory
import com.crl.bettercommandline.CommandSuggester.suggestCommandFromHistory
import com.crl.bettercommandline.CommandSuggester.suggestOneWord
import com.crl.bettercommandline.HistoryManager.addCommand
import com.crl.bettercommandline.config.ModConfig
import net.minecraft.client.gui.screen.ChatInputSuggestor
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.widget.TextFieldWidget
import org.lwjgl.glfw.GLFW
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * This function is called before the key is pressed
 * When the key is pressed, this function will accept suggestions
 */
fun beforeKeyPressed(
    keyCode: Int,
    scanCode: Int,
    modifiers: Int,
    cir: CallbackInfoReturnable<Boolean>,
    charField: TextFieldWidget,
    suggestor: ChatInputSuggestor,
    chatScreen: ChatScreen
) {
    val enable = ModConfig.ENABLED.value
    val showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.value
    if (!enable) {
        return
    }

    val text = charField.text
    val cursorPosition = charField.cursor
    val command = text.substring(0, cursorPosition)
    val useRightCtrl = ModConfig.USE_RIGHT_CTRL.value
    if (keyCode >= GLFW.GLFW_KEY_SPACE && keyCode <= GLFW.GLFW_KEY_GRAVE_ACCENT) {
        clearHistory()
        return
    }


    // Clear history for next suggestion when the user types the left arrow key
    if (keyCode == GLFW.GLFW_KEY_LEFT && cursorPosition != 0) {
        clearHistory()
        return
    }

    if (keyCode == GLFW.GLFW_KEY_RIGHT) {
        // Suggest one word if key CTRL is pressed
        if (modifiers == GLFW.GLFW_MOD_CONTROL) {
            suggestOneWord(command, chatScreen)
            clearHistory()
            return
        }
        // Suggestions are shown when the user types the right arrow key and the cursor
        // is at the end of the text
        if (cursorPosition == text.length && showSuggestionWhenTyping) {
            acceptTypingSuggestion()
            cir.setReturnValue(true)
        }
        // Clear history for next suggestion if the cursor is not at the end of the text
        if (cursorPosition != text.length) {
            clearHistory()
        }
        return
    }


    // Add the command to the history when the user presses the enter key
    if (keyCode == GLFW.GLFW_KEY_ENTER) {
        addCommand(text)
        return
    }


    // If the user want to use the right control key to show the suggestion
    // from Minecraft's suggestion list, then the right control key will be used
    if (keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL && useRightCtrl) {
        suggestor.keyPressed(GLFW.GLFW_KEY_UP, scanCode, modifiers)
        return
    }


    // Use the up and down arrow keys to navigate the command history
    if (keyCode == GLFW.GLFW_KEY_UP) {
        suggestCommandFromHistory(
            command, 1,
            chatScreen
        )
        cir.setReturnValue(true)
    } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
        // If the cursor is not at the end of the text, the user can
        // use the down arrow key to navigate the command history
        // Otherwise, the user can use the down arrow key to show the suggestion
        // from Minecraft's suggestion list
        if (command.length == text.length) return
        suggestCommandFromHistory(
            command, -1,
            chatScreen
        )
        cir.setReturnValue(true)
    }
}

/**
 * This function is called after the chat field is updated
 * When the chat field is updated, the suggestion will be shown
 */
fun afterOnChatFieldUpdate(chatText: String, chatField: TextFieldWidget) {
    val enabled = ModConfig.ENABLED.value
    val showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.value
    if (!enabled || !showSuggestionWhenTyping) {
        return
    }
    if (chatText.length != chatField.cursor) {
        return
    }
    CommandSuggester.showSuggestionWhenTyping(chatText)
}

fun afterInit() {
    val enable = ModConfig.ENABLED.value
    if (!enable) {
        return
    }
    clearHistory()
}

fun tailInit(charField: TextFieldWidget) {
    val enable = ModConfig.ENABLED.value
    val showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.value
    if (!enable || !showSuggestionWhenTyping) {
        return
    }
    CommandSuggester.chatField = charField
}