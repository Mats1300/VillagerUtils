package io.github.redwallhp.villagerutils.helpers;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

/**
 * Helper methods for working with Villagers
 */
public class VillagerHelper {

    /**
     * Get the AbstractVillager that the player is looking at
     *
     * @param player The player to check for
     * @return AbstractVillager entity or null
     */
    public static AbstractVillager getAbstractVillagerInLineOfSight(Player player) {
        List<Entity> entities = player.getNearbyEntities(5, 1, 5);
        entities.removeIf(ent -> !(ent instanceof AbstractVillager));

        for (Block block : player.getLineOfSight(null, 6)) {
            if (block.getType() != Material.AIR) break; // view is obstructed
            for (Entity ent : entities) {
                Vector b = block.getLocation().toVector();
                Vector head = ent.getLocation().toVector().add(new Vector(0, 1, 0));
                Vector foot = ent.getLocation().toVector();
                if (head.isInSphere(b, 1.25) || foot.isInSphere(b, 1.25)) {
                    return (AbstractVillager) ent;
                }
            }
        }
        return null;
    }

    /**
     * Get a Villager Profession by string key using the Registry.
     *
     * @param key the profession key (e.g., "armorer")
     * @return Villager.Profession or null if not found
     */
    public static Villager.Profession getProfessionFromString(String key) {
        if (key.startsWith("minecraft:")) key = key.substring("minecraft:".length());
        return Registry.VILLAGER_PROFESSION.get(NamespacedKey.minecraft(key.toLowerCase()));
    }

    /**
     * Get a Villager Type (biome) by string key using the Registry.
     *
     * @param key the type key (e.g., "plains")
     * @return Villager.Type or null if not found
     */
    public static Villager.Type getVillagerTypeFromString(String key) {
        if (key.startsWith("minecraft:")) key = key.substring("minecraft:".length());
        return Registry.VILLAGER_TYPE.get(NamespacedKey.minecraft(key.toLowerCase()));
    }

    /**
     * Return a sorted list of all profession names using the Registry.
     *
     * @return List of profession keys (lowercase)
     */
    public static List<String> getProfessionNames() {
        return Registry.VILLAGER_PROFESSION.stream()
                .map(p -> p.key().toString().replace("minecraft:", ""))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Return a sorted list of all villager type names using the Registry.
     *
     * @return List of type keys (lowercase)
     */
    public static List<String> getVillagerTypeNames() {
        return Registry.VILLAGER_TYPE.stream()
                .map(t -> t.key().toString().replace("minecraft:", ""))
                .sorted()
                .collect(Collectors.toList());
    }
}