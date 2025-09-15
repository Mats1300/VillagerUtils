package io.github.redwallhp.villagerutils.commands.villager;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;

public class ListFilesCommand extends AbstractCommand {

    public ListFilesCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "listfiles";
    }

    @Override
    public String getUsage() {
        return "/villager listfiles";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        File[] savedFiles = plugin.getSavedVillagersDirectory().listFiles();
        if (savedFiles == null) {
            sender.sendMessage(Component.text("No save directory found.", NamedTextColor.RED));
            return true;
        }

        List<String> files = Stream.of(savedFiles)
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());
        if (files.isEmpty()) {
            sender.sendMessage(Component.text("There are no villager save files.", NamedTextColor.DARK_AQUA));
        } else {
            sender.sendMessage(Component.text("Villager save files: " + String.join(", ", files), NamedTextColor.DARK_AQUA));
        }
        return true;
    }
}
