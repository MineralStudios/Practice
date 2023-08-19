package gg.mineral.practice.commands.config;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.SpectateManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class SpectateConfigCommand extends PlayerCommand {

    public SpectateConfigCommand() {
        super("spectateconfig", "practice.config");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {

        String arg = args.length > 0 ? args[0] : "";
        String toggled;

        switch (arg.toLowerCase()) {
            default:
                ChatMessages.SPECTATE_COMMANDS.send(pl);
                ChatMessages.SPECTATE_ENABLE.send(pl);
                ChatMessages.SPECTATE_SLOT.send(pl);
                ChatMessages.SPECTATE_DISPLAY.send(pl);
                return;
            case "enable":
                if (args.length < 2) {
                    UsageMessages.SPECTATE_ENABLE.send(pl);
                    return;
                }

                toggled = args[1].toLowerCase();

                switch (toggled) {
                    case "false":
                        SpectateManager.setEnabled(false);
                        break;
                    case "true":
                        SpectateManager.setEnabled(true);
                        break;
                    default:
                        UsageMessages.SPECTATE_ENABLE.send(pl);
                        return;
                }

                ChatMessages.SPECTATE_ENABLED.clone().replace("%toggled%", toggled).send(pl);

                return;
            case "setdisplay":
                if (args.length < 1) {
                    UsageMessages.SPECTATE_DISPLAY.send(pl);
                    return;
                }

                SpectateManager.setDisplayItem(pl.getItemInHand());

                if (args.length > 2) {
                    SpectateManager.setDisplayName(args[1].replace("&", "§"));
                }

                ChatMessages.SPECTATE_DISPLAY_SET.send(pl);
                return;
            case "slot":
                if (args.length < 2) {
                    UsageMessages.SPECTATE_SLOT.send(pl);
                    return;
                }

                int slot;
                String strSlot = args[1];
                try {
                    slot = Integer.parseInt(strSlot);
                } catch (Exception e) {
                    ErrorMessages.INVALID_SLOT.send(pl);
                    return;
                }

                SpectateManager.setSlot(slot);

                ChatMessages.SPECTATE_SLOT_SET.clone().replace("%slot%", strSlot).send(pl);
                return;
        }
    }
}
