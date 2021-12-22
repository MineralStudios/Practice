package ms.uk.eclipse.listeners;

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

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.match.Match;

public class BuildListener implements Listener {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();;

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		Profile player = playerManager.getProfileFromMatch(e.getPlayer());

		if (player == null) {
			e.setCancelled(!(e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
			return;
		}

		Match match = player.getMatch();

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
		Profile player = playerManager.getProfileFromMatch(e.getPlayer());
		boolean canPlace = e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		if (player == null) {
			e.setCancelled(!canPlace);
			return;
		}

		Match match = player.getMatch();
		e.setCancelled(!match.getData().getBuild());

		if (e.getBlockPlaced().getType() == Material.TNT) {

			if (match.getPlacedTnt() > 128) {
				player.message(new ErrorMessage("You have reached the maximum tnt limit"));
				e.setCancelled(true);
				return;
			}

			match.increasePlacedTnt();
		}

		match.getBuildLog().add(e.getBlockPlaced().getLocation());
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
		Profile player = playerManager.getProfileFromMatch(e.getPlayer());
		boolean canPlace = e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		if (player == null) {
			e.setCancelled(!canPlace);
			return;
		}

		Match match = player.getMatch();
		e.setCancelled(!match.getData().getBuild());
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getCause() != IgniteCause.FLINT_AND_STEEL) {
			event.setCancelled(true);
		}
	}
}
