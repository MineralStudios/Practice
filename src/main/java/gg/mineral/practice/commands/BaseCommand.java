package gg.mineral.practice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

public abstract class BaseCommand extends Command {
    protected final String permission;

    protected BaseCommand(String name, String permission) {
        super(name);
        this.permission = permission;
    }

    protected BaseCommand(String name) {
        this(name, null);
    }

    @Override
    public final boolean execute(CommandSender sender, String alias, String[] args) {
        execute(sender, args);
        return true;
    }

    protected final void setAliases(String... aliases) {
        if (aliases.length > 0)
            setAliases(aliases.length == 1 ? Collections.singletonList(aliases[0]) : Arrays.asList(aliases));

    }

    protected abstract void execute(CommandSender sender, String[] args);
}
