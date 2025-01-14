package gg.mineral.practice.util;

import de.jeezycore.db.SettingsSQL;
import lombok.Getter;

@Getter
public class CoreLoader {
    private final SettingsSQL settingsSQL = new SettingsSQL();
    /*
     * final MineralsSQL mineralsSQL = new MineralsSQL();
     * 
     * final UUIDChecker uuidChecker = new UUIDChecker();
     */
}
