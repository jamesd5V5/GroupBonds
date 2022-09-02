package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class AboveFriendPosition extends PositionBase {
    public AboveFriendPosition() {
        super("AboveFriend");
        this.setIcon("LIGHT_GRAY_WOOL");
        this.setFriendDependable(true);
    }

    public Location getPosition(Player player, Player friend) {
        return friend == null ? player.getLocation().add(0.0, 3.0, 0.0) : friend.getLocation().add(0.0, 3.0, 0.0);
    }
}
