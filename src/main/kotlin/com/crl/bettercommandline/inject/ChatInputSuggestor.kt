package com.crl.bettercommandline.inject

import com.crl.bettercommandline.CommandSuggester
import com.crl.bettercommandline.config.ModConfig
import net.minecraft.client.gui.widget.TextFieldWidget

fun afterRefresh(chatField: TextFieldWidget) {
    val enable = ModConfig.ENABLED.value
    val showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.value
    if (!enable || !showSuggestionWhenTyping) {
        return
    }
    val chatText = chatField.text
    val cursor = chatField.cursor
    if (cursor != chatText.length) {
        return
    }
    val suggestion = CommandSuggester.getTypingSuggestion(chatText)
    chatField.setSuggestion(suggestion)

}