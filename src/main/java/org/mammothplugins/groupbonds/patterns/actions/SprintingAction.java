package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class SprintingAction extends ActionBase {
    public SprintingAction() {
        super("Toggle Sprint");
        this.setAmount(1);
        this.setIcon("SUGAR");
        this.setType("General");
    }
}
