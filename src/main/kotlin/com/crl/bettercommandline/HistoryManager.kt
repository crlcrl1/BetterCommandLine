package com.crl.bettercommandline

import com.crl.bettercommandline.config.ModConfig
import net.fabricmc.loader.api.FabricLoader
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Manages the command history.
 */
class HistoryManager {
    companion object {
        val instance = HistoryManager()
    }

    private var file: File? = null
    private val maxHistorySize = ModConfig.HISTORY_SIZE.value.num
    val history: ArrayList<String> = ArrayList()

    private fun prepareHistoryFile() {
        if (file != null) {
            return
        }
        file = File(FabricLoader.getInstance().gameDir.toFile(), ".command_history")
    }

    private fun save() {
        prepareHistoryFile()
        try {
            file?.let {
                FileWriter(it).use { writer ->
                    for (s in history) {
                        writer.write(s + "\n")
                    }
                }
            }
        } catch (e: IOException) {
            println("Failed to save command history")
        }
    }

    fun load() {
        prepareHistoryFile()
        try {
            if (!file!!.exists()) {
                save()
            }
            if (file!!.exists()) {
                history.clear()
                history.addAll(FileUtils.readLines(file, "UTF-8"))
            }
            if (history.size > maxHistorySize) {
                history.subList(0, history.size - maxHistorySize).clear()
            }
        } catch (e: IOException) {
            println("Fail to load command history")
        }
    }

    fun addCommand(command: String) {
        if (command.isBlank()) {
            return
        }
        history.removeIf { s: String -> s == command }
        history.add(command)
        if (history.size > maxHistorySize) {
            history.removeAt(0)
        }
        save()
    }
}