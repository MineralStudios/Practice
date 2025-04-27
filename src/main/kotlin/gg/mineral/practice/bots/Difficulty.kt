package gg.mineral.practice.bots

import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.entity.living.player.skin.Skins
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.api.world.ServerWorld
import gg.mineral.practice.queue.QueueSettings
import gg.mineral.practice.util.messages.CC
import org.bukkit.Location
import org.bukkit.World
import java.lang.ref.WeakReference
import java.util.*

enum class Difficulty(val display: String) {
    NOOB(CC.GREEN + "Noob") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration =
            BotConfiguration.builder().username("NoobBot").skin(Skins.MINERAL_GREEN)
                .horizontalAimSpeed(0.3f)
                .verticalAimSpeed(0.3f).horizontalAimAccuracy(0.3f).verticalAimAccuracy(0.25f)
                .horizontalErraticness(0.3f).averageCps(5f).latency(50).sprintResetAccuracy(0.25f)
                .hitSelectAccuracy(0.0f).reach(1.5f).build()

        override fun configEquals(botConfiguration: BotConfiguration) =
            botConfiguration.horizontalAimSpeed == 0.3f && botConfiguration.verticalAimSpeed == 0.3f && botConfiguration.horizontalAimAccuracy == 0.3f && botConfiguration.verticalAimAccuracy == 0.25f && botConfiguration.horizontalErraticness == 0.3f && botConfiguration.averageCps == 5f && botConfiguration.latency == 50 && botConfiguration.sprintResetAccuracy == 0.25f && botConfiguration.hitSelectAccuracy == 0.0f && botConfiguration.reach == 1.5f
    },
    EASY(CC.D_GREEN + "Easy") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration =
            BotConfiguration.builder().username("EasyBot").skin(Skins.MINERAL_GREEN)
                .horizontalAimSpeed(0.3f)
                .verticalAimSpeed(0.3f).horizontalAimAccuracy(0.3f).verticalAimAccuracy(0.25f)
                .horizontalErraticness(0.3f).averageCps(5f).latency(50).sprintResetAccuracy(0.25f)
                .hitSelectAccuracy(0.0f).build()

        override fun configEquals(botConfiguration: BotConfiguration) =
            botConfiguration.horizontalAimSpeed == 0.3f && botConfiguration.verticalAimSpeed == 0.3f && botConfiguration.horizontalAimAccuracy == 0.3f && botConfiguration.verticalAimAccuracy == 0.25f && botConfiguration.horizontalErraticness == 0.3f && botConfiguration.averageCps == 5f && botConfiguration.latency == 50 && botConfiguration.sprintResetAccuracy == 0.25f && botConfiguration.hitSelectAccuracy == 0.0f && botConfiguration.reach == 3.0f
    },
    MEDIUM(CC.YELLOW + "Medium") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration =
            BotConfiguration.builder().username("MediumBot")
                .skin(Skins.MINERAL_YELLOW).horizontalAimSpeed(0.4f)
                .verticalAimSpeed(0.4f).horizontalAimAccuracy(0.35f).verticalAimAccuracy(0.35f).averageCps(8f)
                .latency(50).sprintResetAccuracy(0.45f).hitSelectAccuracy(0.3f).build()

        override fun configEquals(botConfiguration: BotConfiguration) =
            botConfiguration.horizontalAimSpeed == 0.4f && botConfiguration.verticalAimSpeed == 0.4f && botConfiguration.horizontalAimAccuracy == 0.35f && botConfiguration.verticalAimAccuracy == 0.35f && botConfiguration.averageCps == 8f && botConfiguration.latency == 50 && botConfiguration.sprintResetAccuracy == 0.45f && botConfiguration.hitSelectAccuracy == 0.3f
    },
    HARD(CC.GOLD + "Hard") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration =
            BotConfiguration.builder().username("HardBot").skin(Skins.MINERAL_ORANGE)
                .horizontalAimSpeed(0.5f)
                .verticalAimSpeed(0.5f).horizontalAimAccuracy(0.6f).verticalAimAccuracy(0.6f).averageCps(11f)
                .latency(50).sprintResetAccuracy(0.8f).hitSelectAccuracy(0.6f).build()

        override fun configEquals(botConfiguration: BotConfiguration) =
            botConfiguration.horizontalAimSpeed == 0.5f && botConfiguration.verticalAimSpeed == 0.5f && botConfiguration.horizontalAimAccuracy == 0.6f && botConfiguration.verticalAimAccuracy == 0.6f && botConfiguration.averageCps == 11f && botConfiguration.latency == 50 && botConfiguration.sprintResetAccuracy == 0.8f && botConfiguration.hitSelectAccuracy == 0.6f
    },
    EXPERT(CC.RED + "Expert") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration =
            BotConfiguration.builder().username("ExpertBot").skin(Skins.MINERAL_RED)
                .horizontalAimSpeed(0.8f)
                .verticalAimSpeed(0.8f).horizontalAimAccuracy(0.8f).verticalAimAccuracy(0.8f).averageCps(14f)
                .latency(50).sprintResetAccuracy(0.85f).hitSelectAccuracy(0.9f).build()

        override fun configEquals(botConfiguration: BotConfiguration) =
            botConfiguration.horizontalAimSpeed == 0.8f && botConfiguration.verticalAimSpeed == 0.8f && botConfiguration.horizontalAimAccuracy == 0.8f && botConfiguration.verticalAimAccuracy == 0.8f && botConfiguration.averageCps == 14f && botConfiguration.latency == 50 && botConfiguration.sprintResetAccuracy == 0.85f && botConfiguration.hitSelectAccuracy == 0.9f
    },
    PRO(CC.PURPLE + "Pro") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration =
            BotConfiguration.builder().username("ProBot").skin(Skins.MINERAL_PURPLE)
                .horizontalAimSpeed(0.95f)
                .verticalAimSpeed(0.95f).horizontalAimAccuracy(0.95f).verticalAimAccuracy(0.95f).averageCps(17f)
                .latency(50).sprintResetAccuracy(0.95f).hitSelectAccuracy(0.98f).build()

        override fun configEquals(botConfiguration: BotConfiguration) =
            botConfiguration.horizontalAimSpeed == 0.95f && botConfiguration.verticalAimSpeed == 0.95f && botConfiguration.horizontalAimAccuracy == 0.95f && botConfiguration.verticalAimAccuracy == 0.95f && botConfiguration.averageCps == 17f && botConfiguration.latency == 50 && botConfiguration.sprintResetAccuracy == 0.95f && botConfiguration.hitSelectAccuracy == 0.98f
    },
    HARDCORE(CC.PINK + "Hardcore") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration =
            BotConfiguration.builder().username("HardcoreBot")
                .skin(Skins.MINERAL_PINK)
                .horizontalAimSpeed(1.1f).verticalAimSpeed(1.1f).horizontalAimAccuracy(1.1f)
                .verticalAimAccuracy(1.1f).averageCps(20f).latency(50).sprintResetAccuracy(1.0f)
                .hitSelectAccuracy(1.0f).build()

        override fun configEquals(botConfiguration: BotConfiguration) =
            botConfiguration.horizontalAimSpeed == 1.1f && botConfiguration.verticalAimSpeed == 1.1f && botConfiguration.horizontalAimAccuracy == 1.1f && botConfiguration.verticalAimAccuracy == 1.1f && botConfiguration.averageCps == 20f && botConfiguration.latency == 50 && botConfiguration.sprintResetAccuracy == 1.0f && botConfiguration.hitSelectAccuracy == 1.0f
    },
    CUSTOM(CC.GRAY + "Custom") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration {
            val customBotConfiguration = settings!!.customBotConfiguration
            return BotConfiguration.builder().username("CustomBot")
                .horizontalAimSpeed(customBotConfiguration.horizontalAimSpeed)
                .verticalAimSpeed(customBotConfiguration.verticalAimSpeed)
                .horizontalAimAccuracy(customBotConfiguration.horizontalAimAccuracy)
                .verticalAimAccuracy(customBotConfiguration.verticalAimAccuracy)
                .horizontalErraticness(customBotConfiguration.horizontalErraticness)
                .verticalErraticness(customBotConfiguration.verticalErraticness)
                .averageCps(customBotConfiguration.averageCps)
                .cpsDeviation(customBotConfiguration.cpsDeviation)
                .latency(customBotConfiguration.latency)
                .latencyDeviation(customBotConfiguration.latencyDeviation)
                .sprintResetAccuracy(customBotConfiguration.sprintResetAccuracy)
                .hitSelectAccuracy(customBotConfiguration.hitSelectAccuracy).build()
        }

        override fun configEquals(botConfiguration: BotConfiguration) = false
    },
    RANDOM(CC.AQUA + "Random") {
        override fun getConfiguration(settings: QueueSettings?): BotConfiguration {
            val random = Random()

            val latency = random.nextInt((150 - 5) + 1) + 5

            return BotConfiguration.builder().username("RandomBot")
                .horizontalAimSpeed(0.3f + random.nextFloat() * (1.1f - 0.3f))
                .verticalAimSpeed(0.3f + random.nextFloat() * (1.1f - 0.3f))
                .horizontalAimAccuracy(0.25f + random.nextFloat() * (1.1f - 0.25f))
                .verticalAimAccuracy(0.25f + random.nextFloat() * (1.1f - 0.25f))
                .horizontalErraticness(random.nextFloat())
                .verticalErraticness(random.nextFloat())
                .averageCps((random.nextInt((20 - 5) + 1) + 5).toFloat())
                .latency(latency).latencyDeviation(random.nextInt((latency / 30) + 1))
                .sprintResetAccuracy(0.25f + random.nextFloat() * (1.0f - 0.25f))
                .hitSelectAccuracy(random.nextFloat()).build()
        }

        override fun configEquals(botConfiguration: BotConfiguration) = false
    };

    abstract fun getConfiguration(settings: QueueSettings? = null): BotConfiguration

    abstract fun configEquals(botConfiguration: BotConfiguration): Boolean

    companion object {
        fun spawn(config: BotConfiguration, location: Location): WeakReference<ClientInstance> {
            return BotAPI.INSTANCE.spawn(config, object : ServerLocation {
                override val pitch: Float
                    get() = location.pitch
                override val world: ServerWorld<*>
                    get() = object : ServerWorld<World> {
                        override val handle
                            get() = location.world
                    }
                override val x: Double
                    get() = location.x
                override val y: Double
                    get() = location.y
                override val yaw: Float
                    get() = location.yaw
                override val z: Double
                    get() = location.z
            })
        }
    }
}
