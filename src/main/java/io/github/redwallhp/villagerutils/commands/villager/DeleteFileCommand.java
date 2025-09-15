package io.github.redwallhp.villagerutils.commands.villager;

import java.io.File;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import io.github.redwallhp.villagerutils.helpers.FileHelper;

public class DeleteFileCommand extends AbstractCommand {

    public DeleteFileCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "deletefile";
    }

    @Override
    public String getUsage() {
        return "/villager deletefile <filename>";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        String fileName = args[0];
        if (!FileHelper.allowedVillagerSaveFileName(sender, fileName)) {
            return false;
        }

        File file = new File(plugin.getSavedVillagersDirectory(), fileName);
        if (file.exists()) {
            if (file.delete()) {
                sender.sendMessage(Component.text("The file \"" + fileName + "\" was deleted.", NamedTextColor.DARK_AQUA));
                return true;
            } else {
                sender.sendMessage(Component.text("The file could not be deleted!", NamedTextColor.RED));
                return false;
            }
        } else {
            sender.sendMessage(Component.text("There is no file named \"" + fileName + "\".", NamedTextColor.RED));
            return false;
        }
    }
}
