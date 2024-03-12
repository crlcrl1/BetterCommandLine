package com.crl.bettercommandline;

import com.terraformersmc.modmenu.config.option.BooleanConfigOption;
import com.terraformersmc.modmenu.config.option.OptionConvertable;
import net.minecraft.client.option.SimpleOption;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class ModConfig {
    public static final BooleanConfigOption ENABLED = new BooleanConfigOption("enabled", true);
    public static final BooleanConfigOption USE_RIGHT_CTRL = new BooleanConfigOption("useRightCtrl", true);

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
}
