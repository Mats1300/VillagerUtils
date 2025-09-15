package io.github.redwallhp.villagerutils.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.helpers.ItemHelper;

public class VillagerLogger implements Listener {

    private final VillagerUtils plugin;

    public VillagerLogger() {
        plugin = VillagerUtils.instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.getConfiguration().LOGGING)
            return;
        if (event.getEntity() instanceof AbstractVillager) {
            logDeath((AbstractVillager) event.getEntity());
        }
    }

    private void logDeath(AbstractVillager villager) {
        StringBuilder sb = new StringBuilder("[VILLAGER DEATH] ");

        Location l = villager.getLocation();
        sb.append(String.format("Location: %s %d %d %d | ",
                l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));

        if (villager.getKiller() != null) {
            sb.append(String.format("Killer: %s (%s) | ",
                    villager.getKiller().getName(), villager.getKiller().getUniqueId()));
        }

        Component customName = villager.customName();
        if (customName != null && !customName.equals(Component.empty())) {
            String nameStr = PlainTextComponentSerializer.plainText().serialize(customName);
            sb.append(String.format("Name: %s | ", nameStr));
        }

        if (villager instanceof Villager v) {
            sb.append(String.format("Profession: %s | ", v.getProfession()));
            sb.append(String.format("Biome: %s | ", v.getVillagerType()));
            sb.append(String.format("Level: %d | ", v.getVillagerLevel()));
            sb.append(String.format("Experience: %d | ", v.getVillagerExperience()));
        }

        sb.append("Recipes: ");
        for (MerchantRecipe recipe : villager.getRecipes()) {
            sb.append("{").append(getRecipeString(recipe)).append("}");
            if (villager.getRecipes().indexOf(recipe) < villager.getRecipes().size() - 1) {
                sb.append(", ");
            }
        }

        plugin.getLogger().info(sb.toString());
    }


    private String getRecipeString(MerchantRecipe recipe) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Result] ").append(ItemHelper.getItemDescription(recipe.getResult())).append(" ");

        sb.append("[Cost] ");
        for (ItemStack item : recipe.getIngredients()) {
            sb.append(item.toString());
            if (recipe.getIngredients().indexOf(item) < recipe.getIngredients().size() - 1) {
                sb.append(", ");
            } else {
                sb.append(" ");
            }
        }

        sb.append(String.format("[Max Uses] %d ", recipe.getMaxUses()));
        sb.append(String.format("[XP Reward] %b", recipe.hasExperienceReward()));

        return sb.toString();
    }
}
