package gg.mineral.practice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.Setter;

public abstract class PlayerCommand extends BaseCommand {

    @Setter
    @Getter
    Boolean console = false;

    protected PlayerCommand(String name, String permission) {
        super(name, permission);
    }

    protected PlayerCommand(String name) {
        super(name);
    }

    @Override
    protected final void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player || getConsole()) {
            Player player = (Player) sender;

            if (!player.isOp()
                    && permission != null && !player.hasPermission(permission)) {
                player.sendMessage(CC.RED + "You don't have the required permission to perform this command.");
                return;
            }

            execute(player, args);
        } else {
            sender.sendMessage(CC.RED + "Only players can perform this command.");
        }
    }

    public abstract void execute(Player player, String[] args);
}
