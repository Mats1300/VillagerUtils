package io.github.redwallhp.villagerutils.helpers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
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
        if (item == null) {
            return "null";
        }

        StringBuilder description = new StringBuilder();
        if (item.getAmount() != 1) {
            description.append(item.getAmount()).append('x');
        }

        description.append(item.getType().name());
        if (item.getDurability() != 0) {
            description.append(':').append(item.getDurability());
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (skullMeta.getOwner() != null) {
                    description.append(" of \"").append(skullMeta.getOwner()).append("\"");
                }
            } else if (meta instanceof SpawnEggMeta eggMeta) {
                EntityType type = eggMeta.getCustomSpawnedType();
                description.append(" of ").append(type);
            } else if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta bookEnchants = (EnchantmentStorageMeta) meta;
                description.append(" with").append(enchantsToString(bookEnchants.getStoredEnchants()));
            } else if (meta instanceof BookMeta) {
                BookMeta bookMeta = (BookMeta) meta;
                if (bookMeta.getTitle() != null) {
                    description.append(" titled \"").append(bookMeta.getTitle()).append("\"");
                }
                if (bookMeta.getAuthor() != null) {
                    description.append(" by ").append(bookMeta.getAuthor());
                }
            } else if (meta instanceof PotionMeta potionMeta) {
                PotionType type = potionMeta.getBasePotionType();
                description.append(" of ");

                if (type != null) {
                    String lower = type.name().toLowerCase();

                    if (lower.startsWith("long_")) {
                        description.append(lower.substring(5)).append(" (extended)");
                    } else if (lower.startsWith("strong_")) {
                        description.append(lower.substring(7)).append(" (upgraded)");
                    } else {
                        description.append(lower);
                    }
                } else {
                    description.append("unknown");
                }

                List<PotionEffect> effects = potionMeta.getCustomEffects();
                if (effects != null && !effects.isEmpty()) {
                    description.append(" with ");
                    String sep = "";
                    for (PotionEffect effect : potionMeta.getCustomEffects()) {
                        description.append(sep).append(potionToString(effect));
                        sep = "+";
                    }
                }
            }

            if (meta.getDisplayName() != null && !meta.getDisplayName().isEmpty()) {
                description.append(" named \"").append(meta.getDisplayName()).append(ChatColor.WHITE).append("\"");
            }

            List<String> lore = meta.getLore();
            if (lore != null && !lore.isEmpty()) {
                description.append(" lore \"").append(String.join("|", lore)).append(ChatColor.WHITE).append("\"");
            }
        }

        description.append(enchantsToString(item.getEnchantments()));
        return description.toString();
    }

    /**
     * Return the string description of a potion effect.
     *
     * @param effect the effect.
     * @return the description.
     */
    public static String potionToString(PotionEffect effect) {
        StringBuilder description = new StringBuilder();
        description.append(effect.getType().getName()).append("/");
        description.append(effect.getAmplifier() + 1).append("/");
        description.append(effect.getDuration() / 20.0).append('s');
        return description.toString();
    }

    /**
     * Return the string description of a set of enchantments.
     *
     * @param enchants map from enchantment type to level, from the Bukkit API.
     * @return the description.
     */
    public static String enchantsToString(Map<Enchantment, Integer> enchants) {
        StringBuilder description = new StringBuilder();
        if (enchants.size() > 0) {
            description.append(" (");
            String sep = "";
            for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                description.append(sep).append(entry.getKey().getName()).append(':').append(entry.getValue());
                sep = ",";
            }
            description.append(')');
        }
        return description.toString();
    }

}
