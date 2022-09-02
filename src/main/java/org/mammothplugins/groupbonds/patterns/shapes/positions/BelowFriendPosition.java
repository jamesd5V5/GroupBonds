package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class BelowFriendPosition extends PositionBase {
    public BelowFriendPosition() {
        super("BelowFriend");
        this.setIcon("IRON_ORE");
        this.setFriendDependable(true);
    }

    public Location getPosition(Player player, Player friend) {
        return friend == null ? player.getLocation().add(0.0, -1.0, 0.0) : friend.getLocation().add(0.0, -1.0, 0.0);
    }
}
