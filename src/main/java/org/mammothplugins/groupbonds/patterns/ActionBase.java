package org.mammothplugins.groupbonds.patterns;

import org.mammothplugins.groupbonds.patterns.actions.BreakBlockAction;
import org.mammothplugins.groupbonds.patterns.actions.ShiftAction;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.*;

public class ActionBase extends YamlConfig {
    private static final Map<String, ActionBase> actions = new HashMap();

    public static final ActionBase BREAKBLOCK = new BreakBlockAction();
    public static final ActionBase SHIFT = new ShiftAction();

    private String name;
    private String type;
    private String icon;
    private int amount;

    protected ActionBase(String actionName) {
        this.name = removeYmlExtension(actionName);

        this.loadConfiguration(NO_DEFAULT, "BondCharacteristics/Actions/" + name + ".yml");
        this.setHeader("GroupBonds\nAction: " + name);
        this.save();

        actions.put(actionName, this);
    }

    public static void onStart() {
        File[] files = FileUtil.getFiles("BondCharacteristics/Actions/", "yml");
        for (File action : files) {
            try {
                ActionBase actionBase = new ActionBase(ActionBase.removeYmlExtension(action.getName()));
                String actionName = ActionBase.removeYmlExtension(action.getName());
                if (!actions.containsKey(actionName))
                    actions.put(actionName, actionBase);

            } catch (FoException exception) {
            }
        }
    }

    @Override
    protected void onSave() {
        this.set("Icon", this.icon);
        this.set("Type", this.type);
        this.set("Amount", this.amount);
    }

    @Override
    protected void onLoad() {
        this.icon = this.getString("Icon", "STONE");
        this.type = this.getString("Type", "General");
        this.amount = this.getInteger("Amount", 1);
    }

    public void setIcon(String legacyName) {
        this.icon = legacyName;
        this.save();
    }

    public void setAmount(int amount) {
        this.amount = amount;
        this.save();
    }

    public void setType(String type) {
        this.type = type;
        this.save();
    }

    public String getName() {
        return this.name;
    }

    public static final ActionBase getByName(String name) {
        return (ActionBase) actions.get(name);
    }

    public static final Collection<ActionBase> getActions() {
        return Collections.unmodifiableCollection(actions.values());
    }

    public static final List<String> getActionNames() {
        List<String> sortedActions = new ArrayList(actions.keySet());
        Collections.sort(sortedActions);
        /* //todo Come Back to this
        sortedActions.remove("Automatic");
        sortedActions.add(0, "Automatic");
        sortedActions.remove("LeftClick");
        sortedActions.add(1, "LeftClick");
        sortedActions.remove("RightClick Entity");
        sortedActions.add(2, "RightClick Entity");
        sortedActions.remove("RightClick");
        sortedActions.add(3, "RightClick");
         */
        return sortedActions;
    }

    public String getIcon() {
        return this.icon;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getType() {
        return this.type;
    }

    protected ActionBase() {
    }

    private static String removeYmlExtension(String string) {
        if (string.endsWith(".yml"))
            return string.substring(0, string.length() - 4);
        else return string;
    }
}
