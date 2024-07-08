package gg.mineral.practice.bots;

import java.util.Random;

import lombok.Data;

@Data
public class CustomDifficulty {
    float horizontalAimSpeed = 0.5F, verticalAimSpeed = 0.5F, horizontalAimAccuracy = 0.5F, verticalAimAccuracy = 0.5F,
            bowAimingRadius = 2.4f, reach = 3.0f, sprintResetAccuracy = 0.25F,
            hitSelectAccuracy = 0.0F,
            distancingMin = 1.8f, distancingMax = 3.0f, cps = 5f, latency = 50f, latencyDeviation = 0f,
            reactionTimeTicks = 2;

    public void randomize() {
        Random r = new Random();

        this.horizontalAimSpeed = 0.3f + r.nextFloat() * (1.1f - 0.3f);
        this.horizontalAimAccuracy = 0.25f + r.nextFloat() * (1.1f - 0.25f);
        this.verticalAimSpeed = 0.3f + r.nextFloat() * (1.1f - 0.3f);
        this.verticalAimAccuracy = 0.25f + r.nextFloat() * (1.1f - 0.25f);
        this.bowAimingRadius = 0.1f + r.nextFloat() * (2.4f - 0.1f);
        this.sprintResetAccuracy = 0.25f + r.nextFloat() * (1.0f - 0.25f);
        this.hitSelectAccuracy = r.nextFloat();
        this.distancingMin = 1.7f + r.nextFloat() * (2.8f - 1.7f);
        this.distancingMax = 2.9f + r.nextFloat() * (3.0f - 2.9f);
        this.cps = r.nextInt((20 - 5) + 1) + 5;
        this.latency = r.nextInt((250 - 5) + 1) + 5;
        this.latencyDeviation = Math.min(r.nextInt(25 + 1), latency / 2);
        this.reactionTimeTicks = r.nextInt((2) + 1) + 0;
    }

}
