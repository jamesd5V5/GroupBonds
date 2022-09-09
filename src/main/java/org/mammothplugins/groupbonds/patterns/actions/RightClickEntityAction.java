package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class RightClickEntityAction extends ActionBase {
    public RightClickEntityAction() {
        super("RightClick Entity");
        this.setIcon("CARROT_ON_A_STICK");
        this.setAmount(2);
        this.setType("Selective");
    }
}
