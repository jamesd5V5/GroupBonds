package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class ShiftAction extends ActionBase {
    public ShiftAction() {
        super("Toggle Shift");
        this.setIcon("LEATHER_LEGGINGS");
        this.setAmount(1);
        this.setType("General");
    }
}
