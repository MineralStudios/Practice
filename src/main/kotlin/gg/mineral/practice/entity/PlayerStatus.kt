package gg.mineral.practice.entity

enum class PlayerStatus {
    FOLLOWING {
        override fun canFly(profile: Profile) = true
    },
    SPECTATING {
        override fun canFly(profile: Profile) = true
    },
    FIGHTING {
        override fun canFly(profile: Profile) = false
    },
    KIT_EDITOR {
        override fun canFly(profile: Profile) = false
    },
    KIT_CREATOR {
        override fun canFly(profile: Profile) = false
    },
    IDLE {
        override fun canFly(profile: Profile) = profile.player.hasPermission("practice.fly")
    },
    QUEUEING {
        override fun canFly(profile: Profile) = profile.player.hasPermission("practice.fly")
    };

    abstract fun canFly(profile: Profile): Boolean
}
