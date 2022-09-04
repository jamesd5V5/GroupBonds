package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class ConsumeAction extends ActionBase {
    public ConsumeAction() {
        super("Consume");
        this.setIcon("COOKED_BEEF");
        this.setAmount(1);
        this.setType("General");
    }
}
