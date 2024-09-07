package gg.mineral.practice.util;

import de.jeezycore.db.SettingsSQL;
import de.jeezycore.utils.NameTag;
import lombok.Getter;

public class CoreLoader {
    @Getter
    private final NameTag nameTagAPI = new NameTag();
    @Getter
    private final SettingsSQL settingsSQL = new SettingsSQL();
    /*
     * @Getter
     * final MineralsSQL mineralsSQL = new MineralsSQL();
     * 
     * @Getter
     * final UUIDChecker uuidChecker = new UUIDChecker();
     */
}
