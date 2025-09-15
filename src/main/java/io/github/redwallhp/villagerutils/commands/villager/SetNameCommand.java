package io.github.redwallhp.villagerutils.commands.villager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import io.github.redwallhp.villagerutils.helpers.VillagerHelper;

public class SetNameCommand extends AbstractCommand {

    public SetNameCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public String getUsage() {
        return "/villager name <name>";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }
        AbstractVillager target = VillagerHelper.getAbstractVillagerInLineOfSight(player);
        if (target == null) {
            player.sendMessage(Component.text("You're not looking at a villager.", NamedTextColor.RED));
            return false;
        }
        if (args.length == 0) {
            player.sendMessage(Component.text("You must enter a name.", NamedTextColor.RED));
            return false;
        }

        // Parse player input as MiniMessage
        String input = String.join(" ", args);
        Component nameComponent = miniMessage.deserialize(input);

        target.customName(nameComponent);
        player.sendMessage(Component.text("Villager name updated.", NamedTextColor.DARK_AQUA));
        return true;
    }
}
