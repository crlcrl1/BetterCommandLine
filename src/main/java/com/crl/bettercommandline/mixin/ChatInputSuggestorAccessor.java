package com.crl.bettercommandline.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatInputSuggestor.class)
public interface ChatInputSuggestorAccessor {
    @Accessor("windowActive")
    boolean isWindowActive();

    @Accessor("textField")
    TextFieldWidget getTextField();
    
    @Accessor("client")
    MinecraftClient getClient();
}