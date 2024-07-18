package gg.mineral.practice.inventory;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.practice.entity.Profile;
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
import net.minecraft.server.v1_8_R3.ICrafting;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;

public abstract class AnvilMenu implements Menu {

    Int2ObjectOpenHashMap<Consumer<Interaction>> dataMap = new Int2ObjectOpenHashMap<>();
    Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();
    @Setter
    @Getter
    boolean closed = true;
    protected Profile viewer;
    @Getter
    @Nullable
    String text;

    /**
     * The container id of the inventory, used for NMS methods
     */
    private int containerId;

    /**
     * The inventory that is used on the Bukkit side of things
     */
    @Getter
    private Inventory inventory;

    public void closeInventory() {
        handleInventoryCloseEvent(viewer.getPlayer());
        setActiveContainerDefault(viewer.getPlayer());
        sendPacketCloseWindow(viewer.getPlayer(), containerId);
    }

    public boolean shouldUpdate() {
        return true;
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

        public void e() {
        }

        public void a(String s) {
            text = s;
        }

        public void a(IInventory iinventory) {
        }

        public void addSlotListener(ICrafting icrafting) {
        }
    }

    @Override
    public void setSlot(int slot, ItemStack item) {
        if (item == null || slot < 0)
            return;

        items.put(slot, item);
    }

    @Override
    public void setSlot(int slot, ItemStack item, Consumer<Interaction> d) {
        dataMap.put(slot, d);
        setSlot(slot, item);
    }

    @Override
    public void setSlot(int slot, ItemStack item, Runnable d) {
        setSlot(slot, item, p -> d.run());
    }

    @Override
    public void add(ItemStack item) {
        int slot = findUnusedSlot();
        setSlot(slot, item);
    }

    public Integer findUnusedSlot() {
        for (int i = 0; i <= 3; i++)
            if (items.get(i) == null)
                return i;

        return -1;
    }

    @Override
    public void add(ItemStack item, Consumer<Interaction> d) {
        int slot = findUnusedSlot();
        setSlot(slot, item, d);
    }

    @Override
    public void add(ItemStack item, Runnable d) {
        add(item, p -> d.run());
    }

    @Override
    public abstract void update();

    @Override
    public void onClose() {

    }

    @Override
    @Nullable
    public ItemStack getItemBySlot(int slot) {
        return items.get(slot);
    }

    @Override
    @Nullable
    public ItemStack getItemByType(Material m) {
        for (ItemStack i : items.values())
            if (i.getType() == m)
                return i;

        return null;
    }

    @Override
    public boolean contains(ItemStack item) {
        return items.containsValue(item);
    }

    @Override
    public void open(Profile viewer) {
        closed = false;
        this.viewer = viewer;

        update();

        handleInventoryCloseEvent(viewer.getPlayer());
        setActiveContainerDefault(viewer.getPlayer());

        final Object container = newContainerAnvil(viewer.getPlayer());

        inventory = toBukkitInventory(container);

        for (it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<ItemStack> e : items.int2ObjectEntrySet())
            inventory.setItem(e.getIntKey(), e.getValue());

        containerId = getNextContainerId(viewer.getPlayer(), container);
        sendPacketOpenWindow(viewer.getPlayer(), containerId);
        setActiveContainer(viewer.getPlayer(), container);
        setActiveContainerId(container, containerId);
        addActiveContainerSlotListener(container, viewer.getPlayer());
        viewer.setOpenMenu(this);
    }

    @Override
    public void reload() {
        open(viewer);
    }

    @Override
    public void setContents(ItemStack[] contents) {
        for (int i = 0; i < contents.length; i++)
            setSlot(i, contents[i]);
    }

    @Override
    public Consumer<Interaction> getTask(int i) {
        return dataMap.get(i);
    }

    @Override
    public void clear() {
        dataMap.clear();
        items.clear();
    }

    @Override
    public boolean isClickCancelled() {
        ClickCancelled annotation = getClass().getAnnotation(ClickCancelled.class);

        if (annotation == null)
            throw new IllegalArgumentException(
                    "ClickCancelled annotation not found on class " + getClass().getSimpleName());

        return annotation.value();
    }

}
