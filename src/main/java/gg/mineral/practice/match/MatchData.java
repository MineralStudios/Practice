package gg.mineral.practice.match;

import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.queue.QueueEntry;
import land.strafe.server.combat.KnockbackProfile;
import land.strafe.server.combat.KnockbackProfileList;

public class MatchData {
	Arena arena;
	Kit kit;
	KnockbackProfile kb;
	Gametype gametype;
	int noDamageTicks = 20;
	boolean hunger = true;
	boolean boxing = false;
	boolean build = false;
	boolean damage = true;
	boolean griefing = false;
	QueueEntry queueEntry;
	boolean deadlyWater = false;
	boolean regeneration = true;
	boolean ranked = false;
	String kitName = "Custom";
	Integer pearlCooldown = 15;
	final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();

	public MatchData() {

		if (!gametypeManager.getGametypes().isEmpty()) {
			this.gametype = gametypeManager.getGametypes().get(0);
			this.kitName = gametype.getDisplayName();
			this.kit = gametype.getKit();

		}

		if (!arenaManager.getArenas().isEmpty()) {
			this.arena = arenaManager.getArenas().get(0);
		}

		this.kb = KnockbackProfileList.getDefaultKnockbackProfile();
	}

	public MatchData(QueueEntry qe) {
		this.queueEntry = qe;
		this.gametype = qe.getGametype();
		arena = qe.getQueuetype().nextArena(this.gametype);
		kit = this.gametype.getKit();
		noDamageTicks = this.gametype.getNoDamageTicks();
		kb = qe.getQueuetype().getKnockback();
		hunger = this.gametype.getHunger();
		boxing = this.gametype.getBoxing();
		build = this.gametype.getBuild();
		damage = this.gametype.getDamage();
		griefing = this.gametype.getGriefing();
		deadlyWater = this.gametype.getDeadlyWater();
		regeneration = this.gametype.getRegeneration();
		ranked = qe.getQueuetype().isRanked();
	}

	public void setGametype(Gametype g) {
		this.gametype = g;
		kit = g.getKit();
		kitName = g.getName();
		noDamageTicks = g.getNoDamageTicks();
		hunger = g.getHunger();
		boxing = g.getBoxing();
		build = g.getBuild();
		damage = g.getDamage();
		griefing = g.getGriefing();
		deadlyWater = g.getDeadlyWater();
		regeneration = g.getRegeneration();
	}

	public void setArena(Arena a) {
		arena = a;
	}

	public void setKnockback(KnockbackProfile knockbackProfile) {
		kb = knockbackProfile;
	}

	public void setKit(Kit k, String Name) {
		kit = k;
		kitName = Name;
	}

	public void setNoDamageTicks(int i) {
		noDamageTicks = i;
	}

	public void setHunger(boolean b) {
		hunger = b;
	}

	public void setDeadlyWater(boolean b) {
		deadlyWater = b;
	}

	public void setBuild(boolean b) {
		build = b;
	}

	public void setDamage(boolean b) {
		damage = b;
	}

	public void setRegeneration(boolean b) {
		regeneration = b;
	}

	public void setPearlCooldown(int i) {
		pearlCooldown = i;
	}

	public void setGriefing(boolean b) {
		griefing = b;
	}

	public Arena getArena() {
		return arena;
	}

	public KnockbackProfile getKnockback() {
		return kb;
	}

	public Kit getKit() {
		return kit;
	}

	public int getNoDamageTicks() {
		return noDamageTicks;
	}

	public int getPearlCooldown() {
		return pearlCooldown;
	}

	public boolean getHunger() {
		return hunger;
	}

	public boolean getDeadlyWater() {
		return deadlyWater;
	}

	public boolean getRegeneration() {
		return regeneration;
	}

	public boolean getBuild() {
		return build;
	}

	public boolean getDamage() {
		return damage;
	}

	public boolean getGriefing() {
		return griefing;
	}

	public String getKitName() {
		return kitName;
	}

	public QueueEntry getQueueEntry() {
		return queueEntry;
	}

	public boolean isRanked() {
		return ranked;
	}

	public Gametype getGametype() {
		return gametype;
	}

	public boolean getBoxing() {
		return boxing;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		String newLine = CC.R + "\n";

		sb.append(CC.GREEN + "Arena: " + arena.getDisplayName());
		sb.append(newLine);
		sb.append(CC.GREEN + "Kit: " + kitName);
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
