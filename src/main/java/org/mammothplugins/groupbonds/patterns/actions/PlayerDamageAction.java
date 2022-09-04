package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class PlayerDamageAction extends ActionBase {
    public PlayerDamageAction() {
        super("Player Damage");
        this.setIcon("CACTUS");
        this.setAmount(1);
        this.setType("Selective");
    }
}
