package gg.mineral.practice.util;

import de.jeezycore.db.MineralsSQL;
import de.jeezycore.utils.NameTag;
import de.jeezycore.utils.UUIDChecker;
import lombok.Getter;

public class CoreLoader {
    @Getter
    final NameTag nameTagAPI = new NameTag();
    @Getter
    final MineralsSQL mineralsSQL = new MineralsSQL();
    @Getter
    final UUIDChecker uuidChecker = new UUIDChecker();
}
