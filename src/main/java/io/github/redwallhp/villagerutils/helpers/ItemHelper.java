package io.github.redwallhp.villagerutils.helpers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

/**
 * Utility functions for ItemStacks.
 */
public class ItemHelper {
    /**
     * Return a string describing a dropped item stack.
     * <p>
     * The string contains the material type name, data value and amount, as
     * well as a list of enchantments. It is used in methods that log drops.
     *
     * @param item the dropped item stack.
     * @return a string describing a dropped item stack.
     */
    public static String getItemDescription(ItemStack item) {
        if (item == null) return "null";

        StringBuilder description = new StringBuilder();
        if (item.getAmount() != 1) description.append(item.getAmount()).append('x');
        description.append(item.getType().name());

        ItemMeta meta = item.getItemMeta();

        // Damageable items
        if (meta instanceof org.bukkit.inventory.meta.Damageable damageable) {
            int damage = damageable.getDamage();
            if (damage != 0) description.append(":").append(damage);
        }

        if (meta != null) {
            switch (meta.getClass().getSimpleName()) {
                case "SkullMeta" -> {
                    SkullMeta skullMeta = (SkullMeta) meta;
                    if (skullMeta.getOwningPlayer() != null) {
                        description.append(" of \"")
                                .append(skullMeta.getOwningPlayer().getName())
                                .append("\"");
                    }
                }
                case "SpawnEggMeta" -> {
                    String materialName = item.getType().name();
                    if (materialName.endsWith("_SPAWN_EGG")) {
                        String entityName = materialName.substring(0, materialName.length() - 10);
                        try {
                            EntityType type = EntityType.valueOf(entityName);
                            description.append(" of ").append(type.name());
                        } catch (IllegalArgumentException ignored) {
                            // Unknown entity type
                        }
                    }
                }
                case "EnchantmentStorageMeta" -> {
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
                    description.append(" with").append(enchantsToString(esm.getStoredEnchants()));
                }
                case "BookMeta" -> {
                    BookMeta bm = (BookMeta) meta;
                    if (bm.hasTitle()) description.append(" titled \"").append(bm.getTitle()).append("\"");
                    if (bm.hasAuthor()) description.append(" by ").append(bm.getAuthor());
                }
                case "PotionMeta" -> {
                    PotionMeta pm = (PotionMeta) meta;
                    PotionType type = pm.getBasePotionType();
                    description.append(" of ").append(type != null ? formatPotionType(type) : "unknown");

                    List<PotionEffect> effects = pm.getCustomEffects();
                    if (!effects.isEmpty()) {
                        description.append(" with ");
                        for (int i = 0; i < effects.size(); i++) {
                            if (i != 0) description.append("+");
                            description.append(potionToString(effects.get(i)));
                        }
                    }
                }
            }

            // Display name
            if (meta.hasDisplayName()) description.append(" named \"").append(meta.displayName()).append("\"");

            // Lore
            List<Component> lore = meta.lore();
            if (lore != null && !lore.isEmpty()) {
                description.append(" lore \"");
                for (int i = 0; i < lore.size(); i++) {
                    description.append(lore.get(i));
                    if (i != lore.size() - 1) description.append("|");
                }
                description.append("\"");
            }
        }

        description.append(enchantsToString(item.getEnchantments()));
        return description.toString();
    }

    // Helper to format base potion types
    private static String formatPotionType(PotionType type) {
        String lower = type.name().toLowerCase();
        if (lower.startsWith("long_")) return lower.substring(5) + " (extended)";
        if (lower.startsWith("strong_")) return lower.substring(7) + " (upgraded)";
        return lower;
    }

    /**
     * Return the string description of a potion effect.
     *
     * @param effect the effect.
     * @return the description.
     */

    public static String potionToString(PotionEffect effect) {
        // Using String.format for clarity
        return String.format("%s/%d/%.1fs",
                effect.getType().getKey().getKey(), // effect type
                effect.getAmplifier() + 1,          // level
                effect.getDuration() / 20.0);       // duration in seconds
    }

    /**
     * Return the string description of a set of enchantments.
     *
     * @param enchants map from enchantment type to level, from the Bukkit API.
     * @return the description.
     */
    public static String enchantsToString(Map<Enchantment, Integer> enchants) {
        if (enchants.isEmpty()) return "";
        StringBuilder description = new StringBuilder(" (");
        String sep = "";
        for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            description.append(sep).append(entry.getKey().getKey().getKey()).append(':').append(entry.getValue());
            sep = ",";
        }
        description.append(')');
        return description.toString();
    }
}
