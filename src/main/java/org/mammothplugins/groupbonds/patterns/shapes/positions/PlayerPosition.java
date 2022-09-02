package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class PlayerPosition extends PositionBase {
    public PlayerPosition() {
        super("Player");
        this.setIcon("PLAYER_HEAD");
        this.setFriendDependable(false);
    }

    public Location getPosition(Player player, Player friend) {
        return player.getLocation().add(0.0, 1.0, 0.0);
    }
}
