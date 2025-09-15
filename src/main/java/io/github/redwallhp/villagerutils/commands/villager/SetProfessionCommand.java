package io.github.redwallhp.villagerutils.commands.villager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.VillagerSpecificAbstractCommand;
import io.github.redwallhp.villagerutils.helpers.VillagerHelper;
import org.jetbrains.annotations.NotNull;

public class SetProfessionCommand extends VillagerSpecificAbstractCommand implements TabCompleter {

    public SetProfessionCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "profession";
    }

    @Override
    public String getUsage() {
        return "/villager profession <profession>";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }

        Villager villager = getVillagerInLineOfSight(player, "Wandering traders can't change their profession.");
        if (villager == null) {
            return false;
        }

        if (args.length > 1) {
            sender.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        Profession profession = (args.length == 0) ? null : VillagerHelper.getProfessionFromString(args[0]);
        if (profession == null) {
            player.sendMessage(Component.text("You must specify a profession.", NamedTextColor.RED));
            player.sendMessage(Component.text("Valid professions: " + String.join(", ", VillagerHelper.getProfessionNames()), NamedTextColor.GRAY));
            return false;
        }

        villager.setProfession(profession);
        player.sendMessage(Component.text("Villager profession updated.", NamedTextColor.DARK_AQUA));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 2) {
            return VillagerHelper.getProfessionNames().stream()
            .filter(completion -> completion.startsWith(args[1].toLowerCase()))
            .sorted().collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
