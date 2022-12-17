package gg.mineral.practice.match.data;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;
import lombok.Getter;
import lombok.Setter;

public class MatchData {
	@Setter
	@Getter
	Arena arena;
	@Setter
	@Getter
	Kit kit;
	@Setter
	@Getter
	KnockbackProfile knockback;
	@Getter
	Gametype gametype;
	@Setter
	@Getter
	int noDamageTicks = 20, pearlCooldown = 15;
	@Setter
	@Getter
	Boolean hunger = true, boxing = false, build = false, damage = true, griefing = false, deadlyWater = false,
			regeneration = true;
	@Getter
	QueueEntry queueEntry;
	@Getter
	boolean ranked = false;

	public MatchData() {

		if (!GametypeManager.getGametypes().isEmpty()) {
			setGametype(GametypeManager.getGametypes().get(0));
		}

		if (!ArenaManager.getArenas().isEmpty()) {
			this.arena = ArenaManager.getArenas().get(0);
		}

		this.knockback = KnockbackProfileList.getDefaultKnockbackProfile();
	}

	public MatchData(QueueEntry qe) {
		this.queueEntry = qe;
		this.gametype = qe.getGametype();
		arena = qe.getQueuetype().nextArena(this.gametype);
		kit = this.gametype.getKit();
		noDamageTicks = this.gametype.getNoDamageTicks();
		knockback = qe.getQueuetype().getKnockback();
		hunger = this.gametype.getHunger();
		boxing = this.gametype.getBoxing();
		build = this.gametype.getBuild();
		damage = this.gametype.getDamage();
		griefing = this.gametype.getGriefing();
		deadlyWater = this.gametype.getDeadlyWater();
		regeneration = this.gametype.getRegeneration();
		ranked = qe.getQueuetype().isRanked();
	}

	public void setGametype(Gametype gametype) {
		this.gametype = gametype;
		kit = gametype.getKit();
		noDamageTicks = gametype.getNoDamageTicks();
		hunger = gametype.getHunger();
		boxing = gametype.getBoxing();
		build = gametype.getBuild();
		damage = gametype.getDamage();
		griefing = gametype.getGriefing();
		deadlyWater = gametype.getDeadlyWater();
		regeneration = gametype.getRegeneration();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		String newLine = CC.R + "\n";

		sb.append(CC.GREEN + "Arena: " + arena.getDisplayName());
		sb.append(newLine);
		sb.append(CC.GREEN + "Kit: " + kit.getName());
		sb.append(newLine);
		sb.append(CC.GREEN + "Hit Delay: " + noDamageTicks);
		sb.append(newLine);
		sb.append(CC.GREEN + "Hunger: " + hunger);
		sb.append(newLine);
		sb.append(CC.GREEN + "Build: " + build);
		sb.append(newLine);
		sb.append(CC.GREEN + "Damage: " + damage);
		sb.append(newLine);
		sb.append(CC.GREEN + "Griefing: " + griefing);
		sb.append(newLine);
		sb.append(CC.GREEN + "Deadly Water: " + deadlyWater);
		sb.append(newLine);
		sb.append(CC.GREEN + "Regeneration: " + regeneration);
		sb.append(newLine);
		sb.append(CC.GREEN + "Pearl Cooldown: " + pearlCooldown + " seconds");
		return sb.toString();
	}
}
