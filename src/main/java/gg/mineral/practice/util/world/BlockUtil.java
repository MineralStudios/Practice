package gg.mineral.practice.util.world;

import gg.mineral.practice.entity.Profile;

public class BlockUtil {

    public static void sendFakeBlock(Profile profile, BlockData blockData) {
        blockData.update(profile.getPlayer());
        profile.getFakeBlocks().put(blockData);
    }

    public static void clearFakeBlocks(Profile profile) {
        profile.getFakeBlocks().getRegisteredObjects().stream().peek(blockData -> blockData.remove(profile.getPlayer()))
                .forEach(profile.getFakeBlocks()::unregister);
    }
}
