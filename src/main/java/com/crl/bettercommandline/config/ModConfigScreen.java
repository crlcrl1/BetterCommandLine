package com.crl.bettercommandline.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ModConfigScreen extends GameOptionsScreen {
    private final Screen parent;
    private OptionListWidget optionListWidget;

    public ModConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("title.bettercommandline"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        optionListWidget = new OptionListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        optionListWidget.addAll(ModConfig.asOptions());
        this.addSelectableChild(this.optionListWidget);
        this.addDrawableChild(
                ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
                            ModConfigManager.save();
                            if (this.client != null) {
                                this.client.setScreen(this.parent);
                            }
                        })
                        .position(this.width / 2 - 100, this.height - 27)
                        .size(200, 20)
                        .build()
        );
    }

    @Override
    public void removed() {
        ModConfigManager.save();
    }

    @Override
    public void render(DrawContext DrawContext, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(DrawContext);
        this.optionListWidget.render(DrawContext, mouseX, mouseY, delta);
        DrawContext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 0xffffff);
        super.render(DrawContext, mouseX, mouseY, delta);
    }
}
