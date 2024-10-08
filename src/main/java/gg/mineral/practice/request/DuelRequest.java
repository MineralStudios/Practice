package gg.mineral.practice.request;

import gg.mineral.practice.duel.DuelSettings;
import gg.mineral.practice.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DuelRequest {
	private final Profile sender;
	private final DuelSettings duelSettings;
}
