package ms.uk.eclipse.match;

import land.strafe.server.combat.KnockbackProfile;
import land.strafe.server.combat.KnockbackProfileList;
import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.kit.Kit;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.queue.QueueEntry;

public class MatchData {
	Arena arena;
	Kit kit;
	KnockbackProfile kb;
	Gametype g;
	int noDamageTicks = 20;
	boolean hunger = true;
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
		Gametype g = gametypeManager.getGametypes().get(0);
		kitName = g.getName();
		arena = arenaManager.getArenas().get(0);
		kit = g.getKit();
		kb = KnockbackProfileList.getDefaultKnockbackProfile();
	}

	public MatchData(QueueEntry qe) {
		this.queueEntry = qe;
		Gametype g = qe.getGametype();
		this.g = g;
		arena = qe.getQueuetype().nextArena(g);
		kit = g.getKit();
		noDamageTicks = g.getNoDamageTicks();
		kb = qe.getQueuetype().getKnockback();
		hunger = g.getHunger();
		build = g.getBuild();
		damage = g.getDamage();
		griefing = g.getGriefing();
		deadlyWater = g.getDeadlyWater();
		regeneration = g.getRegeneration();
		ranked = qe.getQueuetype().isRanked();
	}

	public void setGametype(Gametype g) {
		this.g = g;
		kit = g.getKit();
		kitName = g.getName();
		noDamageTicks = g.getNoDamageTicks();
		kb = noDamageTicks < 10 ? KnockbackProfileList.getComboKnockbackProfile()
				: KnockbackProfileList.getDefaultKnockbackProfile();
		hunger = g.getHunger();
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
		return g;
	}
}
