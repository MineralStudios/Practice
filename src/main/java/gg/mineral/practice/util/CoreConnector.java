package gg.mineral.practice.util;

import de.jeezycore.db.MineralsSQL;
import de.jeezycore.utils.NameTag;
import de.jeezycore.utils.UUIDChecker;
import gg.mineral.practice.PracticePlugin;
import lombok.Getter;

public class CoreConnector {
    public static CoreConnector INSTANCE;

    public static boolean connected() {
        return INSTANCE != null;
    }

    static {
        try {
            INSTANCE = new CoreConnector();
        } catch (Exception e) {
            INSTANCE = null;
            PracticePlugin.INSTANCE.getLogger().warning(
                    "The core plugin has failed to link with the practice plugin. Please ensure the core plugin is installed.");
        }
    }

    @Getter
    final NameTag nameTagAPI = new NameTag();
    @Getter
    final MineralsSQL mineralsSQL = new MineralsSQL();
    @Getter
    final UUIDChecker uuidChecker = new UUIDChecker();

}
