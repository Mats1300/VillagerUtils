package io.github.redwallhp.villagerutils.commands.villager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

public class SetStaticCommand extends VillagerSpecificAbstractCommand implements TabCompleter {

    public SetStaticCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "static";
    }

    @Override
    public String getUsage() {
        return "/villager static <boolean>";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot spawn villagers.", NamedTextColor.RED));
            return false;
        }

        Villager villager = getVillagerInLineOfSight(player, "Wandering traders don't acquire new trades.");
        if (villager == null) {
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(Component.text(getUsage(), NamedTextColor.RED));
            return false;
        }

        boolean value = Boolean.parseBoolean(args[0]);
        if (value) {
            plugin.getVillagerMeta().STATIC_MERCHANTS.add(villager.getUniqueId().toString());
            sender.sendMessage(Component.text("This villager will not acquire its own trades.", NamedTextColor.DARK_AQUA));
        } else {
            plugin.getVillagerMeta().STATIC_MERCHANTS.remove(villager.getUniqueId().toString());
            sender.sendMessage(Component.text("This villager will acquire its own trades.", NamedTextColor.DARK_AQUA));
        }
        plugin.getVillagerMeta().save();
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 2) {
            return Stream.of("false", "true")
            .filter(completion -> completion.startsWith(args[1].toLowerCase()))
            .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
