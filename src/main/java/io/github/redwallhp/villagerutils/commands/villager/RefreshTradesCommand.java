package io.github.redwallhp.villagerutils.commands.villager;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import io.github.redwallhp.villagerutils.helpers.VillagerHelper;

public class RefreshTradesCommand extends AbstractCommand {

    public RefreshTradesCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "refreshtrades";
    }

    @Override
    public String getUsage() {
        return "/villager refreshtrades";
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

        List<MerchantRecipe> recipes = target.getRecipes();
        for (MerchantRecipe recipe : recipes) {
            recipe.setUses(0);
        }
        target.setRecipes(recipes);

        Location particleLoc = target.getLocation().add(0, 1.5, 0);
        particleLoc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, particleLoc, 10, 0.5, 0.5, 0.5);
        player.sendMessage(Component.text("Villager trades refreshed.", NamedTextColor.DARK_AQUA));
        return true;
    }

}
