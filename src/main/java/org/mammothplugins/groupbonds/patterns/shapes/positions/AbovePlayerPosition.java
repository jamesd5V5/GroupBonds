package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class AbovePlayerPosition extends PositionBase {
    public AbovePlayerPosition() {
        super("AbovePlayer");
        this.setIcon("WHITE_WOOL");
        this.setFriendDependable(false);
    }

    public Location getPosition(Player player, Player friend) {
        return player.getLocation().add(0.0, 3.0, 0.0);
    }
}
