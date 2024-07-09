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
        // Get the cursor position
        int cursorPosition = ((ChatScreenAccessor) this).getChatField().getCursor();
        String command = text.substring(0, cursorPosition);
        boolean useRightCtrl = ModConfig.USE_RIGHT_CTRL.getValue();
        if (keyCode >= GLFW.GLFW_KEY_SPACE && keyCode <= GLFW.GLFW_KEY_GRAVE_ACCENT) {
            CommendSuggester.Companion.getInstance().clearHistory();
            return;
        }

        // Clear history for next suggestion when the user types the left arrow key
        if (keyCode == GLFW.GLFW_KEY_LEFT && cursorPosition != 0) {
            CommendSuggester.Companion.getInstance().clearHistory();
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            // Suggest one word if key CTRL is pressed
            if (modifiers == GLFW.GLFW_MOD_CONTROL) {
                CommendSuggester.Companion.getInstance().suggestOneWord(command, (ChatScreen) (Object) this);
                CommendSuggester.Companion.getInstance().clearHistory();
                return;
            }
            // Suggestions are shown when the user types the right arrow key and the cursor
            // is at the end of the text
            if (cursorPosition == text.length() && showSuggestionWhenTyping) {
                CommendSuggester.Companion.getInstance().acceptTypingSuggestion();
                cir.setReturnValue(true);
            }
            // Clear history for next suggestion if the cursor is not at the end of the text
            if (cursorPosition != text.length()) {
                CommendSuggester.Companion.getInstance().clearHistory();
            }
            return;
        }

        // Add the command to the history when the user presses the enter key
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            HistoryManager.Companion.getInstance().addCommand(text);
            return;
        }

        // If the user want to use the right control key to show the suggestion
        // from Minecraft's suggestion list, then the right control key will be used
        if (keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL && useRightCtrl) {
            ChatInputSuggestor suggestor = ((ChatScreenAccessor) this).getChatInputSuggestor();
            suggestor.keyPressed(GLFW.GLFW_KEY_UP, scanCode, modifiers);
            return;
        }

        // Use the up and down arrow keys to navigate the command history
        if (keyCode == GLFW.GLFW_KEY_UP) {
            CommendSuggester.Companion.getInstance().suggestCommandFromHistory(command, 1,
                    (ChatScreen) (Object) this);
            cir.setReturnValue(true);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            // If the cursor is not at the end of the text, the user can
            // use the down arrow key to navigate the command history
            // Otherwise, the user can use the down arrow key to show the suggestion
            // from Minecraft's suggestion list
            if (command.length() == ((ChatScreenAccessor) this).getChatField().getText().length())
                return;
            CommendSuggester.Companion.getInstance().suggestCommandFromHistory(command, -1,
                    (ChatScreen) (Object) this);
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/String;)V")
    private void init(CallbackInfo ci) {
        boolean enable = ModConfig.ENABLED.getValue();
        if (!enable) {
            return;
        }
        CommendSuggester.Companion.getInstance().clearHistory();
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
        CommendSuggester.Companion.getInstance().showSuggestionWhenTyping(chatText);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void provideSuggest(CallbackInfo ci) {
        boolean enable = ModConfig.ENABLED.getValue();
        boolean showSuggestionWhenTyping = ModConfig.SHOW_SUGGESTION_WHEN_TYPING.getValue();
        if (!enable || !showSuggestionWhenTyping) {
            return;
        }
        CommendSuggester.Companion.getInstance().setChatField(((ChatScreenAccessor) this).getChatField());
    }
}

