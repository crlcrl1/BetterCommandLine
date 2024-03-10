package com.crl.bettercommandline.mixin;

import com.crl.bettercommandline.CommendSuggester;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinKeyBinding {
    private static final CommendSuggester suggester = new CommendSuggester();

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    private void suggestCommand(int keyCode, int scanCode, int modifiers,
                                CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_UP) {
            int index = ((ChatScreenAccessor) this).getChatField().getCursor();
            String command = ((ChatScreenAccessor) this).getChatField().getText();
            command = command.substring(0, index);
            suggester.suggestCommandFromHistory(((ScreenAccessor) this).getClient(), command, 1,
                    (ChatScreen) (Object) this);
            ((ChatScreenAccessor) this).getChatField().setCursor(index);
            cir.setReturnValue(true);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            int index = ((ChatScreenAccessor) this).getChatField().getCursor();
            String command = ((ChatScreenAccessor) this).getChatField().getText();
            command = command.substring(0, index);
            suggester.suggestCommandFromHistory(((ScreenAccessor) this).getClient(), command, -1,
                    (ChatScreen) (Object) this);
            ((ChatScreenAccessor) this).getChatField().setCursor(index);
            cir.setReturnValue(true);
        }
    }
}
