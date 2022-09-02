package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class FriendHeadPosition extends PositionBase {
    public FriendHeadPosition() {
        super("FriendHead");
        this.setIcon("IRON_HELMET");
        this.setFriendDependable(true);
    }

    public Location getPosition(Player player, Player friend) {
        return friend == null ? player.getLocation().add(0.0, 2.0, 0.0) : friend.getLocation().add(0.0, 2.0, 0.0);
    }
}
