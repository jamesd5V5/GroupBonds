package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class JumpAction extends ActionBase {
    public JumpAction() {
        super("Jump");
        this.setIcon("SLIME_BLOCK");
        this.setAmount(1);
        this.setType("General");
    }
}
