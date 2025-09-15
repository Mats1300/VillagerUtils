package io.github.redwallhp.villagerutils.commands.villager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.VillagerSpecificAbstractCommand;
import org.jetbrains.annotations.NotNull;

public class SetLevelCommand extends VillagerSpecificAbstractCommand implements TabCompleter {

    public SetLevelCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "level";
    }

    @Override
    public String getUsage() {
        return "/villager level <level>";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }

        Villager villager = getVillagerInLineOfSight(player, "Wandering traders can't change their level.");
        if (villager == null) return false;

        if (args.length == 0) {
            player.sendMessage(Component.text("Silly, you need a number 1-5 here!", NamedTextColor.RED));
            return false;
        }

        if (args.length > 1) {
            player.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        int level;
        try {
            level = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            player.sendMessage(Component.text("The level must be a valid number.", NamedTextColor.RED));
            return false;
        }

        if (level < 1 || level > 5) {
            player.sendMessage(Component.text("The level must be between 1 and 5.", NamedTextColor.RED));
            return false;
        }

        villager.setVillagerLevel(level);
        player.sendMessage(Component.text("Villager level set to " + level + ".", NamedTextColor.DARK_AQUA));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 2) {
            return IntStream.rangeClosed(1, 5)
                    .mapToObj(Integer::toString)
                    .filter(s -> s.startsWith(args[1]))
                    .collect(Collectors.toList());
        }
            return Collections.emptyList();
        }
    }
