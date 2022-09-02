package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class BreakBlockAction extends ActionBase {
    public BreakBlockAction() {
        super("BreakBlock");
        this.setIcon("IRON_PICKAXE");
        this.setAmount(1);
        this.setType("General");
    }
}
