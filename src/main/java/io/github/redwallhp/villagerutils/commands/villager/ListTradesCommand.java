package io.github.redwallhp.villagerutils.commands.villager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import io.github.redwallhp.villagerutils.helpers.TradeHelper;
import io.github.redwallhp.villagerutils.helpers.VillagerHelper;

public class ListTradesCommand extends AbstractCommand {

    public ListTradesCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "listtrades";
    }

    @Override
    public String getUsage() {
        return "/villager listtrades";
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

        if (args.length != 0) {
            player.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        int index = 1;
        for (MerchantRecipe recipe : target.getRecipes()) {
            sender.sendMessage(Component.text("---------- Trade #" + index + " ----------", NamedTextColor.DARK_AQUA));
            TradeHelper.describeTrade(sender, recipe);
            ++index;
        }
        sender.sendMessage(Component.text("======== Total trades: " + (index - 1) + " ========", NamedTextColor.DARK_AQUA));
        return true;
    }

}
