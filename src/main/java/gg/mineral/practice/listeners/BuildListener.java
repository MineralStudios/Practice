package gg.mineral.practice.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class BuildListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		Profile profile = PlayerManager
				.get(p -> p.getUUID().equals(e.getPlayer().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (profile == null) {
			e.setCancelled(!(e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
			return;
		}

		Match match = profile.getMatch();

		if (match.getBuildLog().contains(e.getBlock().getLocation())) {
			e.setCancelled(!match.getData().getBuild());
			return;
		}

		boolean canBreak = !match.getData().getGriefing();

		e.setCancelled(canBreak);

		if (e.getBlock().getType() == Material.TNT && !canBreak) {
			match.decreasePlacedTnt();
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Profile profile = PlayerManager
				.get(p -> p.getUUID().equals(e.getPlayer().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);
		boolean canPlace = e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		if (profile == null) {
			e.setCancelled(!canPlace);
			return;
		}

		Match match = profile.getMatch();
		e.setCancelled(!match.getData().getBuild());

		if (e.getBlockPlaced().getType() == Material.TNT) {

			if (match.getPlacedTnt() > 128) {
				profile.message(ErrorMessages.MAX_TNT);
				e.setCancelled(true);
				return;
			}

			match.increasePlacedTnt();
		}

		match.getBuildLog().add(e.getBlockPlaced().getLocation());
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
		Profile profile = PlayerManager
				.get(p -> p.getUUID().equals(e.getPlayer().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);
		boolean canPlace = e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		if (profile == null) {
			e.setCancelled(!canPlace);
			return;
		}

		e.setCancelled(!profile.getMatch().getData().getBuild());
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		event.setCancelled(event.getCause() != IgniteCause.FLINT_AND_STEEL);
	}
}
