package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class FriendFeetPosition extends PositionBase {
    public FriendFeetPosition() {
        super("FriendFeet");
        this.setIcon("IRON_BOOTS");
        this.setFriendDependable(true);
    }

    public Location getPosition(Player player, Player friend) {
        return friend == null ? player.getLocation() : friend.getLocation();
    }
}
