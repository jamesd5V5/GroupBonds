package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class PlayerFeetPosition extends PositionBase {
    public PlayerFeetPosition() {
        super("PlayerFeet");
        this.setIcon("CHAINMAIL_BOOTS");
        this.setFriendDependable(false);
    }

    public Location getPosition(Player player, Player friend) {
        return player.getLocation();
    }
}
