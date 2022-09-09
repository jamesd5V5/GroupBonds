package org.mammothplugins.groupbonds.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mammothplugins.groupbonds.PlayerCache;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mammothplugins.groupbonds.patterns.ShapeBase;
import org.mineacademy.fo.remain.Remain;

public class PlayPatternTask extends BukkitRunnable {
    private Player player;
    private PlayerCache.BondCache bondCache;
    private BondBase.TierCache.PatternCache patternCache;
    private Entity target;
    public boolean cancelEvent = false;

    public PlayPatternTask(Player player, PlayerCache.BondCache bondCache, BondBase.TierCache.PatternCache patternCache) {
        this.patternCache = patternCache;
        this.bondCache = bondCache;
        this.player = player;
    }

    public PlayPatternTask(Player player, PlayerCache.BondCache bondCache, BondBase.TierCache.PatternCache patternCache, Entity target) {
        this.patternCache = patternCache;
        this.bondCache = bondCache;
        this.player = player;
        this.target = target;
    }

    @Override
    public void run() {
        String action = this.patternCache.getActionName();
        if (bondCache.isAllowed(player)) {
            PlayerCache playerCache = PlayerCache.from(player);
            BondBase bondBase = bondCache.getBondBase();
            BondBase.TierCache appliedTierCache = bondBase.getTierCache(player);

            int tierRange = appliedTierCache.getRange();
            String actionName = bondBase.getActionName(appliedTierCache.getTier(), this.patternCache.getNumber());
            boolean isInside = bondBase.isInside(appliedTierCache.getTier(), this.patternCache.getNumber());

            Entity otherEntity;
            if (isInside) {
                for (Entity entity : player.getNearbyEntities(tierRange, tierRange, tierRange))
                    if (entity instanceof Player && !entity.getUniqueId().toString().equals(this.bondCache.getFriendUUID()))
                        this.playPattern(actionName, (Player) entity, bondBase, appliedTierCache, action, this.target);
            } else {
                boolean hasFoundFriend = false;
                for (Player onlinePlayer : Remain.getOnlinePlayers())
                    if (onlinePlayer.getUniqueId().equals(playerCache.getFriend(bondBase))) {
                        this.playPattern(actionName, onlinePlayer, bondBase, appliedTierCache, action, this.target);
                        hasFoundFriend = true;
                    }

                if (!hasFoundFriend) {
                    otherEntity = null;
                    this.playPattern(actionName, (Player) otherEntity, bondBase, appliedTierCache, action, this.target);
                }
            }
        }
    }

    private void playPattern(String actionName, Player friend, BondBase bondBase, BondBase.TierCache appliedTierCache, String action, Entity target) {
        boolean activatedEvent = false;
        boolean resetEvent = false;
        boolean playShape = false;
        boolean playPower = false;
        ActionBase actionBase = ActionBase.getByName(actionName);
        PowerBase powerBase = PowerBase.getPower("Toss");
        if (actionName.equals("Automatic") && action.equals("Others"))
            activatedEvent = true;

        if (actionName.equals("Staying Still") && action.equals("Others"))
            activatedEvent = true;


        if (!activatedEvent) {
            if (actionBase.getType().equals("General") && actionName.equals(action)) {
                activatedEvent = true;
                resetEvent = true;
            }
            if (actionBase.getType().equals("Selective") && target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                if (actionName.equals(action)) {
                    activatedEvent = true;
                    resetEvent = true;
                }

                if (actionName.equals(action) && ShapeBase.getShape(this.patternCache.getShapeName()) != null) {
                    powerBase = PowerBase.getPower(this.patternCache.getPowerName());
                    powerBase.playPower(this.player, friend, bondBase, appliedTierCache, this.patternCache, actionName, this.patternCache.getStrength(), livingTarget);
                    playPower = false;
                }
            }
        }

        if (activatedEvent) {
            ShapeBase shapeBase = ShapeBase.DOT;
            if (!this.patternCache.getShapeName().equals("NONE")) {
                playShape = ShapeBase.getShape(this.patternCache.getShapeName()) != null;
                shapeBase = ShapeBase.getShape(this.patternCache.getShapeName());
            }

            if (!this.patternCache.getPowerName().equals("NONE")) {
                playPower = PowerBase.getPower(this.patternCache.getPowerName()) != null;
                powerBase = PowerBase.getPower(this.patternCache.getPowerName());
            }

            if (playShape) {
                shapeBase.playShape(this.player, friend, this.patternCache);
            }

            if (playPower)
                powerBase.playPower(this.player, friend, bondBase, appliedTierCache, this.patternCache, actionName, this.patternCache.getStrength());
        }
        if (resetEvent)
            this.resetEvent(this.player, this.bondCache, this.patternCache);
    }

    public boolean isACancelledEvent() {
        return this.cancelEvent;
    }

    private void resetEvent(Player player, PlayerCache.BondCache bondCache, BondBase.TierCache.PatternCache patternCache) {
        bondCache.resetCanThePatternRun(player, patternCache);
        this.cancelEvent = true;
    }
}
