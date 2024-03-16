package com.crl.bettercommandline.config;

import com.crl.bettercommandline.config.options.BooleanConfigOption;
import com.terraformersmc.modmenu.config.option.OptionConvertable;
import net.minecraft.client.option.SimpleOption;
import com.crl.bettercommandline.config.options.EnumConfigOption;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class ModConfig {
    public static final BooleanConfigOption ENABLED = new BooleanConfigOption("enabled", true);
    public static final BooleanConfigOption USE_RIGHT_CTRL = new BooleanConfigOption("use_right_ctrl", true, "use_right_ctrl");
    public static final BooleanConfigOption SHOW_SUGGESTION_WHEN_TYPING =
            new BooleanConfigOption("show_suggestion_when_typing", true, "show_suggestion_when_typing");
    public static final EnumConfigOption<HistorySize> HISTORY_SIZE =
            new EnumConfigOption<>("history_size", HistorySize.FIVE_HUNDRED, "history_size");

    public static SimpleOption<?>[] asOptions() {
        ArrayList<SimpleOption<?>> options = new ArrayList<>();
        for (Field field : ModConfig.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())
                    && OptionConvertable.class.isAssignableFrom(field.getType())
            ) {
                try {
                    options.add(((OptionConvertable) field.get(null)).asOption());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return options.toArray(SimpleOption[]::new);
    }

    public enum HistorySize {
        ONE_HUNDRED,
        TWO_HUNDRED,
        FIVE_HUNDRED,
    }
}


