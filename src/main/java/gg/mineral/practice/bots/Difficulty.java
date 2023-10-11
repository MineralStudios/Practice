package gg.mineral.practice.bots;

import org.bukkit.Location;

import gg.mineral.botapi.entity.FakePlayer;
import gg.mineral.botapi.entity.config.BotConfiguration;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;

public enum Difficulty {
    EASY(CC.GREEN + "Easy") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().location(location).name("EasyBot" + suffix)
                    .aimSpeed(0.15F).aimAccuracy(1.0F).reactionTime(2).cps(5).bowAimingRadius(2.4F).latency(50)
                    .sprintResetAccuracy(0.25F).hitSelectAccuracy(0.0F).distancingMin(1.8F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = new FakePlayer(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    MEDIUM(CC.YELLOW + "Medium") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().location(location).name("MediumBot" + suffix)
                    .aimSpeed(0.3F).aimAccuracy(1.5F).reactionTime(2).cps(8).bowAimingRadius(1.2f).latency(50)
                    .sprintResetAccuracy(0.45F).hitSelectAccuracy(0.3f).distancingMin(2.1F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = new FakePlayer(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    HARD(CC.GOLD + "Hard") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().location(location).name("HardBot" + suffix)
                    .aimSpeed(0.5F).aimAccuracy(2.0F).reactionTime(1).cps(11).bowAimingRadius(0.6f).latency(50)
                    .sprintResetAccuracy(0.8F).hitSelectAccuracy(0.6f).distancingMin(2.4F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = new FakePlayer(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    EXPERT(CC.RED + "Expert") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().location(location).name("ExpertBot" + suffix)
                    .aimSpeed(1F).aimAccuracy(8F).reactionTime(0).cps(14).bowAimingRadius(0.3f).latency(50)
                    .sprintResetAccuracy(0.85F).hitSelectAccuracy(0.9f).distancingMin(2.6F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = new FakePlayer(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    PRO(CC.PURPLE + "Pro") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {

            BotConfiguration config = BotConfiguration.builder().location(location).name("ProBot" + suffix)
                    .aimSpeed(2F).aimAccuracy(32F).reactionTime(0).cps(17).bowAimingRadius(0.2f).latency(50)
                    .sprintResetAccuracy(0.95F).hitSelectAccuracy(0.98f).distancingMin(2.7F).distancingMax(3.0F)
                    .build();
            FakePlayer fakePlayer = new FakePlayer(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    HARDCORE(CC.PINK + "Hardcore") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().location(location).name("HardcoreBot" + suffix)
                    .aimSpeed(4F).aimAccuracy(64F).reactionTime(0).cps(20).bowAimingRadius(0.1f).latency(50)
                    .sprintResetAccuracy(1.0F).hitSelectAccuracy(1.0F).distancingMin(2.8F).distancingMax(3.0F)
                    .build();
            FakePlayer fakePlayer = new FakePlayer(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    CUSTOM(CC.GRAY + "Custom") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            CustomDifficulty difficulty = profile.getMatchData().getCustomBotDifficulty();
            BotConfiguration config = BotConfiguration.builder().location(location).name("CustomBot" + suffix)
                    .aimSpeed(difficulty.getAimSpeed()).aimAccuracy(difficulty.getAimAccuracy())
                    .reactionTime((int) difficulty.getReactionTimeTicks()).cps((int) difficulty.getCps())
                    .bowAimingRadius(difficulty.getBowAimingRadius()).latency((int) difficulty.getLatency())
                    .sprintResetAccuracy(difficulty.getSprintResetAccuracy())
                    .hitSelectAccuracy(difficulty.getHitSelectAccuracy()).distancingMin(difficulty.getDistancingMin())
                    .distancingMax(difficulty.getDistancingMax())
                    .build();
            FakePlayer fakePlayer = new FakePlayer(config);
            fakePlayer.spawn();

            return fakePlayer;
        }
    },
    RANDOM(CC.AQUA + "Random") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            CustomDifficulty difficulty = new CustomDifficulty();
            difficulty.randomize();
            BotConfiguration config = BotConfiguration.builder().location(location).name("RandomBot" + suffix)
                    .aimSpeed(difficulty.getAimSpeed()).aimAccuracy(difficulty.getAimAccuracy())
                    .reactionTime((int) difficulty.getReactionTimeTicks()).cps((int) difficulty.getCps())
                    .bowAimingRadius(difficulty.getBowAimingRadius()).latency((int) difficulty.getLatency())
                    .sprintResetAccuracy(difficulty.getSprintResetAccuracy())
                    .hitSelectAccuracy(difficulty.getHitSelectAccuracy()).distancingMin(difficulty.getDistancingMin())
                    .distancingMax(difficulty.getDistancingMax())
                    .build();
            FakePlayer fakePlayer = new FakePlayer(config);
            fakePlayer.spawn();
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
