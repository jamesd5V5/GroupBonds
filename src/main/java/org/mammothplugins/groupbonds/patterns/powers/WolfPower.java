package org.mammothplugins.groupbonds.patterns.powers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.scheduler.BukkitRunnable;
import org.mammothplugins.groupbonds.GroupBonds;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;

import java.util.Arrays;

public class WolfPower extends PowerBase {
    public WolfPower() {
        super("Summon Wolves");
        this.setUnits("Wolves");
        this.setIcon("BONE");
        this.setLore(Arrays.asList("&eSelective & General:", "&7The player will summmon", "&7wolves to fight for a limited amount of time.", "Wolves won't hurt friends."));
    }

    @Override
    public void playPower(final Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if (actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) {
            for (int i = 0; i < strength; i++) {
                Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class);
                wolf.setOwner(player);
                CompSound.WOLF_BARK.play(player.getLocation(), 10.0F, 50.0F);
                (new BukkitRunnable() {
                    public void run() {
                        if (!wolf.isDead()) {
                            CompParticle.CLOUD.spawn(wolf.getLocation());
                            wolf.remove();
                        }

                        if (player.isOnline())
                            CompSound.WOLF_BARK.play(player.getLocation(), 10.0F, -50.0F);
                        else
                            wolf.remove();
                    }
                }).runTaskLater(GroupBonds.getInstance(), (long) (patternCache.getDuration() * 20));
            }
        }

    }

    @Override
    public void playPower(final Player player, Player friend, BondBase friendBondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && (friend != null && !target.getUniqueId().equals(friend.getUniqueId()) || friend == null)) {
            if (!target.equals(friend))
                for (int i = 0; i < strength; i++) {
                    Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class);
                    wolf.setOwner(player);
                    CompSound.WOLF_BARK.play(player.getLocation(), 10.0F, 50.0F);
                    (new BukkitRunnable() {
                        public void run() {
                            if (!wolf.isDead()) {
                                CompParticle.CLOUD.spawn(wolf.getLocation());
                                wolf.remove();
                            }

                            if (player.isOnline())
                                CompSound.WOLF_BARK.play(player.getLocation(), 10.0F, -50.0F);
                            else
                                wolf.remove();
                        }
                    }).runTaskLater(GroupBonds.getInstance(), (long) (patternCache.getDuration() * 20));
                }
        }
    }
}
