package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class RightClickAction extends ActionBase {
    public RightClickAction() {
        super("RightClick");
        this.setIcon("CARROT_ON_A_STICK");
        this.setAmount(1);
        this.setType("General");
    }
}
