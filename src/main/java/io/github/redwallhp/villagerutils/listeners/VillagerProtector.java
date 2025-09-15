package io.github.redwallhp.villagerutils.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionType;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.helpers.WorldGuardHelper;

public class VillagerProtector implements Listener {

    private final VillagerUtils plugin;

    public VillagerProtector() {
        plugin = VillagerUtils.instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handle WorldGuard based protection from players
     */
    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof AbstractVillager villager)) {
            return;
        }
        if (!plugin.getConfiguration().WORLDGUARD_PROTECT) {
            return;
        }
        if (!plugin.hasWG()) {
            return;
        }

        Player player;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player) {
                player = (Player) projectile.getShooter();
            } else {
                return;
            }
        } else {
            return;
        }

        boolean isProhibited = WorldGuardHelper.isVillagerViolenceProhibited(player, villager);
        if (isProhibited) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You don't have permission to harm villagers in this region.", NamedTextColor.RED));
        }
    }

    /**
     * Prevent players from applying name tags to villagers when they can't
     * build at the villager location.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        // There's no trivial way to map EquipmentSlot enum to Inventory index.
        if (event.getHand() == EquipmentSlot.HAND &&
                player.getEquipment().getItemInMainHand().getType() != Material.NAME_TAG) {
            return;
        }
        if (event.getHand() == EquipmentSlot.OFF_HAND &&
                player.getEquipment().getItemInOffHand().getType() != Material.NAME_TAG) {
            return;
        }

        if (!WorldGuardHelper.canBuild(player, event.getRightClicked().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You can't rename that villager in this region.", NamedTextColor.RED));
        }
    }

    /**
     * Handle WorldGuard based protection from players' potions
     */
    @EventHandler
    public void onAreaEffectApply(AreaEffectCloudApplyEvent event) {
        if (!plugin.getConfiguration().WORLDGUARD_PROTECT) {
            return;
        }
        if (!(event.getEntity().getSource() instanceof Player player)) {
            return;
        }
        if (!plugin.hasWG()) {
            return;
        }

        PotionType type = event.getEntity().getBasePotionType();
        List<PotionType> blacklist = new ArrayList<>();
        blacklist.add(PotionType.HARMING);
        blacklist.add(PotionType.POISON);
        blacklist.add(PotionType.SLOWNESS);
        blacklist.add(PotionType.WEAKNESS);

        Iterator<LivingEntity> iterator = event.getAffectedEntities().iterator();
        while (iterator.hasNext()) {
            LivingEntity ent = iterator.next();
            if (ent instanceof AbstractVillager && blacklist.contains(type)) {
                if (WorldGuardHelper.isVillagerViolenceProhibited(player, (AbstractVillager) ent)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Prevent blacklisted mob types from harming villagers. e.g. EVOKER,
     * EVOKER_FANGS, VEX, VINDICATOR
     */
    @EventHandler
    public void onEntityDamageByMob(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof AbstractVillager)) {
            return;
        }
        if (plugin.getConfiguration().PROTECT_FROM_MOBS.isEmpty()) return;

        Entity damager = event.getDamager();
        boolean isProhibited = false;

        if (plugin.getConfiguration().PROTECT_FROM_MOBS.contains(event.getDamager().getType())) {
            isProhibited = true;
        } else if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Entity shooter &&
                    !(shooter instanceof Player) &&
                    plugin.getConfiguration().PROTECT_FROM_MOBS.contains(shooter.getType())) {
                isProhibited = true;
            }
        }

        if (isProhibited) {
            event.setCancelled(true);
        }
    }
}