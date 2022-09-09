package org.mammothplugins.groupbonds.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mammothplugins.groupbonds.GroupBonds;
import org.mammothplugins.groupbonds.PlayerCache;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;

public class JoinAndLeaveEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CheckMovingTask.addUUID(player.getUniqueId());

        PlayerCache playerCache = PlayerCache.from(player);
        for (PlayerCache.BondCache bondCache : playerCache.getBondCaches())
            if (bondCache.isHasLeftBeforeExitCommand())
                for (BondBase.TierCache tierCache : bondCache.getBondBase().getTiers())
                    for (BondBase.TierCache.PatternCache patternCache : tierCache.getPatterns()) {
                        if (patternCache.hasPower()) {
                            PowerBase powerBase = PowerBase.getPower(patternCache.getPowerName());
                            if (PowerBase.customPowerNames.contains(powerBase.getName())) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        powerBase.runExitCommandWithOutDelay(player, (double) patternCache.getStrength());
                                        playerCache.setHasLeftBeforeExitCommand(bondCache.getBondBase(), false);
                                    }
                                }.runTaskLater(GroupBonds.getInstance(), 20);
                            }
                        }
                    }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        CheckMovingTask.removeUUID(player.getUniqueId());
    }
}
