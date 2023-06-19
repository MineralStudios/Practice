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

            FakePlayer fakePlayer = new FakePlayer((CraftWorld) p.getPlayer().getWorld(), "Easy Bot");
            fakePlayer.spawn();
            fakePlayer.startAiming(0.5F, 0.5F);
            fakePlayer.startAttacking(6);
            fakePlayer.startMoving(FakePlayer.Direction.FORWARDS);
            fakePlayer.startStrafing();
            fakePlayer.setSprintResetAccuracy(0.8F);
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

        setSlot(1, ItemStacks.MEDIUM, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer((CraftWorld) p.getPlayer().getWorld(), "Medium Bot");
            fakePlayer.spawn();
            fakePlayer.startAiming(0.7F, 0.7F);
            fakePlayer.startAttacking(9);
            fakePlayer.startMoving(FakePlayer.Direction.FORWARDS);
            fakePlayer.startStrafing();
            fakePlayer.setSprintResetAccuracy(0.9F);
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

        setSlot(2, ItemStacks.HARD, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer((CraftWorld) p.getPlayer().getWorld(), "Hard Bot");
            fakePlayer.spawn();
            fakePlayer.startAiming(1F, 1F);
            fakePlayer.startAttacking(12);
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

        setSlot(3, ItemStacks.EXPERT, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer((CraftWorld) p.getPlayer().getWorld(), "Expert Bot");
            fakePlayer.spawn();
            fakePlayer.startAiming(5F, 5F);
            fakePlayer.startAttacking(15);
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

        setSlot(4, ItemStacks.HACKER, interaction -> {
            Profile p = interaction.getProfile();

            FakePlayer fakePlayer = new FakePlayer((CraftWorld) p.getPlayer().getWorld(), "Hacker Bot");
            fakePlayer.spawn();
            fakePlayer.startAiming(10F, 10F);
            fakePlayer.startAttacking(20);
            fakePlayer.setReach(3.5f);
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

        return true;
    }
}
