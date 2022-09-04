package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class PickupArrowAction extends ActionBase {
    public PickupArrowAction() {
        super("PickupArrow");
        this.setIcon("ARROW");
        this.setAmount(1);
        this.setType("General");
    }
}
