package io.github.redwallhp.villagerutils.commands.villager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.VillagerSpecificAbstractCommand;
import io.github.redwallhp.villagerutils.helpers.FileHelper;

public class SaveFileCommand extends VillagerSpecificAbstractCommand {

    public SaveFileCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "savefile";
    }

    @Override
    public String getUsage() {
        return "/villager savefile <filename>";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }

        Villager villager = getVillagerInLineOfSight(player, "This command doesn't work with wandering traders.");
        if (villager == null) {
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("You must specify a file name.", NamedTextColor.RED));
            return false;
        }

        String fileName = args[0];
        if (!FileHelper.allowedVillagerSaveFileName(sender, fileName)) {
            return false;
        }

        YamlConfiguration config = new YamlConfiguration();

        Component customName = villager.customName();
        String name = customName != null ? PlainTextComponentSerializer.plainText().serialize(customName) : null;

        config.set("name", name);
        config.set("biome", villager.getVillagerType().toString());
        config.set("profession", villager.getProfession().toString());
        config.set("level", villager.getVillagerLevel());
        config.set("static", plugin.getVillagerMeta().STATIC_MERCHANTS.contains(villager.getUniqueId().toString()));

        List<Map<String, Object>> recipes = new ArrayList<>();
        for (MerchantRecipe recipe : villager.getRecipes()) {
            Map<String, Object> recipeMap = new HashMap<>();
            recipeMap.put("ingredients", recipe.getIngredients());
            recipeMap.put("max-uses", recipe.getMaxUses());
            recipeMap.put("price-multiplier", recipe.getPriceMultiplier());
            recipeMap.put("result", recipe.getResult());
            recipeMap.put("uses", recipe.getUses());
            recipeMap.put("villager-experience", recipe.getVillagerExperience());
            recipeMap.put("experience-reward", recipe.hasExperienceReward());
            recipes.add(recipeMap);
        }
        config.set("recipes", recipes);

        File file = new File(plugin.getSavedVillagersDirectory(), fileName);
        try {
            config.save(file);
            player.sendMessage(Component.text("Villager trades saved to \"" + fileName + "\".", NamedTextColor.DARK_AQUA));
        } catch (IOException ex) {
            player.sendMessage(Component.text("Error saving file: " + ex.getMessage(), NamedTextColor.RED));
        }
        return true;
    }
}
