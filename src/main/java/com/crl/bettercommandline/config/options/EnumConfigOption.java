package com.crl.bettercommandline.config.options;

import com.crl.bettercommandline.BetterCommandLine;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.terraformersmc.modmenu.config.option.ConfigOptionStorage;
import com.terraformersmc.modmenu.config.option.OptionConvertable;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;

public class EnumConfigOption<E extends Enum<E>> implements OptionConvertable {
    private final String key, translationKey;
    private final Class<E> enumClass;
    private final E defaultValue;
    private final String tooltipKey;

    public EnumConfigOption(String key, E defaultValue, String tooltip) {
        ConfigOptionStorage.setEnum(key, defaultValue);
        this.key = key;
        this.translationKey = "option." + BetterCommandLine.MOD_ID + "." + key;
        this.enumClass = defaultValue.getDeclaringClass();
        this.defaultValue = defaultValue;
        if (tooltip != null) {
            this.tooltipKey = "tooltip." + BetterCommandLine.MOD_ID + "." + tooltip;
        } else {
            this.tooltipKey = null;
        }
    }

    public String getKey() {
        return key;
    }

    public E getValue() {
        return ConfigOptionStorage.getEnum(key, enumClass);
    }

    public void setValue(E value) {
        ConfigOptionStorage.setEnum(key, value);
    }

    public void cycleValue() {
        ConfigOptionStorage.cycleEnum(key, enumClass);
    }

    public void cycleValue(int amount) {
        ConfigOptionStorage.cycleEnum(key, enumClass, amount);
    }

    public E getDefaultValue() {
        return defaultValue;
    }

    private static <E extends Enum<E>> Text getValueText(EnumConfigOption<E> option, E value) {
        return Text.translatable(option.translationKey + "." + value.name().toLowerCase(Locale.ROOT));
    }

    public Text getButtonText() {
        return ScreenTexts.composeGenericOptionText(Text.translatable(translationKey), getValueText(this, getValue()));
    }

    @Override
    public SimpleOption<E> asOption() {
        if (tooltipKey != null) {
            return new SimpleOption<>(translationKey, SimpleOption.constantTooltip(Text.translatable(tooltipKey)),
                    (text, value) -> getValueText(this, value),
                    new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(enumClass.getEnumConstants()),
                            Codec.STRING.xmap(
                                    string -> Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.name().toLowerCase().equals(string)).findAny().orElse(null),
                                    newValue -> newValue.name().toLowerCase()
                            )
                    ),
                    getValue(), value -> ConfigOptionStorage.setEnum(key, value)
            );
        }
        return new SimpleOption<>(translationKey, SimpleOption.emptyTooltip(),
                (text, value) -> getValueText(this, value),
                new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(enumClass.getEnumConstants()),
                        Codec.STRING.xmap(
                                string -> Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.name().toLowerCase().equals(string)).findAny().orElse(null),
                                newValue -> newValue.name().toLowerCase()
                        )
                ),
                getValue(), value -> ConfigOptionStorage.setEnum(key, value)
        );
    }

    @SuppressWarnings("unchecked")
    public void setFromJson(String name, JsonObject json, Type type) {
        JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(name.toLowerCase(Locale.ROOT));
        if (jsonPrimitive != null && jsonPrimitive.isString()) {
            if (type instanceof Class<?>) {
                Enum<?> found = null;
                for (Enum<?> value : ((Class<Enum<?>>) type).getEnumConstants()) {
                    if (value.name().toLowerCase(Locale.ROOT).equals(jsonPrimitive.getAsString())) {
                        found = value;
                        break;
                    }
                }
                if (found != null) {
                    ConfigOptionStorage.setEnumTypeless(key, found);
                }
            }
        }
    }
}

