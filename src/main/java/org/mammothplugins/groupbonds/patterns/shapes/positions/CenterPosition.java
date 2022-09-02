package org.mammothplugins.groupbonds.patterns.shapes.positions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;

public class CenterPosition extends PositionBase {
    public CenterPosition() {
        super("Center");
        this.setIcon("FURNACE");
        this.setFriendDependable(true);
    }

    public Location getPosition(Player player, Player friend) {
        if (friend == null) {
            return player.getLocation();
        } else {
            Location center = player.getLocation();
            if (friend.getWorld().equals(player.getWorld())) {
                double x = (player.getLocation().getX() + friend.getLocation().getX()) / 2.0;
                double y = (player.getLocation().getY() + friend.getLocation().getY()) / 2.0;
                double z = (player.getLocation().getZ() + friend.getLocation().getZ()) / 2.0;
                return new Location(player.getWorld(), x, y, z);
            } else {
                return center;
            }
        }
    }
}
