package gg.mineral.practice.inventory.menus;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.fakeplayer.FakePlayer;

public class SelectOpponentTypeMenu extends PracticeMenu {
    QueueEntry queueEntry;
    SelectGametypeMenu menu;
    final static String TITLE = CC.BLUE + "Select Opponent Type";

    public SelectOpponentTypeMenu(QueueEntry queueEntry, SelectGametypeMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.queueEntry = queueEntry;
        this.menu = menu;

    }

    @Override
    public boolean update() {

        setSlot(2, ItemStacks.BOT_MODE, interaction -> {

            Profile p = interaction.getProfile();
            FakePlayer fakePlayer = new FakePlayer((CraftWorld) p.getPlayer().getWorld(), "Bot");
            fakePlayer.spawn();
            fakePlayer.startAiming(20F);
            fakePlayer.startAttacking(20);
            fakePlayer.startMoving(FakePlayer.Direction.FORWARDS);
            fakePlayer.startStrafing();
            fakePlayer.setSprintResetAccuracy(1.0F);
            fakePlayer.startSprinting();
            Profile fakeProfile = ProfileManager.getOrCreateProfile(fakePlayer.getBukkitEntity());

            Match m = new Match(p, fakeProfile, new MatchData(queueEntry)) {
                @Override
                public void end(Profile victim) {
                    super.end(victim);

                    if (victim.getPlayer().getHandle() instanceof FakePlayer) {
                        FakePlayer fakePlayer = (FakePlayer) victim.getPlayer().getHandle();
                        fakePlayer.destroy();
                    }
                }

                @Override
                public void end(Profile attacker, Profile victim) {
                    super.end(attacker, victim);

                    if (attacker.getPlayer().getHandle() instanceof FakePlayer) {
                        FakePlayer fakePlayer = (FakePlayer) attacker.getPlayer().getHandle();
                        fakePlayer.destroy();
                    }

                    if (victim.getPlayer().getHandle() instanceof FakePlayer) {
                        FakePlayer fakePlayer = (FakePlayer) victim.getPlayer().getHandle();
                        fakePlayer.destroy();
                    }
                }
            };
            m.start();
        });

        setSlot(6, ItemStacks.PLAYER_MODE, interaction -> {
            Profile p = interaction.getProfile();
            p.addPlayerToQueue(queueEntry);
            viewer.openMenu(menu);
        });

        return true;
    }
}
