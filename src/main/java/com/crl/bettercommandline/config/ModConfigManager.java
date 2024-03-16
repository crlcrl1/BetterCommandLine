package com.crl.bettercommandline.config;

import com.crl.bettercommandline.BetterCommandLine;
import com.crl.bettercommandline.config.options.BooleanConfigOption;
import com.google.gson.*;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.config.option.ConfigOptionStorage;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    @SuppressWarnings("unchecked")
    private static void load() {
        prepareConfigFile();

        try {
            if (!file.exists()) {
                save();
            }
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                JsonObject json = new JsonParser().parse(br).getAsJsonObject();

                for (Field field : ModConfig.class.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
                            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));
                            if (jsonPrimitive != null && jsonPrimitive.isBoolean()) {
                                BooleanConfigOption option = (BooleanConfigOption) field.get(null);
                                ConfigOptionStorage.setBoolean(option.getKey(), jsonPrimitive.getAsBoolean());
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException | IllegalAccessException e) {
            System.err.println("Couldn't load Mod Menu configuration file; reverting to defaults");
            e.printStackTrace();
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
                        config.addProperty(field.getName().toLowerCase(Locale.ROOT), ConfigOptionStorage.getBoolean(option.getKey()));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        String jsonString = ModMenu.GSON.toJson(config);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            System.err.println("Couldn't save Mod Menu configuration file");
            e.printStackTrace();
        }
    }
}
