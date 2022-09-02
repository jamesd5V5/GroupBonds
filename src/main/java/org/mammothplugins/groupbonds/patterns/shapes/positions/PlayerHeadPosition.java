package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class PlayerHeadPosition extends PositionBase {
    public PlayerHeadPosition() {
        super("PlayerHead");
        this.setIcon("CHAINMAIL_HELMET");
        this.setFriendDependable(false);
    }

    public Location getPosition(Player player, Player friend) {
        return player.getLocation().add(0.0, 2.0, 0.0);
    }
}
