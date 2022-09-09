package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class MoveAction extends ActionBase {
    public MoveAction() {
        super("Move");
        this.setIcon("CHAINMAIL_BOOTS");
        this.setAmount(1);
        this.setType("General");
    }
}
