package gg.mineral.practice.match;

public class OldStyleKnockback extends CustomKnockback {
    public OldStyleKnockback() {
        super();
        setFriction(2.0D);
        setHorizontal(0.35D);
        setVertical(0.35D);
        setVerticalLimit(0.4D);
        setHorizontalExtra(0.425D);
        setVerticalExtra(0.085D);
    }
}
