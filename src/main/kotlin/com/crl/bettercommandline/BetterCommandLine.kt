package com.crl.bettercommandline

import com.crl.bettercommandline.config.ModConfigManager
import net.fabricmc.api.ModInitializer

class BetterCommandLine : ModInitializer {
    companion object {
        const val MOD_ID = "bettercommandline"
    }

    override fun onInitialize() {
        ModConfigManager.initializeConfig()
        HistoryManager.load()
    }
}