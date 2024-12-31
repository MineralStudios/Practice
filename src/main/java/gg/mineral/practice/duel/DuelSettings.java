package gg.mineral.practice.duel;

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
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

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
            regeneration = true, oldCombat = false;
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

        sb.append(CC.GREEN).append("Kit: ").append(kit.getName());
        sb.append(newLine);
        sb.append(CC.GREEN).append("Arena: ").append(arena.getDisplayName());
        sb.append(newLine);
        sb.append(CC.GREEN).append("Knockback: ").append(knockback.getName());
        sb.append(newLine);
        sb.append(CC.GREEN).append("Hit Delay: ").append(noDamageTicks);
        sb.append(newLine);
        sb.append(CC.GREEN).append("Hunger: ").append(hunger);
        sb.append(newLine);
        sb.append(CC.GREEN).append("Build: ").append(build);
        sb.append(newLine);
        sb.append(CC.GREEN).append("Damage: ").append(damage);
        sb.append(newLine);
        sb.append(CC.GREEN).append("Griefing: ").append(griefing);
        sb.append(newLine);
        sb.append(CC.GREEN).append("Deadly Water: ").append(deadlyWater);
        sb.append(newLine);
        sb.append(CC.GREEN).append("Regeneration: ").append(regeneration);
        // TODO 2v2 with bots in /duel
        // sb.append(newLine);
        // sb.append(CC.GREEN + "2v2: " + team2v2);
        // sb.append(newLine);
        // sb.append(CC.GREEN + "Bots: " + bots);
        sb.append(newLine);
        sb.append(CC.GREEN).append("Boxing: ").append(boxing);
        sb.append(newLine);
        sb.append(CC.GREEN).append("Pearl Cooldown: ").append(pearlCooldown).append(" seconds");
        return sb.toString();
    }
}
