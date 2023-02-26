package gg.mineral.practice.inventory;

import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.ContainerAnvil;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.Slot;

public class AnvilMenu implements Menu {

    Int2ObjectOpenHashMap<Consumer<Interaction>> dataMap = new Int2ObjectOpenHashMap<>();
    Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();
    @Setter
    @Getter
    Boolean clickCancelled = false;

    /**
     * The {@link Plugin} that this anvil GUI is associated with
     */
    private final Plugin plugin;
    /**
     * The player who has the GUI open
     */
    private final Player player;
    /**
     * A state that decides where the anvil GUI is able to get closed by the user
     */
    private final boolean preventClose;

    /**
     * An {@link Consumer} that is called when the anvil GUI is closed
     */
    private final Consumer<Player> closeListener;

    /**
     * The container id of the inventory, used for NMS methods
     */
    private int containerId;

    /**
     * The inventory that is used on the Bukkit side of things
     */
    @Getter
    private Inventory inventory;
    /**
     * The listener holder class
     */
    private final ListenUp listener = new ListenUp();

    /**
     * Represents the state of the inventory being open
     */
    private boolean open;

    /**
     * Create an AnvilGUI and open it for the player.
     *
     * @param plugin           A {@link org.bukkit.plugin.java.JavaPlugin} instance
     * @param player           The {@link Player} to open the inventory for
     * @param inventoryTitle   What to have the text already set to
     * @param initialContents  The initial contents of the inventory
     * @param preventClose     Whether to prevent the inventory from closing
     * @param closeListener    A {@link Consumer} when the inventory closes
     * @param completeFunction A {@link BiFunction} that is called when the player
     *                         clicks the {@link Slot#OUTPUT} slot
     */
    private AnvilMenu(
            Plugin plugin,
            Player player,
            String inventoryTitle,
            ItemStack[] initialContents,
            boolean preventClose,
            Set<Integer> interactableSlots,
            Consumer<Player> closeListener,
            Consumer<Player> inputLeftClickListener,
            Consumer<Player> inputRightClickListener) {
        this.plugin = plugin;
        this.player = player;
        this.preventClose = preventClose;
        this.closeListener = closeListener;

        openInventory();
    }

    /**
     * Opens the anvil GUI
     */
    private void openInventory() {
        handleInventoryCloseEvent(player);
        setActiveContainerDefault(player);

        Bukkit.getPluginManager().registerEvents(listener, plugin);

        final Object container = newContainerAnvil(player);

        inventory = toBukkitInventory(container);
        // We need to use setItem instead of setContents because a Minecraft
        // ContainerAnvil
        // contains two separate inventories: the result inventory and the ingredients
        // inventory.
        // The setContents method only updates the ingredients inventory unfortunately,
        // but setItem handles the index going into the result inventory.
        for (Entry<Integer, ItemStack> e : items.entrySet()) {
            inventory.setItem(e.getKey(), e.getValue());
        }

        containerId = getNextContainerId(player, container);
        sendPacketOpenWindow(player, containerId);
        setActiveContainer(player, container);
        setActiveContainerId(container, containerId);
        addActiveContainerSlotListener(container, player);

        open = true;
    }

    /**
     * Closes the inventory if it's open.
     */
    public void closeInventory() {
        closeInventory(true);
    }

    /**
     * Closes the inventory if it's open, only sending the close inventory packets
     * if the arg is true
     *
     * @param sendClosePacket Whether to send the close inventory event, packet, etc
     */
    private void closeInventory(boolean sendClosePacket) {
        if (!open) {
            return;
        }

        open = false;

        HandlerList.unregisterAll(listener);

        if (sendClosePacket) {
            handleInventoryCloseEvent(player);
            setActiveContainerDefault(player);
            sendPacketCloseWindow(player, containerId);
        }

        if (closeListener != null) {
            closeListener.accept(player);
        }
    }

