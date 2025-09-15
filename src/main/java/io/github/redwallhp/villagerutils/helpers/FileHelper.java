package io.github.redwallhp.villagerutils.helpers;

import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class FileHelper {
    /**
     * Check whether a filename is an allowable villager save file name and send
     * an error message to the command sender if the filename is not allowed.
     * <p>
     * Villager save file names can only consist of letters, digits, underscores
     * and hyphens.
     *
     * @param sender   the command sender.
     * @param fileName the filename.
     * @return true if the filename is allowed.
     */
    public static boolean allowedVillagerSaveFileName(CommandSender sender, String fileName) {
        if (Pattern.matches("(\\w|-)+", fileName)) {
            return true;
        } else {
            sender.sendMessage(Component.text(
                    "The file name can only contain letters, digits, underscores and hyphens.",
                    NamedTextColor.RED
            ));
            return false;
        }
    }
}
