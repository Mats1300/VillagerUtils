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
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.VillagerSpecificAbstractCommand;
import io.github.redwallhp.villagerutils.helpers.VillagerHelper;
import org.jetbrains.annotations.NotNull;

public class SetBiomeCommand extends VillagerSpecificAbstractCommand implements TabCompleter {

    public SetBiomeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "biome";
    }

    @Override
    public String getUsage() {
        return "/villager biome <biome>";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }

        Villager villager = getVillagerInLineOfSight(player, "Wandering traders can't change their appearance.");
        if (villager == null) {
            return false;
        }

        if (args.length > 1) {
            sender.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        Villager.Type biome = (args.length == 0) ? null : VillagerHelper.getVillagerTypeFromString(args[0]);
        if (biome == null) {
            player.sendMessage(Component.text("You must specify a villager biome.", NamedTextColor.RED));
            player.sendMessage(Component.text("Valid biomes: " + String.join(", ", VillagerHelper.getVillagerTypeNames()), NamedTextColor.GRAY));
            return false;
        }

        villager.setVillagerType(biome);
        player.sendMessage(Component.text("Villager biome updated.", NamedTextColor.DARK_AQUA));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 2) {
            return VillagerHelper.getVillagerTypeNames().stream()
            .filter(completion -> completion.startsWith(args[1].toLowerCase()))
            .sorted().collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
