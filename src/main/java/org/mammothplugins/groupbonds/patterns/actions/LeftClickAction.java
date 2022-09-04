package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class LeftClickAction extends ActionBase {
    public LeftClickAction() {
        super("LeftClick");
        this.setIcon("IRON_SWORD");
        this.setAmount(1);
        this.setType("General");
    }
}
