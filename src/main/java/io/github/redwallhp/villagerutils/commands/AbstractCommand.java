package io.github.redwallhp.villagerutils.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import io.github.redwallhp.villagerutils.VillagerUtils;

public abstract class AbstractCommand {

    protected VillagerUtils plugin;
    private final String permission;
    private final Map<String, AbstractCommand> subCommands;

    public AbstractCommand(VillagerUtils plugin, String permission) {
        this.plugin = plugin;
        this.permission = permission;
        this.subCommands = new LinkedHashMap<>();
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!checkPermission(sender)) return false;

        if (!subCommands.isEmpty() && args.length > 0) {
            AbstractCommand subcmd = subCommands.get(args[0].toLowerCase());
            if (subcmd != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subcmd.execute(sender, subArgs);
            } else {
                sender.sendMessage(Component.text("Usage: " + getUsage(), NamedTextColor.RED));
                sender.sendMessage(getSubcommandsHelp());
                return false;
            }
        }

        return action(sender, args);
    }

    public boolean checkPermission(CommandSender sender) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
            return false;
        }
        return true;
    }

    public void addSubCommand(AbstractCommand command) {
        subCommands.put(command.getName(), command);
    }

    public AbstractCommand getSubCommand(String name) {
        return subCommands.get(name.toLowerCase());
    }

    public Collection<String> getSubCommandNames() {
        return subCommands.keySet();
    }

    private Component getSubcommandsHelp() {
        String joined = subCommands.keySet().stream()
                .sorted()
                .collect(Collectors.joining(", "));

        return Component.text("Subcommands: ", NamedTextColor.DARK_AQUA)
                .append(Component.text(joined, NamedTextColor.GRAY));
    }

    /**
     * The action the command should perform
     * 
     * @return true if the command was successful
     */
    public abstract boolean action(CommandSender sender, String[] args);

    /**
     * The name of the command
     */
    public abstract String getName();

    /**
     * The command usage, for this specific command
     */
    public abstract String getUsage();

}
