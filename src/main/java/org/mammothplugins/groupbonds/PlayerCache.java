package org.mammothplugins.groupbonds;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.YamlConfig;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
public final class PlayerCache extends YamlConfig {

    private static volatile Map<UUID, PlayerCache> cacheMap = new HashMap<>();

    private final UUID uniqueId;

    private String wow;

    private Set<BondCache> bondCaches;

    private PlayerCache(UUID uniqueId) {
        this.uniqueId = uniqueId;

        this.setPathPrefix("Players." + uniqueId.toString());
        this.loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
    }

    @Override
    protected void onLoad() {
        this.wow = get("Wow", String.class);
        this.bondCaches = this.getSet("Bonds", BondCache.class);
    }

    @Override
    public void onSave() {
        this.set("Wow", this.wow);
        this.set("Bonds", this.bondCaches);
    }

    public void setWow(String wow) {
        this.wow = wow;
        save();
    }

    /* ------------------------------------------------------------------------------- */
    /* Bonds */
    /* ------------------------------------------------------------------------------- */
    public BondCache getBond(BondBase bondBase) {
        for (BondCache bondCache : this.bondCaches) {
            if (bondCache != null && bondCache.getBondBase().getName().equals(bondBase.getName())) {
                return bondCache;
            }
        }
        return null;
    }

    public boolean hasBond(BondBase bondBase) {
        return this.getBond(bondBase) != null;
    }

    public void addBond(BondBase bondBase, UUID friendUUID) {
        Valid.checkNotNull(bondBase, "The bond must not be null!");
        if (!this.hasBond(bondBase) && !this.getFriend(bondBase).equals(friendUUID)) {
            Map<Integer, Long> patternExecution = new HashMap<Integer, Long>();
            for (final BondBase.TierCache.PatternCache patternCache : bondBase.getPatterns(1))
                patternExecution.put(patternCache.getNumber(), System.currentTimeMillis());

            this.bondCaches.add(new BondCache(bondBase, friendUUID, 1, false, patternExecution));
            this.save("Bonds", this.bondCaches);
        }
    }

    public void removeBond(BondBase bondBase) {
        BondCache bondCache = this.getBond(bondBase);
        if (bondCache != null) {
            this.bondCaches.remove(bondCache);
            this.save("Bonds", this.bondCaches);
        }
    }

    public void clearBonds() {
        this.bondCaches.clear();
        this.save("Bonds", null);
    }

    public boolean getHasLeftBeforeExitCommand(BondBase bondBase) {
        final BondCache bondCache = this.getBond(bondBase);
        return bondCache != null && bondCache.isHasLeftBeforeExitCommand();
    }

    public void setHasLeftBeforeExitCommand(BondBase bondBase, boolean hasLeftBeforeExitCommand) {
        final BondCache bondCache = this.getBond(bondBase);
        bondCache.setHasLeftBeforeExitCommand(hasLeftBeforeExitCommand);
        this.save("Bonds", this.bondCaches); //todo IDK if this will work
    }

    /* ------------------------------------------------------------------------------- */
    /* Friend */
    /* ------------------------------------------------------------------------------- */
    public UUID getFriend(BondBase bondBase) {
        BondCache bondCache = this.getBond(bondBase);
        return (bondCache != null) ? bondCache.getFriendUUID() : UUID.fromString("a586293e-3c5a-46a2-afb4-8ebfd43a9a6d");
    }

    public boolean hasFriend(BondBase bondBase) {
        return this.getFriend(bondBase) != null;
    }

    /* ------------------------------------------------------------------------------- */
    /* Players */
    /* ------------------------------------------------------------------------------- */
    @Nullable
    public Player toPlayer() {
        final Player player = Remain.getPlayerByUUID(this.uniqueId);

        return player != null && player.isOnline() ? player : null;
    }

    public void removeFromMemory() {
        synchronized (cacheMap) {
            cacheMap.remove(this.uniqueId);
        }
    }

    @Override
    public String toString() {
        return "PlayerCache{" + this.uniqueId + "}";
    }

    public int getBondTier(BondBase bondBase) {
        final BondCache bondCache = this.getBond(bondBase);
        return (bondCache != null) ? bondCache.getTier() : 0;
    }

    /* ------------------------------------------------------------------------------- */
    /* Static access */
    /* ------------------------------------------------------------------------------- */

    public static PlayerCache from(Player player) {
        synchronized (cacheMap) {
            final UUID uniqueId = player.getUniqueId();
            final String playerName = player.getName();

            PlayerCache cache = cacheMap.get(uniqueId);

            if (cache == null) {
                cache = new PlayerCache(uniqueId);

                cacheMap.put(uniqueId, cache);
            }

            return cache;
        }
    }

    public static PlayerCache from(UUID uuid) {
        PlayerCache cache = PlayerCache.cacheMap.get(uuid);
        if (cache == null) {
            cache = new PlayerCache(uuid);
            PlayerCache.cacheMap.put(uuid, cache);
        }
        return cache;
    }

