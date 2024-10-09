package gg.mineral.practice.match.data;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.knockback.Knockback;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.duel.DuelSettings;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfileList;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public class MatchData {
	private short queueAndGameTypeHash = -1;
	byte arenaId;
	Kit kit;
	Knockback knockback;
	int noDamageTicks = 20, pearlCooldown = 15;
	boolean hunger = true, boxing = false, build = false, damage = true, griefing = false, deadlyWater = false,
			regeneration = true;
	private boolean ranked = false;
	protected Byte2BooleanOpenHashMap enabledArenas = new Byte2BooleanOpenHashMap();
	private ItemStack displayItem = ItemStacks.WOOD_AXE;

	private MatchData() {

		if (!GametypeManager.getGametypes().isEmpty())
			setGametype(GametypeManager.getGametypes().values().iterator().next());

		this.knockback = KnockbackProfileList.getDefaultKnockbackProfile();
	}

	public MatchData(Queuetype queuetype, Gametype gametype, QueueSettings queueSettings) {
		this();
		setQueuetype(queuetype);
		setGametype(gametype);
		this.queueAndGameTypeHash = (short) (queuetype.getId() << 8 | gametype.getId());
		this.enabledArenas = new Byte2BooleanOpenHashMap(queueSettings.getEnabledArenas());
	}

	public MatchData(UUID queueEntryId) {
		this();
		Queuetype queuetype = QueuetypeManager.getQueuetypes().get(QueueSettings.getQueueTypeId(queueEntryId));
		Gametype gametype = GametypeManager.getGametypes().get(QueueSettings.getGameTypeId(queueEntryId));
		setQueuetype(queuetype);
		setGametype(gametype);
		this.queueAndGameTypeHash = (short) (queuetype.getId() << 8 | gametype.getId());

	}

	@Nullable
	public Gametype getGametype() {
		return queueAndGameTypeHash == -1 ? null
				: GametypeManager.getGametypes().get((byte) (queueAndGameTypeHash & 0xFF));
	}

	@Nullable
	public Queuetype getQueuetype() {
		return queueAndGameTypeHash == -1 ? null
				: QueuetypeManager.getQueuetypes().get((byte) (queueAndGameTypeHash >> 8));
	}

	public MatchData(DuelSettings duelSettings) {
		this();
		Queuetype queuetype = duelSettings.getQueuetype();
		Gametype gametype = duelSettings.getGametype();
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
		this.knockback = queuetype.getKnockback();
	}

	public void setGametype(@NonNull Gametype gametype) {
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
		Queuetype queuetype = getQueuetype();
		Gametype gametype = getGametype();

		if (queuetype == null || gametype == null)
			return new Int2ObjectOpenHashMap<>();
		return p.getCustomKits(queuetype, gametype);
	}

	public int getElo(Profile p) {
		Gametype gametype = getGametype();

		if (gametype == null)
			return 0;
		return gametype.getElo(p);
	}
}
