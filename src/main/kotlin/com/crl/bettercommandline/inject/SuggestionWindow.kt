package com.crl.bettercommandline.inject

import com.crl.bettercommandline.CommandSuggester.chatField
import com.crl.bettercommandline.CommandSuggester.showSuggestionWhenTyping
import com.crl.bettercommandline.config.ModConfig

fun afterSelect() {
    val enable = ModConfig.ENABLED.value
    val showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.value
    if (!enable || !showSuggestionWhenTyping) {
        return
    }

    val chatField = chatField
    if (chatField != null) {
        showSuggestionWhenTyping(chatField.text)
    }
}