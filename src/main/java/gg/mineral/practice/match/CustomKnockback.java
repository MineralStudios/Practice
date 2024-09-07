package gg.mineral.practice.match;

import gg.mineral.server.combat.KnockbackProfile;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import net.minecraft.server.v1_8_R3.EntityLiving;

@Getter
@Setter
public class CustomKnockback extends KnockbackProfile {

    private double friction = 2.0D, horizontal = 0.46D, vertical = 0.36D,
            verticalLimit = 0.4D, horizontalExtra = 0.52D, verticalExtra = 0.045D;

    public CustomKnockback() {
        super("", "Custom");
    }

    @Override
    public Map<String, Object> callFirstStage(EntityLiving attacker, EntityLiving victim) {
        if (friction > 0) {
            victim.motX /= friction;
            victim.motY /= friction;
            victim.motZ /= friction;
        } else {
            victim.motX = 0;
            victim.motY = 0;
            victim.motZ = 0;
        }

        double distanceX = attacker.locX - victim.locX;
        double distanceZ = attacker.locZ - victim.locZ;

        double magnitude = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);

        victim.motX -= distanceX / magnitude * horizontal;
        victim.motY += vertical;
        victim.motZ -= distanceZ / magnitude * horizontal;

        if (victim.motY > verticalLimit)
            victim.motY = verticalLimit;

        return null;
    }

    @Override
    public Map<String, Object> callSecondStage(EntityLiving attacker, EntityLiving victim, int knockbackEnchantLevel) {
        int extraKBMult = knockbackEnchantLevel;
        if (attacker.isSprinting())
            extraKBMult += 1;

        if (extraKBMult > 0) {
            double yaw = Math.toRadians(attacker.yaw);
            double sin = -Math.sin(yaw);
            double cos = Math.cos(yaw);
            victim.motX += sin * horizontalExtra * extraKBMult;
            victim.motY += verticalExtra;
            victim.motZ += cos * horizontalExtra * extraKBMult;
            attacker.motX *= 0.6D;
            attacker.motZ *= 0.6D;
            attacker.setSprinting(false);
        }
        return null;
    }
}
