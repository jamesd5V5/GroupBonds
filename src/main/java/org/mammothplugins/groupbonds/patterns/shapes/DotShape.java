package org.mammothplugins.groupbonds.patterns.shapes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mammothplugins.groupbonds.GroupBonds;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ShapeBase;
import org.mineacademy.fo.remain.CompParticle;

import java.util.concurrent.atomic.AtomicInteger;

public class DotShape extends ShapeBase {
    public DotShape() {
        super("Dot");
        this.setIcon("GHAST_TEAR");
    }

    public void playShape(Player player, Player friend, BondBase.TierCache.PatternCache patternCache) {
        if (!PositionBase.getPosition(patternCache.getPositionName()).isFriendDependable() || friend != null) {
            int duration = patternCache.getDuration();
            final int iterations = duration * 20;
            final CompParticle particle = CompParticle.valueOf(patternCache.getParticleName());
            final AtomicInteger start = new AtomicInteger();
            (new BukkitRunnable() {
                public void run() {
                    if (player.isOnline()) {
                        if (PositionBase.getPosition(patternCache.getPositionName()).isFriendDependable() && friend == null) {
                            this.cancel();
                        } else {
                            Location location = PositionBase.getPosition(patternCache.getPositionName()).getPosition(player, friend);
                            particle.spawn(location);
                            start.getAndIncrement();
                            if (start.doubleValue() >= (double) (iterations / 5) || iterations == 0 || !player.isOnline()) {
                                this.cancel();
                            }

                        }
                    }
                }
            }).runTaskTimerAsynchronously(GroupBonds.getInstance(), 0L, 5L);
        }
    }
}