    /**
     * Simply holds the listeners for the GUI
     */
    private class ListenUp implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!event.getInventory().equals(inventory)) {
                return;
            }

            final Player clicker = (Player) event.getWhoClicked();
            // prevent players from merging items from the anvil inventory
            final Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory != null
                    && clickedInventory.equals(clicker.getInventory())
                    && event.getClick().equals(ClickType.DOUBLE_CLICK)) {
                event.setCancelled(true);
                return;
            }

            if (event.getRawSlot() < 3 || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                event.setCancelled(clickCancelled);

                Consumer<Interaction> task = dataMap.get(event.getRawSlot());

                if (task != null) {
                    Profile profile = ProfileManager
                            .getOrCreateProfile((org.bukkit.entity.Player) event.getWhoClicked());
                    task.accept(new Interaction(profile, event.getClick()));
                }

            }
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent event) {
            if (event.getInventory().equals(inventory)) {
                event.setCancelled(clickCancelled);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (open && event.getInventory().equals(inventory)) {
                closeInventory(false);
                if (preventClose) {
                    Bukkit.getScheduler().runTask(plugin, AnvilMenu.this::openInventory);
                }
            }
        }
    }

    /**
     * Class wrapping the values you receive from the onComplete event
     */
    public static final class Completion {

        /**
         * The {@link ItemStack} in the anvilGui slots
         */
        @Getter
        private final ItemStack leftItem, rightItem, outputItem;

        /**
         * The {@link Player} that clicked the output slot
         */
        @Getter
        private final Player player;

        /**
         * The text the player typed into the field
         */
        @Getter
        private final String text;

        /**
         * The event parameter constructor
         * 
         * @param leftItem   The left item in the combine slot of the anvilGUI
         * @param rightItem  The right item in the combine slot of the anvilGUI
         * @param outputItem The item that would have been outputted, when the items
         *                   would have been combined
         * @param player     The player that clicked the output slot
         * @param text       The text the player typed into the rename text field
         */
        public Completion(ItemStack leftItem, ItemStack rightItem, ItemStack outputItem, Player player, String text) {
            this.leftItem = leftItem;
            this.rightItem = rightItem;
            this.outputItem = outputItem;
            this.player = player;
            this.text = text;
        }
    }

    /**
     * {@inheritDoc}
     */

    public int getNextContainerId(Player player, Object container) {
        return toNMS(player).nextContainerCounter();
    }

    /**
     * {@inheritDoc}
     */

    public void handleInventoryCloseEvent(Player player) {
        CraftEventFactory.handleInventoryCloseEvent(toNMS(player));
    }

    /**
     * {@inheritDoc}
     */

    public void sendPacketOpenWindow(Player player, int containerId) {
        toNMS(player).playerConnection
                .sendPacket(new PacketPlayOutOpenWindow(
                        containerId, "minecraft:anvil", new ChatMessage(Blocks.ANVIL.a() + ".name")));
    }

    /**
     * {@inheritDoc}
     */

    public void sendPacketCloseWindow(Player player, int containerId) {
        toNMS(player).playerConnection.sendPacket(new PacketPlayOutCloseWindow(containerId));
    }

    /**
     * {@inheritDoc}
     */

    public void setActiveContainerDefault(Player player) {
        toNMS(player).activeContainer = toNMS(player).defaultContainer;
    }

    /**
     * {@inheritDoc}
     */

    public void setActiveContainer(Player player, Object container) {
        toNMS(player).activeContainer = (Container) container;
    }

    /**
     * {@inheritDoc}
     */

    public void setActiveContainerId(Object container, int containerId) {
        ((Container) container).windowId = containerId;
    }

    /**
     * {@inheritDoc}
     */

    public void addActiveContainerSlotListener(Object container, Player player) {
        ((Container) container).addSlotListener(toNMS(player));
    }

    /**
     * {@inheritDoc}
     */

    public Inventory toBukkitInventory(Object container) {
        return ((Container) container).getBukkitView().getTopInventory();
    }

    /**
     * {@inheritDoc}
     */

    public Object newContainerAnvil(Player player) {
        return new AnvilContainer(toNMS(player));
    }

    /**
     * Turns a {@link Player} into an NMS one
     *
     * @param player The player to be converted
     * @return the NMS EntityPlayer
     */
    private EntityPlayer toNMS(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    /**
     * Modifications to ContainerAnvil that makes it so you don't have to have xp to
     * use this anvil
     */
    private class AnvilContainer extends ContainerAnvil {

        public AnvilContainer(EntityHuman entityhuman) {
            super(entityhuman.inventory, entityhuman.world, new BlockPosition(0, 0, 0), entityhuman);
        }

        public boolean a(EntityHuman human) {
            return true;
        }

        public void b(EntityHuman entityhuman) {
        }
    }

    @Override
    public void setSlot(int slot, ItemStack item) {
        if (item == null || slot < 0) {
            return;
        }

        items.put(slot, item);
    }

    @Override
    public void setSlot(int slot, ItemStack item, Consumer<Interaction> d) {
        dataMap.put(slot, d);
        setSlot(slot, item);
    }

    @Override
    public void setSlot(int slot, ItemStack item, Runnable d) {
        setSlot(slot, item, p -> {
            d.run();
        });
    }

    @Override
    public void add(ItemStack item) {
        int slot = findUnusedSlot();
        setSlot(slot, item);
    }

    public Integer findUnusedSlot() {
        for (int i = 0; i <= 3; i++) {
            if (items.get(i) == null) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void add(ItemStack item, Consumer<Interaction> d) {
        int slot = findUnusedSlot();
        setSlot(slot, item, d);
    }

    @Override
    public void add(ItemStack item, Runnable d) {
        add(item, p -> {
            d.run();
        });
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public void onClose() {

    }

    @Override
    public ItemStack getItemBySlot(int slot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItemBySlot'");
    }

    @Override
    public ItemStack getItemByType(Material m) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItemByType'");
    }

    @Override
    public boolean contains(ItemStack item) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contains'");
    }

    @Override
    public void open(Profile viewer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'open'");
    }

    @Override
    public void reload() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reload'");
    }

    @Override
    public void setContents(ItemStack[] contents) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setContents'");
    }

    @Override
    public Consumer<Interaction> getTask(int slot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTask'");
    }

    @Override
    public void clear() {
        dataMap.clear();
        items.clear();
    }

}
