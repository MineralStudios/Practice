package gg.mineral.practice.match;

import gg.mineral.api.knockback.Knockback;
import gg.mineral.api.knockback.KnockbackMode;

public class CustomKnockback extends Knockback {

    public static double origKnockbackFriction = 2.0D;
    public static double origKnockbackHorizontal = 0.46D;
    public static double origKnockbackVertical = 0.36D;
    public static double origKnockbackVerticalLimit = 0.4D;
    public static double origKnockbackExtraHorizontal = 0.52D;
    public static double origKnockbackExtraVertical = 0.045D;

    public CustomKnockback() {
        super("Custom");
        this.knockbackMode = KnockbackMode.NORMAL;
    }
}
