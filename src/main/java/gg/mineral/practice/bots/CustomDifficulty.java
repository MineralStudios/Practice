package gg.mineral.practice.bots;

import java.util.Random;

import lombok.Data;

@Data
public class CustomDifficulty {
    float horizontalAimSpeed = 0.5F, verticalAimSpeed = 0.5F, horizontalAimAccuracy = 0.5F, verticalAimAccuracy = 0.5F,
            horizontalAimErraticness = 0.5f, verticalAimErraticness = 0.5f, sprintResetAccuracy = 0.25F,
            hitSelectAccuracy = 0.0F, cps = 5f, latency = 50f, latencyDeviation = 0f;

    public void randomize() {
        Random r = new Random();

        this.horizontalAimSpeed = 0.3f + r.nextFloat() * (1.1f - 0.3f);
        this.horizontalAimAccuracy = 0.25f + r.nextFloat() * (1.1f - 0.25f);
        this.verticalAimSpeed = 0.3f + r.nextFloat() * (1.1f - 0.3f);
        this.verticalAimAccuracy = 0.25f + r.nextFloat() * (1.1f - 0.25f);
        this.horizontalAimErraticness = r.nextFloat();
        this.verticalAimErraticness = r.nextFloat();
        this.sprintResetAccuracy = 0.25f + r.nextFloat() * (1.0f - 0.25f);
        this.hitSelectAccuracy = r.nextFloat();
        this.cps = r.nextInt((20 - 5) + 1) + 5;
        this.latency = r.nextInt((150 - 5) + 1) + 5;
        this.latencyDeviation = r.nextInt((int) ((latency / 30) + 1));
    }

}
