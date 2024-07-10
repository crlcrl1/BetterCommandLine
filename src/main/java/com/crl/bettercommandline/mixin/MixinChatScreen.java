package com.crl.bettercommandline.mixin;

import com.crl.bettercommandline.mixin.accessor.ChatScreenAccessor;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.crl.bettercommandline.inject.ChatScreenKt.*;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    private void suggestCommand(int keyCode, int scanCode, int modifiers,
                                CallbackInfoReturnable<Boolean> cir) {
        TextFieldWidget chatField = ((ChatScreenAccessor) this).getChatField();
        ChatInputSuggestor suggestor = ((ChatScreenAccessor) this).getChatInputSuggestor();
        ChatScreen chatScreen = (ChatScreen) (Object) this;
        beforeKeyPressed(keyCode, scanCode, modifiers, cir, chatField, suggestor, chatScreen);
    }

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/String;)V")
    private void init(CallbackInfo ci) {
        afterInit();
    }

    @Inject(at = @At("RETURN"), method = "onChatFieldUpdate")
    private void onChatFieldUpdate(String chatText, CallbackInfo ci) {
        TextFieldWidget chatField = ((ChatScreenAccessor) this).getChatField();
        afterOnChatFieldUpdate(chatText, chatField);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void provideSuggest(CallbackInfo ci) {
        TextFieldWidget chatField = ((ChatScreenAccessor) this).getChatField();
        tailInit(chatField);
    }
}

