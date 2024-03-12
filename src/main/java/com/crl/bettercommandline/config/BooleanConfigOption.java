package com.crl.bettercommandline.config;

import com.crl.bettercommandline.BetterCommandLine;
import com.terraformersmc.modmenu.config.option.ConfigOptionStorage;
import com.terraformersmc.modmenu.config.option.OptionConvertable;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class BooleanConfigOption implements OptionConvertable {
    private final String key;
    private final String translationKey;
    private final Text enabledText;
    private final Text disabledText;
    private final String tooltipKey;

    public BooleanConfigOption(String key, boolean defaultValue) {
        this(key, defaultValue, "true", "false", null);
    }

    public BooleanConfigOption(String key, boolean defaultValue, String enabledText,
                               String disabledText, String tooltip) {
        ConfigOptionStorage.setBoolean(key, defaultValue);
        this.key = key;
        this.translationKey = "option." + BetterCommandLine.MOD_ID + "." + key;
        this.enabledText = Text.translatable(translationKey + "." + enabledText);
        this.disabledText = Text.translatable(translationKey + "." + disabledText);
        if (tooltip != null) {
            this.tooltipKey = "tooltip." + BetterCommandLine.MOD_ID + "." + tooltip;
        } else {
            this.tooltipKey = null;
        }
    }

    public BooleanConfigOption(String key, boolean defaultValue, String tooltip) {
        this(key, defaultValue, "true", "false", tooltip);
    }


    public boolean getValue() {
        return ConfigOptionStorage.getBoolean(key);
    }

    public void setValue(boolean value) {
        ConfigOptionStorage.setBoolean(key, value);
    }

    public String getKey() {
        return key;
    }

    public Text getButtonText() {
        return ScreenTexts.composeGenericOptionText(Text.of(key), getValue() ? enabledText : disabledText);
    }

    @Override
    public SimpleOption<Boolean> asOption() {
        if (enabledText != null && disabledText != null) {
            return new SimpleOption<>(translationKey,
                    tooltipKey == null ? SimpleOption.emptyTooltip() : SimpleOption.constantTooltip(Text.translatable(tooltipKey)),
                    (text, value) -> value ? enabledText : disabledText, SimpleOption.BOOLEAN, getValue(),
                    newValue -> ConfigOptionStorage.setBoolean(key, newValue));
        }
        return SimpleOption.ofBoolean(translationKey, getValue(), (value) -> ConfigOptionStorage.setBoolean(key, value));
    }
}