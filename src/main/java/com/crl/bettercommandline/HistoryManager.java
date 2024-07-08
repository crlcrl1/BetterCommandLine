package com.crl.bettercommandline;

import com.crl.bettercommandline.config.ModConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Manages the command history.
 */
public class HistoryManager {
    private static File file;
    private static final int maxHistorySize;
    private static final ArrayList<String> history = new ArrayList<>();

    static {
        maxHistorySize = ModConfig.HISTORY_SIZE.getValue().getNum();
    }

    private static void prepareHistoryFile() {
        if (file != null) {
            return;
        }
        file = new File(FabricLoader.getInstance().getGameDir().toFile(), ".command_history");
    }

    private static void save() {
        prepareHistoryFile();
        try (FileWriter writer = new FileWriter(file)) {
            for (String s : history) {
                writer.write(s + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to save command history");
        }
    }

    public static void load() {
        prepareHistoryFile();
        try {
            if (!file.exists()) {
                save();
            }
            if (file.exists()) {
                history.clear();
                history.addAll(FileUtils.readLines(file, "UTF-8"));
            }
            if (history.size() > maxHistorySize) {
                history.subList(0, history.size() - maxHistorySize).clear();
            }
        } catch (IOException e) {
            System.out.println("Fail to load command history");
        }
    }

    public static void addCommand(String command) {
        if (command.isBlank()) {
            return;
        }
        history.removeIf(s -> s.equals(command));
        history.add(command);
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
        save();
    }

    public static ArrayList<String> getHistory() {
        return history;
    }
}
