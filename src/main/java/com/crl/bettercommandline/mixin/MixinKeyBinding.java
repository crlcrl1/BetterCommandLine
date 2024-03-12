package com.crl.bettercommandline.mixin;

import com.crl.bettercommandline.CommendSuggester;
import com.crl.bettercommandline.config.ModConfig;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinKeyBinding {
    @Unique
    private static CommendSuggester suggester;

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    private void suggestCommand(int keyCode, int scanCode, int modifiers,
                                CallbackInfoReturnable<Boolean> cir) {
        boolean enable = ModConfig.ENABLED.getValue();
        if (!enable) {
            return;
        }
        boolean useRightCtrl = ModConfig.USE_RIGHT_CTRL.getValue();
        if (keyCode >= GLFW.GLFW_KEY_SPACE && keyCode <= GLFW.GLFW_KEY_GRAVE_ACCENT) {
            suggester.clearHistory();
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT) {
            suggester.clearHistory();
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL && useRightCtrl) {
            ChatInputSuggestor suggestor = ((ChatScreenAccessor) this).getChatInputSuggestor();
            suggestor.keyPressed(GLFW.GLFW_KEY_UP, scanCode, modifiers);
            return;
        }

        String command = ((ChatScreenAccessor) this).getChatField().getText();
        int index = ((ChatScreenAccessor) this).getChatField().getCursor();
        command = command.substring(0, index);

        if (keyCode == GLFW.GLFW_KEY_UP) {
            suggester.suggestCommandFromHistory(((ScreenAccessor) this).getClient(), command, 1,
                    (ChatScreen) (Object) this);
            cir.setReturnValue(true);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if (command.length() == ((ChatScreenAccessor) this).getChatField().getText().length())
                return;
            suggester.suggestCommandFromHistory(((ScreenAccessor) this).getClient(), command, -1,
                    (ChatScreen) (Object) this);
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/String;)V")
    private void init(CallbackInfo ci) {
        suggester = new CommendSuggester();
    }
}
