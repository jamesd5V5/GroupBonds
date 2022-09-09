package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class LeftClickEntityAction extends ActionBase {
    public LeftClickEntityAction() {
        super("LeftClick Entity");
        this.setIcon("IRON_SWORD");
        this.setAmount(2);
        this.setType("Selective");
    }
}
