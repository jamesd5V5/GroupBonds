package org.mammothplugins.groupbonds;

import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.command.GroupCommands;
import org.mammothplugins.groupbonds.events.ActionEvents;
import org.mammothplugins.groupbonds.events.JoinAndLeaveEvents;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mammothplugins.groupbonds.patterns.ShapeBase;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;

public class GroupBonds extends SimplePlugin {

    private ActionEvents actionEvents;

    @Override
    protected void onPluginStart() {
        registerCommand(new GroupCommands());
        BondBase.onStart();

        ActionBase.onStart();
        PowerBase.onStart();
        ShapeBase.onStart();
        PositionBase.onStart();

        this.registerEvents(new ActionEvents());
        this.actionEvents = new ActionEvents(); //For Automatic/moving Actions
        this.actionEvents.runTaskTimer(getInstance(), 20L, 20L);

        this.registerEvents(new JoinAndLeaveEvents());
    }

    @Override
    protected void onReloadablesStart() {
        Valid.checkBoolean(HookManager.isVaultLoaded(), "You need to install Vault so that we can work with packets, offline player data, prefixes and groups.");
    }

    public static GroupBonds getInstance() {
        return (GroupBonds) SimplePlugin.getInstance();
    }

}
