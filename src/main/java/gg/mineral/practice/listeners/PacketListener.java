package gg.mineral.practice.listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import lombok.val;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
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

public class PacketListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        val profile = ProfileManager.getOrCreateProfile(event.getPlayer());
        injectPlayer(profile);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    protected void injectPlayer(Profile profile) {

        val handle = profile.getPlayer().getHandle();
        val playerConnection = handle.playerConnection;

        playerConnection.getIncomingPacketListeners().add(packet -> {
            if (packet instanceof PacketPlayInSteerVehicle)
                if (profile.isInMatchCountdown())
                    return true;

            return false;
        });

        playerConnection.getOutgoingPacketListeners().add(packet -> {
            if (packet instanceof PacketPlayOutNamedEntitySpawn namedEntitySpawn) {
                val uuid = namedEntitySpawn.getB();
                val entityID = namedEntitySpawn.getA();
                if (!uuid.equals(profile.getUuid()) && !profile.getSetVisiblePlayers().contains(uuid))
                    return true;

                profile.getVisiblePlayers().put(entityID, uuid);
            }

            if (packet instanceof PacketPlayOutEntityDestroy destroy)
                for (int id : destroy.getA()) {
                    if (id == profile.getRidingEntityID())
                        profile.setRidingEntityID(-1);
                    profile.getVisiblePlayers().remove(id);
                }

            if (packet instanceof PacketPlayOutPlayerInfo playerInfo) {
                val action = playerInfo.getA();
                val data = playerInfo.getB().iterator();

                while (data.hasNext()) {
                    val playerInfoData = data.next();

                    if (playerInfoData == null)
                        continue;
                    val uuid = playerInfoData.a().getId();

                    if (!uuid.equals(profile.getUuid()) && !profile.getSetVisiblePlayersOnTab().contains(uuid)
                            && action != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                        data.remove();
                        continue;
                    }

                    if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER) {
                        profile.getVisiblePlayersOnTab().add(uuid);
                    } else if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                        profile.getVisiblePlayersOnTab().remove(uuid);
                    } else if (!uuid.equals(profile.getUuid()) && !profile.getVisiblePlayersOnTab().contains(uuid))
                        data.remove();
                }

                if (playerInfo.getB().isEmpty())
                    return true;
            }

            if (packet instanceof PacketPlayOutNamedSoundEffect soundEffect) {
                val sound = soundEffect.getA();

                if (sound == null)
                    return false;

                if (sound.equals("RANDOM.bow") || sound.equals("RANDOM.bowhit") || sound.equals("RANDOM.pop")
                        || sound.equals("game.player.hurt")) {

                    int x = soundEffect.getB(), y = soundEffect.getC(), z = soundEffect.getD();

                    boolean isVisible = false, isInMatch = false;

                    for (val entity : profile.getPlayer().getWorld().getEntitiesByClasses(Player.class,
                            Projectile.class)) {
                        if (!(entity instanceof Player) && !(entity instanceof Projectile))
                            continue;

                        Player player = null;
                        val location = entity.getLocation();

                        if (entity instanceof Player)
                            player = (Player) entity;

                        if (entity instanceof Projectile projectile)
                            if (projectile.getShooter() instanceof Player)
                                player = (Player) projectile.getShooter();

                        if (player == null)
                            continue;

                        boolean one = (int) (location.getX() * 8.0D) == x;
                        boolean two = (int) (location.getY() * 8.0D) == y;
                        boolean three = (int) (location.getZ() * 8.0D) == z;

                        if (!one || !two || !three)
                            continue;

                        boolean pass = false;

                        switch (sound) {
                            case "RANDOM.bow": {
                                val hand = player.getItemInHand();
                                if (hand == null)
                                    break;
                                if (hand.getType() == Material.POTION || hand.getType() == Material.BOW
                                        || hand.getType() == Material.ENDER_PEARL)
                                    pass = true;

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

                        if (pass) {
                            isInMatch = true;
                            if (player.getUniqueId().equals(profile.getUuid())
                                    || profile.getVisiblePlayers().containsValue(player.getUniqueId())) {
                                isVisible = true;
                                break;
                            }
                        }
                    }

                    if (isInMatch && !isVisible)
                        return true;
                }
            }

            if (packet instanceof PacketPlayOutWorldEvent worldEvent) {
                int effect = worldEvent.getA();
                if (effect != 2002)
                    return false;

                val position = worldEvent.getB();

                int x = position.getX(), y = position.getY(), z = position.getZ();

                boolean isVisible = false, isInMatch = false;

                for (val projectile : profile.getPlayer().getWorld().getEntitiesByClass(Projectile.class)) {
                    int projectileX = MathHelper.floor(x);
                    int projectileY = MathHelper.floor(y);
                    int projectileZ = MathHelper.floor(z);

                    if (!(projectile.getShooter() instanceof Player))
                        continue;
                    if (x != projectileX || y != projectileY || z != projectileZ)
                        continue;

                    isInMatch = true;
                    val shooter = (Player) projectile.getShooter();
                    if (shooter.getUniqueId().equals(profile.getUuid())
                            || profile.getVisiblePlayers().containsValue(shooter.getUniqueId())) {
                        isVisible = true;
                        break;
                    }
                }

                if (isInMatch && !isVisible)
                    return true;

            }

            if (packet instanceof PacketPlayOutEntityEquipment equipment) {
                int entityId = equipment.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutBed bed) {
                int entityId = bed.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutAnimation animation) {
                int entityId = animation.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutCollect collect) {
                int entityId = collect.getB();

                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutSpawnEntity spawnEntity) {
                int x = spawnEntity.getB() / 32, y = spawnEntity.getC() / 32, z = spawnEntity.getD() / 32;

                boolean isVisible = false, isInMatch = false;

                for (val projectile : profile.getPlayer().getWorld().getEntitiesByClass(Projectile.class)) {
                    int projectileX = MathHelper.floor(x);
                    int projectileY = MathHelper.floor(y);
                    int projectileZ = MathHelper.floor(z);

                    if (!(projectile.getShooter() instanceof Player))
                        continue;
                    if (x != projectileX || y != projectileY || z != projectileZ)
                        continue;

                    isInMatch = true;
                    val shooter = (Player) projectile.getShooter();
                    if (shooter.getUniqueId().equals(profile.getUuid())
                            || profile.getVisiblePlayers().containsValue(shooter.getUniqueId())) {
                        isVisible = true;
                        break;
                    }
                }

                if (isInMatch && !isVisible)
                    return true;
            }

            if (packet instanceof PacketPlayOutSpawnEntityLiving spawnEntityLiving) {
                int entityId = spawnEntityLiving.getA();

                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutSpawnEntityPainting spawnEntityPainting) {
                val position = spawnEntityPainting.getB();
                int x = position.getX(), y = position.getY(), z = position.getZ();

                boolean isVisible = false, isInMatch = false;

                for (val player : profile.getPlayer().getWorld().getEntitiesByClass(Player.class)) {
                    val playerLocation = player.getLocation();

                    int range = 5;
                    if (playerLocation.distance(new Location(player.getWorld(), x, y, z)) > range)
                        continue;

                    val directionToPainting = new Vector(x - playerLocation.getX(), y - playerLocation.getY(),
                            z - playerLocation.getZ()).normalize();
                    float playerYaw = playerLocation.getYaw();
                    val playerFacingDirection = new Vector(-Math.sin(Math.toRadians(playerYaw)), 0,
                            Math.cos(Math.toRadians(playerYaw))).normalize();

                    if (directionToPainting.dot(playerFacingDirection) < 0.5)
                        continue;

                    isInMatch = true;
                    if (player.getUniqueId().equals(profile.getUuid())
                            || profile.getVisiblePlayers().containsValue(player.getUniqueId())) {
                        isVisible = true;
                        break;
                    }
                }

                if (isInMatch && !isVisible)
                    return true;
            }

            if (packet instanceof PacketPlayOutSpawnEntityExperienceOrb spawnEntityExperienceOrb) {
                int x = spawnEntityExperienceOrb.getB() / 32, y = spawnEntityExperienceOrb.getC() / 32,
                        z = spawnEntityExperienceOrb.getD() / 32;

                boolean isVisible = false, isInMatch = false;

                for (val player : profile.getPlayer().getWorld().getEntitiesByClass(Player.class)) {
                    val playerLocation = player.getLocation();

                    int range = 5;
                    if (playerLocation.distance(new Location(player.getWorld(), x, y, z)) > range)
                        continue;

                    isInMatch = true;
                    if (player.getUniqueId().equals(profile.getUuid())
                            || profile.getVisiblePlayers().containsValue(player.getUniqueId())) {
                        isVisible = true;
                        break;
                    }
                }

                if (isInMatch && !isVisible)
                    return true;
            }

            if (packet instanceof PacketPlayOutEntityVelocity entityVelocity) {
                int entityId = entityVelocity.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutEntity entityPacket) {
                int entityId = entityPacket.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutEntityTeleport entityTeleport) {
                int entityId = entityTeleport.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutEntityHeadRotation entityHeadRotation) {
                int entityId = entityHeadRotation.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutEntityStatus entityStatus) {
                int entityId = entityStatus.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutAttachEntity attachEntity) {
                int entityId = attachEntity.getB();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutEntityMetadata entityMetadata) {
                int entityId = entityMetadata.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutEntityEffect entityEffect) {
                int entityId = entityEffect.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutRemoveEntityEffect removeEntityEffect) {
                int entityId = removeEntityEffect.getA();
                if (entityId == handle.getId())
                    return false;
                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            if (packet instanceof PacketPlayOutBlockBreakAnimation blockBreakAnimation) {
                int entityId = blockBreakAnimation.getA();

                if (entityId == handle.getId())
                    return false;

                val entity = handle.getWorld().a(entityId);

                if (entity == null)
                    return false;

                if (!profile.getVisiblePlayers().containsValue(entity.getUniqueID()))
                    return true;
            }

            return false;
        });
    }

    protected void removePlayer(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.getOutgoingPacketListeners().clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        val receiver = event.getPlayer();
        val item = event.getItem();

        val dropper = getPlayerWhoDropped(item);
        if (dropper == null)
            return;

        if (dropper.equals(receiver.getUniqueId()))
            return;

        val receiverProfile = ProfileManager.getProfile(receiver);

        if (receiverProfile == null || !receiverProfile.getVisiblePlayers().containsValue(dropper))
            event.setCancelled(true);
    }

    private UUID getPlayerWhoDropped(Item item) {
        if (((CraftEntity) item).getHandle() instanceof EntityItem entityItem && entityItem.lastHolder != null)
            return entityItem.lastHolder.getUniqueID();

        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(PlayerPickupItemEvent event) {
        val receiver = event.getPlayer();

        val item = event.getItem();
        if (item.getItemStack().getType() != Material.ARROW)
            return;

        val entity = ((CraftEntity) item).getHandle().getBukkitEntity();
        if (!(entity instanceof Arrow))
            return;

        val arrow = (Arrow) entity;
        if (!(arrow.getShooter() instanceof Player))
            return;

        val shooter = (Player) arrow.getShooter();
        val receiverProfile = ProfileManager.getProfile(receiver);
        if (receiverProfile == null || !receiverProfile.getVisiblePlayers().containsValue(shooter.getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event) {
        val potion = event.getEntity();
        if (potion.getShooter() instanceof Player shooter) {
            for (val livingEntity : event.getAffectedEntities()) {
                if (livingEntity instanceof Player receiver) {
                    val receiverProfile = ProfileManager.getProfile(receiver);
                    if (receiverProfile == null
                            || !receiverProfile.getVisiblePlayers().containsValue(shooter.getUniqueId())) {
                        event.setCancelled(true);
                        event.setIntensity(receiver, 0.0D);
                    }
                }
            }
        }
    }
}