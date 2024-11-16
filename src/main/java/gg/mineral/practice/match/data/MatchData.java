package gg.mineral.practice.match.data;

import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.api.knockback.Knockback;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.duel.DuelSettings;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.queue.QueueSettings.QueueEntry;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfileList;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
public class MatchData {
	private Queuetype queuetype;
	private Gametype gametype;
	@Setter
	private byte arenaId;
	private Kit kit;
	private Knockback knockback;
	private int noDamageTicks = 20, pearlCooldown = 15;
	private boolean hunger = true, boxing = false, build = false, damage = true, griefing = false, deadlyWater = false,
			regeneration = true;
	private boolean ranked = false;
	protected Byte2BooleanOpenHashMap enabledArenas = new Byte2BooleanOpenHashMap();
	private ItemStack displayItem = ItemStacks.WOOD_AXE;

	private MatchData() {

		if (!GametypeManager.getGametypes().isEmpty())
			setGametype(GametypeManager.getGametypes().values().iterator().next());

		if (this.knockback == null)
			this.knockback = noDamageTicks < 10 ? KnockbackProfileList.getComboKnockbackProfile()
					: KnockbackProfileList.getDefaultKnockbackProfile();
	}

	public MatchData(Queuetype queuetype, Gametype gametype, QueueSettings queueSettings) {
		this();
		setQueuetype(queuetype);
		setGametype(gametype);
		this.enabledArenas = new Byte2BooleanOpenHashMap(queueSettings.getEnabledArenas());

		if (this.knockback == null)
			this.knockback = noDamageTicks < 10 ? KnockbackProfileList.getComboKnockbackProfile()
					: KnockbackProfileList.getDefaultKnockbackProfile();
	}

	public MatchData(QueueEntry queueEntry, QueueSettings queueSettings) {
		this(queueEntry.queuetype(), queueEntry.gametype(), queueSettings);
	}

	public MatchData(QueueEntry queueEntry) {
		this();
		val queuetype = queueEntry.queuetype();
		val gametype = queueEntry.gametype();
		setQueuetype(queuetype);
		setGametype(gametype);
	}

	public MatchData(DuelSettings duelSettings) {
		this();
		val queuetype = duelSettings.getQueuetype();
		val gametype = duelSettings.getGametype();
		if (queuetype != null)
			setQueuetype(queuetype);
		if (gametype != null)
			setGametype(gametype);
		this.arenaId = duelSettings.getArenaId();
		this.kit = duelSettings.getKit();
		this.knockback = duelSettings.getKnockback();
		this.noDamageTicks = duelSettings.getNoDamageTicks();
		this.hunger = duelSettings.isHunger();
		this.boxing = duelSettings.isBoxing();
		this.build = duelSettings.isBuild();
		this.damage = duelSettings.isDamage();
		this.griefing = duelSettings.isGriefing();
		this.deadlyWater = duelSettings.isDeadlyWater();
		this.regeneration = duelSettings.isRegeneration();
		this.pearlCooldown = duelSettings.getPearlCooldown();
	}

	public void setQueuetype(@NonNull Queuetype queuetype) {
		this.queuetype = queuetype;
		this.knockback = queuetype.getKnockback();
	}

	public void setGametype(@NonNull Gametype gametype) {
		this.gametype = gametype;
		this.displayItem = gametype.getDisplayItem().clone();
		this.kit = gametype.getKit();
		this.noDamageTicks = gametype.getNoDamageTicks();
		this.hunger = gametype.isHunger();
		this.boxing = gametype.isBoxing();
		this.build = gametype.isBuild();
		this.damage = gametype.isDamage();
		this.griefing = gametype.isGriefing();
		this.deadlyWater = gametype.isDeadlyWater();
		this.regeneration = gametype.isRegeneration();
		this.pearlCooldown = gametype.getPearlCooldown();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		String newLine = CC.R + "\n";

		Arena arena = ArenaManager.getArenas().get(arenaId);

		sb.append(CC.GREEN + "Kit: " + kit.getName());
		sb.append(newLine);
		sb.append(CC.GREEN + "Arena: " + arena.getDisplayName());
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

	public Int2ObjectOpenHashMap<ItemStack[]> getCustomKits(Profile p) {
		val queuetype = getQueuetype();
		val gametype = getGametype();

		if (queuetype == null || gametype == null)
			return new Int2ObjectOpenHashMap<>();
		return p.getCustomKits(queuetype, gametype);
	}

	public int getElo(Profile p) {
		val gametype = getGametype();

		if (gametype == null)
			return 0;
		return gametype.getElo(p);
	}

	public short getQueueAndGameTypeHash() {
		val queuetypeId = queuetype == null ? 0 : queuetype.getId();
		val gametypeId = gametype == null ? 0 : gametype.getId();
		return (short) (queuetypeId << 8 | gametypeId);
	}
}
