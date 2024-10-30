package gg.mineral.practice.bots;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.entity.living.player.skin.Skins;
import gg.mineral.bot.api.math.ServerLocation;
import gg.mineral.bot.api.world.ServerWorld;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public enum Difficulty {

    EASY(CC.GREEN + "Easy") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            return BotConfiguration.builder().username("EasyBot").skin(Skins.MINERAL_GREEN)
                    .horizontalAimSpeed(0.3F)
                    .verticalAimSpeed(0.3F).horizontalAimAccuracy(0.3F).verticalAimAccuracy(0.25F)
                    .horizontalErraticness(0.3f).averageCps(5).latency(50).sprintResetAccuracy(0.25F)
                    .hitSelectAccuracy(0.0F).build();
        }
    },
    MEDIUM(CC.YELLOW + "Medium") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            return BotConfiguration.builder().username("MediumBot")
                    .skin(Skins.MINERAL_YELLOW).horizontalAimSpeed(0.4F)
                    .verticalAimSpeed(0.4F).horizontalAimAccuracy(0.35F).verticalAimAccuracy(0.35F).averageCps(8)
                    .latency(50).sprintResetAccuracy(0.45F).hitSelectAccuracy(0.3f).build();
        }
    },
    HARD(CC.GOLD + "Hard") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            return BotConfiguration.builder().username("HardBot").skin(Skins.MINERAL_ORANGE)
                    .horizontalAimSpeed(0.5F)
                    .verticalAimSpeed(0.5F).horizontalAimAccuracy(0.6F).verticalAimAccuracy(0.6F).averageCps(11)
                    .latency(50).sprintResetAccuracy(0.8F).hitSelectAccuracy(0.6f).build();
        }
    },
    EXPERT(CC.RED + "Expert") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            return BotConfiguration.builder().username("ExpertBot").skin(Skins.MINERAL_RED)
                    .horizontalAimSpeed(0.8F)
                    .verticalAimSpeed(0.8F).horizontalAimAccuracy(0.8F).verticalAimAccuracy(0.8F).averageCps(14)
                    .latency(50).sprintResetAccuracy(0.85F).hitSelectAccuracy(0.9f).build();
        }
    },
    PRO(CC.PURPLE + "Pro") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            return BotConfiguration.builder().username("ProBot").skin(Skins.MINERAL_PURPLE)
                    .horizontalAimSpeed(0.95F)
                    .verticalAimSpeed(0.95F).horizontalAimAccuracy(0.95F).verticalAimAccuracy(0.95F).averageCps(17)
                    .latency(50).sprintResetAccuracy(0.95F).hitSelectAccuracy(0.98f).build();
        }
    },
    HARDCORE(CC.PINK + "Hardcore") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            return BotConfiguration.builder().username("HardcoreBot")
                    .skin(Skins.MINERAL_PINK)
                    .horizontalAimSpeed(1.1F).verticalAimSpeed(1.1F).horizontalAimAccuracy(1.1F)
                    .verticalAimAccuracy(1.1F).averageCps(20).latency(50).sprintResetAccuracy(1.0F)
                    .hitSelectAccuracy(1.0F).build();
        }
    },
    CUSTOM(CC.GRAY + "Custom") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            val customBotConfiguration = settings.getCustomBotConfiguration();
            return BotConfiguration.builder().username("CustomBot")
                    .horizontalAimSpeed(customBotConfiguration.getHorizontalAimSpeed())
                    .verticalAimSpeed(customBotConfiguration.getVerticalAimSpeed())
                    .horizontalAimAccuracy(customBotConfiguration.getHorizontalAimAccuracy())
                    .verticalAimAccuracy(customBotConfiguration.getVerticalAimAccuracy())
                    .horizontalErraticness(customBotConfiguration.getHorizontalErraticness())
                    .verticalErraticness(customBotConfiguration.getVerticalErraticness())
                    .averageCps(customBotConfiguration.getAverageCps())
                    .cpsDeviation(customBotConfiguration.getCpsDeviation())
                    .latency(customBotConfiguration.getLatency())
                    .latencyDeviation(customBotConfiguration.getLatencyDeviation())
                    .sprintResetAccuracy(customBotConfiguration.getSprintResetAccuracy())
                    .hitSelectAccuracy(customBotConfiguration.getHitSelectAccuracy()).build();
        }
    },
    RANDOM(CC.AQUA + "Random") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {

            val r = new Random();

            val latency = r.nextInt((150 - 5) + 1) + 5;

            return BotConfiguration.builder().username("RandomBot")
                    .horizontalAimSpeed(0.3f + r.nextFloat() * (1.1f - 0.3f))
                    .verticalAimSpeed(0.3f + r.nextFloat() * (1.1f - 0.3f))
                    .horizontalAimAccuracy(0.25f + r.nextFloat() * (1.1f - 0.25f))
                    .verticalAimAccuracy(0.25f + r.nextFloat() * (1.1f - 0.25f))
                    .horizontalErraticness(r.nextFloat())
                    .verticalErraticness(r.nextFloat())
                    .averageCps(r.nextInt((20 - 5) + 1) + 5)
                    .latency(latency).latencyDeviation(r.nextInt((int) ((latency / 30) + 1)))
                    .sprintResetAccuracy(0.25f + r.nextFloat() * (1.0f - 0.25f))
                    .hitSelectAccuracy(r.nextFloat()).build();
        }
    };

    @Getter
    private final String display;

    public static FakePlayer spawn(BotConfiguration config, Location location) {
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

    public abstract BotConfiguration getConfiguration(QueueSettings settings);
}
