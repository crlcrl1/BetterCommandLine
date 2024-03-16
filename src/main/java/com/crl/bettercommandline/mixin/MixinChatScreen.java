package com.crl.bettercommandline.mixin;

import com.crl.bettercommandline.CommendSuggester;
import com.crl.bettercommandline.HistoryManager;
import com.crl.bettercommandline.config.ModConfig;
import com.crl.bettercommandline.mixin.accessor.ChatScreenAccessor;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    private void suggestCommand(int keyCode, int scanCode, int modifiers,
                                CallbackInfoReturnable<Boolean> cir) {
        boolean enable = ModConfig.ENABLED.getValue();
        boolean showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.getValue();
        if (!enable) {
            return;
        }
        String text = ((ChatScreenAccessor) this).getChatField().getText();
        int index = ((ChatScreenAccessor) this).getChatField().getCursor();
        String command = text.substring(0, index);
        boolean useRightCtrl = ModConfig.USE_RIGHT_CTRL.getValue();
        if (keyCode >= GLFW.GLFW_KEY_SPACE && keyCode <= GLFW.GLFW_KEY_GRAVE_ACCENT) {
            CommendSuggester.clearHistory();
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT && index != 0) {
            CommendSuggester.clearHistory();
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            if (index == text.length() && showSuggestionWhenTyping) {
                CommendSuggester.acceptTypingSuggestion();
            }
            if (index != text.length()) {
                CommendSuggester.clearHistory();
            }
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            HistoryManager.addCommand(text);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL && useRightCtrl) {
            ChatInputSuggestor suggestor = ((ChatScreenAccessor) this).getChatInputSuggestor();
            suggestor.keyPressed(GLFW.GLFW_KEY_UP, scanCode, modifiers);
            return;
        }


        if (keyCode == GLFW.GLFW_KEY_UP) {
            CommendSuggester.suggestCommandFromHistory(command, 1, (ChatScreen) (Object) this);
            cir.setReturnValue(true);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if (command.length() == ((ChatScreenAccessor) this).getChatField().getText().length())
                return;
            CommendSuggester.suggestCommandFromHistory(command, -1, (ChatScreen) (Object) this);
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/String;)V")
    private void init(CallbackInfo ci) {
        boolean enable = ModConfig.ENABLED.getValue();
        if (!enable) {
            return;
        }
        CommendSuggester.clearHistory();
    }

    @Inject(at = @At("RETURN"), method = "onChatFieldUpdate")
    private void onChatFieldUpdate(String chatText, CallbackInfo ci) {
        boolean enable = ModConfig.ENABLED.getValue();
        boolean showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.getValue();
        if (!enable || !showSuggestionWhenTyping) {
            return;
        }
        TextFieldWidget chatField = ((ChatScreenAccessor) this).getChatField();
        if (chatText.length() != chatField.getCursor())
            return;
        CommendSuggester.showSuggestionWhenTyping(chatText);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void provideSuggest(CallbackInfo ci) {
        boolean enable = ModConfig.ENABLED.getValue();
        boolean showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.getValue();
        if (!enable || !showSuggestionWhenTyping) {
            return;
        }
        CommendSuggester.setChatField(((ChatScreenAccessor) this).getChatField());
    }
}

