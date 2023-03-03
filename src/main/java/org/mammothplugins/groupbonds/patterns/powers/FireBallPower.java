package org.mammothplugins.groupbonds.patterns.powers;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mammothplugins.groupbonds.GroupBonds;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;

import java.util.Arrays;

public class FireBallPower extends PowerBase {
    public FireBallPower() {
        super("Fireball");
        this.setIcon("GHAST_TEAR");
        this.setLore(Arrays.asList("&eSelective & General:", "&7The player will", "&7launch a fireball."));
    }

    @Override
    public void playPower(final Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if (actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) {
            final Fireball fireball = (Fireball) player.getWorld().spawn(player.getLocation().add(player.getLocation().getDirection().add(new Vector(0.0, 1.5, 0.0))), Fireball.class);
            fireball.setDirection(player.getLocation().getDirection());
            CompSound.GHAST_FIREBALL.play(player.getLocation(), 10.0F, -50.0F);
            (new BukkitRunnable() {
                public void run() {
                    if (!fireball.isDead()) {
                        CompParticle.CLOUD.spawn(fireball.getLocation());
                        fireball.remove();
                    }

                    //if (player.isOnline())
                    //CompSound.GHAST_FIREBALL.play(player.getLocation(), 10.0F, -50.0F);
                }
            }).runTaskLater(GroupBonds.getInstance(), (long) (patternCache.getDuration() * 20));
        }

    }

    @Override
    public void playPower(final Player player, Player friend, BondBase friendBondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && (friend != null && !target.getUniqueId().equals(friend.getUniqueId()) || friend == null)) {
            final Fireball fireball = (Fireball) player.getWorld().spawn(player.getLocation().add(player.getLocation().getDirection().add(new org.bukkit.util.Vector(0.0, 1.5, 0.0))), Fireball.class);
            fireball.setDirection(player.getLocation().getDirection());
            CompSound.GHAST_FIREBALL.play(player.getLocation(), 10.0F, -50.0F);
            (new BukkitRunnable() {
                public void run() {
                    if (!fireball.isDead()) {
                        CompParticle.CLOUD.spawn(fireball.getLocation());
                        fireball.remove();
                    }

                    //if (player.isOnline())
                    //CompSound.GHAST_FIREBALL.play(player.getLocation(), 10.0F, -50.0F);
                }
            }).runTaskLater(GroupBonds.getInstance(), (long) (patternCache.getDuration() * 20));
        }
    }
}
