package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class BelowPlayerPosition extends PositionBase {
    public BelowPlayerPosition() {
        super("BelowPlayer");
        this.setIcon("COAL_ORE");
        this.setFriendDependable(false);
    }

    public Location getPosition(Player player, Player friend) {
        return player.getLocation().add(0.0, -1.0, 0.0);
    }
}
