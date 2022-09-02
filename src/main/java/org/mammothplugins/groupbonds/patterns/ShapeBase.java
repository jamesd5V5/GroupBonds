package org.mammothplugins.groupbonds.patterns;

import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.shapes.DotShape;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.*;

public class ShapeBase extends YamlConfig {
    private static final Map<String, ShapeBase> shapes = new HashMap();

    //When adding new Shapes, Make sure to add to getShape()....
    public static final ShapeBase DOT = new DotShape();

    private String name;
    private String icon;

    protected ShapeBase(String shapeName) {
        this.name = removeYmlExtension(shapeName);

        this.loadConfiguration(NO_DEFAULT, "BondCharacteristics/Shapes/" + name + ".yml");
        this.setHeader("GroupBonds\nShape: " + name);
        this.save();
    }

    @Override
    protected void onSave() {
        this.set("Icon", this.icon);
    }

    @Override
    protected void onLoad() {
        this.icon = this.getString("Icon", "BOOK");
    }

    public static void onStart() {
        File[] files = FileUtil.getFiles("BondCharacteristics/Shapes/", "yml");
        for (File shape : files) {
            try {
                ShapeBase shapeBase = new ShapeBase(ShapeBase.removeYmlExtension(shape.getName()));
                String shapeName = ShapeBase.removeYmlExtension(shape.getName());
                if (!shapes.containsKey(shapeName))
                    shapes.put(shapeName, shapeBase);

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

    public void setIcon(String legacyName) {
        this.icon = legacyName;
    }

    public static ShapeBase getShape(String name) {
        if (name.equals("Dot"))
            return new DotShape();

        return shapes.get(name);
    }

    public static final Collection<ShapeBase> getShapes() {
        return Collections.unmodifiableCollection(shapes.values());
    }

    public static final List<String> getShapeNames() {
        List<String> names = new ArrayList(shapes.keySet());
        Collections.sort(names);
        return names;
    }

    public void playShape(Player player, Player friend, BondBase.TierCache.PatternCache patternCache) {
        Common.broadcast("Plz dont play in this ShapeBase Play");
    }


    private static String removeYmlExtension(String string) {
        if (string.endsWith(".yml"))
            return string.substring(0, string.length() - 4);
        else return string;
    }
}
