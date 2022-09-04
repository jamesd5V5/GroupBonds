package org.mammothplugins.groupbonds.patterns.actions;

import org.mammothplugins.groupbonds.patterns.ActionBase;

public class ItemDropAction extends ActionBase {
    public ItemDropAction() {
        super("ItemDrop");
        this.setIcon("COBBLESTONE");
        this.setAmount(1);
        this.setType("General");
    }
}
