package gg.mineral.practice.bots;

import org.bukkit.Location;

import gg.mineral.botapi.entity.FakePlayer;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;

public enum Difficulty {
    EASY(CC.GREEN + "Easy") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn(location, "EasyBot" + suffix);
            fakePlayer.startAiming(0.15F, 1.0F, 2);
            fakePlayer.startAttacking(5);
            fakePlayer.setArrowBlockAccuracy(2.4f);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(0.25F);
            fakePlayer.setHitSelectAccuracy(0.0f);
            fakePlayer.setRange(1.8F, 3.0F);
            return fakePlayer;
        }
    },
    MEDIUM(CC.YELLOW + "Medium") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn(location, "MediumBot" + suffix);
            fakePlayer.startAiming(0.3F, 1.5F, 2);
            fakePlayer.startAttacking(8);
            fakePlayer.setArrowBlockAccuracy(1.2f);
            fakePlayer.setLatency(50);
            fakePlayer.setRange(2.1F, 3.0F);
            fakePlayer.setSprintResetAccuracy(0.45F);
            fakePlayer.setHitSelectAccuracy(0.3f);
            return fakePlayer;
        }
    },
    HARD(CC.GOLD + "Hard") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn(location, "HardBot" + suffix);
            fakePlayer.startAiming(0.5F, 2.0F, 1);
            fakePlayer.startAttacking(11);
            fakePlayer.setArrowBlockAccuracy(0.6f);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(0.8F);
            fakePlayer.setHitSelectAccuracy(0.6f);
            fakePlayer.setRange(2.4F, 3.0F);
            return fakePlayer;
        }
    },
    EXPERT(CC.RED + "Expert") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn(location, "ExpertBot" + suffix);
            fakePlayer.startAiming(1F, 8F, 0);
            fakePlayer.startAttacking(14);
            fakePlayer.setArrowBlockAccuracy(0.3f);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(0.85F);
            fakePlayer.setHitSelectAccuracy(0.9f);
            fakePlayer.setRange(2.6F, 3.0F);
            return fakePlayer;
        }
    },
    PRO(CC.PURPLE + "Pro") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn(location, "ProBot" + suffix);
            fakePlayer.startAiming(2F, 32F, 0);
            fakePlayer.startAttacking(17);
            fakePlayer.setArrowBlockAccuracy(0.2f);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(0.95F);
            fakePlayer.setHitSelectAccuracy(0.98f);
            fakePlayer.setRange(2.7F, 3.0F);
            return fakePlayer;
        }
    },
    HARDCORE(CC.PINK + "Hardcore") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            FakePlayer fakePlayer = new FakePlayer();
            fakePlayer.spawn(location, "HardcoreBot" + suffix);
            fakePlayer.startAiming(4F, 64F, 0);
            fakePlayer.startAttacking(20);
            fakePlayer.setArrowBlockAccuracy(0.1f);
            fakePlayer.setLatency(50);
            fakePlayer.setSprintResetAccuracy(1.0F);
            fakePlayer.setHitSelectAccuracy(1.0f);
            fakePlayer.setRange(2.8F, 3.0F);
            return fakePlayer;
        }
    },
    CUSTOM(CC.GRAY + "Custom") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            FakePlayer fakePlayer = new FakePlayer();
            CustomDifficulty difficulty = profile.getMatchData().getCustomBotDifficulty();
            fakePlayer.spawn(location, "CustomBot" + suffix);
            fakePlayer.startAiming(difficulty.getAimSpeed(), difficulty.getAimAccuracy(),
                    (int) difficulty.getReactionTimeTicks());
            fakePlayer.startAttacking((int) difficulty.getCps());
            fakePlayer.setReach(difficulty.getReach());
            fakePlayer.setArrowBlockAccuracy(difficulty.getBowAimingRadius());
            fakePlayer.setLatency((int) difficulty.getLatency());
            fakePlayer.setSprintResetAccuracy(difficulty.getSprintResetAccuracy());
            fakePlayer.setHitSelectAccuracy(difficulty.getHitSelectAccuracy());
            fakePlayer.setRange(difficulty.getDistancingMin(), difficulty.getDistancingMax());
            return fakePlayer;
        }
    },
    RANDOM(CC.AQUA + "Random") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            FakePlayer fakePlayer = new FakePlayer();
            CustomDifficulty difficulty = new CustomDifficulty();
            difficulty.randomize();
            fakePlayer.spawn(location, "RandomBot" + suffix);
            fakePlayer.startAiming(difficulty.getAimSpeed(), difficulty.getAimAccuracy(),
                    (int) difficulty.getReactionTimeTicks());
            fakePlayer.startAttacking((int) difficulty.getCps());
            fakePlayer.setReach(difficulty.getReach());
            fakePlayer.setArrowBlockAccuracy(difficulty.getBowAimingRadius());
            fakePlayer.setLatency((int) difficulty.getLatency());
            fakePlayer.setSprintResetAccuracy(difficulty.getSprintResetAccuracy());
            fakePlayer.setHitSelectAccuracy(difficulty.getHitSelectAccuracy());
            fakePlayer.setRange(difficulty.getDistancingMin(), difficulty.getDistancingMax());
            return fakePlayer;
        }
    };

    @Getter
    final String display;

    Difficulty(String name) {
        this.display = name;
    }

    public abstract FakePlayer spawn(Profile profile, Location location, String suffix);
}
