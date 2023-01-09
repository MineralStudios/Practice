package gg.mineral.practice.util;

import gg.mineral.practice.PracticePlugin;

public class CoreConnector {
    public static CoreLoader INSTANCE;

    public static boolean connected() {
        return INSTANCE != null;
    }

    static {
        if (Package.getPackage("de.jeezycore") != null) {
            INSTANCE = new CoreLoader();

        } else {

            INSTANCE = null;
            PracticePlugin.INSTANCE.getLogger().warning(
                    "The core plugin has failed to link with the practice plugin. Please ensure the core plugin is installed.");
        }
    }

}
