package gg.mineral.practice.duel;

import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.knockback.Knockback;

import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemStacks;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DuelSettings {
    private short queueAndGameTypeHash = -1;
    private byte arenaId;
    private Kit kit;
    private Knockback knockback;
    private int noDamageTicks = 20, pearlCooldown = 15;
    private boolean hunger = true, boxing = false, build = false, damage = true, griefing = false, deadlyWater = false,
            regeneration = true;
    private ItemStack displayItem = ItemStacks.WOOD_AXE;

    public DuelSettings(Queuetype queuetype, Gametype gametype) {
        setQueuetype(queuetype);
        setGametype(gametype);
        this.queueAndGameTypeHash = (short) (queuetype.getId() << 8 | gametype.getId());
    }

    private void setQueuetype(@NonNull Queuetype queuetype) {
        this.knockback = queuetype.getKnockback();
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
}
