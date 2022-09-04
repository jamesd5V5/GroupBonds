package org.mammothplugins.groupbonds.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
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
                boolean hasItemInHand = true;
                for (BondBase.TierCache.PatternCache patternCache : bondCache.getBondBase().getPatterns(bondCache.getTier())) {
                    if (patternCache.getActionName().equals(actionName)) {
                        String actionItemName = patternCache.getActionItemName();
                        if (actionItemName.equals(CompMaterial.fromItem(player.getItemInHand()).name()) || actionItemName.equals("Anything") || actionItemName.equals("Hand")) {
                            if (actionItemName.equals("Hand") && player.getItemInHand().getItemMeta() != null)
                                hasItemInHand = false;
                            if (hasItemInHand && bondCache.checkLastRunTime(patternCache)) {
                                PlayPatternTask playPatternTask = new PlayPatternTask(player, bondCache, patternCache, target);
                                playPatternTask.run();
                                playPatternTaskList.add(playPatternTask);
                            }
                        }
                    }
                }
            }
        }
        return playPatternTaskList;
    }


    @EventHandler
    public void onPlayerBreakBlockEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        callActionWithCancel(player, "BreakBlock");
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        callActionWithCancel(player, "Consume");
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        List<PlayPatternTask> playPatternTaskList = callActionWithCancel(player, "ItemDrop");
        for (PlayPatternTask playPatternTask : playPatternTaskList)
            if (playPatternTask.isACancelledEvent())
                event.setCancelled(playPatternTask.isACancelledEvent());
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getY() > event.getFrom().getY()) {
            callAction(player, "Jump");
        }

        //callAction(player, "Move");
        //if (CheckingMovementTask.getMovingPlayers().containsKey(player.getUniqueId()) && !(Boolean)CheckingMovementTask.getMovingPlayers().get(player.getUniqueId())) {
        //    CheckingMovementTask.setMovingPlayers(player.getUniqueId(), true);
        //}


    }

    @EventHandler
    public void onPlayerToggleShiftEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        callAction(player, "Toggle Shift");
    }

    @EventHandler
    public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        callAction(player, "Toggle Sprint");
    }

    @EventHandler
    public void onPlayerLeftClick(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            List<PlayPatternTask> playPatternTaskList = callActionWithCancel(player, "LeftClick");
            for (PlayPatternTask playPatternTask : playPatternTaskList)
                if (playPatternTask.isACancelledEvent())
                    event.setCancelled(playPatternTask.isACancelledEvent());
        }
    }

    @EventHandler
    public void onPlayerRightClickEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        List<PlayPatternTask> playPatternTaskList = callActionWithCancel(player, "RightClick Entity", event.getRightClicked());
        for (PlayPatternTask playPatternTask : playPatternTaskList)
            if (playPatternTask.isACancelledEvent())
                event.setCancelled(playPatternTask.isACancelledEvent());
    }

    /*
        @EventHandler
        public void onClickAir(PlayerInteractEvent event) {
            Player player = event.getPlayer();
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                List<PlayPatternTask> playPatternTaskList = callActionWithCancel(player, "RightClick");
                for (PlayPatternTask playPatternTask : playPatternTaskList)
                    if (playPatternTask.isACancelledEvent())
                        event.setCancelled(playPatternTask.isACancelledEvent());
            }

            if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
                List<PlayPatternTask> playPatternTaskList = callActionWithCancel(player, "LeftClick");
                for (PlayPatternTask playPatternTask : playPatternTaskList)
                    if (playPatternTask.isACancelledEvent())
                        event.setCancelled(playPatternTask.isACancelledEvent());
            }

        }




        @EventHandler
        public void onPlayerPickUpArrowEvent(PlayerPickupArrowEvent event) {
            Player player = event.getPlayer();
            callActionWithCancel(player, "PickupArrow");
        }

    @EventHandler
    public void onPlayerPickUpItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        callActionWithCancel(player, "PickupItem");
    }

     */

/*
    @EventHandler
    public void onPlayerDamaged(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Entity damager = event.getDamager();
            if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                Projectile projectile = (Projectile) event.getDamager();
                LivingEntity shooter = (LivingEntity) projectile.getShooter();
                damager = shooter;
            }

            List<PlayPatternTask> playPatternTaskList = callActionWithCancel(player, "Player Damage", (Entity) damager);
            for (PlayPatternTask playPatternTask : playPatternTaskList)
                if (playPatternTask.isACancelledEvent())
                    event.setCancelled(playPatternTask.isACancelledEvent());

        }
    }

 */

}
