package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import gg.mineral.practice.bots.CustomDifficulty;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class CustomBotDifficultyMenu extends PracticeMenu {
    SelectGametypeMenu menu;
    final static String TITLE = CC.BLUE + "Create Custom Difficulty";
    final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
    }

    public CustomBotDifficultyMenu(SelectGametypeMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {

        CustomDifficulty difficulty = viewer.getMatchData().getCustomBotDifficulty() == null ? new CustomDifficulty()
                : viewer.getMatchData().getCustomBotDifficulty();
        viewer.getMatchData().setCustomBotDifficulty(difficulty);
        viewer.getMatchData()
                .setBotDifficulty(Difficulty.CUSTOM);

        String aimSpeedStr = "Aim Speed: " + DECIMAL_FORMAT.format(difficulty.getAimSpeed());
        String aimAccuracyStr = "Aim Accuracy: " + DECIMAL_FORMAT.format(difficulty.getAimAccuracy());
        String bowAimingRadiusStr = "Bow Aiming Radius: "
                + DECIMAL_FORMAT.format(difficulty.getBowAimingRadius());
        String reachStr = "Reach: " + DECIMAL_FORMAT.format(difficulty.getReach());
        String sprintResetAccuracyStr = "Sprint Reset Accuracy: "
                + DECIMAL_FORMAT.format(difficulty.getSprintResetAccuracy());
        String hitSelectAccuracyStr = "Hit Select Accuracy: "
                + DECIMAL_FORMAT.format(difficulty.getHitSelectAccuracy());
        String distancingMinimumStr = "Distancing Minimum: " + DECIMAL_FORMAT.format(difficulty.getDistancingMin());
        String distancingMaximumStr = "Distancing Maximum: " + DECIMAL_FORMAT.format(difficulty.getDistancingMax());
        String cpsStr = "CPS: " + DECIMAL_FORMAT.format(difficulty.getCps());
        String pingStr = "Ping: " + DECIMAL_FORMAT.format(difficulty.getLatency());

        setSlot(0, ItemStacks.AIM_SPEED.name(aimSpeedStr).build(), interaction -> {
            interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                difficulty.setAimSpeed(value);
            }));
        });

        setSlot(1, ItemStacks.AIM_ACCURACY.name(aimAccuracyStr).build(), interaction -> {
            interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                difficulty.setAimAccuracy(value);
            }));
        });

        setSlot(2, ItemStacks.BOW_AIMING_RADIUS.name(bowAimingRadiusStr).build(), interaction -> {
            interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                difficulty.setBowAimingRadius(value);
            }));
        });

        setSlot(3, ItemStacks.REACH.name(reachStr).build(), interaction -> {
            interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                difficulty.setReach(value);
            }));
        });

        setSlot(4, ItemStacks.SPRINT_RESET_ACCURACY.name(sprintResetAccuracyStr).build(), interaction -> {
            interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                difficulty.setSprintResetAccuracy(value);
            }));
        });

        setSlot(5, ItemStacks.HIT_SELECT_ACCURACY.name(hitSelectAccuracyStr).build(), interaction -> {
            interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                difficulty.setHitSelectAccuracy(value);
            }));
        });

        setSlot(6, ItemStacks.DISTANCING_MINIMUM.name(distancingMinimumStr).build(), interaction -> {
            interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                difficulty.setDistancingMin(value);
            }));
        });

        setSlot(7, ItemStacks.DISTANCING_MAXIMUM.name(distancingMaximumStr).build(), interaction -> {
            interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                difficulty.setDistancingMax(value);
            }));
        });

        setSlot(8, ItemStacks.CPS.name(cpsStr)
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setCps(value);
                    }));
                });

        setSlot(9, ItemStacks.PING.name(pingStr)
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setLatency(value);
                    }));
                });

        setSlot(30, ItemStacks.CLICK_TO_APPLY_CHANGES.name("Save Difficulty").build(), interaction -> {
            Profile p = interaction.getProfile();
            p.getMatchData().setCustomBotDifficulty(difficulty);
            p.openMenu(menu);
        });

        setSlot(32, ItemStacks.RANDOM_DIFFICULTY, interaction -> {
            Profile p = interaction.getProfile();
            difficulty.randomize();
            p.getMatchData().setCustomBotDifficulty(difficulty);
            reload();
        });

        return true;
    }

}
