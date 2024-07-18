package gg.mineral.practice.match.data;

import gg.mineral.api.knockback.Knockback;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.bots.CustomDifficulty;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.match.CustomKnockback;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfileList;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import java.util.function.Supplier;
import java.util.Collection;

@Getter
public class MatchData {
	@Setter
	Arena arena;
	@Setter
	Kit kit;
	@Setter
	Knockback knockback;
	@Setter
	CustomKnockback customKnockback;
	@Setter
	CustomDifficulty customBotDifficulty;
	Gametype gametype;
	@Setter
	int noDamageTicks = 20, pearlCooldown = 15;
	@Setter
	Difficulty botDifficulty = Difficulty.EASY;
	@Setter
	boolean hunger = true, boxing = false, build = false, damage = true, griefing = false, deadlyWater = false,
			regeneration = true, team2v2 = false, botTeammate = false, botQueue = false, arenaSelection = true;
	protected Object2BooleanOpenHashMap<Arena> enabledArenas = new Object2BooleanOpenHashMap<>();

	public MatchData() {

		if (!GametypeManager.getGametypes().isEmpty())
			setGametype(GametypeManager.getGametypes().get(0));

		if (!ArenaManager.getArenas().isEmpty())
			this.arena = ArenaManager.getArenas().get(0);

		this.knockback = KnockbackProfileList.getDefaultKnockbackProfile();
	}

	public void setEnabledArenas(Collection<Arena> enabledArenas) {
		for (Arena arena : enabledArenas)
			this.enabledArenas.put(arena, true);
	}

	public <D extends MatchData> D newClone(Supplier<D> supplier) {
		D data = supplier.get();
		data.kit = this.kit;
		data.knockback = this.knockback;
		data.customKnockback = this.customKnockback;
		data.customBotDifficulty = this.customBotDifficulty;
		data.gametype = this.gametype;
		data.noDamageTicks = this.noDamageTicks;
		data.pearlCooldown = this.pearlCooldown;
		data.botDifficulty = this.botDifficulty;
		data.hunger = this.hunger;
		data.boxing = this.boxing;
		data.build = this.build;
		data.damage = this.damage;
		data.griefing = this.griefing;
		data.deadlyWater = this.deadlyWater;
		data.regeneration = this.regeneration;
		data.team2v2 = this.team2v2;
		data.botTeammate = this.botTeammate;
		data.botQueue = this.botQueue;
		data.arenaSelection = this.arenaSelection;
		data.enabledArenas = new Object2BooleanOpenHashMap<>(this.enabledArenas);
		return data;
	}

	public <D extends MatchData> D cloneBotAndArenaData(Supplier<D> supplier) {
		D data = supplier.get();
		data.customBotDifficulty = this.customBotDifficulty;
		data.botDifficulty = this.botDifficulty;
		data.botTeammate = this.botTeammate;
		data.botQueue = this.botQueue;
		data.arenaSelection = this.arenaSelection;

		if (data.arenaSelection && !this.enabledArenas.isEmpty())
			data.enabledArenas = new Object2BooleanOpenHashMap<>(this.enabledArenas);
		return data;
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
		pearlCooldown = gametype.getPearlCooldown();
	}

	public void enableArena(Arena arena, boolean enabled) {
		enabledArenas.put(arena, enabled);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		String newLine = CC.R + "\n";

		sb.append(CC.GREEN + "Arena: " + arena.getDisplayName());
		sb.append(newLine);
		sb.append(CC.GREEN + "Kit: " + kit.getName());
		sb.append(newLine);
		sb.append(CC.GREEN + "Knockback: " + knockback.getName());
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
		// TODO 2v2 with bots in /duel
		// sb.append(newLine);
		// sb.append(CC.GREEN + "2v2: " + team2v2);
		// sb.append(newLine);
		// sb.append(CC.GREEN + "Bots: " + bots);
		sb.append(newLine);
		sb.append(CC.GREEN + "Boxing: " + boxing);
		sb.append(newLine);
		sb.append(CC.GREEN + "Pearl Cooldown: " + pearlCooldown + " seconds");
		return sb.toString();
	}
}