    public static void clearCaches() {
        synchronized (cacheMap) {
            cacheMap.clear();
        }
    }

    public static final class BondCache implements ConfigSerializable {
        private final BondBase bondBase;
        private final UUID friendUUID;
        private int tier;
        private boolean hasLeftBeforeExitCommand;
        public Map<Integer, Long> patternExecution;

        public BondCache(BondBase bondBase, UUID friendUUID, int tier, boolean hasLeftBeforeExitCommand, Map<Integer, Long> patternExecution) {
            this.bondBase = bondBase;
            this.friendUUID = friendUUID;
            this.tier = tier;
            this.hasLeftBeforeExitCommand = hasLeftBeforeExitCommand;
            this.patternExecution = patternExecution;
        }

        public static BondCache deserialize(SerializedMap map) {
            String name = map.getString("Name");
            Valid.checkNotNull(name, "Could not the name of the bond.");
            BondBase base = BondBase.getByName(name);
            Valid.checkNotNull(base, "Could not find BondBase with the name " + name + ".");
            UUID friend = map.getUUID("FriendUUID");
            Valid.checkNotNull(friend, "Could not find the friend " + friend.toString() + ".");
            Integer tier = map.getInteger("Tier");
            Valid.checkNotNull(tier, "Bond " + name + " lacks the Tier key!");
            boolean hasLeftBeforeExitCommand = map.getBoolean("Has Left Before Exit Command");
            Valid.checkNotNull(hasLeftBeforeExitCommand, "The Bond " + name + " lacks a boolean!");
            final Map<Integer, Long> patternExecution = new HashMap<Integer, Long>();
            for (final BondBase.TierCache.PatternCache patternCache : base.getPatterns(tier)) {
                patternExecution.put(patternCache.getNumber(), System.currentTimeMillis());
            }
            return new BondCache(base, friend, tier, hasLeftBeforeExitCommand, patternExecution);
        }

        @Override
        public SerializedMap serialize() {
            SerializedMap map = new SerializedMap();
            map.put("Name", this.bondBase.getName());
            map.put("FriendUUID", this.friendUUID);
            map.put("Tier", this.tier);
            map.put("Has Left Before Exit Command", this.hasLeftBeforeExitCommand);
            return map;
        }

        public boolean checkLastRunTime(BondBase.TierCache.PatternCache patternCache) {
            final int delay = patternCache.getDelay();
            if (this.patternExecution.get(patternCache.getNumber()) == null) {
                this.patternExecution.put(patternCache.getNumber(), System.currentTimeMillis());
            }
            final long lastExecutedTime = this.patternExecution.get(patternCache.getNumber());
            if (delay == 0) {
                return true;
            }
            if (lastExecutedTime == 0L || System.currentTimeMillis() - lastExecutedTime > delay * 1000) {
                this.updateLastExecutedTime(patternCache);
                return true;
            }
            return false;
        }

        private void updateLastExecutedTime(final BondBase.TierCache.PatternCache patternCache) {
            this.patternExecution.put(patternCache.getNumber(), System.currentTimeMillis());
        }

        public void resetCanThePatternRun(final Player player, final BondBase.TierCache.PatternCache patternCache) {
            final int delay = patternCache.getDelay();
            final String powerName = patternCache.getPowerName();
            if (delay != 0 && !powerName.equals("NONE")) {
                Remain.sendActionBar(player, "&7The Power " + powerName + " will be recharged in " + delay + " seconds.");
                new BukkitRunnable() {
                    public void run() {
                        Remain.sendActionBar(player, "&7The Power " + powerName + " is ready to use.");
                        CompSound.LEVEL_UP.play(player);
                    }
                }.runTaskLater((Plugin) SimplePlugin.getInstance(), (long) (patternCache.getDelay() * 20));
            }
            this.updateLastExecutedTime(patternCache);
        }


        public BondBase getBondBase() {
            return this.bondBase;
        }

        public boolean isAllowed(final Player player) {
            final boolean isEnabled = this.bondBase.isEnabled();
            if (!isEnabled) {
                return false;
            }
            for (final String worldName : this.bondBase.getDisabledWorlds()) {
                if (player.getWorld().getName().equals(worldName)) {
                    return false;
                }
            }
            return true;
        }

        public BondBase getFriendBondBase() {
            return this.bondBase;
        }

        public int getTier() {
            return this.tier;
        }

        public UUID getFriendUUID() {
            return this.friendUUID;
        }

        public boolean isHasLeftBeforeExitCommand() {
            return this.hasLeftBeforeExitCommand;
        }

        private void setHasLeftBeforeExitCommand(boolean hasLeftBeforeExitCommand) {
            this.hasLeftBeforeExitCommand = hasLeftBeforeExitCommand;
        }

        public Map<Integer, Long> getPatternExecution() {
            return this.patternExecution;
        }
    }
}
