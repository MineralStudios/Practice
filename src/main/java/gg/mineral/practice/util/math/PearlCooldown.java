package gg.mineral.practice.util.math;

import java.util.UUID;

import org.bukkit.Bukkit;

import gg.mineral.practice.PracticePlugin;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.val;

@Getter
public class PearlCooldown {
	private final Object2IntOpenHashMap<UUID> cooldowns = new Object2IntOpenHashMap<>();

	public PearlCooldown() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			for (val entry : cooldowns.object2IntEntrySet()) {
				val uuid = entry.getKey();
				var cooldown = entry.getIntValue();

				if (cooldown > 0) {
					cooldowns.put(uuid, --cooldown);
					val player = Bukkit.getPlayer(uuid);
					player.setLevel(cooldown);
				}
			}
		}, 0L, 20L);
	}

	public boolean isActive(UUID uuid) {
		return cooldowns.getInt(uuid) > 0;
	}

	public int getTimeRemaining(UUID uuid) {
		return cooldowns.getInt(uuid);
	}
}
