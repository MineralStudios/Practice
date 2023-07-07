package gg.mineral.practice.inventory.menus;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.match.BotMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.fakeplayer.FakePlayer;

public class SelectBotDifficultyMenu extends PracticeMenu {
    QueueEntry queueEntry;
    final static String TITLE = CC.BLUE + "Select Difficulty";

    public SelectBotDifficultyMenu(QueueEntry queueEntry) {
        super(TITLE);
        setClickCancelled(true);
        this.queueEntry = queueEntry;
    }

    @Override
    public boolean update() {

        setSlot(0, ItemStacks.EASY, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn((CraftWorld) p.getPlayer().getWorld(), "EasyBot");
            fakePlayer.startAiming(0.15F, 1.5F);
            fakePlayer.startAttacking(6);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(0.5F);
            fakePlayer.setRange(1.7F, 2.8F);

            BotMatch m = new BotMatch(p, fakePlayer, new MatchData(queueEntry));

            m.start();
        });

        setSlot(1, ItemStacks.MEDIUM, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn((CraftWorld) p.getPlayer().getWorld(), "MediumBot");
            fakePlayer.startAiming(0.3F, 3F);
            fakePlayer.startAttacking(10);
            fakePlayer.setLatency(50);
            fakePlayer.setRange(2.3F, 2.9F);
            fakePlayer.setSprintResetAccuracy(0.75F);

            BotMatch m = new BotMatch(p, fakePlayer, new MatchData(queueEntry));

            m.start();
        });

        setSlot(2, ItemStacks.HARD, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn((CraftWorld) p.getPlayer().getWorld(), "HardBot");
            fakePlayer.startAiming(0.5F, 5F);
            fakePlayer.startAttacking(13);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(0.95F);
            fakePlayer.setRange(2.6F, 3.0F);

            BotMatch m = new BotMatch(p, fakePlayer, new MatchData(queueEntry));

            m.start();
        });

        setSlot(3, ItemStacks.EXPERT, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn((CraftWorld) p.getPlayer().getWorld(), "ExpertBot");
            fakePlayer.startAiming(1F, 10F);
            fakePlayer.startAttacking(16);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(1.0F);
            fakePlayer.setRange(2.9F, 3.0F);

            BotMatch m = new BotMatch(p, fakePlayer, new MatchData(queueEntry));

            m.start();
        });

        setSlot(4, ItemStacks.HACKER, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn((CraftWorld) p.getPlayer().getWorld(), "HackerBot");
            fakePlayer.startAiming(1F, 10F);
            fakePlayer.startAttacking(20);
            fakePlayer.setReach(3.5f);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(1.0F);
            fakePlayer.setRange(3.3F, 3.5F);

            BotMatch m = new BotMatch(p, fakePlayer, new MatchData(queueEntry));

            m.start();
        });

        return true;
    }
}
