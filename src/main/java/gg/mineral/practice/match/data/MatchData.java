package gg.mineral.practice.match.data;

import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.knockback.Knockback;
import gg.mineral.practice.duel.DuelSettings;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.match.OldStyleKnockback;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.queue.QueueSettings.QueueEntry;
import gg.mineral.practice.util.items.ItemStacks;
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
	@NonNull
	private Kit kit;
	@Nullable
	private Knockback knockback;
	private int noDamageTicks = 20, pearlCooldown = 15;
	@Setter
	private boolean hunger = true, boxing = false, build = false, damage = true, griefing = false, deadlyWater = false,
			regeneration = true, oldCombat = false;
	private boolean ranked = false;
	protected Byte2BooleanOpenHashMap enabledArenas = new Byte2BooleanOpenHashMap();
	private ItemStack displayItem = ItemStacks.WOOD_AXE;

	private MatchData() {

		if (!GametypeManager.getGametypes().isEmpty())
			setGametype(GametypeManager.getGametypes().values().iterator().next());
	}

	public MatchData(Queuetype queuetype, Gametype gametype, QueueSettings queueSettings) {
		this();
		setQueuetype(queuetype);
		setGametype(gametype);
		this.oldCombat = queueSettings.isOldCombat();
		if (knockback == null && this.oldCombat)
			this.knockback = new OldStyleKnockback();
		this.enabledArenas = new Byte2BooleanOpenHashMap(queueSettings.getEnabledArenas());
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
		this.kit = duelSettings.getKit() == null ? GametypeManager.getGametypes().get((byte) 0).getKit()
				: duelSettings.getKit();
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
		this.oldCombat = duelSettings.isOldCombat();
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
