package gg.mineral.practice.listeners;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutBed;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutCollect;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityPainting;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;

import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;

public class PacketListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = ProfileManager.getOrCreateProfile(event.getPlayer());
        injectPlayer(profile);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    protected void injectPlayer(Profile profile) {

        profile.getPlayer().getHandle().playerConnection.getOutgoingPacketListeners().add(packet -> {
            if (packet instanceof PacketPlayOutNamedEntitySpawn namedEntitySpawn) {
                UUID uuid = namedEntitySpawn.getB();
                if (!profile.testVisibility(uuid)) {
                    profile.getVisiblePlayers().remove(uuid);
                    return true;
                }
                profile.getVisiblePlayers().add(uuid);
            }

            if (packet instanceof PacketPlayOutEntityDestroy destroy)
                for (int id : destroy.getA())
                    for (UUID uuid : profile.getVisiblePlayers())
                        if (profile.getPlayer().getHandle().getId() == id)
                            if (!profile.testVisibility(uuid))
                                profile.getVisiblePlayers().remove(uuid);
                            else
                                return true;

            if (packet instanceof PacketPlayOutPlayerInfo playerInfo) {
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = playerInfo.getA();
                Iterator<PlayerInfoData> data = playerInfo.getB().iterator();

                while (data.hasNext()) {
                    PlayerInfoData playerInfoData = data.next();

                    if (playerInfoData == null)
                        continue;
                    UUID uuid = playerInfoData.a().getId();

                    if (!profile.testTabVisibility(uuid)
                            && action != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                        profile.getVisiblePlayersOnTab().remove(uuid);
                        data.remove();
                        continue;
                    }

                    if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER)
                        profile.getVisiblePlayersOnTab().add(uuid);
                    else if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER)
                        profile.getVisiblePlayersOnTab().remove(uuid);
                    else if (!profile.getVisiblePlayersOnTab().contains(uuid))
                        data.remove();
                }

                if (playerInfo.getB().isEmpty())
                    return true;
            }

            if (packet instanceof PacketPlayOutNamedSoundEffect soundEffect) {
                String sound = soundEffect.getA();

                if (sound == null)
                    return false;
                if (sound.equals("RANDOM.bow") || sound.equals("RANDOM.bowhit") || sound.equals("RANDOM.pop")
                        || sound.equals("game.player.hurt")) {

                    int x = soundEffect.getB(), y = soundEffect.getC(), z = soundEffect.getD();

                    boolean isVisible = false, isInMatch = false;

                    for (Entity entity : profile.getPlayer().getWorld().getEntitiesByClasses(Player.class,
                            Projectile.class)) {
                        if (!(entity instanceof Player) && !(entity instanceof Projectile))
                            continue;

                        Player player = null;
                        Location location = entity.getLocation();

                        if (entity instanceof Player)
                            player = (Player) entity;

                        if (entity instanceof Projectile projectile)
                            if (projectile.getShooter() instanceof Player)
                                player = (Player) projectile.getShooter();

                        if (player == null)
                            continue;

                        boolean one = (location.getX() * 8.0D) == x;
                        boolean two = (location.getY() * 8.0D) == y;
                        boolean three = (location.getZ() * 8.0D) == z;

                        if (!one || !two || !three)
                            continue;

                        boolean pass = false;

                        switch (sound) {
                            case "RANDOM.bow": {
                                ItemStack hand = player.getItemInHand();
                                if (hand == null)
                                    break;
                                if (hand.getType() == Material.POTION || hand.getType() == Material.BOW
                                        || hand.getType() == Material.ENDER_PEARL) {
                                    pass = true;
                                }
                                break;
                            }
                            case "RANDOM.bowhit": {
                                if (entity instanceof Arrow) {
                                    pass = true;
                                    break;
                                }
                            }
                            default: {
                                if (entity instanceof Player) {
                                    pass = true;
                                    break;
                                }
                            }
                        }

                        System.out.println("Pass: " + pass);

                        if (pass) {
                            isInMatch = true;
                            if (profile.getVisiblePlayers().contains(player.getUniqueId()))
                                isVisible = true;
                        }

                        System.out.println("IsInMatch: " + isInMatch);
                        System.out.println("IsVisible: " + isVisible);
                    }

                    if (isInMatch && !isVisible)
                        return true;
                }
            }

            if (packet instanceof PacketPlayOutWorldEvent worldEvent) {
                int effect = worldEvent.getA();
                if (effect != 2002)
                    return false;

                BlockPosition position = worldEvent.getB();

                int x = position.getX();
                int y = position.getY();
                int z = position.getZ();

                boolean isVisible = false;
                boolean isInMatch = false;

                for (ThrownPotion potion : profile.getPlayer().getWorld().getEntitiesByClass(ThrownPotion.class)) {
                    int potionX = MathHelper.floor(x);
                    int potionY = MathHelper.floor(y);
                    int potionZ = MathHelper.floor(z);

                    if (!(potion.getShooter() instanceof Player))
                        continue;
                    if (x != potionX || y != potionY || z != potionZ)
                        continue;

                    isInMatch = true;
                    Player shooter = (Player) potion.getShooter();
                    if (profile.getVisiblePlayers().contains(shooter.getUniqueId()))
                        isVisible = true;
                }

                if (isInMatch && !isVisible)
                    return true;

            }

            if (packet instanceof PacketPlayOutEntityEquipment equipment) {

            }

            if (packet instanceof PacketPlayOutBed bed) {

            }

            if (packet instanceof PacketPlayOutAnimation animation) {

            }

            if (packet instanceof PacketPlayOutCollect collect) {

            }

            if (packet instanceof PacketPlayOutSpawnEntity spawnEntity) {

            }

            if (packet instanceof PacketPlayOutSpawnEntityLiving spawnEntityLiving) {

            }

            if (packet instanceof PacketPlayOutSpawnEntityPainting spawnEntityPainting) {

            }

            if (packet instanceof PacketPlayOutSpawnEntityExperienceOrb spawnEntityExperienceOrb) {

            }

            if (packet instanceof PacketPlayOutEntityVelocity entityVelocity) {

            }

            if (packet instanceof PacketPlayOutEntity entity) {

            }

            if (packet instanceof PacketPlayOutEntityTeleport entityTeleport) {

            }

            if (packet instanceof PacketPlayOutEntityHeadRotation entityHeadRotation) {

            }

            if (packet instanceof PacketPlayOutEntityHeadRotation entityHeadRotation) {

            }

            if (packet instanceof PacketPlayOutEntityStatus entityStatus) {

            }

            if (packet instanceof PacketPlayOutAttachEntity attachEntity) {

            }

            if (packet instanceof PacketPlayOutEntityMetadata entityMetadata) {

            }

            if (packet instanceof PacketPlayOutEntityEffect entityEffect) {

            }

            if (packet instanceof PacketPlayOutRemoveEntityEffect removeEntityEffect) {

            }

            if (packet instanceof PacketPlayOutBlockBreakAnimation blockBreakAnimation) {

            }

            return false;
        });
    }

    protected void removePlayer(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.getOutgoingPacketListeners().clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player receiver = event.getPlayer();
        Item item = event.getItem();

        UUID dropper = getPlayerWhoDropped(item);
        if (dropper == null)
            return;

        Profile receiverProfile = ProfileManager.getOrCreateProfile(receiver);

        if (!receiverProfile.getVisiblePlayers().contains(dropper))
            event.setCancelled(true);
    }

    private UUID getPlayerWhoDropped(Item item) {
        if (((CraftEntity) item).getHandle() instanceof EntityItem entityItem)
            return entityItem.lastHolder.getUniqueID();

        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(PlayerPickupItemEvent event) {
        Player receiver = event.getPlayer();

        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.ARROW)
            return;

        Entity entity = ((CraftEntity) item).getHandle().getBukkitEntity();
        if (!(entity instanceof Arrow))
            return;

        Arrow arrow = (Arrow) entity;
        if (!(arrow.getShooter() instanceof Player))
            return;

        Player shooter = (Player) arrow.getShooter();
        Profile receiverProfile = ProfileManager.getOrCreateProfile(receiver);
        if (!receiverProfile.getVisiblePlayers().contains(shooter.getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        if (!(potion.getShooter() instanceof Player))
            return;

        Player shooter = (Player) potion.getShooter();

        for (LivingEntity livingEntity : event.getAffectedEntities()) {
            if (!(livingEntity instanceof Player))
                return;

            Player receiver = (Player) livingEntity;
            Profile receiverProfile = ProfileManager.getOrCreateProfile(receiver);
            if (!receiverProfile.getVisiblePlayers().contains(shooter.getUniqueId())) {
                event.setCancelled(true);
                event.setIntensity(receiver, 0.0D);
            }
        }
    }
}