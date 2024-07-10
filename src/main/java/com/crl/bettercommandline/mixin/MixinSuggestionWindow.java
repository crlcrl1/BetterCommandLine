package com.crl.bettercommandline.mixin;

import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.crl.bettercommandline.inject.SuggestionWindowKt.afterSelect;

@Mixin(ChatInputSuggestor.SuggestionWindow.class)
public class MixinSuggestionWindow {
    @Inject(at = @At("RETURN"), method = "select")
    private void showSuggest(int index, CallbackInfo ci) {
        afterSelect();
    }
}
