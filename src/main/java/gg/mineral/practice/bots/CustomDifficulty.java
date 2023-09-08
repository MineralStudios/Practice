package gg.mineral.practice.bots;

import java.util.Random;

import lombok.Data;

@Data
public class CustomDifficulty {
    float aimSpeed = 0.5F, aimAccuracy = 5F, bowAimingRadius = 0.6f, reach = 3.0f, sprintResetAccuracy = 0.95F,
            hitSelectAccuracy = 0.95F,
            distancingMin = 2.6f, distancingMax = 3.0f, cps = 13f, latency = 50f, reactionTimeTicks = 0;

    public void randomize() {
        Random r = new Random();

        this.aimSpeed = 0.15f + r.nextFloat() * (1f - 0.15f);
        this.aimAccuracy = 0.75f + r.nextFloat() * (16f - 0.75f);
        this.bowAimingRadius = 0.1f + r.nextFloat() * (2.4f - 0.1f);
        this.sprintResetAccuracy = 0.5f + r.nextFloat() * (1.0f - 0.5f);
        this.hitSelectAccuracy = 0.5f + r.nextFloat() * (1.0f - 0.5f);
        this.distancingMin = 1.7f + r.nextFloat() * (2.9f - 1.7f);
        this.distancingMax = 2.8f + r.nextFloat() * (3.0f - 2.8f);
        this.cps = r.nextInt((20 - 6) + 1) + 6;
        this.latency = r.nextInt((200 - 10) + 1) + 10;
        this.reactionTimeTicks = r.nextInt((2) + 1) + 0;
    }

}
