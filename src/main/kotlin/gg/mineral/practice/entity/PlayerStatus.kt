package gg.mineral.practice.entity

import org.bukkit.GameMode

enum class PlayerStatus(val gameMode: GameMode) {
    FOLLOWING(GameMode.SPECTATOR) {
        override fun canFly(profile: Profile) = true
    },
    SPECTATING(GameMode.SPECTATOR) {
        override fun canFly(profile: Profile) = true
    },
    FIGHTING(GameMode.SURVIVAL) {
        override fun canFly(profile: Profile) = false
    },
    KIT_EDITOR(GameMode.SURVIVAL) {
        override fun canFly(profile: Profile) = false
    },
    KIT_CREATOR(GameMode.CREATIVE) {
        override fun canFly(profile: Profile) = false
    },
    IDLE(GameMode.SURVIVAL) {
        override fun canFly(profile: Profile) = profile.player.hasPermission("practice.fly")
    },
    QUEUEING(GameMode.SURVIVAL) {
        override fun canFly(profile: Profile) = profile.player.hasPermission("practice.fly")
    };

    abstract fun canFly(profile: Profile): Boolean
}
