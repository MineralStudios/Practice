package gg.mineral.practice.request;

import gg.mineral.practice.duel.DuelSettings;
import gg.mineral.practice.entity.Profile;

public record DuelRequest(Profile sender, DuelSettings duelSettings) {
}
