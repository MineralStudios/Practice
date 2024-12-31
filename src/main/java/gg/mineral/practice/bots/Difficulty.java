package gg.mineral.practice.bots;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.entity.living.player.skin.Skins;
import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.math.ServerLocation;
import gg.mineral.bot.api.world.ServerWorld;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

@Getter
@RequiredArgsConstructor
public enum Difficulty {
    NOOB(CC.GREEN + "Noob") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            return BotConfiguration.builder().username("NoobBot").skin(Skins.MINERAL_GREEN)
                    .horizontalAimSpeed(0.3F)
                    .verticalAimSpeed(0.3F).horizontalAimAccuracy(0.3F).verticalAimAccuracy(0.25F)
                    .horizontalErraticness(0.3f).averageCps(5).latency(50).sprintResetAccuracy(0.25F)
                    .hitSelectAccuracy(0.0F).reach(1.5f).build();
        }

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return botConfiguration.getHorizontalAimSpeed() == 0.3F
                    && botConfiguration.getVerticalAimSpeed() == 0.3F
                    && botConfiguration.getHorizontalAimAccuracy() == 0.3F
                    && botConfiguration.getVerticalAimAccuracy() == 0.25F
                    && botConfiguration.getHorizontalErraticness() == 0.3f
                    && botConfiguration.getAverageCps() == 5
                    && botConfiguration.getLatency() == 50
                    && botConfiguration.getSprintResetAccuracy() == 0.25F
                    && botConfiguration.getHitSelectAccuracy() == 0.0F && botConfiguration.getReach() == 1.5f;
        }
    },
    EASY(CC.D_GREEN + "Easy") {
        @Override
        public BotConfiguration getConfiguration(QueueSettings settings) {
            return BotConfiguration.builder().username("EasyBot").skin(Skins.MINERAL_GREEN)
                    .horizontalAimSpeed(0.3F)
                    .verticalAimSpeed(0.3F).horizontalAimAccuracy(0.3F).verticalAimAccuracy(0.25F)
                    .horizontalErraticness(0.3f).averageCps(5).latency(50).sprintResetAccuracy(0.25F)
                    .hitSelectAccuracy(0.0F).build();
        }

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return botConfiguration.getHorizontalAimSpeed() == 0.3F
                    && botConfiguration.getVerticalAimSpeed() == 0.3F
                    && botConfiguration.getHorizontalAimAccuracy() == 0.3F
                    && botConfiguration.getVerticalAimAccuracy() == 0.25F
                    && botConfiguration.getHorizontalErraticness() == 0.3f
                    && botConfiguration.getAverageCps() == 5
                    && botConfiguration.getLatency() == 50
                    && botConfiguration.getSprintResetAccuracy() == 0.25F
                    && botConfiguration.getHitSelectAccuracy() == 0.0F && botConfiguration.getReach() == 3.0f;
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

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return botConfiguration.getHorizontalAimSpeed() == 0.4F
                    && botConfiguration.getVerticalAimSpeed() == 0.4F
                    && botConfiguration.getHorizontalAimAccuracy() == 0.35F
                    && botConfiguration.getVerticalAimAccuracy() == 0.35F
                    && botConfiguration.getAverageCps() == 8
                    && botConfiguration.getLatency() == 50
                    && botConfiguration.getSprintResetAccuracy() == 0.45F
                    && botConfiguration.getHitSelectAccuracy() == 0.3f;
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

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return botConfiguration.getHorizontalAimSpeed() == 0.5F
                    && botConfiguration.getVerticalAimSpeed() == 0.5F
                    && botConfiguration.getHorizontalAimAccuracy() == 0.6F
                    && botConfiguration.getVerticalAimAccuracy() == 0.6F
                    && botConfiguration.getAverageCps() == 11
                    && botConfiguration.getLatency() == 50
                    && botConfiguration.getSprintResetAccuracy() == 0.8F
                    && botConfiguration.getHitSelectAccuracy() == 0.6f;
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

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return botConfiguration.getHorizontalAimSpeed() == 0.8F
                    && botConfiguration.getVerticalAimSpeed() == 0.8F
                    && botConfiguration.getHorizontalAimAccuracy() == 0.8F
                    && botConfiguration.getVerticalAimAccuracy() == 0.8F
                    && botConfiguration.getAverageCps() == 14
                    && botConfiguration.getLatency() == 50
                    && botConfiguration.getSprintResetAccuracy() == 0.85F
                    && botConfiguration.getHitSelectAccuracy() == 0.9f;
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

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return botConfiguration.getHorizontalAimSpeed() == 0.95F
                    && botConfiguration.getVerticalAimSpeed() == 0.95F
                    && botConfiguration.getHorizontalAimAccuracy() == 0.95F
                    && botConfiguration.getVerticalAimAccuracy() == 0.95F
                    && botConfiguration.getAverageCps() == 17
                    && botConfiguration.getLatency() == 50
                    && botConfiguration.getSprintResetAccuracy() == 0.95F
                    && botConfiguration.getHitSelectAccuracy() == 0.98f;
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

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return botConfiguration.getHorizontalAimSpeed() == 1.1F
                    && botConfiguration.getVerticalAimSpeed() == 1.1F
                    && botConfiguration.getHorizontalAimAccuracy() == 1.1F
                    && botConfiguration.getVerticalAimAccuracy() == 1.1F
                    && botConfiguration.getAverageCps() == 20
                    && botConfiguration.getLatency() == 50
                    && botConfiguration.getSprintResetAccuracy() == 1.0F
                    && botConfiguration.getHitSelectAccuracy() == 1.0F;
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

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return false;
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
                    .latency(latency).latencyDeviation(r.nextInt((latency / 30) + 1))
                    .sprintResetAccuracy(0.25f + r.nextFloat() * (1.0f - 0.25f))
                    .hitSelectAccuracy(r.nextFloat()).build();
        }

        @Override
        public boolean configEquals(BotConfiguration botConfiguration) {
            return false;
        }
    };

    private final String display;

    public static ClientInstance spawn(BotConfiguration config, Location location) {
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

    public abstract boolean configEquals(BotConfiguration botConfiguration);

}
