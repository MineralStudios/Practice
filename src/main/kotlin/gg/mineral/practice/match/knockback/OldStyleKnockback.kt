package gg.mineral.practice.match.knockback

class OldStyleKnockback : CustomKnockback() {
    init {
        friction = 2.0
        horizontal = 0.35
        vertical = 0.35
        verticalLimit = 0.4
        horizontalExtra = 0.425
        verticalExtra = 0.085
    }
}
