package ms.uk.eclipse.commands.config;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.CreatedMessage;
import ms.uk.eclipse.core.utils.message.DeletedMessage;
import ms.uk.eclipse.core.utils.message.SetValueMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.kit.Kit;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class GametypeCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();

	public GametypeCommand() {
		super("gametype", "practice.permission.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = "";
		if (args.length > 0) {
			arg = args[0];
		}

		Profile player = playerManager.getProfile(pl);
		Gametype gametype;
		Arena arena;
		switch (arg.toLowerCase()) {
			case "create":
				if (args.length < 2) {
					player.message(new UsageMessage("/gametype create <Name>"));
					return;
				}

				gametype = new Gametype(args[1]);

				if (gametypeManager.contains(gametype)) {
					player.message(ErrorMessages.GAMETYPE_ALREADY_EXISTS);
					return;
				}

				gametypeManager.registerGametype(gametype);
				player.message(new CreatedMessage("The " + args[1] + " gametype"));

				return;
			case "loadkit":
				if (args.length < 2) {
					player.message(new UsageMessage("/gametype loadkit <Name>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				player.giveKit(gametype.getKit());
				player.message(new SetValueMessage("Your inventory", "the " + args[1] + " kit", CC.GREEN));

				return;
			case "kit":
				if (args.length < 2) {
					player.message(new UsageMessage("/gametype kit <Gametype>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				ItemStack[] contents = player.getInventory().getContents();
				ItemStack[] armourContents = player.getInventory().getArmorContents();

				gametype.setKit(new Kit(contents, armourContents));
				player.message(new SetValueMessage("The kit for " + args[1], "your inventory contents", CC.GREEN));

				return;
			case "setdisplay":
				if (args.length < 2) {
					player.message(new UsageMessage("/gametype setdisplay <Gametype> <DisplayName>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				gametype.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					gametype.setDisplayName(args[2].replace("&", "§"));
				}

				player.message(new SetValueMessage("The display item ", "for " + args[1], CC.GREEN));

				return;
			case "nodamageticks":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype nodamageticks <Gametype> <Ticks>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				try {
					gametype.setNoDamageTicks(Integer.parseInt(args[2]));
					player.message(new SetValueMessage("The hit delay ", args[2], CC.GREEN));
					return;
				} catch (Exception e) {
				}

				player.message(new UsageMessage("/gametype nodamageticks <Gametype> <Ticks>"));

				return;
			case "regen":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype regen <Gametype> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				switch (args[2]) {
					case "false":
						gametype.setRegeneration(false);
						player.message(new SetValueMessage("Regeneration", "false", CC.RED));
						return;
					case "true":
						gametype.setRegeneration(true);
						player.message(new SetValueMessage("Regeneration", "true", CC.GREEN));
						return;
				}

				player.message(new UsageMessage("/gametype regen <Gametype> <True/False>"));

				return;
			case "griefing":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype griefing <Gametype> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[2].equalsIgnoreCase("false")) {
					gametype.setGriefing(false);
					player.message(new SetValueMessage("Griefing", "false", CC.RED));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {
					gametype.setGriefing(true);
					player.message(new SetValueMessage("Griefing", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/gametype griefing <Gametype> <True/False>"));

				return;
			case "queue":
				if (args.length < 4) {
					player.message(new UsageMessage("/gametype queue <Gametype> <Queuetype> <Slot/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);
				Queuetype queuetype = queuetypeManager.getQueuetypeByName(args[2]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (queuetype == null) {
					player.message(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST);
					return;
				}

				String s = args[3];

				if (s.equalsIgnoreCase("false")) {
					gametype.removeFromQueuetype(queuetype);
				} else {
					Integer slot;
					try {
						slot = Integer.parseInt(s);
					} catch (Exception e) {
						return;
					}
					gametype.addToQueuetype(queuetype, slot);
				}

				player.message(new SetValueMessage("The slot", s, CC.PRIMARY));

				return;
			case "build":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype build <Gametype> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[2].equalsIgnoreCase("false")) {
					gametype.setBuild(false);
					player.message(new SetValueMessage("Build", "false", CC.RED));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {
					gametype.setBuild(true);
					player.message(new SetValueMessage("Build", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/gametype build <Gametype> <True/False>"));

				return;
			case "deadlywater":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype deadlywater <Gametype> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[2].equalsIgnoreCase("false")) {
					gametype.setDeadlyWater(false);
					player.message(new SetValueMessage("Deadly water", "false", CC.RED));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {
					gametype.setDeadlyWater(true);
					player.message(new SetValueMessage("Deadly water", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/gametype deadlywater <Gametype> <True/False>"));

				return;
			case "looting":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype looting <Gametype> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[2].equalsIgnoreCase("false")) {
					gametype.setLooting(false);
					player.message(new SetValueMessage("Looting", "false", CC.RED));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {
					gametype.setLooting(true);
					player.message(new SetValueMessage("Looting", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/gametype looting <Gametype> <True/False>"));

				return;
			case "damage":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype damage <Gametype> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[2].equalsIgnoreCase("false")) {
					gametype.setDamage(false);
					player.message(new SetValueMessage("Damage", "false", CC.RED));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {
					gametype.setDamage(true);
					player.message(new SetValueMessage("Damage", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/gametype damage <Gametype> <True/False>"));

				return;
			case "hunger":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype hunger <Gametype> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[2].equalsIgnoreCase("false")) {
					gametype.setHunger(false);
					player.message(new SetValueMessage("Hunger", "false", CC.RED));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {
					gametype.setHunger(true);
					player.message(new SetValueMessage("Hunger", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/gametype hunger <Gametype> <True/False>"));

				return;
			case "epearl":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype epearl <Gametype> <Time(s)>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				String cooldown = args[2];
				gametype.setPearlCooldown(Integer.parseInt(cooldown));
				player.message(new SetValueMessage("The pearl cooldown ", cooldown, CC.GREEN));

				return;
			case "list":
				player.message(new StrikingMessage("Gametype List", CC.PRIMARY, true));

				for (Gametype g : gametypeManager.getGametypes()) {
					player.message(new ChatMessage(g.getName(), CC.SECONDARY, false));
				}

				return;
			case "arena":
				if (args.length < 4) {
					player.message(new UsageMessage("/gametype arena <Gametype> <Arena> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);
				arena = arenaManager.getArenaByName(args[2]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[3].equalsIgnoreCase("false")) {
					gametype.enableArena(arena, false);
					player.message(new SetValueMessage("That arena", "disabled", CC.RED));
					return;
				}

				if (args[3].equalsIgnoreCase("true")) {
					gametype.enableArena(arena, true);
					player.message(new SetValueMessage("That arena", "enabled", CC.GREEN));
					return;
				}

				return;
			case "event":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype event <Gametype> <True/False>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[2].equalsIgnoreCase("false")) {
					gametype.setEvent(false);
					player.message(new SetValueMessage("Event mode for this gametype", "disabled", CC.RED));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {
					gametype.setEvent(true);
					player.message(new SetValueMessage("Event mode for this gametype", "enabled", CC.GREEN));
					return;
				}

				return;
			case "seteventarena":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype seteventarena <Gametype> <Arena>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);
				arena = arenaManager.getArenaByName(args[2]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				gametype.setEventArena(arena);
				player.message(new SetValueMessage("The event arena", arena.getName(), CC.RED));
				return;

			case "enablearenaforall":
				if (args.length < 3) {
					player.message(new UsageMessage("/gametype enablearenaforall <Arena> <True/False>"));
					return;
				}

				arena = arenaManager.getArenaByName(args[1]);

				if (args[2].equalsIgnoreCase("false")) {

					for (Gametype g : gametypeManager.getGametypes()) {
						g.enableArena(arena, false);
					}

					player.message(new SetValueMessage("That arena", "disabled for all gametypes", CC.GREEN));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {

					for (Gametype g : gametypeManager.getGametypes()) {
						g.enableArena(arena, true);
					}

					player.message(new SetValueMessage("That arena", "enabled for all gametypes", CC.GREEN));
					return;
				}

				return;
			case "delete":
				if (args.length < 2) {
					player.message(new UsageMessage("/gametype delete <Gametype>"));
					return;
				}

				gametype = gametypeManager.getGametypeByName(args[1]);

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				gametypeManager.remove(gametype);
				player.message(new DeletedMessage("The " + args[1] + " gametype"));

				return;
			default:
				player.message(new StrikingMessage("Gametype Help", CC.PRIMARY, true));
				player.message(new ChatMessage("/gametype create <Name>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype kit <Gametype>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype loadkit <Gametype>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype nodamageticks <Gametype> <Ticks>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype griefing <Gametype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype build <Gametype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype looting <Gametype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype damage <Gametype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype hunger <Gametype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype regen <Gametype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype epearl <Gametype> <Time(s)>", CC.SECONDARY, false));
				player.message(
						new ChatMessage("/gametype enablearenaforall <Arena> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype setdisplay <Gametype> <DisplayName>", CC.SECONDARY, false));
				player.message(
						new ChatMessage("/gametype queue <Gametype> <Queuetype> <Slot/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype list", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype arena <Gametype> <Arena> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype seteventarena <Gametype> <Arena>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype event <Gametype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype deadlywater <Gametype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/gametype delete <Name>", CC.SECONDARY, false));
				return;
		}
	}
}
