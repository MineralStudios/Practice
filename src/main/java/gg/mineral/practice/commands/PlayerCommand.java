package gg.mineral.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerCommand extends BaseCommand {
    protected PlayerCommand(String name, String permission) {
        super(name, permission);
    }

    protected PlayerCommand(String name) {
        super(name);
    }

    @Override
    protected final void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            execute((Player) sender, args);
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can perform this command.");
        }
    }

    public abstract void execute(Player player, String[] args);
}
