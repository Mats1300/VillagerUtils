package io.github.redwallhp.villagerutils.commands;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.villager.VillagerCommand;
import io.github.redwallhp.villagerutils.commands.vtrade.VtradeCommand;
import org.jetbrains.annotations.NotNull;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final VillagerUtils plugin;
    private final HashMap<String, AbstractCommand> commands;

    public CommandManager() {
        this.plugin = VillagerUtils.instance;
        commands = new HashMap<>();
        registerCommands();
    }

    private void registerCommands() {

        commands.put("villager", new VillagerCommand(plugin));
        commands.put("vtrade", new VtradeCommand(plugin));

        for (String key : commands.keySet()) {
            Objects.requireNonNull(plugin.getCommand(key)).setExecutor(this);
            Objects.requireNonNull(plugin.getCommand(key)).setTabCompleter(this);
        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String name, String[] args) {
        if (commands.containsKey(cmd.getName().toLowerCase())) {
            AbstractCommand command = commands.get(cmd.getName().toLowerCase());
            command.execute(sender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        AbstractCommand abstractCommand = commands.get(command.getName().toLowerCase());
        if (abstractCommand != null) {
            if (args.length == 0) {
                return new ArrayList<>(abstractCommand.getSubCommandNames());
            } else if (args.length == 1) {
                return abstractCommand.getSubCommandNames().stream()
                .filter(sub -> sub.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            } else {
                AbstractCommand subCommand = abstractCommand.getSubCommand(args[0]);
                if (subCommand instanceof TabCompleter) {
                    return ((TabCompleter) subCommand).onTabComplete(sender, command, alias, args);
                }
            }
        }
        return Collections.emptyList();
    }

}
