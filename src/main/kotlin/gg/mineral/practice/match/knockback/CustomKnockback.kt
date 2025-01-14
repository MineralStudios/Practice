package gg.mineral.practice.match.knockback

import gg.mineral.server.combat.KnockbackProfile
import net.minecraft.server.v1_8_R3.EntityLiving
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

open class CustomKnockback : KnockbackProfile {
    var friction = 2.0
    var horizontal = 0.46
    var vertical = 0.36
    var verticalLimit = 0.4
    var horizontalExtra = 0.52
    var verticalExtra = 0.045

    constructor() : super("", "Custom")

    constructor(knockback: CustomKnockback) : super(knockback.scriptFilePath, knockback.name) {
        this.friction = knockback.friction
        this.horizontal = knockback.horizontal
        this.vertical = knockback.vertical
        this.verticalLimit = knockback.verticalLimit
        this.horizontalExtra = knockback.horizontalExtra
        this.verticalExtra = knockback.verticalExtra
    }

    override fun callFirstStage(attacker: EntityLiving, victim: EntityLiving) {
        if (friction > 0) {
            victim.motX /= friction
            victim.motY /= friction
            victim.motZ /= friction
        } else {
            victim.motX = 0.0
            victim.motY = 0.0
            victim.motZ = 0.0
        }

        val distanceX = attacker.locX - victim.locX
        val distanceZ = attacker.locZ - victim.locZ

        val magnitude = sqrt(distanceX * distanceX + distanceZ * distanceZ)

        victim.motX -= distanceX / magnitude * horizontal
        victim.motY += vertical
        victim.motZ -= distanceZ / magnitude * horizontal

        if (victim.motY > verticalLimit) victim.motY = verticalLimit
    }

    override fun callSecondStage(attacker: EntityLiving, victim: EntityLiving, knockbackEnchantLevel: Int) {
        var extraKBMult = knockbackEnchantLevel
        if (attacker.isSprinting) extraKBMult += 1

        if (extraKBMult > 0) {
            val yaw = Math.toRadians(attacker.yaw.toDouble())
            val sin = -sin(yaw)
            val cos = cos(yaw)
            victim.motX += sin * horizontalExtra * extraKBMult
            victim.motY += verticalExtra
            victim.motZ += cos * horizontalExtra * extraKBMult
            attacker.motX *= 0.6
            attacker.motZ *= 0.6
            attacker.isSprinting = false
        }
    }
}
