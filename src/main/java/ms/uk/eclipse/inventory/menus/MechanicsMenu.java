package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.inventory.SubmitAction;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.match.MatchData;
import ms.uk.eclipse.match.PartyMatch;
import ms.uk.eclipse.party.Party;
import ms.uk.eclipse.tasks.MenuTask;
import ms.uk.eclipse.tournaments.Tournament;

public class MechanicsMenu extends Menu {
        SubmitAction action;

        public MechanicsMenu(SubmitAction action) {
                super(new StrikingMessage("Game Mechanics", CC.PRIMARY, true));
                setClickCancelled(true);
                this.action = action;
        }

        @Override
        public boolean update() {
                MatchData match = viewer.getMatchData();
                ItemStack kit = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                                .lore(new ChatMessage(match.getKitName(), CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Select Kit", CC.PRIMARY, false).toString()).build();
                ItemStack kb = new ItemBuilder(Material.STICK)
                                .lore(new ChatMessage(match.getKnockback().getName(), CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Change Knockback", CC.PRIMARY, false).toString()).build();
                ItemStack hitDelay = new ItemBuilder(Material.WATCH)
                                .lore(new ChatMessage(match.getNoDamageTicks() + " Ticks", CC.SECONDARY, false)
                                                .toString())
                                .name(new ChatMessage("Hit Delay", CC.PRIMARY, false).toString()).build();
                ItemStack hunger = new ItemBuilder(Material.COOKED_BEEF)
                                .lore(new ChatMessage(match.getHunger() + "", CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Toggle Hunger", CC.PRIMARY, false).toString()).build();
                ItemStack deadlyWater = new ItemBuilder(Material.BLAZE_ROD)
                                .lore(new ChatMessage(match.getDeadlyWater() + "", CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Deadly Water", CC.PRIMARY, false).toString()).build();
                ItemStack build = new ItemBuilder(Material.BRICK)
                                .lore(new ChatMessage(match.getBuild() + "", CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Toggle Build", CC.PRIMARY, false).toString()).build();
                ItemStack damage = new ItemBuilder(Material.DIAMOND_AXE)
                                .lore(new ChatMessage(match.getDamage() + "", CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Toggle Damage", CC.PRIMARY, false).toString()).build();
                ItemStack griefing = new ItemBuilder(Material.TNT)
                                .lore(new ChatMessage(match.getGriefing() + "", CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Toggle Griefing", CC.PRIMARY, false).toString()).build();
                ItemStack pearlcd = new ItemBuilder(Material.ENDER_PEARL)
                                .lore(new ChatMessage(match.getPearlCooldown() + " Seconds", CC.SECONDARY, false)
                                                .toString())
                                .name(new ChatMessage("Pearl Cooldown", CC.PRIMARY, false).toString()).build();
                ItemStack arena = new ItemBuilder(Material.WATER_LILY)
                                .lore(new ChatMessage(match.getArena().getName(), CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Arena", CC.PRIMARY, false).toString()).build();
                ItemStack regen = new ItemBuilder(Material.GOLDEN_APPLE)
                                .lore(new ChatMessage(match.getRegeneration() + "", CC.SECONDARY, false).toString())
                                .name(new ChatMessage("Regeneration", CC.PRIMARY, false).toString()).build();
                ItemStack sendDuel = new ItemBuilder(Material.STICK)
                                .name(new ChatMessage("Submit", CC.PRIMARY, false).toString()).build();
                ItemStack resetMeta = new ItemBuilder(Material.PAPER)
                                .name(new ChatMessage("Reset Settings", CC.PRIMARY, false).toString()).build();
                setSlot(10, kit, new MenuTask(new SelectKitMenu(this)));
                setSlot(11, kb, new MenuTask(new SelectKnockbackMenu(this)));
                setSlot(12, hitDelay, new MenuTask(new HitDelayMenu(this)));
                MechanicsMenu menu = this;
                Thread hungerTask = new Thread() {
                        @Override
                        public void run() {
                                match.setHunger(!match.getHunger());
                                viewer.openMenu(menu);
                        }
                };
                setSlot(13, hunger, hungerTask);
                Thread buildTask = new Thread() {
                        @Override
                        public void run() {
                                match.setBuild(!match.getBuild());
                                viewer.openMenu(menu);
                        }
                };
                setSlot(14, build, buildTask);
                Thread damageTask = new Thread() {
                        @Override
                        public void run() {
                                match.setDamage(!match.getDamage());
                                viewer.openMenu(menu);
                        }
                };
                setSlot(15, damage, damageTask);
                Thread griefingTask = new Thread() {
                        @Override
                        public void run() {
                                match.setGriefing(!match.getGriefing());
                                viewer.openMenu(menu);
                        }
                };
                setSlot(16, griefing, griefingTask);
                setSlot(19, pearlcd, new MenuTask(new PearlCooldownMenu(this)));
                setSlot(20, arena, new MenuTask(new SelectArenaMenu(this, action)));
                Thread deadlyWaterTask = new Thread() {
                        @Override
                        public void run() {
                                match.setDeadlyWater(!match.getDeadlyWater());
                                viewer.openMenu(menu);
                        }
                };
                setSlot(21, deadlyWater, deadlyWaterTask);
                Thread regenTask = new Thread() {
                        @Override
                        public void run() {
                                match.setRegeneration(!match.getRegeneration());
                                viewer.openMenu(menu);
                        }
                };
                setSlot(22, regen, regenTask);
                Thread submitTask = new Thread() {
                        @Override
                        public void run() {
                                viewer.sendDuelRequest(viewer.getDuelReciever());
                        }
                };

                if (action == SubmitAction.P_SPLIT) {
                        submitTask = new Thread() {
                                @Override
                                public void run() {
                                        viewer.bukkit().closeInventory();
                                        Party p = viewer.getParty();

                                        if (!viewer.getParty().getPartyLeader().equals(viewer)) {
                                                viewer.message(new ErrorMessage("You must be the party leader"));
                                                return;
                                        }

                                        if (p.getPartyMembers().size() < 2) {
                                                viewer.message(new ErrorMessage(
                                                                "You need at least 2 people in a party"));
                                                return;
                                        }

                                        PartyMatch m = new PartyMatch(p, viewer.getMatchData());
                                        m.start();
                                }
                        };
                } else if (action == SubmitAction.TOURNAMENT) {
                        submitTask = new Thread() {
                                @Override
                                public void run() {
                                        viewer.bukkit().closeInventory();
                                        Tournament t = new Tournament(viewer);
                                        t.start();
                                }
                        };
                }

                setSlot(31, sendDuel, submitTask);
                Thread resetTask = new Thread() {
                        @Override
                        public void run() {
                                viewer.resetMatchData();
                                viewer.openMenu(menu);
                        }
                };
                setSlot(27, resetMeta, resetTask);
                return true;
        }

        public SubmitAction getAction() {
                return action;
        }
}
