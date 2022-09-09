package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class AutomaticAction extends ActionBase {
    public AutomaticAction() {
        super("Automatic");
        this.setIcon("COMMAND_BLOCK");
        this.setType("General");
        this.setAmount(1);
    }
}
