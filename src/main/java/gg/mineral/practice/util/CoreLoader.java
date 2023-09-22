package gg.mineral.practice.util;

import de.jeezycore.db.FriendsSQL;
import de.jeezycore.db.SettingsSQL;
import de.jeezycore.utils.NameTag;
import lombok.Getter;

public class CoreLoader {
    @Getter
    final NameTag nameTagAPI = new NameTag();
    @Getter
    SettingsSQL settingsSQL = new SettingsSQL();
    @Getter
    FriendsSQL friendsSQL = new FriendsSQL();
    /*
     * @Getter
     * final MineralsSQL mineralsSQL = new MineralsSQL();
     * 
     * @Getter
     * final UUIDChecker uuidChecker = new UUIDChecker();
     */
}
