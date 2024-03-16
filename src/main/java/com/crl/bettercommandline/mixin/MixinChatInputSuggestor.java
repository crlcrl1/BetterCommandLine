package com.crl.bettercommandline.mixin;

import com.crl.bettercommandline.CommendSuggester;
import com.crl.bettercommandline.config.ModConfig;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatInputSuggestor.class)
public class MixinChatInputSuggestor {
    @Inject(at = @At("RETURN"), method = "refresh")
    private void refresh(CallbackInfo ci) {
        boolean enable = ModConfig.ENABLED.getValue();
        boolean showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.getValue();
        if (!enable || !showSuggestionWhenTyping) {
            return;
        }
        TextFieldWidget chatField = ((ChatInputSuggestorAccessor) this).getTextField();
        String chatText = chatField.getText();
        int cursor = chatField.getCursor();
        if (cursor != chatText.length()) {
            return;
        }
        String suggestion = CommendSuggester.getTypingSuggestion(chatText);
        chatField.setSuggestion(suggestion);
    }
}
