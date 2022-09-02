package org.mammothplugins.groupbonds.patterns.shapes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.patterns.shapes.positions.*;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.*;

public class PositionBase extends YamlConfig {

    private static final Map<String, PositionBase> positions = new HashMap();

    //When adding a new Postion, make sure to check getPosition(name)...
    public static final PositionBase CENTER = new CenterPosition();

    public static final PositionBase PLAYER = new PlayerPosition();
    public static final PositionBase PLAYERHEAD = new PlayerHeadPosition();
    public static final PositionBase PLAYERFEET = new PlayerFeetPosition();
    public static final PositionBase ABOVEPLAYER = new AbovePlayerPosition();
    public static final PositionBase BELOWPLAYER = new BelowPlayerPosition();

    public static final PositionBase FRIEND = new FriendPosition();
    public static final PositionBase FRIENDHEAD = new FriendHeadPosition();
    public static final PositionBase FRIENDFEET = new FriendFeetPosition();
    public static final PositionBase ABOVEFRIEND = new AboveFriendPosition();
    public static final PositionBase BELOWFRIEND = new BelowFriendPosition();

    private String name;
    private String icon;
    private boolean isFriendDependable;

    protected PositionBase(String positionName) {
        this.name = removeYmlExtension(positionName);

        this.loadConfiguration(NO_DEFAULT, "BondCharacteristics/Positions/" + name + ".yml");
        this.setHeader("GroupBonds\nPosition: " + name);
        this.save();
    }

    @Override
    protected void onSave() {
        this.set("Icon", this.icon);
        this.set("Friend_Dependable", this.isFriendDependable);
    }

    @Override
    protected void onLoad() {
        this.icon = this.getString("Icon", "MAP");
        this.isFriendDependable = this.getBoolean("Friend_Dependable", false);
    }

    public static void onStart() {
        File[] files = FileUtil.getFiles("BondCharacteristics/Positions/", "yml");
        for (File position : files) {
            try {
                PositionBase positionBase = new PositionBase(PositionBase.removeYmlExtension(position.getName()));
                String positionName = PositionBase.removeYmlExtension(position.getName());
                if (!positions.containsKey(positionName))
                    positions.put(positionName, positionBase);

            } catch (FoException exception) {
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    public boolean isFriendDependable() {
        return this.isFriendDependable;
    }

    public void setIcon(String legacyName) {
        this.icon = legacyName;
    }

    public void setFriendDependable(boolean isFriendDependable) {
        this.isFriendDependable = isFriendDependable;
    }

    public Location getPosition(Player player, Player friend) {
        return player.getLocation();
    }

    public static final PositionBase getPosition(String name) {
        if (name.equals("Player"))
            return new PlayerPosition();
        if (name.equals("PlayerHead"))
            return new PlayerHeadPosition();
        if (name.equals("PlayerFeet"))
            return new PlayerFeetPosition();
        if (name.equals("Friend"))
            return new FriendPosition();
        if (name.equals("FriendHead"))
            return new FriendHeadPosition();
        if (name.equals("FriendFeet"))
            return new FriendFeetPosition();
        if (name.equals("Center"))
            return new CenterPosition();
        if (name.equals("BelowPlayer"))
            return new BelowPlayerPosition();
        if (name.equals("BelowFriend"))
            return new BelowFriendPosition();
        if (name.equals("AbovePlayer"))
            return new AbovePlayerPosition();
        if (name.equals("AboveFriend"))
            return new AboveFriendPosition();
        return (PositionBase) positions.get(name);
    }

    public static final Collection<PositionBase> getPositions() {
        return Collections.unmodifiableCollection(positions.values());
    }

    public static final List<String> getPositionNames() {
        List<String> sortedPositions = new ArrayList(positions.keySet());
        sortedPositions.remove("Center");
        sortedPositions.add(0, "Center");

        sortedPositions.remove("AbovePlayer");
        sortedPositions.add(1, "AbovePlayer");
        sortedPositions.remove("AboveFriend");
        sortedPositions.add(2, "AboveFriend");
        sortedPositions.remove("PlayerHead");
        sortedPositions.add(3, "PlayerHead");
        sortedPositions.remove("FriendHead");
        sortedPositions.add(4, "FriendHead");
        sortedPositions.remove("Player");
        sortedPositions.add(5, "Player");
        sortedPositions.remove("Friend");
        sortedPositions.add(6, "Friend");
        sortedPositions.remove("PlayerFeet");
        sortedPositions.add(7, "PlayerFeet");
        sortedPositions.remove("FriendFeet");
        sortedPositions.add(8, "FriendFeet");
        sortedPositions.remove("BelowPlayer");
        sortedPositions.add(9, "BelowPlayer");
        sortedPositions.remove("BelowFriend");
        sortedPositions.add(10, "BelowFriend");
        return sortedPositions;
    }

    private static String removeYmlExtension(String string) {
        if (string.endsWith(".yml"))
            return string.substring(0, string.length() - 4);
        else return string;
    }
}
