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
                    .horizontalAimSpeed(0.35F).verticalAimSpeed(0.35F).horizontalAimAccuracy(0.35F)
                    .verticalAimAccuracy(0.35F).reactionTime(2).cps(5).bowAimingRadius(2.4F).latency(50)
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
                    .horizontalAimSpeed(0.5F).verticalAimSpeed(0.5F).horizontalAimAccuracy(0.5F)
                    .verticalAimAccuracy(0.5F).reactionTime(2).cps(8).bowAimingRadius(1.2f).latency(50)
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
                    .horizontalAimSpeed(0.65F).verticalAimSpeed(0.65F).horizontalAimAccuracy(0.65F)
                    .verticalAimAccuracy(0.65F).reactionTime(1).cps(11).bowAimingRadius(0.6f).latency(50)
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
                    .horizontalAimSpeed(0.8F).verticalAimSpeed(0.8F).horizontalAimAccuracy(0.8F)
                    .verticalAimAccuracy(0.8F).reactionTime(0).cps(14).bowAimingRadius(0.3f).latency(50)
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
                    .horizontalAimSpeed(0.95F).verticalAimSpeed(0.95F).horizontalAimAccuracy(0.95F)
                    .verticalAimAccuracy(0.95F).reactionTime(0).cps(17).bowAimingRadius(0.2f).latency(50)
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
                    .horizontalAimSpeed(1.1F).verticalAimSpeed(1.1F).horizontalAimAccuracy(1.1F)
                    .verticalAimAccuracy(1.1F).reactionTime(0).cps(20).bowAimingRadius(0.1f).latency(50)
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
                    .horizontalAimSpeed(difficulty.getHorizontalAimSpeed())
                    .verticalAimSpeed(difficulty.getVerticalAimSpeed())
                    .horizontalAimAccuracy(difficulty.getHorizontalAimAccuracy())
                    .verticalAimAccuracy(difficulty.getVerticalAimAccuracy())
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
                    .horizontalAimSpeed(difficulty.getHorizontalAimSpeed())
                    .verticalAimSpeed(difficulty.getVerticalAimSpeed())
                    .horizontalAimAccuracy(difficulty.getHorizontalAimAccuracy())
                    .verticalAimAccuracy(difficulty.getVerticalAimAccuracy())
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
