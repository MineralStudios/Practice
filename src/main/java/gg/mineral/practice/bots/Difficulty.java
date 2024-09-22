package gg.mineral.practice.bots;

import org.bukkit.Location;
import org.bukkit.World;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.entity.living.player.skin.Skins;
import gg.mineral.bot.api.math.ServerLocation;
import gg.mineral.bot.api.world.ServerWorld;

import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Difficulty {

    EASY(CC.GREEN + "Easy") {
        @Override
        public FakePlayer spawn(MatchData matchData, Location location, String suffix) {

            BotConfiguration config = BotConfiguration.builder().username("EasyBot" + suffix).skin(Skins.MINERAL_GREEN)
                    .horizontalAimSpeed(0.3F)
                    .verticalAimSpeed(0.3F).horizontalAimAccuracy(0.3F).verticalAimAccuracy(0.25F)
                    .horizontalErraticness(0.3f).averageCps(5).latency(50).sprintResetAccuracy(0.25F)
                    .hitSelectAccuracy(0.0F).build();

            return BotAPI.INSTANCE.spawn(config, new ServerLocation() {
                @Override
                public ServerWorld<?> getWorld() {
                    return new ServerWorld<World>() {
                        @Override
                        public World getHandle() {
                            return location.getWorld();
                        }
                    };
                }

                @Override
                public double getX() {
                    return location.getX();
                }

                @Override
                public double getY() {
                    return location.getY();
                }

                @Override
                public double getZ() {
                    return location.getZ();
                }

                @Override
                public float getYaw() {
                    return location.getYaw();
                }

                @Override
                public float getPitch() {
                    return location.getPitch();
                }
            });
        }
    },
    MEDIUM(CC.YELLOW + "Medium") {
        @Override
        public FakePlayer spawn(MatchData matchData, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().username("MediumBot" + suffix)
                    .skin(Skins.MINERAL_YELLOW).horizontalAimSpeed(0.4F)
                    .verticalAimSpeed(0.4F).horizontalAimAccuracy(0.35F).verticalAimAccuracy(0.35F).averageCps(8)
                    .latency(50).sprintResetAccuracy(0.45F).hitSelectAccuracy(0.3f).build();
            return BotAPI.INSTANCE.spawn(config, new ServerLocation() {
                @Override
                public ServerWorld<?> getWorld() {
                    return new ServerWorld<World>() {
                        @Override
                        public World getHandle() {
                            return location.getWorld();
                        }
                    };
                }

                @Override
                public double getX() {
                    return location.getX();
                }

                @Override
                public double getY() {
                    return location.getY();
                }

                @Override
                public double getZ() {
                    return location.getZ();
                }

                @Override
                public float getYaw() {
                    return location.getYaw();
                }

                @Override
                public float getPitch() {
                    return location.getPitch();
                }
            });
        }
    },
    HARD(CC.GOLD + "Hard") {
        @Override
        public FakePlayer spawn(MatchData matchData, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().username("HardBot" + suffix).skin(Skins.MINERAL_ORANGE)
                    .horizontalAimSpeed(0.5F)
                    .verticalAimSpeed(0.5F).horizontalAimAccuracy(0.6F).verticalAimAccuracy(0.6F).averageCps(11)
                    .latency(50).sprintResetAccuracy(0.8F).hitSelectAccuracy(0.6f).build();
            return BotAPI.INSTANCE.spawn(config, new ServerLocation() {
                @Override
                public ServerWorld<?> getWorld() {
                    return new ServerWorld<World>() {
                        @Override
                        public World getHandle() {
                            return location.getWorld();
                        }
                    };
                }

                @Override
                public double getX() {
                    return location.getX();
                }

                @Override
                public double getY() {
                    return location.getY();
                }

                @Override
                public double getZ() {
                    return location.getZ();
                }

                @Override
                public float getYaw() {
                    return location.getYaw();
                }

                @Override
                public float getPitch() {
                    return location.getPitch();
                }
            });
        }
    },
    EXPERT(CC.RED + "Expert") {
        @Override
        public FakePlayer spawn(MatchData matchData, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().username("ExpertBot" + suffix).skin(Skins.MINERAL_RED)
                    .horizontalAimSpeed(0.8F)
                    .verticalAimSpeed(0.8F).horizontalAimAccuracy(0.8F).verticalAimAccuracy(0.8F).averageCps(14)
                    .latency(50).sprintResetAccuracy(0.85F).hitSelectAccuracy(0.9f).build();
            return BotAPI.INSTANCE.spawn(config, new ServerLocation() {
                @Override
                public ServerWorld<?> getWorld() {
                    return new ServerWorld<World>() {
                        @Override
                        public World getHandle() {
                            return location.getWorld();
                        }
                    };
                }

                @Override
                public double getX() {
                    return location.getX();
                }

                @Override
                public double getY() {
                    return location.getY();
                }

                @Override
                public double getZ() {
                    return location.getZ();
                }

                @Override
                public float getYaw() {
                    return location.getYaw();
                }

                @Override
                public float getPitch() {
                    return location.getPitch();
                }
            });
        }
    },
    PRO(CC.PURPLE + "Pro") {
        @Override
        public FakePlayer spawn(MatchData matchData, Location location, String suffix) {
            BotConfiguration config = BotConfiguration.builder().username("ProBot" + suffix).skin(Skins.MINERAL_PURPLE)
                    .horizontalAimSpeed(0.95F)
                    .verticalAimSpeed(0.95F).horizontalAimAccuracy(0.95F).verticalAimAccuracy(0.95F).averageCps(17)
                    .latency(50).sprintResetAccuracy(0.95F).hitSelectAccuracy(0.98f).build();
            return BotAPI.INSTANCE.spawn(config, new ServerLocation() {
                @Override
                public ServerWorld<?> getWorld() {
                    return new ServerWorld<World>() {
                        @Override
                        public World getHandle() {
                            return location.getWorld();
                        }
                    };
                }

                @Override
                public double getX() {
                    return location.getX();
                }

                @Override
                public double getY() {
                    return location.getY();
                }

                @Override
                public double getZ() {
                    return location.getZ();
                }

                @Override
                public float getYaw() {
                    return location.getYaw();
                }

                @Override
                public float getPitch() {
                    return location.getPitch();
                }
            });
        }
    },
    HARDCORE(CC.PINK + "Hardcore") {
        @Override
        public FakePlayer spawn(MatchData matchData, Location location, String suffix) {

            BotConfiguration config = BotConfiguration.builder().username("HardcoreBot" + suffix)
                    .skin(Skins.MINERAL_PINK)
                    .horizontalAimSpeed(1.1F).verticalAimSpeed(1.1F).horizontalAimAccuracy(1.1F)
                    .verticalAimAccuracy(1.1F).averageCps(20).latency(50).sprintResetAccuracy(1.0F)
                    .hitSelectAccuracy(1.0F).build();
            return BotAPI.INSTANCE.spawn(config, new ServerLocation() {
                @Override
                public ServerWorld<?> getWorld() {
                    return new ServerWorld<World>() {
                        @Override
                        public World getHandle() {
                            return location.getWorld();
                        }
                    };
                }

                @Override
                public double getX() {
                    return location.getX();
                }

                @Override
                public double getY() {
                    return location.getY();
                }

                @Override
                public double getZ() {
                    return location.getZ();
                }

                @Override
                public float getYaw() {
                    return location.getYaw();
                }

                @Override
                public float getPitch() {
                    return location.getPitch();
                }
            });
        }
    },
    CUSTOM(CC.GRAY + "Custom") {
        @Override
        public FakePlayer spawn(MatchData matchData, Location location, String suffix) {
            CustomDifficulty difficulty = matchData.getCustomBotDifficulty();

            BotConfiguration config = BotConfiguration.builder().username("CustomBot" + suffix)
                    .horizontalAimSpeed(difficulty.getHorizontalAimSpeed())
                    .verticalAimSpeed(difficulty.getVerticalAimSpeed())
                    .horizontalAimAccuracy(difficulty.getHorizontalAimAccuracy())
                    .verticalAimAccuracy(difficulty.getVerticalAimAccuracy())
                    .horizontalErraticness(difficulty.getHorizontalAimErraticness())
                    .verticalErraticness(difficulty.getVerticalAimErraticness())
                    .averageCps((int) difficulty.getCps())
                    .latency((int) difficulty.getLatency()).latencyDeviation((int) difficulty.getLatencyDeviation())
                    .sprintResetAccuracy(difficulty.getSprintResetAccuracy())
                    .hitSelectAccuracy(difficulty.getHitSelectAccuracy()).build();
            return BotAPI.INSTANCE.spawn(config, new ServerLocation() {
                @Override
                public ServerWorld<?> getWorld() {
                    return new ServerWorld<World>() {
                        @Override
                        public World getHandle() {
                            return location.getWorld();
                        }
                    };
                }

                @Override
                public double getX() {
                    return location.getX();
                }

                @Override
                public double getY() {
                    return location.getY();
                }

                @Override
                public double getZ() {
                    return location.getZ();
                }

                @Override
                public float getYaw() {
                    return location.getYaw();
                }

                @Override
                public float getPitch() {
                    return location.getPitch();
                }
            });
        }
    },
    RANDOM(CC.AQUA + "Random") {
        @Override
        public FakePlayer spawn(MatchData matchData, Location location, String suffix) {
            CustomDifficulty difficulty = new CustomDifficulty();
            difficulty.randomize();
            BotConfiguration config = BotConfiguration.builder().username("RandomBot" + suffix)
                    .horizontalAimSpeed(difficulty.getHorizontalAimSpeed())
                    .verticalAimSpeed(difficulty.getVerticalAimSpeed())
                    .horizontalAimAccuracy(difficulty.getHorizontalAimAccuracy())
                    .verticalAimAccuracy(difficulty.getVerticalAimAccuracy())
                    .horizontalErraticness(difficulty.getHorizontalAimErraticness())
                    .verticalErraticness(difficulty.getVerticalAimErraticness())
                    .averageCps((int) difficulty.getCps())
                    .latency((int) difficulty.getLatency()).latencyDeviation((int) difficulty.getLatencyDeviation())
                    .sprintResetAccuracy(difficulty.getSprintResetAccuracy())
                    .hitSelectAccuracy(difficulty.getHitSelectAccuracy()).build();
            return BotAPI.INSTANCE.spawn(config, new ServerLocation() {
                @Override
                public ServerWorld<?> getWorld() {
                    return new ServerWorld<World>() {
                        @Override
                        public World getHandle() {
                            return location.getWorld();
                        }
                    };
                }

                @Override
                public double getX() {
                    return location.getX();
                }

                @Override
                public double getY() {
                    return location.getY();
                }

                @Override
                public double getZ() {
                    return location.getZ();
                }

                @Override
                public float getYaw() {
                    return location.getYaw();
                }

                @Override
                public float getPitch() {
                    return location.getPitch();
                }
            });
        }
    };

    @Getter
    final String display;

    public abstract FakePlayer spawn(MatchData matchData, Location location, String suffix);
}
