package org.mammothplugins.groupbonds.events;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.Remain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckMovingTask extends BukkitRunnable {
    //todo Remove this If not doing staying still
    public static Map<UUID, Boolean> movingPlayers = new HashMap();

    @Override
    public void run() {
        for (Player player : Remain.getOnlinePlayers()) {
            if (movingPlayers.containsKey(player.getUniqueId()))
                movingPlayers.replace(player.getUniqueId(), false);
        }

    }

    public static Map<UUID, Boolean> getMovingPlayers() {
        return movingPlayers;
    }

    public static void addUUID(UUID uuid) {
        if (!movingPlayers.containsKey(uuid))
            movingPlayers.put(uuid, false);
    }

    public static void removeUUID(UUID uuid) {
        if (movingPlayers.containsKey(uuid))
            movingPlayers.remove(uuid);
    }

    public static void setMovingPlayer(UUID uuid, boolean isMoving) {
        if (movingPlayers.containsKey(uuid))
            movingPlayers.replace(uuid, true);
    }
}
