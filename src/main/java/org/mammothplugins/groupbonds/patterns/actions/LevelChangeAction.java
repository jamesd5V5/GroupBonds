package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class LevelChangeAction extends ActionBase {
    public LevelChangeAction() {
        super("LevelChange");
        this.setIcon("EXPERIENCE_BOTTLE");
        this.setAmount(1);
        this.setType("General");
    }
}
