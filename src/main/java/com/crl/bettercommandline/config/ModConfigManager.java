package com.crl.bettercommandline.config;

import com.crl.bettercommandline.BetterCommandLine;
import com.crl.bettercommandline.config.options.BooleanConfigOption;
import com.crl.bettercommandline.config.options.EnumConfigOption;
import com.google.gson.*;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.config.option.ConfigOptionStorage;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;

public class ModConfigManager {
    private static File file;

    private static void prepareConfigFile() {
        if (file != null) {
            return;
        }
        file = new File(FabricLoader.getInstance().getConfigDir().toFile(), BetterCommandLine.MOD_ID + ".json");
    }

    public static void initializeConfig() {
        load();
    }

    private static void load() {
        prepareConfigFile();
        if (!file.exists()) {
            save();
        }

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                JsonObject json = JsonParser.parseReader(br).getAsJsonObject();

                for (Field field : ModConfig.class.getDeclaredFields()) {
                    int modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                        if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
                            String name = field.getName();
                            BooleanConfigOption option = (BooleanConfigOption) field.get(null);
                            option.setFromJson(name, json);
                        } else if (EnumConfigOption.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
                            String name = field.getName();
                            Type generic = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                            if (generic instanceof Class<?>) {
                                EnumConfigOption<?> option = (EnumConfigOption<?>) field.get(null);
                                option.setFromJson(name, json, generic);
                            }
                        }
                    }
                }
            } catch (IOException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @SuppressWarnings("unchecked")
    public static void save() {
        ModMenu.clearModCountCache();
        prepareConfigFile();

        JsonObject config = new JsonObject();

        try {
            for (Field field : ModConfig.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                    if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
                        BooleanConfigOption option = (BooleanConfigOption) field.get(null);
                        String name = field.getName().toLowerCase(Locale.ROOT);
                        boolean value = ConfigOptionStorage.getBoolean(option.getKey());
                        config.addProperty(name, value);
                    } else if (EnumConfigOption.class.isAssignableFrom(field.getType()) &&
                            field.getGenericType() instanceof ParameterizedType
                    ) {
                        Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        if (type instanceof Class<?>) {
                            EnumConfigOption<?> option = (EnumConfigOption<?>) field.get(null);
                            String name = field.getName().toLowerCase(Locale.ROOT);
                            Enum<?> enumType = ConfigOptionStorage.getEnumTypeless(option.getKey(),
                                    (Class<Enum<?>>) type);
                            String value = enumType.name().toLowerCase(Locale.ROOT);
                            config.addProperty(name, value);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.err.println("Couldn't save Mod Menu configuration file");
        }

        String jsonString = ModMenu.GSON.toJson(config);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            System.err.println("Couldn't save Mod Menu configuration file");
        }
    }
}
