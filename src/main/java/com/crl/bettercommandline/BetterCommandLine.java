package com.crl.bettercommandline;

import com.crl.bettercommandline.config.ModConfigManager;
import net.fabricmc.api.ModInitializer;

public class BetterCommandLine implements ModInitializer {
    public static final String MOD_ID = "bettercommandline";

    @Override
    public void onInitialize() {
        ModConfigManager.initializeConfig();
        HistoryManager.load();
    }
}
