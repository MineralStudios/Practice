package gg.mineral.practice.entity;

import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PlayerStatus {
	FOLLOWING(profile -> true), SPECTATING(profile -> true), FIGHTING(profile -> false),
	IDLE(profile -> profile.getPlayer().hasPermission("practice.fly")),
	QUEUEING(profile -> profile.getPlayer().hasPermission("practice.fly"));

	private final Function<Profile, Boolean> canFly;
}
