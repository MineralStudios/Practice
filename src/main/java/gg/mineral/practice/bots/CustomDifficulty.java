package gg.mineral.practice.bots;

import java.util.Random;

import lombok.Data;

@Data
public class CustomDifficulty {
    float horizontalAimSpeed = 0.5F, verticalAimSpeed = 0.5F, horizontalAimAccuracy = 0.5F, verticalAimAccuracy = 0.5F,
            bowAimingRadius = 2.4f, reach = 3.0f, sprintResetAccuracy = 0.25F,
            hitSelectAccuracy = 0.0F,
            distancingMin = 1.8f, distancingMax = 3.0f, cps = 5f, latency = 50f, reactionTimeTicks = 2;

    public void randomize() {
        Random r = new Random();

        this.horizontalAimSpeed = 0.35f + r.nextFloat() * (1.1f - 0.35f);
        this.horizontalAimAccuracy = 0.35f + r.nextFloat() * (1.1f - 0.35f);
        this.verticalAimSpeed = 0.35f + r.nextFloat() * (1.1f - 0.35f);
        this.verticalAimAccuracy = 0.35f + r.nextFloat() * (1.1f - 0.35f);
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
