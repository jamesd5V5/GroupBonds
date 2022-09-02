package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class FriendPosition extends PositionBase {
    public FriendPosition() {
        super("Friend");
        this.setIcon("ZOMBIE_HEAD");
        this.setFriendDependable(true);
    }

    public Location getPosition(Player player, Player friend) {
        return friend == null ? player.getLocation().add(0.0, 1.0, 0.0) : friend.getLocation().add(0.0, 1.0, 0.0);
    }
}
