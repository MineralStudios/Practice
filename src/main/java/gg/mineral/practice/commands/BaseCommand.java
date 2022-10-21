package gg.mineral.practice.commands;

import java.util.Arrays;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand extends Command {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private final String permission;

    protected BaseCommand(String name, String permission) {
        super(name);
        this.permission = permission;
    }

    protected BaseCommand(String name) {
        this(name, "practice.permission.member");
    }

    @Override
    public final boolean execute(CommandSender sender, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!permission.equalsIgnoreCase("practice.permission.member") && !player.hasPermission(permission)) {
                player.sendMessage(ChatColor.RED + "You don't have the required permission to perform this command.");
                return true;
            }
        }

        execute(sender, args);
        return true;
    }

    protected final void setAliases(String... aliases) {
        if (aliases.length > 0) {
            setAliases(aliases.length == 1 ? Collections.singletonList(aliases[0]) : Arrays.asList(aliases));
        }
    }

    protected final void setUsage(String... uses) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < uses.length; i++) {
            String use = uses[i];

            builder.append(use);

            if (i + 1 != uses.length) {
                builder.append(LINE_SEPARATOR);
            }
        }

        setUsage(builder.toString());
    }

    protected abstract void execute(CommandSender sender, String[] args);
}
