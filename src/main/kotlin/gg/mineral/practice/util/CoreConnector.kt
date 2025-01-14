package gg.mineral.practice.util

import gg.mineral.practice.PracticePlugin
import org.bukkit.Bukkit

object CoreConnector {
    var INSTANCE: CoreLoader? = null

    fun connected(): Boolean {
        return INSTANCE != null
    }

    init {
        if (Bukkit.getPluginManager().isPluginEnabled("JeezyCore")) {
            INSTANCE = CoreLoader()
        } else {
            INSTANCE = null
            PracticePlugin.INSTANCE.logger.warning(
                "The core plugin has failed to link with the practice plugin. Please ensure the core plugin is installed."
            )
        }
    }
}
