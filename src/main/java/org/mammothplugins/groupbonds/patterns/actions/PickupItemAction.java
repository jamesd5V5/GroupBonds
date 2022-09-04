package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class PickupItemAction extends ActionBase {
    public PickupItemAction() {
        super("PickupItem");
        this.setIcon("COAL");
        this.setAmount(1);
        this.setType("General");
    }
}
