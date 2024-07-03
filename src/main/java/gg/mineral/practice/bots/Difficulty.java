package gg.mineral.practice.bots;

import org.bukkit.Location;

import gg.mineral.botapi.entity.config.AimConfiguration;
import gg.mineral.botapi.entity.config.BotConfiguration;
import gg.mineral.botapi.entity.player.self.FakePlayer;
import gg.mineral.botapi.manager.FakePlayerManager;
import gg.mineral.botapi.math.position.ServerLocation;
import gg.mineral.botapi.world.ServerWorld;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Difficulty {

    EASY(CC.GREEN + "Easy") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            ServerLocation serverLocation = new ServerLocation(new ServerWorld() {
                public Object getHandle() {
                    return location.getWorld();
                }

                @Override
                public String getName() {
                    return location.getWorld().getName();
                }
            }, location.getX(), location.getY(),
                    location.getZ());

            BotConfiguration config = BotConfiguration.builder().location(serverLocation).name("EasyBot" + suffix)
                    .aimConfiguration(AimConfiguration.builder().horizontalAimSpeed(0.35F).verticalAimSpeed(0.35F)
                            .horizontalAimAccuracy(0.3F).verticalAimAccuracy(0.3F).build())
                    .cps(5).bowAimingRadius(2.4F).latency(50).sprintResetAccuracy(0.25F).hitSelectAccuracy(0.0F)
                    .distancingMin(1.8F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = FakePlayerManager.create(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    MEDIUM(CC.YELLOW + "Medium") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            ServerLocation serverLocation = new ServerLocation(new ServerWorld() {
                public Object getHandle() {
                    return location.getWorld();
                }

                @Override
                public String getName() {
                    return location.getWorld().getName();
                }
            }, location.getX(), location.getY(),
                    location.getZ());
            BotConfiguration config = BotConfiguration.builder().location(serverLocation).name("MediumBot" + suffix)
                    .aimConfiguration(AimConfiguration.builder().horizontalAimSpeed(0.5F).verticalAimSpeed(0.5F)
                            .horizontalAimAccuracy(0.4F).verticalAimAccuracy(0.4F).build())
                    .cps(8).bowAimingRadius(1.2f).latency(50).sprintResetAccuracy(0.45F).hitSelectAccuracy(0.3f)
                    .distancingMin(2.1F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = FakePlayerManager.create(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    HARD(CC.GOLD + "Hard") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            ServerLocation serverLocation = new ServerLocation(new ServerWorld() {
                public Object getHandle() {
                    return location.getWorld();
                }

                @Override
                public String getName() {
                    return location.getWorld().getName();
                }
            }, location.getX(), location.getY(),
                    location.getZ());
            BotConfiguration config = BotConfiguration.builder().location(serverLocation).name("HardBot" + suffix)
                    .aimConfiguration(AimConfiguration.builder().horizontalAimSpeed(0.65F).verticalAimSpeed(0.65F)
                            .horizontalAimAccuracy(0.6F).verticalAimAccuracy(0.6F).build())
                    .cps(11).bowAimingRadius(0.6f).latency(50).sprintResetAccuracy(0.8F).hitSelectAccuracy(0.6f)
                    .distancingMin(2.4F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = FakePlayerManager.create(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    EXPERT(CC.RED + "Expert") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            ServerLocation serverLocation = new ServerLocation(new ServerWorld() {
                public Object getHandle() {
                    return location.getWorld();
                }

                @Override
                public String getName() {
                    return location.getWorld().getName();
                }
            }, location.getX(), location.getY(),
                    location.getZ());
            BotConfiguration config = BotConfiguration.builder().location(serverLocation).name("ExpertBot" + suffix)
                    .aimConfiguration(AimConfiguration.builder().horizontalAimSpeed(0.8F).verticalAimSpeed(0.8F)
                            .horizontalAimAccuracy(0.8F).verticalAimAccuracy(0.8F).build())
                    .cps(14).bowAimingRadius(0.3f).latency(50).sprintResetAccuracy(0.85F).hitSelectAccuracy(0.9f)
                    .distancingMin(2.6F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = FakePlayerManager.create(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    PRO(CC.PURPLE + "Pro") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            ServerLocation serverLocation = new ServerLocation(new ServerWorld() {
                public Object getHandle() {
                    return location.getWorld();
                }

                @Override
                public String getName() {
                    return location.getWorld().getName();
                }
            }, location.getX(), location.getY(),
                    location.getZ());

            BotConfiguration config = BotConfiguration.builder().location(serverLocation).name("ProBot" + suffix)
                    .aimConfiguration(AimConfiguration.builder().horizontalAimSpeed(0.95F).verticalAimSpeed(0.95F)
                            .horizontalAimAccuracy(0.95F).verticalAimAccuracy(0.95F).build())
                    .cps(17).bowAimingRadius(0.2f).latency(50).sprintResetAccuracy(0.95F).hitSelectAccuracy(0.98f)
                    .distancingMin(2.7F).distancingMax(3.0F).build();
            FakePlayer fakePlayer = FakePlayerManager.create(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    HARDCORE(CC.PINK + "Hardcore") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {

            ServerLocation serverLocation = new ServerLocation(new ServerWorld() {
                public Object getHandle() {
                    return location.getWorld();
                }

                @Override
                public String getName() {
                    return location.getWorld().getName();
                }
            }, location.getX(), location.getY(),
                    location.getZ());

            BotConfiguration config = BotConfiguration.builder().location(serverLocation).name("HardcoreBot" + suffix)
                    .aimConfiguration(AimConfiguration.builder().horizontalAimSpeed(1.1F).verticalAimSpeed(1.1F)
                            .horizontalAimAccuracy(1.1F)
                            .verticalAimAccuracy(1.1F).build())
                    .cps(20).bowAimingRadius(0.1f).latency(50)
                    .sprintResetAccuracy(1.0F).hitSelectAccuracy(1.0F).distancingMin(2.8F).distancingMax(3.0F)
                    .build();
            FakePlayer fakePlayer = FakePlayerManager.create(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    CUSTOM(CC.GRAY + "Custom") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            CustomDifficulty difficulty = profile.getMatchData().getCustomBotDifficulty();
            ServerLocation serverLocation = new ServerLocation(new ServerWorld() {
                public Object getHandle() {
                    return location.getWorld();
                }

                @Override
                public String getName() {
                    return location.getWorld().getName();
                }
            }, location.getX(), location.getY(),
                    location.getZ());
            BotConfiguration config = BotConfiguration.builder().location(serverLocation).name("CustomBot" + suffix)
                    .aimConfiguration(AimConfiguration.builder().horizontalAimSpeed(difficulty.getHorizontalAimSpeed())
                            .verticalAimSpeed(difficulty.getVerticalAimSpeed())
                            .horizontalAimAccuracy(difficulty.getHorizontalAimAccuracy())
                            .verticalAimAccuracy(difficulty.getVerticalAimAccuracy()).build())
                    .cps((int) difficulty.getCps()).bowAimingRadius(difficulty.getBowAimingRadius())
                    .latency((int) difficulty.getLatency()).sprintResetAccuracy(difficulty.getSprintResetAccuracy())
                    .hitSelectAccuracy(difficulty.getHitSelectAccuracy()).distancingMin(difficulty.getDistancingMin())
                    .distancingMax(difficulty.getDistancingMax()).build();
            FakePlayer fakePlayer = FakePlayerManager.create(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    },
    RANDOM(CC.AQUA + "Random") {
        @Override
        public FakePlayer spawn(Profile profile, Location location, String suffix) {
            CustomDifficulty difficulty = new CustomDifficulty();
            difficulty.randomize();
            ServerLocation serverLocation = new ServerLocation(new ServerWorld() {
                public Object getHandle() {
                    return location.getWorld();
                }

                @Override
                public String getName() {
                    return location.getWorld().getName();
                }
            }, location.getX(), location.getY(),
                    location.getZ());
            BotConfiguration config = BotConfiguration.builder().location(serverLocation).name("RandomBot" + suffix)
                    .aimConfiguration(AimConfiguration.builder().horizontalAimSpeed(difficulty.getHorizontalAimSpeed())
                            .verticalAimSpeed(difficulty.getVerticalAimSpeed())
                            .horizontalAimAccuracy(difficulty.getHorizontalAimAccuracy())
                            .verticalAimAccuracy(difficulty.getVerticalAimAccuracy()).build())
                    .cps((int) difficulty.getCps()).bowAimingRadius(difficulty.getBowAimingRadius())
                    .latency((int) difficulty.getLatency()).sprintResetAccuracy(difficulty.getSprintResetAccuracy())
                    .hitSelectAccuracy(difficulty.getHitSelectAccuracy()).distancingMin(difficulty.getDistancingMin())
                    .distancingMax(difficulty.getDistancingMax()).build();
            FakePlayer fakePlayer = FakePlayerManager.create(config);
            fakePlayer.spawn();
            return fakePlayer;
        }
    };

    @Getter
    final String display;

    public abstract FakePlayer spawn(Profile profile, Location location, String suffix);
}
