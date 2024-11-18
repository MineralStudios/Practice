package gg.mineral.practice.duel;

import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.knockback.Knockback;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfileList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

@Getter
@Setter
@NoArgsConstructor
public class DuelSettings {
    @Nullable
    private Gametype gametype;
    @Nullable
    private Queuetype queuetype;
    private byte arenaId;
    @Nullable
    private Kit kit;
    @Nullable
    private Knockback knockback;
    private int noDamageTicks = 20, pearlCooldown = 15;
    private boolean hunger = true, boxing = false, build = false, damage = true, griefing = false, deadlyWater = false,
            regeneration = true;
    private ItemStack displayItem = ItemStacks.WOOD_AXE;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String newLine = CC.R + "\n";

        val arena = ArenaManager.getArenas().get(arenaId);

        val knockback = this.knockback == null
                ? noDamageTicks < 10 ? KnockbackProfileList.getComboKnockbackProfile()
                        : KnockbackProfileList.getDefaultKnockbackProfile()
                : this.knockback;

        val kit = this.kit == null ? GametypeManager.getGametypes().get((byte) 0).getKit() : this.kit;

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
}
