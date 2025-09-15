package io.github.redwallhp.villagerutils.commands.villager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;

/**
 * Command that spawns a Villager at the player's location from a saved villager configuration file.
 * <p>
 * The villager's attributes (name, biome, profession, level, recipes, etc.) are loaded from a YAML
 * file located in the plugin's saved villagers directory.
 * </p>
 */
public class SpawnFileCommand extends AbstractCommand {

    /**
     * Creates a new {@code SpawnFileCommand}.
     *
     * @param plugin the VillagerUtils plugin instance
     */
    public SpawnFileCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "spawnfile";
    }

    @Override
    public String getUsage() {
        return "/villager spawnfile <filename>";
    }

    /**
     * Executes the command to spawn a Villager from a saved configuration file.
     * <p>
     * This method:
     * <ul>
     *   <li>Validates the command sender is a player.</li>
     *   <li>Ensures a valid file name argument is provided.</li>
     *   <li>Loads the villager configuration from the specified YAML file.</li>
     *   <li>Spawns a villager with the attributes defined in the file, including
     *   name, biome type, profession, level, static flag, and trade recipes.</li>
     *   <li>Provides feedback to both the player and the server log.</li>
     * </ul>
     *
     * @param sender the command sender (must be a Player to execute successfully)
     * @param args   the command arguments (first argument is the filename to load)
     * @return true if the villager was successfully spawned; false otherwise
     */
    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }
        Location loc = player.getLocation();

        if (args.length < 1) {
            sender.sendMessage(Component.text("You must specify a file name.", NamedTextColor.RED));
            return false;
        }

        String fileName = args[0];
        if (!Pattern.matches("(\\w|-)+", fileName)) {
            sender.sendMessage(Component.text("The file name can only contain letters, digits, underscores, and hyphens.", NamedTextColor.RED));
            return false;
        }

        File file = new File(plugin.getSavedVillagersDirectory(), fileName);
        if (!file.canRead()) {
            sender.sendMessage(Component.text("The file \"" + fileName + "\" cannot be read.", NamedTextColor.RED));
            return false;
        }

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            sender.sendMessage(Component.text("Error loading \"" + fileName + "\": " + ex.getMessage(), NamedTextColor.RED));
            return false;
        }

        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        // Use Adventure Component for names
        String rawName = config.getString("name");
        if (rawName != null) {
            villager.customName(Component.text(rawName));
        }

        // Lookup villager biome safely
        String biomeName = config.getString("biome", "plains").toLowerCase();
        Type biome = Registry.VILLAGER_TYPE.get(NamespacedKey.minecraft(biomeName));
        if (biome != null) {
            villager.setVillagerType(biome);
        } else {
            sender.sendMessage(Component.text("Invalid biome in save file.", NamedTextColor.RED));
            return false;
        }

        // Lookup villager profession safely
        String professionName = config.getString("profession", "armorer").toLowerCase();
        Profession profession = Registry.VILLAGER_PROFESSION.get(NamespacedKey.minecraft(professionName));
        if (profession != null) {
            villager.setProfession(profession);
        } else {
            sender.sendMessage(Component.text("Invalid profession in save file.", NamedTextColor.RED));
            return false;
        }

        villager.setVillagerLevel(config.getInt("level"));

        if (config.getBoolean("static")) {
            plugin.getVillagerMeta().STATIC_MERCHANTS.add(villager.getUniqueId().toString());
            plugin.getVillagerMeta().save();
        }

        // Load recipes
        List<MerchantRecipe> recipes = new ArrayList<>();
        for (Map<?, ?> untypedMap : config.getMapList("recipes")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> recipeMap = (Map<String, Object>) untypedMap;

            List<ItemStack> ingredients = new ArrayList<>();
            Object ing = recipeMap.get("ingredients");
            if (ing instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof ItemStack stack) {
                        ingredients.add(stack);
                    }
                }
            }

            int maxUses = (Integer) recipeMap.getOrDefault("max-uses", 1);
            double priceMultiplier = (Double) recipeMap.getOrDefault("price-multiplier", 0.0);
            ItemStack result = (ItemStack) recipeMap.get("result");
            int uses = (Integer) recipeMap.getOrDefault("uses", 0);
            int villagerExperience = (Integer) recipeMap.getOrDefault("villager-experience", 0);
            boolean experienceReward = (Boolean) recipeMap.getOrDefault("experience-reward", false);

            MerchantRecipe recipe = new MerchantRecipe(result, uses, maxUses, experienceReward, villagerExperience, (float) priceMultiplier);
            recipe.setIngredients(ingredients);
            recipes.add(recipe);
        }
        villager.setRecipes(recipes);

        String description = villager.getVillagerType().toString().toLowerCase() +
                " villager, profession " + villager.getProfession().toString().toLowerCase() +
                ", level " + villager.getVillagerLevel();

        plugin.getLogger().info(String.format("%s spawned %s at %d, %d, %d",
                player.getName(), description, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

        player.sendMessage(Component.text("Spawned " + description + ".", NamedTextColor.DARK_AQUA));
        return true;
    }
}
