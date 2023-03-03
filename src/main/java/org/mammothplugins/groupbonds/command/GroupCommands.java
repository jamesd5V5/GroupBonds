package org.mammothplugins.groupbonds.command;

import org.bukkit.Material;
import org.mammothplugins.groupbonds.PlayerCache;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.bonds.BondChat;
import org.mammothplugins.groupbonds.menu.AdminMenu;
import org.mammothplugins.groupbonds.menu.NonItems;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.remain.CompSound;

@AutoRegister
public final class GroupCommands extends SimpleCommand {

    public GroupCommands() {
        super("group|g|groups");
        setPermission("bonds.commands.baseCommand");
    }

    @Override
    protected void onCommand() {
        this.checkConsole();
        boolean foundCommand = false;
        if (this.args.length == 0) {
            Common.tell(this.getPlayer(), new String[]{BondChat.getLine()});
            Common.tell(this.getPlayer(), new String[]{"&3&lBond Usage:"});
            Common.tell(this.getPlayer(), new String[]{"&3/b &7= base command"});
            if (this.hasPerm("bonds.commands.info"))
                Common.tell(this.getPlayer(), new String[]{"&3/b &einfo &7= gives bond info"});
            if (this.hasPerm("bonds.commands.request"))
                Common.tell(this.getPlayer(), new String[]{"&3/b &e<player name> &7= sends a bond request"});
            if (this.hasPerm("bonds.commands.remove"))
                Common.tell(this.getPlayer(), new String[]{"&3/b &eremove <bond name> &7= removes your bond with your friend"});
            if (this.hasPerm("bonds.commands.accept"))
                Common.tell(this.getPlayer(), new String[]{"&3/b &eaccept &7= accept a bond request"});
            if (this.hasPerm("bonds.commands.deny"))
                Common.tell(this.getPlayer(), new String[]{"&3/b &edeny &7= deny a bond request"});
            if (this.hasPerm("bonds.commands.menu"))
                Common.tell(this.getPlayer(), new String[]{"&3/b &emenu &7= opens the bond menu"});
            if (this.hasPerm("bonds.commands.adminMenu"))
                Common.tell(this.getPlayer(), new String[]{"&3/b &eadmin &7= opens the admin menu"});

            Common.tell(this.getPlayer(), new String[]{BondChat.getLine()});
            foundCommand = true;
        }
        if (this.args.length == 1) {
            if (this.hasPerm("bonds.commands.adminMenu") && "admin".equalsIgnoreCase(this.args[0])) {
                (new AdminMenu()).displayTo(this.getPlayer());
                foundCommand = true;
            }
            if ("list".equalsIgnoreCase(this.args[0])) {
                String list = BondBase.getBonds().size() + " List: ";

                int count = 0;
                for (BondBase bondBase : BondBase.getBonds()) {
                    if (count != 0)
                        list = list + ", ";
                    list = list + bondBase.getName();
                    count++;
                }

                Common.tell(sender, list);
                foundCommand = true;
            }

            if ("cache".equalsIgnoreCase(this.args[0])) {
                PlayerCache playerCache = PlayerCache.from(getPlayer());
                Common.tell(getSender(), ": " + playerCache.getWow() + " & " + playerCache.getBondCaches().toString());
                foundCommand = true;
            }
            if ("power".equalsIgnoreCase(this.args[0])) {
                PlayerCache playerCache = PlayerCache.from(getPlayer());
                Common.tell(getSender(), "Default Powers: " + PowerBase.getPowerNames().toString());
                Common.tell(getSender(), "Custom Powers: " + PowerBase.customPowerNames.toString());
                Common.tell(getSender(), "No Strength Powers: " + PowerBase.doesNotUseStrength.toString());
                foundCommand = true;
            }
            if ("bondPlayer".equalsIgnoreCase(this.args[0])) {
                PlayerCache playerCache = PlayerCache.from(getPlayer());
                BondBase bondBase = BondBase.getByName("Sheep");
                playerCache.addBond(bondBase, playerCache.getUniqueId());

                Common.tell(getSender(), "Added: group " + playerCache.getFriend(bondBase));
                foundCommand = true;
            }
            if ("item".equalsIgnoreCase(this.args[0])) {
                for (Material material : Material.values()) {
                    Common.broadcast(material.name());
                }
                foundCommand = true;
            }
            if ("itemi".equalsIgnoreCase(this.args[0])) { //if (!material.equals(null))
                for (Material material : Material.values()) {
                    if (!material.name().equals("AIR") && !material.name().equals("GRASS_PATH") && !material.name().contains("LEGACY"))
                        Common.broadcast("IsItem: " + material.name());
                }
                foundCommand = true;
            }
            if ("size".equalsIgnoreCase(this.args[0])) { //if (!material.equals(null))
                int count = 0;
                for (Material material : Material.values()) {
                    if (!material.name().equals("AIR") && !material.name().equals("GRASS_PATH")) {
                        if (NonItems.isAnItem(material)) {
                            count++;
                            Common.broadcast(material.name());
                        }
                        /*
                        boolean passed = true;
                        for (NonItems nn : NonItems.values()) {
                            if (nn.name().equals(material.name())) {
                                passed = false;
                            }
                        }
                        if (passed) {
                            count++;
                        } else {
                            Common.broadcast(material.name() + " did not pass.");
                        }
                         */
                    }
                }
                Common.broadcast("Size: " + count);
                foundCommand = true;
            }
        }
        if (this.args.length == 2) {
            if ("createb".equalsIgnoreCase(this.args[0])) {
                String bondName = this.args[1];
                BondBase.createBond(bondName);
                Common.tell(sender, "&2" + bondName + " was created.");
                foundCommand = true;
            }
            if ("removeb".equalsIgnoreCase(this.args[0])) {
                String bondName = this.args[1];
                BondBase.removeBond(bondName);
                Common.tell(sender, "&3" + bondName + " was removed.");
                foundCommand = true;
            }
            if ("createT".equalsIgnoreCase(this.args[0])) {
                int tier = 5;
                BondBase bondBase = BondBase.getByName(this.args[1]);
                bondBase.addTier(tier, 5, 5);
                Common.tell(sender, "&2" + bondBase + " now has the tier " + tier);
                foundCommand = true;
            }


        }
        if (!foundCommand) {
            Common.tell(this.getPlayer(), new String[]{"&cWrong Arguments! Use /b for help."});
            CompSound.VILLAGER_NO.play(this.getPlayer());
        }

    }
}
