package com.crl.bettercommandline.mixin;

import com.crl.bettercommandline.mixin.accessor.ChatInputSuggestorAccessor;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.crl.bettercommandline.inject.ChatInputSuggestorKt.afterRefresh;

@Mixin(ChatInputSuggestor.class)
public class MixinChatInputSuggestor {
    @Inject(at = @At("RETURN"), method = "refresh")
    private void refresh(CallbackInfo ci) {
        TextFieldWidget chatField = ((ChatInputSuggestorAccessor) this).getTextField();
        afterRefresh(chatField);
    }
}
