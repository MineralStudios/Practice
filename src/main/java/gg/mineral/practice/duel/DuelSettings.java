package gg.mineral.practice.duel;

import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.api.knockback.Knockback;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemStacks;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
@Setter
public class DuelSettings {
    private Gametype gametype;
    private Queuetype queuetype;
    private byte arenaId;
    private Kit kit;
    private Knockback knockback;
    private int noDamageTicks = 20, pearlCooldown = 15;
    private boolean hunger = true, boxing = false, build = false, damage = true, griefing = false, deadlyWater = false,
            regeneration = true;
    private ItemStack displayItem = ItemStacks.WOOD_AXE;

    public DuelSettings() {
        val defaultGametype = GametypeManager.getGametypes().get((byte) 0);
        if (defaultGametype != null)
            setGametype(defaultGametype);
        else
            throw new IllegalStateException("No default gametype found.");

        val defaultQueuetype = QueuetypeManager.getQueuetypes().get((byte) 0);

        if (defaultQueuetype != null)
            setQueuetype(defaultQueuetype);
        else
            throw new IllegalStateException("No default queuetype found.");
    }

    public DuelSettings(Queuetype queuetype, Gametype gametype) {
        setQueuetype(queuetype);
        setGametype(gametype);
    }

    private void setQueuetype(@NonNull Queuetype queuetype) {
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
}
