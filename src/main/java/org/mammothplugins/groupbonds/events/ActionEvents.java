package org.mammothplugins.groupbonds.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.mammothplugins.groupbonds.PlayerCache;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.List;

public class ActionEvents implements Listener {

    public static void callAction(Player player, String actionName) {
        callActionWithCancel(player, actionName);
    }

    private static List<PlayPatternTask> callActionWithCancel(Player player, String actionName) {
        List<PlayPatternTask> playPatternTaskList = new ArrayList();
        PlayerCache playerCache = PlayerCache.from(player);
        for (PlayerCache.BondCache bondCache : playerCache.getBondCaches()) {
            boolean hasItemInHand = true;
            for (BondBase.TierCache.PatternCache patternCache : bondCache.getBondBase().getPatterns(bondCache.getTier())) {
                if (patternCache.getActionName().equals(actionName)) {
                    String actionItemName = patternCache.getActionItemName();
                    if (actionItemName.equals(CompMaterial.fromItem(player.getItemInHand()).name()) || actionItemName.equals("Anything") || actionItemName.equals("Hand")) {
                        if (actionItemName.equals("Hand") && player.getItemInHand().getItemMeta() != null)
                            hasItemInHand = false;
                        if (hasItemInHand && bondCache.checkLastRunTime(patternCache)) {
                            PlayPatternTask playPatternTask = new PlayPatternTask(player, bondCache, patternCache);
                            playPatternTask.run();
                            playPatternTaskList.add(playPatternTask);
                        }
                    }
                }
            }
        }


        return playPatternTaskList;
    }

    private static List<PlayPatternTask> callActionWithCancel(Player player, String actionName, Entity target) {
        List<PlayPatternTask> playPatternTaskList = new ArrayList();
        if (target instanceof LivingEntity) {
            PlayerCache playerCache = PlayerCache.from(player);
            for (PlayerCache.BondCache bondCache : playerCache.getBondCaches()) {

            }
        }
        return playPatternTaskList;
    }

    @EventHandler
    public void onPlayerToggleShiftEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        callAction(player, "Toggle Shift");
    }

    @EventHandler
    public void onPlayerBreakBlockEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        callActionWithCancel(player, "BreakBlock");
    }

}
