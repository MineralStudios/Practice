package gg.mineral.practice.util.world;

import gg.mineral.practice.entity.Profile;
import lombok.val;

public class BlockUtil {

    public static void sendFakeBlock(Profile profile, BlockData blockData) {
        blockData.update(profile.getPlayer());
        profile.getFakeBlocks().put(blockData);
    }

    public static void sendFakeBlocks(Profile profile, BlockData blockData, int radius) {
        sendFakeBlock(profile, blockData);
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i == 0 && j == 0)
                    continue;

                sendFakeBlock(profile, blockData.clone().translate(i, 0, j));
            }
        }
    }

    public static void clearFakeBlocks(Profile profile) {
        val iter = profile.getFakeBlocks().iterator();

        while (iter.hasNext()) {
            val blockData = iter.next().getValue();
            blockData.remove(profile.getPlayer());
            iter.remove();
        }
    }
}
