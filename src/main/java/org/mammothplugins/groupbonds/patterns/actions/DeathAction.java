package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class DeathAction extends ActionBase {
    public DeathAction() {
        super("Death");
        this.setIcon("SKELETON_SKULL");
        this.setAmount(1);
        this.setType("General");
    }
}
