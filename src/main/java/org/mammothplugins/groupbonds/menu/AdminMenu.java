package org.mammothplugins.groupbonds.menu;

import com.earth2me.essentials.libs.checkerframework.checker.nullness.qual.Nullable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mammothplugins.groupbonds.patterns.ShapeBase;
import org.mammothplugins.groupbonds.patterns.shapes.PositionBase;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimpleConversation;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AdminMenu extends Menu {

    private final Button bondApplyButton;
    private final Button bondEditButton;
    private final Button bondCreationButton;

    public AdminMenu() {
        this.setTitle("&9&lBonds &3Admin Menu");
        this.setSize(27);
        this.bondApplyButton = new Button() {
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    if (player.hasPermission("bonds.apply")) {
                        //(AdminMenu.this.new BondApplyMenu()).displayTo(player);
                    } else {
                        AdminMenu.this.restartMenu("&c&lDenied Permission!");
                        CompSound.VILLAGER_NO.play(player);
                    }
                }

            }

            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.ENCHANTING_TABLE, "&3&lArrange Bonds", new String[]{"Configure the player's bonds", "This includes applying, upgrading", "and removing bonds from players", " ", "&f(Click to Arrange Player Bonds)"}).make();
            }
        };
        this.bondEditButton = new Button() {
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    if (player.hasPermission("bonds.edit")) {
                        (AdminMenu.this.new EditBondsMenu()).displayTo(player);
                    } else {
                        AdminMenu.this.restartMenu("&c&lDenied Permission!");
                        CompSound.VILLAGER_NO.play(player);
                    }
                }

            }

            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.BREWING_STAND, "&3&lEdit Bonds", new String[]{"&7Configure bonds", "&7This includes its design and", "&7the ability to delete the bond.", " ", "&f(Click to Configure)"}).make();
            }
        };
        this.bondCreationButton = new Button() {
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    //(AdminMenu.this.new BondCreatingConversation()).show(player);
                }

            }

            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.CRAFTING_TABLE, "&3&lCreate Bonds", new String[]{"Current Bonds: &3" + BondBase.getBonds().size(), "Customize a brand new", "Friend Bond", " ", "&f(Click to Make Bonds)"}).make();
            }
        };
    }

    public ItemStack getItemAt(int slot) {
        if (slot == 15) {
            return this.bondEditButton.getItem();
        } else if (slot == 13) {
            return this.bondApplyButton.getItem();
        } else {
            return slot == 11 ? this.bondCreationButton.getItem() : ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
        }
    }

    public class EditBondsMenu extends MenuPagged<BondBase> {
        private final Button bondCreationButton;
        private final Button duplicateBondButton;
        private final Button clearTierListButton;

        protected EditBondsMenu() {
            super(AdminMenu.this, BondBase.getBonds());
            this.setTitle("&3&lEditing Bonds &r&3Menu");
            this.bondCreationButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new BondCreatingConversation().show(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.WRITABLE_BOOK, "&3&lCreate Bonds", new String[]{"&eCurrent Bonds: &3" + BondBase.getBonds().size(), "Customize a brand new", "Group Bond.", " ", "&f(Click to Create)"}).make();
                }
            };
            this.duplicateBondButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new BondDuplicateMenu().displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&3&lDuplicate Bond", new String[]{"&7Duplicate a Bond", "&7with all of its current", "&7data.", " ", "&f(Click to Duplicate)"}).make();
                }
            };
            this.clearTierListButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new ClearBondsConversation().show(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.SKELETON_SKULL, "&3&lClear Bonds", new String[]{"&7Removes all Bonds.", "&7You cannot undo this! ", " ", "&f(Click to Clear)"}).make();
                }
            };
        }

        protected ItemStack convertToItemStack(BondBase bondBase) {
            List<String> convertingLore = new ArrayList(bondBase.getLore());
            //if (APISupport.hasPlaceHolderAPI) {//TODO
            //    convertingLore = PlaceholderAPI.setPlaceholders(this.getViewer(), (List)convertingLore);
            //}

            convertingLore.add(0, "&eEnabled: &r&3" + bondBase.isEnabled());
            convertingLore.add(" ");
            convertingLore.add("&f(Left click to Edit)");
            convertingLore.add("&f(Right click to Remove)");
            return ItemCreator.of(CompMaterial.fromString(bondBase.getIcon()), "&3&l" + bondBase.getName(), convertingLore).make();
        }

        protected void onPageClick(Player player, BondBase bondBase, ClickType clickType) {
            if (clickType == ClickType.LEFT)
                new EditBondMenu(bondBase).displayTo(player);
            if (clickType == ClickType.RIGHT)
                new RemoveBondConversation(bondBase).show(player);
        }

        public ItemStack getItemAt(int slot) {
            if (slot == this.getSize() - 7) {
                return this.bondCreationButton.getItem();
            } else if (slot == this.getSize() - 5) {
                return this.duplicateBondButton.getItem();
            } else if (slot == this.getSize() - 3) {
                return this.clearTierListButton.getItem();
            } else {
                boolean multiplePages = this.getPages().size() > 1;
                if (slot >= this.getSize() - 9 && slot != this.getSize() - 7 && slot != this.getSize() - 5 && slot != this.getSize() - 3) {
                    if (multiplePages) {
                        if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    } else if (this.getSize() != 4) {
                        return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                    }
                }

                return super.getItemAt(slot);
            }
        }

        private class BondDuplicateMenu extends MenuPagged<BondBase> {
            private int invSize;
            private Button backButton;

            protected BondDuplicateMenu() {
                super(BondBase.getBonds());
                this.setTitle("&3&lDuplicate Which Bond");
                this.invSize = this.getSize();
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            (AdminMenu.this.new EditBondsMenu()).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
            }

            protected ItemStack convertToItemStack(BondBase bondBase) {
                List<String> convertingLore = new ArrayList(bondBase.getLore());
                //if (APISupport.hasPlaceHolderAPI) {//TODO
                //     convertingLore = PlaceholderAPI.setPlaceholders(this.getViewer(), (List)convertingLore);
                // }

                convertingLore.add(0, "&eEnabled: &r&3" + bondBase.isEnabled());
                convertingLore.add(" ");
                convertingLore.add("&f(Left click to Duplicate)");
                return ItemCreator.of(CompMaterial.fromString(bondBase.getIcon()), "&3&l" + bondBase.getName(), convertingLore).make();
            }

            protected void onPageClick(Player player, BondBase bondBase, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    new DuplicateBondConversation(player, bondBase);
                }

            }

            public ItemStack getItemAt(int slot) {
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else {
                    boolean multiplePages = this.getPages().size() > 1;
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 1) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        } else if (this.getSize() != 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }

        public class BondCreatingConversation extends SimplePrompt {
            public BondCreatingConversation() {
                super(false);
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aPlease type the name of a new Bond. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                for (BondBase bondBase : BondBase.getBonds()) {
                    if (bondBase.getName().equals(input))
                        return false;
                }

                return true;
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cThe bond named &e" + invalidInput + "&c already exists! Try again! &7Type &oexit &r&7to leave.";
            }

            protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                Player player = this.getPlayer(context);
                BondBase.createBond(input);
                CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                (AdminMenu.this.new EditBondsMenu()).displayTo(AdminMenu.this.getViewer());
            }
        }

        public class DuplicateBondConversation {
            private BondBase duplicatedBond;
            private Player player;

            public DuplicateBondConversation(Player player, BondBase duplicatedBond) {
                (new AddDuplicateBondConversation()).show(player);
                this.player = player;
                this.duplicatedBond = duplicatedBond;
            }

            private final class AddDuplicateBondConversation extends SimplePrompt {
                public AddDuplicateBondConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type the name of the new duplicated Bond. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    for (BondBase bondBase : BondBase.getBonds()) {
                        if (bondBase.getName().equals(input))
                            return false;
                    }
                    return true;
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    return "&cThe bond named &e" + invalidInput + "&c already exists! Try again! &7Type &oexit &r&7to leave.";
                }

                @Nullable
                protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                    Player player = this.getPlayer(context);
                    DuplicateBondConversation.this.duplicatedBond.duplicateBond(input);
                    CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                    (AdminMenu.this.new EditBondsMenu()).displayTo(AdminMenu.this.getViewer());
                }
            }
        }

        public class ClearBondsConversation extends SimplePrompt {
            public ClearBondsConversation() {
                super(false);
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aTo remove all bonds, text &oclear&r&a. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("clear"))
                    return true;

                return false;
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cThe input &e" + invalidInput + "&c is invalid! Try again! &7Type &oclear &r&7 to remove all bonds or &oexit &r&7to leave.";
            }

            protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                Player player = this.getPlayer(context);
                BondBase.clearBonds();
                CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                (AdminMenu.this.new EditBondsMenu()).displayTo(AdminMenu.this.getViewer());
            }
        }

        public class RemoveBondConversation extends SimplePrompt {
            private BondBase bondBase;

            public RemoveBondConversation(BondBase bondBase) {
                super(false);
                this.bondBase = bondBase;
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aTo remove the bond &2" + bondBase.getName() + " &atype &oremove. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("remove"))
                    return true;

                return false;
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cThe input &e" + invalidInput + "&c is invalid! Try again! &7Type &oremove &r&7to remove " + bondBase.getName() + " &7or &oexit &r&7to leave.";
            }

            protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                Player player = this.getPlayer(context);
                BondBase.removeBond(bondBase.getName());
                CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                //
                (AdminMenu.this.new EditBondsMenu()).displayTo(AdminMenu.this.getViewer());
            }
        }
    }

    private final class EditBondMenu extends Menu {
        private final Button backButton;
        private final Button enabledButton;
        private final Button isEnabledPane;
        private final Button tierButton;
        private final Button hasTierPane;
        private final Button hasTierButton;
        private final Button maxTierListButton;
        private final Button patternButton;
        private final Button economyButton;
        private final Button rangeButton;
        private final Button changeIconButton;
        private final Button loreButton;
        private boolean hasTiers;

        public EditBondMenu(BondBase bondBase) {
            this.setTitle("&3&l" + bondBase.getName());
            this.setSize(9 * 4);

            this.hasTiers = bondBase.isHasTiers();

            this.backButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        (AdminMenu.this.new EditBondsMenu()).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                }
            };
            this.enabledButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (bondBase.isEnabled()) {
                            bondBase.setEnabled(false);
                        } else {
                            bondBase.setEnabled(true);
                        }
                        (AdminMenu.this.new EditBondMenu(bondBase)).displayTo(player);
                    }
                }

                public ItemStack getItem() {
                    return ItemCreator.of(bondBase.isEnabled() ? CompMaterial.EMERALD_BLOCK : CompMaterial.REDSTONE_BLOCK, "&3&lEnabled", new String[]{"&eEnabled: &r&3" + bondBase.isEnabled(), "&7Whether or not this bond", "&7will be used.", " ", "&f(Click to Change)"}).glow(bondBase.isEnabled()).make();
                }
            };
            this.isEnabledPane = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                }

                public ItemStack getItem() {
                    return ItemCreator.of(bondBase.isEnabled() ? CompMaterial.LIME_STAINED_GLASS_PANE : CompMaterial.RED_STAINED_GLASS_PANE, "&3&l" + (bondBase.isEnabled() ? "Enabled" : "Disabled"), new String[0]).make();
                }
            };
            this.tierButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (EditBondMenu.this.hasTiers) {
                            new TierListMenu(bondBase).displayTo(player);
                        } else {
                            EditBondMenu.this.restartMenu("&c&lTiers are Disabled!");
                            CompSound.VILLAGER_NO.play(player);
                        }
                    }
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.EXPERIENCE_BOTTLE, "&3&lTiers", new String[]{"&eMax Tier: &3", "&eTier Changes: &3CHANGE ME", "&7Bonds can give", "&7players different appearances", "&7and abilities for each tier.", " ", "&f(" + "CHANGE ME"}).make(); //bondBase.getTiers().size(), (BondEditMenuBond.this.hasTiers ? "Click to Change)" : "Enable to Edit))
                }
            };
            this.hasTierPane = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                }

                public ItemStack getItem() {
                    return ItemCreator.of(bondBase.isHasTiers() ? CompMaterial.LIME_STAINED_GLASS_PANE : CompMaterial.RED_STAINED_GLASS_PANE, "&3&l" + (bondBase.isHasTiers() ? "Enabled" : "Disabled"), new String[0]).make();
                }
            };
            this.hasTierButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (bondBase.isHasTiers()) {
                            bondBase.setHasTiers(false);
                            bondBase.clearTiers();
                        } else
                            bondBase.setHasTiers(true);
                        new EditBondMenu(bondBase).displayTo(player);
                    }
                }

                public ItemStack getItem() {
                    return ItemCreator.of(bondBase.isHasTiers() ? CompMaterial.LIME_DYE : CompMaterial.RED_DYE, "&3&l" + (bondBase.isHasTiers() ? "Enabled" : "Disabled"), new String[]{"&7Whether or not this bond will", "&7have multiple tier levels", "&7with different abilities.", " ", "&f(Click to " + (bondBase.isHasTiers() ? "Disabled" : "Enable") + ")"}).glow(bondBase.isHasTiers()).make();
                }
            };
            this.maxTierListButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        //AdminMenu.this.new MaxTierConversation(player, bondBase);
                    }

                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.COMPARATOR, "&3&lMax Tier", new String[]{"&eCurrent Max Tier: &3" + "bondBase.getMaxTier()", "&7The capped tier of the", "&7bond " + bondBase.getName() + ".", " ", "&f(Click to Change)"}).make();
                }
            };
            this.patternButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new PatternListMenu(bondBase, bondBase.getTierCache(1)).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.BLUE_BANNER, "&3&lPatterns", new String[]{"&eCurrent Patterns: &3" + "CHANGE ME", "&7Each pattern can", "&7be customized", "&7to have certain", "&7powers and particles.", " ", "&f(Click to Change)"}).make(); //bondBase.getPatterns(1).size()
                }
            };
            this.economyButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        AdminMenu.this.new PriceConversation(player, bondBase, bondBase.getTierCache(1));
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.EMERALD, "&3&lPrice", new String[]{"&eCurrent Price: &3" + "CHANGE ME", "&7The price to purchase", "&7this bond.", " ", "&f(Click to Change)"}).make(); //bondBase.getTierCache(1).getPrice()
                }
            };
            this.rangeButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        AdminMenu.this.new RangeConversation(player, bondBase, bondBase.getTierCache(1));
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.LEAD, "&3&lRange", new String[]{"&eCurrent Range: &3" + "CHANGE ME", "&7When the friend is within", "&7range of the player,", "&7certain patterns can", "&7be activated.", " ", "&f(Click to Change)"}).make(); //bondBase.getTierCache(1).getRange()
                }
            };
            this.changeIconButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new SelectIconMenu(bondBase).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.fromString(bondBase.getIcon()), "&3&lIcon", new String[]{"&eCurrent Icon: &3" + bondBase.getIcon(), "&7The material that represents", "&7the bond in the menu.", " ", "&f(Click to Change)"}).make();
                }
            };
            this.loreButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new BondLoreConversation(player, bondBase);
                }

                public ItemStack getItem() {
                    List<String> convertingLore = new ArrayList(bondBase.getLore());
                    //if (APISupport.hasPlaceHolderAPI) {
                    //    convertingLore = PlaceholderAPI.setPlaceholders(BondEditMenuBond.this.getViewer(), (List) convertingLore);
                    // }

                    ((List) convertingLore).add(0, "&eCurrent Lore: ");
                    ((List) convertingLore).add(" ");
                    ((List) convertingLore).add("&f(Click to Change)");
                    return ItemCreator.of(CompMaterial.BOOK, "&3&lLore", (Collection) convertingLore).make();
                }
            };
        }

        public ItemStack getItemAt(int slot) {
            if (slot == this.getSize() - 1) {
                return this.backButton.getItem();
            } else if (slot == 9) {
                return this.enabledButton.getItem();
            } else if (slot % 9 == 0) {
                return this.isEnabledPane.getItem();
            } else if (slot == 11) {
                return this.hasTierPane.getItem(); //Econ
            } else if (slot == 12) {
                return this.economyButton.getItem();
            } else if (slot == 13) {
                return this.hasTierButton.getItem(); //Econ
            } else if (slot == 20) {
                return this.hasTierPane.getItem();
            } else if (slot == 21) {
                return this.tierButton.getItem();
            } else if (slot == 22) {
                return this.hasTierButton.getItem();
            } else if (slot == 15) {
                return this.changeIconButton.getItem();
            } else if (slot == 16) {
                return this.loreButton.getItem();
            } else {
                if (!this.hasTiers) {
                    if (slot == 24) {
                        return this.patternButton.getItem();
                    }

                    if (slot == 25) {
                        return this.rangeButton.getItem();
                    }
                } else if (slot == 25) {
                    return this.maxTierListButton.getItem();
                }

                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
            }
        }

        private class SelectIconMenu extends MenuPagged<CompMaterial> {
            private Button backButton;
            private Button changeIconButton;
            private BondBase bondBase;


            protected SelectIconMenu(BondBase bondBase) {
                super(Arrays.asList(CompMaterial.values()).stream().filter((material) -> {
                    return material.getMaterial().isItem();
                    /*return (!material.name().contains("WALL") && !material.name().contains("STEM") && !material.name().contains("POTTED")
                            && !material.name().contains("BED") && !material.name().contains("_OFF") && !material.name().contains("_ON") && !material.name().contains("SOIL")
                            && !material.name().contains("AIR") && !material.name().contains("SIGN_POST"));

                     */

                }).collect(Collectors.toList()));
                this.setTitle("&3&lSelect An Icon");
                this.bondBase = bondBase;
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            (AdminMenu.this.new EditBondMenu(bondBase)).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
                this.changeIconButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.valueOf(bondBase.getIcon()), "&3&lIcon", new String[]{"&eCurrent Icon: &3" + bondBase.getIcon(), "&7The material that represents", "&7the bond in the menus."}).make();
                    }
                };
            }

            @Override
            protected ItemStack convertToItemStack(CompMaterial material) {
                return ItemCreator.of(CompMaterial.fromString(material.name()), "&3&l" + material.name(), new String[]{"&7This item will be the bond's", "&7display icon in all menus.", "  ", "&f(Click to Select)"}).glow(this.bondBase.getIcon().equals(material.name())).make();

            }

            @Override
            protected void onPageClick(Player player, CompMaterial material, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    this.bondBase.setIcon(material.name());
                    (AdminMenu.this.new EditBondMenu(this.bondBase)).displayTo(player);
                }
            }

            public ItemStack getItemAt(int slot) {
                if (slot == this.getSize() - 1)
                    return this.backButton.getItem();
                else if (slot == this.getSize() - 5)
                    return this.changeIconButton.getItem();
                else {
                    boolean multiplePages = this.getPages().size() > 1;
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 5 && slot != this.getSize() - 1)
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4)
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        } else if (this.getSize() != 4)
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                    return super.getItemAt(slot);
                }
            }
        }

        public class BondLoreConversation {
            private BondBase bondBase;
            private Player player;

            public BondLoreConversation(Player player, BondBase bondBase) {
                this.player = player;
                this.bondBase = bondBase;
                (new BondSetLoreConversation()).show(player);
            }

            private final class BondSetLoreConversation extends SimplePrompt {
                public BondSetLoreConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type the lore of the bond &e" + BondLoreConversation.this.bondBase.getName() + "&a. To go to the next line, input &e/n&a. You can also use Color Codes and PlaceHolders. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    return true;
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    return "&cSomething went wrong. Try again! &7Type &oexit &r&7to leave.";
                }

                @Nullable
                protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                    List<String> lore = Arrays.asList(input.split("/n"));
                    BondLoreConversation.this.bondBase.setLore(lore);
                    CompSound.CAT_MEOW.play(BondLoreConversation.this.player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                    (AdminMenu.this.new EditBondMenu(BondLoreConversation.this.bondBase)).displayTo(BondLoreConversation.this.player);
                }
            }
        }
    }

    private final class TierListMenu extends MenuPagged<BondBase.TierCache> {
        BondBase bondBase;
        private final Button addTierButton;
        private final Button duplicateTierButton;
        private final Button clearTierListButton;
        private final Button backButton;

        protected TierListMenu(BondBase bondBase) {
            super(bondBase.getTiers());
            boolean namesEndsWithS = bondBase.getName().endsWith("s");
            this.setTitle("&3&l" + bondBase.getName() + (namesEndsWithS ? "' " : "'s ") + "&r&3Tiers");
            this.bondBase = bondBase;
            this.addTierButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new TierConversation(player, bondBase);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.WRITABLE_BOOK, "&3&lAdd Tier", new String[]{"&7Customize the bond's", "&7abilities at certain tiers.", " ", "&f(Click to Add)"}).make();
                }
            };
            this.duplicateTierButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new TierDuplicateMenu(bondBase).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&3&lDuplicate Tier", new String[]{"&7Create a copy of a", "&7tier from this bond.", " ", "&f(Click to Duplicate)"}).make();
                }
            };
            this.clearTierListButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new ClearTiersConversation().show(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.SKELETON_SKULL, "&3&lClear Tiers", new String[]{"&7Removes all tiers", "&7from this bond.", " ", "&f(Click to Clear)"}).make();
                }
            };
            this.backButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        (AdminMenu.this.new EditBondMenu(bondBase)).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                }
            };
        }

        protected ItemStack convertToItemStack(BondBase.TierCache tierCache) {
            int nextTier = this.bondBase.getMaxTier();
            int i;
            for (i = 0; i < this.bondBase.getTiers().size(); ++i)
                if ((this.bondBase.getTiers().get(i)).getTier() == tierCache.getTier() && i != this.bondBase.getTiers().size() - 1)
                    nextTier = (this.bondBase.getTiers().get(i + 1)).getTier();

            i = (this.bondBase.getTiers().get(this.bondBase.getTiers().size() - 1)).getTier();
            int nextTierDisplay = tierCache.getTier() == i ? nextTier : nextTier - 1;
            List<String> convertingLore = new ArrayList(this.bondBase.getTierLore(tierCache.getTier()));
            //if (APISupport.hasPlaceHolderAPI) {//TODO
            //    convertingLore = PlaceholderAPI.setPlaceholders(this.getViewer(), (List) convertingLore);
            //}

            convertingLore.add(0, "&eApplies to Tiers: &3" + tierCache.getTier() + (nextTier != tierCache.getTier() && nextTier - tierCache.getTier() != 1 ? "-" + nextTierDisplay : ""));
            convertingLore.add(1, "&ePatterns: &r&3" + tierCache.getPatterns().size());
            convertingLore.add(2, "&ePrice: &r&3" + tierCache.getPrice());
            convertingLore.add(3, "&eRange: &r&3" + tierCache.getRange());
            convertingLore.add(" ");
            convertingLore.add("&f(Left click to Edit)");
            convertingLore.add("&f(Right click to Remove)");
            return ItemCreator.of(CompMaterial.BOOK, "&3&lTier &r&3" + MathUtil.toRoman(tierCache.getTier()), convertingLore).amount(tierCache.getTier()).make();
        }

        protected void onPageClick(Player player, BondBase.TierCache tierCache, ClickType clickType) {
            if (clickType.isLeftClick())
                new TierPatternPowerMenu(this.bondBase, tierCache).displayTo(player);


            if (clickType.isRightClick()) {
                if (tierCache.getTier() != 1) {
                    new TierRemoveConversation(bondBase, tierCache).show(player);
                } else {
                    this.restartMenu("&cYou must keep tier lvl 1!");
                    CompSound.VILLAGER_NO.play(player);
                }
            }
        }

        public ItemStack getItemAt(int slot) {
            if (slot == this.getSize() - 7) {
                return this.addTierButton.getItem();
            } else if (slot == this.getSize() - 5) {
                return this.duplicateTierButton.getItem();
            } else if (slot == this.getSize() - 3) {
                return this.clearTierListButton.getItem();
            } else {
                boolean multiplePages = this.getPages().size() > 1;
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else {
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 7 && slot != this.getSize() - 5 && slot != this.getSize() - 3 && slot != this.getSize() - 1) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        } else if (this.getSize() != 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }


        public class TierConversation {
            private BondBase bondBase;
            private Player player;

            public TierConversation(Player player, BondBase bondBase) {
                (new AddTierConversation()).show(player);
                this.player = player;
                this.bondBase = bondBase;
            }

            private final class AddTierConversation extends SimplePrompt {
                public AddTierConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type a numerical Tier lvl. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    if (!Valid.isInteger(input))
                        return false;
                    else {
                        int level = Integer.parseInt(input);
                        if (TierConversation.this.bondBase.hasTier(level))
                            return false;
                        else
                            return level > 0;
                    }
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    if (!Valid.isInteger(invalidInput))
                        return "&cYour input needs to be a number! &cTry again! &7Type &oexit &r&7to leave.";
                    else {
                        int level = Integer.parseInt(invalidInput);
                        if (level > 0)
                            return TierConversation.this.bondBase.hasTier(level) ? "&cThe tier &e" + invalidInput + " &calready exists with the bond &e" + TierConversation.this.bondBase.getName() + "&c! &cTry again! &7Type &oexit &r&7to leave." : "&cSomething went wrong, try again!";
                        else
                            return "&cYour input needs to be between greater than 0! &cTry again! &7Type &oexit &r&7to leave.";
                    }
                }

                @Nullable
                protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                    int level = Integer.parseInt(input);
                    TierConversation.this.bondBase.addTier(level, 5, 100);
                    CompSound.CAT_MEOW.play(TierConversation.this.player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                    (AdminMenu.this.new TierListMenu(TierConversation.this.bondBase)).displayTo(AdminMenu.this.getViewer());
                }
            }
        }

        private class TierDuplicateMenu extends MenuPagged<BondBase.TierCache> {
            private int invSize;
            private BondBase bondBase;
            private Button backButton;

            protected TierDuplicateMenu(BondBase bondBase) {
                super(bondBase.getTiers());
                this.setTitle("&3&lDuplicate Which Pattern");
                this.invSize = this.getSize();
                this.bondBase = bondBase;
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            (AdminMenu.this.new TierListMenu(bondBase)).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
            }

            protected ItemStack convertToItemStack(BondBase.TierCache tierCache) {
                int nextTier = this.bondBase.getMaxTier();
                int i;
                for (i = 0; i < this.bondBase.getTiers().size(); ++i)
                    if ((this.bondBase.getTiers().get(i)).getTier() == tierCache.getTier() && i != this.bondBase.getTiers().size() - 1)
                        nextTier = (this.bondBase.getTiers().get(i + 1)).getTier();

                i = (this.bondBase.getTiers().get(this.bondBase.getTiers().size() - 1)).getTier();
                int nextTierDisplay = tierCache.getTier() == i ? nextTier : nextTier - 1;
                List<String> convertingLore = new ArrayList(this.bondBase.getTierLore(tierCache.getTier()));
                // if (APISupport.hasPlaceHolderAPI) { //TODO
                //     convertingLore = PlaceholderAPI.setPlaceholders(this.getViewer(), (List) convertingLore);
                //}

                ((List) convertingLore).add(0, "&eApplies to Tiers: &3" + tierCache.getTier() + (nextTier != tierCache.getTier() && nextTier - tierCache.getTier() != 1 ? "-" + nextTierDisplay : ""));
                ((List) convertingLore).add(1, "&ePatterns: &r&3" + tierCache.getPatterns().size());
                ((List) convertingLore).add(2, "&ePrice: &r&3" + tierCache.getPrice());
                ((List) convertingLore).add(3, "&eRange: &r&3" + tierCache.getRange());
                ((List) convertingLore).add(" ");
                ((List) convertingLore).add("&f(Click to Duplicate)");
                return ItemCreator.of(CompMaterial.BOOK, "&3&lTier &r&3" + MathUtil.toRoman(tierCache.getTier()), (Collection) convertingLore).amount(tierCache.getTier()).make();
            }

            protected void onPageClick(Player player, BondBase.TierCache tierCache, ClickType clickType) {
                if (clickType.isLeftClick())
                    new DuplicateTierConversation(player, this.bondBase, tierCache.getTier());
            }

            public ItemStack getItemAt(int slot) {
                if (slot == this.getSize() - 1)
                    return this.backButton.getItem();
                else {
                    boolean multiplePages = this.getPages().size() > 1;
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 1) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4)
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();

                        } else if (this.getSize() != 4)
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                    }
                    return super.getItemAt(slot);
                }
            }
        }

        public class DuplicateTierConversation {
            private BondBase bondBase;
            private Player player;
            private int duplicateTierLvl;

            public DuplicateTierConversation(Player player, BondBase bondBase, int duplicateTierLvl) {
                (new AddDuplicateTierConversation()).show(player);
                this.player = player;
                this.bondBase = bondBase;
                this.duplicateTierLvl = duplicateTierLvl;
            }

            private final class AddDuplicateTierConversation extends SimplePrompt {
                public AddDuplicateTierConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type a numerical Tier lvl to contain the duplicated data. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    if (!Valid.isInteger(input))
                        return false;
                    else {
                        int level = Integer.parseInt(input);
                        if (DuplicateTierConversation.this.bondBase.hasTier(level))
                            return false;
                        else
                            return level > 0 && level < DuplicateTierConversation.this.bondBase.getMaxTier() + 1;
                    }
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    if (!Valid.isInteger(invalidInput))
                        return "&cYour input needs to be a number! &cTry again! &7Type &oexit &r&7to leave.";
                    else {
                        int level = Integer.parseInt(invalidInput);
                        if (level > 0 && level < DuplicateTierConversation.this.bondBase.getMaxTier() + 1)
                            return DuplicateTierConversation.this.bondBase.hasTier(level) ? "&cThe tier &e" + invalidInput + " &calready exists with the bond &e" + DuplicateTierConversation.this.bondBase.getName() + "&c! &cTry again! &7Type &oexit &r&7to leave." : "&cSomething went wrong, try again!";
                        else
                            return "&cYour input needs to be between 1-" + DuplicateTierConversation.this.bondBase.getMaxTier() + " exclusive! &cTry again! &7Type &oexit &r&7to leave.";
                    }
                }

                @Nullable
                protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                    int level = Integer.parseInt(input);
                    DuplicateTierConversation.this.bondBase.duplicateTierLvl(DuplicateTierConversation.this.duplicateTierLvl, level);
                    CompSound.CAT_MEOW.play(DuplicateTierConversation.this.player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                    (AdminMenu.this.new TierListMenu(DuplicateTierConversation.this.bondBase)).displayTo(AdminMenu.this.getViewer());
                }
            }
        }

        public class ClearTiersConversation extends SimplePrompt {
            public ClearTiersConversation() {
                super(false);
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aTo remove all tiers for " + bondBase.getName() + ", text &oclear&r&a. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("clear"))
                    return true;

                return false;
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cThe input &e" + invalidInput + "&c is invalid! Try again! &7Type &oclear &r&7 to remove all tiers for " + bondBase.getName() + " or &oexit &r&7to leave.";
            }

            protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                Player player = this.getPlayer(context);
                bondBase.clearTiers();
                CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                (AdminMenu.this.new TierListMenu(bondBase)).displayTo(AdminMenu.this.getViewer());
            }
        }

        public class TierRemoveConversation extends SimplePrompt {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;

            public TierRemoveConversation(BondBase bondBase, BondBase.TierCache tierCache) {
                super(false);
                this.bondBase = bondBase;
                this.tierCache = tierCache;
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aTo remove the tier &2" + bondBase.getName() + " " + MathUtil.toRoman(tierCache.getTier()) + " &atype &oremove. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("remove"))
                    return true;

                return false;
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cThe input &e" + invalidInput + "&c is invalid! Try again! &7Type &oremove &r&7to remove " + bondBase.getName() + " " + MathUtil.toRoman(tierCache.getTier()) + " &7or &oexit &r&7to leave.";
            }

            protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                Player player = this.getPlayer(context);
                bondBase.removeTier(tierCache.getTier());
                CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                (AdminMenu.this.new TierListMenu(bondBase)).displayTo(AdminMenu.this.getViewer());
            }
        }
    }

    private final class TierPatternPowerMenu extends Menu {
        private final Button backButton;
        private final Button patternButton;
        private final Button loreButton;
        private final Button economyButton;
        private final Button rangeButton;

        public TierPatternPowerMenu(BondBase bondBase, BondBase.TierCache tierCache) {
            this.setTitle("&3&l" + bondBase.getName() + " &r&3Tier " + MathUtil.toRoman(tierCache.getTier()));
            this.setSize(27);
            this.backButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        (AdminMenu.this.new TierListMenu(bondBase)).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                }
            };
            this.patternButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new PatternListMenu(bondBase, tierCache).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.BLUE_BANNER, "&3&lPatterns", new String[]{"&eCurrent Patterns: &3" + tierCache.getPatterns().size(), "&7Each pattern can", "&7be customized", "&7to have certain", "&7powers and particles.", " ", "&f(Click to Change)"}).make();
                }
            };
            this.loreButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new TierLoreConversation(player, bondBase, tierCache);
                }

                public ItemStack getItem() {
                    List<String> convertingLore = new ArrayList(bondBase.getTierLore(tierCache.getTier()));
                    // if (APISupport.hasPlaceHolderAPI) { //TODO
                    //     convertingLore = PlaceholderAPI.setPlaceholders(TierPatternPowerMenu.this.getViewer(), (List)convertingLore);
                    // }

                    ((List) convertingLore).add(0, "&eCurrent Lore: ");
                    ((List) convertingLore).add(" ");
                    ((List) convertingLore).add("&f(Click to change)");
                    return ItemCreator.of(CompMaterial.BOOK, "&3&lLore", convertingLore).make();
                }
            };
            this.economyButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new PriceConversation(player, bondBase, tierCache);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.EMERALD, "&3&lPrice", new String[]{"&eCurrent Price: &3" + tierCache.getPrice(), "&7The price to purchase", "&7this bond.", " ", "&f(Click to Change)"}).make();
                }
            };
            this.rangeButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    new RangeConversation(player, bondBase, tierCache);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.LEAD, "&3&lRange", new String[]{"&eCurrent Range: &3" + tierCache.getRange(), "&7When the friend is within", "&7range of the player,", "&7certain patterns can", "&7be activated.", " ", "&f(Click to Change)"}).make();
                }
            };
        }

        public ItemStack getItemAt(int slot) {
            if (slot == this.getSize() - 1) {
                return this.backButton.getItem();
            } else if (slot == 10) {
                return this.patternButton.getItem();
            } else if (slot == 12) {
                return this.loreButton.getItem();
            } else if (slot == 14) {
                return this.economyButton.getItem();
            } else {
                return slot == 16 ? this.rangeButton.getItem() : ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
            }
        }

        public class TierLoreConversation {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private Player player;

            public TierLoreConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache) {
                this.player = player;
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                (new TierSetLoreConversation()).show(player);
            }

            private final class TierSetLoreConversation extends SimplePrompt {
                public TierSetLoreConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type the lore for &e" + bondBase.getName() + " " + MathUtil.toRoman(TierLoreConversation.this.tierCache.getTier()) + "&a. To go to the next line, input &e/n&a. You can also use Color Codes and PlaceHolders. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    return true;
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    return "&cSomething went wrong. Try again! &7Type &oexit &r&7to leave.";
                }

                @Nullable
                protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                    List<String> lore = Arrays.asList(input.split("/n"));
                    TierLoreConversation.this.bondBase.setTierLore(TierLoreConversation.this.tierCache.getTier(), lore);
                    CompSound.CAT_MEOW.play(TierLoreConversation.this.player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                    new TierPatternPowerMenu(TierLoreConversation.this.bondBase, TierLoreConversation.this.tierCache).displayTo(TierLoreConversation.this.player);
                }
            }
        }
    }

    public class PriceConversation {
        private BondBase bondBase;
        private BondBase.TierCache tierCache;
        private Player player;

        public PriceConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache) {
            (new ChangePriceConversation()).show(player);
            this.player = player;
            this.bondBase = bondBase;
            this.tierCache = tierCache;
        }

        private final class ChangePriceConversation extends SimplePrompt {
            public ChangePriceConversation() {
                super(false);
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aPlease type your desired price. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                if (!Valid.isInteger(input)) {
                    return false;
                } else {
                    int level = Integer.parseInt(input);
                    return level > 0 && level < Integer.MAX_VALUE;
                }
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cYour input &e" + invalidInput + " &cwas not a whole number. Try again! &7Type &oexit &r&7to leave.";
            }

            @Nullable
            protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                int price = Integer.parseInt(input);
                PriceConversation.this.bondBase.setTierPrice(PriceConversation.this.tierCache.getTier(), price);
                CompSound.CAT_MEOW.play(PriceConversation.this.player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                if (PriceConversation.this.bondBase.isHasTiers())
                    new TierPatternPowerMenu(PriceConversation.this.bondBase, PriceConversation.this.tierCache).displayTo(AdminMenu.this.getViewer());
                else
                    new EditBondMenu(PriceConversation.this.bondBase).displayTo(AdminMenu.this.getViewer());
            }
        }
    }

    public class RangeConversation {
        private BondBase bondBase;
        private BondBase.TierCache tierCache;
        private Player player;

        public RangeConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache) {
            (new BondRangeConversation()).show(player);
            this.player = player;
            this.bondBase = bondBase;
            this.tierCache = tierCache;
        }

        private final class BondRangeConversation extends SimplePrompt {
            public BondRangeConversation() {
                super(false);
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aPlease type the range for this tier lvl between 1-10 blocks exclusive. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                if (!Valid.isInteger(input)) {
                    return false;
                } else {
                    int level = Integer.parseInt(input);
                    return level > 0 && level < 11;
                }
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cYour input &e" + invalidInput + " &cwas not a whole number between 1 and 10 exclusive. Try again! &7Type &oexit &r&7to leave.";
            }

            @Nullable
            protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                int range = Integer.parseInt(input);
                RangeConversation.this.bondBase.setRange(RangeConversation.this.tierCache.getTier(), range);
                CompSound.CAT_MEOW.play(RangeConversation.this.player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                if (RangeConversation.this.bondBase.isHasTiers())
                    new TierPatternPowerMenu(RangeConversation.this.bondBase, RangeConversation.this.tierCache).displayTo(AdminMenu.this.getViewer());
                else
                    new EditBondMenu(RangeConversation.this.bondBase).displayTo(AdminMenu.this.getViewer());
            }
        }
    }

    private final class PatternListMenu extends MenuPagged<Integer> {
        BondBase bondBase;
        BondBase.TierCache tierCache;
        int currentTier;
        private final Button addPatternButton;
        private final Button duplicatePatternListButton;
        private final Button clearPatternListButton;
        private final Button backButton;

        protected PatternListMenu(BondBase bondBase, BondBase.TierCache tierCache) {
            super(bondBase.getPatternNumbers(tierCache.getTier()));
            boolean namesEndsWithS = bondBase.getName().endsWith("s");
            this.setTitle("&3&l" + bondBase.getName() + (namesEndsWithS ? "' " : "'s ") + "&r&3Patterns");
            this.bondBase = bondBase;
            this.tierCache = tierCache;
            this.currentTier = tierCache.getTier();
            this.addPatternButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        bondBase.addPattern(tierCache.getTier());
                        (AdminMenu.this.new PatternListMenu(bondBase, tierCache)).displayTo(player);
                    }
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.WRITABLE_BOOK, "&3&lAdd Pattern", new String[]{"&7Each pattern can give", "&7players certain powers", "&7and cosmetics.", " ", "&f(Click to Add)"}).make();
                }
            };
            this.duplicatePatternListButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new PatternDuplicateMenu(bondBase, tierCache).displayTo(player);
                }

                public ItemStack getItem() {
                    ArrayList<String> lore = new ArrayList();
                    lore.add("&7Copy a pattern");
                    lore.add("&7from the bond");
                    if (bondBase.isHasTiers()) {
                        lore.add("&7" + bondBase.getName() + " &7Tier " + MathUtil.toRoman(tierCache.getTier()) + ".");
                    } else {
                        lore.add("&7" + bondBase.getName() + ".");
                    }

                    lore.add(" ");
                    lore.add("&f(Click to Duplicate)");
                    return ItemCreator.of(CompMaterial.PAPER, "&3&lDuplicate Pattern", lore).make();
                }
            };
            this.clearPatternListButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new ClearPatternsConversation(bondBase, tierCache).show(player);
                }

                public ItemStack getItem() {
                    ArrayList<String> lore = new ArrayList();
                    lore.add("&7Remove all patterns");
                    lore.add("&7from the bond");
                    if (bondBase.isHasTiers()) {
                        lore.add("&7" + bondBase.getName() + " &7Tier " + MathUtil.toRoman(tierCache.getTier()) + ".");
                    } else {
                        lore.add("&7" + bondBase.getName() + ".");
                    }

                    lore.add(" ");
                    lore.add("&f(Click to Clear)");
                    return ItemCreator.of(CompMaterial.SKELETON_SKULL, "&3&lClear Patterns", lore).make();
                }
            };
            this.backButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (bondBase.isHasTiers())
                            (AdminMenu.this.new TierPatternPowerMenu(bondBase, tierCache)).displayTo(player);
                        else
                            (AdminMenu.this.new EditBondMenu(bondBase)).displayTo(player);
                    }
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                }
            };
        }

        protected ItemStack convertToItemStack(Integer number) {
            BondBase.TierCache.PatternCache patternCache = this.bondBase.getPattern(this.currentTier, number);
            return ItemCreator.of(CompMaterial.BROWN_DYE, "&3&lPattern &r&3" + number, new String[]{"&eAction: &3" + patternCache.getActionName(), "&ePower: &r&3" + patternCache.getPowerName(), "&eParticle: &r&3" + patternCache.getParticleName(), " ", "&f(Left Click to Edit)", "&f(Right click to Remove)"}).make();
        }

        protected void onPageClick(Player player, Integer number, ClickType clickType) {
            if (clickType.isLeftClick())
                new PatternEditMenu(this.bondBase, this.tierCache, this.bondBase.getPattern(this.tierCache.getTier(), number)).displayTo(player);


            if (clickType.isRightClick())
                new RemovePatternConversation(this.bondBase, this.tierCache, this.bondBase.getPattern(this.tierCache.getTier(), number)).show(player);
        }

        public ItemStack getItemAt(int slot) {
            if (slot == this.getSize() - 7) {
                return this.addPatternButton.getItem();
            } else if (slot == this.getSize() - 5) {
                return this.duplicatePatternListButton.getItem();
            } else if (slot == this.getSize() - 3) {
                return this.clearPatternListButton.getItem();
            } else {
                boolean multiplePages = this.getPages().size() > 1;
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else {
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 7 && slot != this.getSize() - 5 && slot != this.getSize() - 3 && slot != this.getSize() - 1) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        } else if (this.getSize() != 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }

        private class PatternDuplicateMenu extends MenuPagged<Integer> {
            private int invSize;
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private Button backButton;

            protected PatternDuplicateMenu(BondBase bondBase, BondBase.TierCache tierCache) {
                super(bondBase.getPatternNumbers(tierCache.getTier()));
                this.setTitle("&3&lDuplicate Which Pattern");
                this.invSize = this.getSize();
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            (AdminMenu.this.new PatternListMenu(bondBase, tierCache)).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
            }

            protected ItemStack convertToItemStack(Integer number) {
                BondBase.TierCache.PatternCache patternCache = this.bondBase.getPattern(this.tierCache.getTier(), number);
                return ItemCreator.of(CompMaterial.BROWN_DYE, "&3&lPattern &r&3" + number, new String[]{"&eAction: &3" + patternCache.getActionName(), "&ePower: &r&3" + patternCache.getPowerName(), "&eParticle: &r&3" + patternCache.getParticleName(), " ", "&f(Click to Duplicate)"}).make();
            }

            protected void onPageClick(Player player, Integer number, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    this.bondBase.duplicatePattern(this.tierCache.getTier(), number);
                    (AdminMenu.this.new PatternListMenu(this.bondBase, this.tierCache)).displayTo(player);
                }

            }

            public ItemStack getItemAt(int slot) {
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else {
                    boolean multiplePages = this.getPages().size() > 1;
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 1) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        } else if (this.getSize() != 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }

        public class ClearPatternsConversation extends SimplePrompt {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;

            public ClearPatternsConversation(BondBase bondBase, BondBase.TierCache tierCache) {
                super(false);
                this.bondBase = bondBase;
                this.tierCache = tierCache;
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aTo remove all patterns, text &oclear&r&a. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("clear"))
                    return true;

                return false;
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cThe input &e" + invalidInput + "&c is invalid! Try again! &7Type &oclear &r&7 to remove all patterns or &oexit &r&7to leave.";
            }

            protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                Player player = this.getPlayer(context);
                bondBase.clearPatterns(tierCache.getTier());
                CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {

                (AdminMenu.this.new PatternListMenu(bondBase, tierCache)).displayTo(AdminMenu.this.getViewer());
            }
        }

        public class RemovePatternConversation extends SimplePrompt {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;

            public RemovePatternConversation(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                super(false);
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
            }

            protected String getPrompt(ConversationContext conversationContext) {
                return "&aTo remove the pattern &2" + patternCache.getNumber() + " &atype &oremove. &7Type &oexit &r&7to leave.";
            }

            protected boolean isInputValid(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("remove"))
                    return true;

                return false;
            }

            protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                return "&cThe input &e" + invalidInput + "&c is invalid! Try again! &7Type &oremove &r&7to remove pattern " + patternCache.getNumber() + " &7or &oexit &r&7to leave.";
            }

            protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                Player player = this.getPlayer(context);
                bondBase.removePattern(tierCache.getTier(), patternCache.getNumber());
                CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                return null;
            }

            public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                (AdminMenu.this.new PatternListMenu(bondBase, tierCache)).displayTo(AdminMenu.this.getViewer());
            }
        }
    }

    private final class PatternEditMenu extends Menu {
        private final Button backButton;
        private final Button powerButton;
        private final Button hasPowerButton;
        private final Button hasPowerPane;
        private final Button hasShapeButton;
        private final Button particleButton;
        private final Button hasParticlePane;
        private final Button delayButton;
        private final Button insideButton;
        private final Button actionButton;
        private final Button durationButton;

        public PatternEditMenu(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
            this.setTitle("&3&l" + bondBase.getName() + "'s &r&3Pattern " + patternCache.getNumber());
            this.setSize(36);
            this.backButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        (AdminMenu.this.new PatternListMenu(bondBase, tierCache)).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                }
            };
            this.powerButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (!patternCache.getPowerName().equals("NONE"))
                            new PowerListMenu(bondBase, tierCache, patternCache).displayTo(player);
                        else {
                            PatternEditMenu.this.restartMenu("&c&lPowers are Disabled!");
                            CompSound.VILLAGER_NO.play(player);
                        }
                    }

                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.IRON_AXE, "&3&lPower", new String[]{"&eCurrent Power: &3" + patternCache.getPowerName(), "&7Gives the player a ability", "&7when the pattern is activated.", " ", "&f(" + (patternCache.getPowerName().equals("NONE") ? "The Power is Disabled)" : "Click to Change)")}).make();
                }
            };
            this.hasPowerButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (patternCache.getPowerName().equals("NONE"))
                            bondBase.setPower(tierCache.getTier(), patternCache.getNumber(), "Toss");
                        else
                            bondBase.setPower(tierCache.getTier(), patternCache.getNumber(), "NONE");

                        (AdminMenu.this.new PatternEditMenu(bondBase, tierCache, patternCache)).displayTo(player);
                    }
                }

                public ItemStack getItem() {
                    return ItemCreator.of(patternCache.getPowerName().equals("NONE") ? CompMaterial.RED_DYE : CompMaterial.LIME_DYE, "&3&l" + (patternCache.getPowerName().equals("NONE") ? "Disabled" : "Enabled"), new String[]{"&7Whether or not this pattern", "&7will give the player an ability.", " ", "&f(Click to " + (patternCache.getPowerName().equals("NONE") ? "Enable" : "Disable") + ")"}).glow(!patternCache.getPowerName().equals("NONE")).make();
                }
            };
            this.hasPowerPane = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                }

                public ItemStack getItem() {
                    return ItemCreator.of(patternCache.getPowerName().equals("NONE") ? CompMaterial.RED_STAINED_GLASS_PANE : CompMaterial.LIME_STAINED_GLASS_PANE, "&3&l" + (patternCache.getPowerName().equals("NONE") ? "Disabled" : "Enabled"), new String[0]).make();
                }
            };
            this.particleButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (!patternCache.getParticleName().equals("NONE")) {
                            new ParticleListMenu(bondBase, tierCache, patternCache).displayTo(player);
                        } else {
                            PatternEditMenu.this.restartMenu("&c&lParticles are Disabled!");
                            CompSound.VILLAGER_NO.play(player);
                        }
                    }

                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.NETHER_STAR, "&3&lParticle", new String[]{"&eCurrent Particle: &3" + patternCache.getParticleName(), "&eCurrent Position: &3" + (patternCache.getParticleName().equals("NONE") ? "NONE" : patternCache.getPositionName()), "&eCurrent Shape: &3" + patternCache.getShapeName(), "&7The visual display of the", "&7the pattern when played.", " ", "&f(" + (patternCache.getParticleName().equals("NONE") ? "The Particle is Disabled)" : "Click to Change)")}).make();
                }
            };
            this.hasShapeButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (patternCache.getShapeName().equals("NONE")) {
                            bondBase.setShape(tierCache.getTier(), patternCache.getNumber(), "Dot");
                            bondBase.setParticle(tierCache.getTier(), patternCache.getNumber(), "VILLAGER_HAPPY");
                        } else {
                            bondBase.setShape(tierCache.getTier(), patternCache.getNumber(), "NONE");
                            bondBase.setParticle(tierCache.getTier(), patternCache.getNumber(), "NONE");
                        }

                        (AdminMenu.this.new PatternEditMenu(bondBase, tierCache, patternCache)).displayTo(player);
                    }

                }

                public ItemStack getItem() {
                    return ItemCreator.of(patternCache.getShapeName().equals("NONE") ? CompMaterial.RED_DYE : CompMaterial.LIME_DYE, "&3&l" + (patternCache.getShapeName().equals("NONE") ? "Disabled" : "Enabled"), new String[]{"&7Whether or not this pattern", "&7will have a particle display.", " ", "&f(Click to " + (patternCache.getShapeName().equals("NONE") ? "Enable" : "Disable") + ")"}).glow(!patternCache.getShapeName().equals("NONE")).make();
                }
            };
            this.hasParticlePane = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                }

                public ItemStack getItem() {
                    return ItemCreator.of(patternCache.getParticleName().equals("NONE") ? CompMaterial.RED_STAINED_GLASS_PANE : CompMaterial.LIME_STAINED_GLASS_PANE, "&3&l" + (patternCache.getParticleName().equals("NONE") ? "Disabled" : "Enabled"), new String[0]).make();
                }
            };
            this.delayButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new DelayConversation(player, bondBase, tierCache, patternCache);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.REPEATER, "&3&lDelay", new String[]{"&eCurrent Delay: &3" + patternCache.getDelay() + " seconds", "&7The pattern can only be", "&7 played once after each delay.", " ", "&f(Click to Change)"}).make();
                }
            };
            this.insideButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        if (patternCache.isInside())
                            bondBase.setInside(tierCache.getTier(), patternCache.getNumber(), false);
                        else
                            bondBase.setInside(tierCache.getTier(), patternCache.getNumber(), true);
                        (AdminMenu.this.new PatternEditMenu(bondBase, tierCache, patternCache)).displayTo(player);
                    }
                }

                public ItemStack getItem() {
                    return ItemCreator.of(patternCache.isInside() ? CompMaterial.CHEST_MINECART : CompMaterial.MINECART, "&3&lInside", new String[]{"&eInside: &3" + (patternCache.isInside() ? "true" : "false"), "&7Whether or not the player needs", "&7to be in range of " + tierCache.getRange() + " blocks", "&7with their friend in order", "&7for the pattern to activate.", " ", "&f(Click to " + (patternCache.isInside() ? "Disable)" : "Enabled)")}).make();
                }
            };
            this.actionButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick() && !patternCache.getActionName().equals("AUTO"))
                        new ActionListMenu(bondBase, tierCache, patternCache).displayTo(player);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.FISHING_ROD, "&3&lAction", new String[]{"&eCurrent Action: &3" + patternCache.getActionName(), "&eItem in Hand: &3" + patternCache.getActionItemName(), "&7The event that activates", "&7this particular pattern.", " ", "&f(" + (patternCache.getActionName().equals("AUTO") ? "The Action is Disabled)" : "Click to Change)")}).make();
                }
            };
            this.durationButton = new Button() {
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (clickType.isLeftClick())
                        new DurationConversation(player, bondBase, tierCache, patternCache);
                }

                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.POTION, "&3&lDuration", new String[]{"&eCurrent Duration: &3" + patternCache.getDuration() + " seconds", "&7Once the pattern activates,", "&7the pattern will play until", "&7the duration ends.", "&7The Duration must be <= the Delay.", " ", "&f(Click to Change)"}).make();
                }
            };
        }

        public ItemStack getItemAt(int slot) {
            if (slot == this.getSize() - 1) {
                return this.backButton.getItem();
            } else if (slot == 10) {
                return this.hasPowerPane.getItem();
            } else if (slot == 11) {
                return this.powerButton.getItem();
            } else if (slot == 12) {
                return this.hasPowerButton.getItem();
            } else if (slot == 19) {
                return this.hasParticlePane.getItem();
            } else if (slot == 20) {
                return this.particleButton.getItem();
            } else if (slot == 21) {
                return this.hasShapeButton.getItem();
            } else if (slot == 16) {
                return this.actionButton.getItem();
            } else if (slot == 25) {
                return this.insideButton.getItem();
            } else if (slot == 14) {
                return this.delayButton.getItem();
            } else {
                return slot == 23 ? this.durationButton.getItem() : ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
            }
        }

        public class DelayConversation {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;
            private Player player;

            public DelayConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                (new BondDelayConversation()).show(player);
                this.player = player;
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
            }

            private final class BondDelayConversation extends SimplePrompt {
                public BondDelayConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type the amount of seconds this pattern will be delayed. Maximum 60 seconds. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    if (!Valid.isInteger(input))
                        return false;
                    else {
                        int level = Integer.parseInt(input);
                        return level >= 0 && level < 61;
                    }
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    return "&cYour input &e" + invalidInput + " &cwas not a whole number between 0 and 60 exclusive. Try again! &7Type &oexit &r&7to leave.";
                }

                @Nullable
                protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                    int delay = Integer.parseInt(input);
                    DelayConversation.this.bondBase.setDelay(DelayConversation.this.tierCache.getTier(), DelayConversation.this.patternCache.getNumber(), delay);
                    if (DelayConversation.this.patternCache.getDuration() > delay)
                        DelayConversation.this.bondBase.setDuration(DelayConversation.this.tierCache.getTier(), DelayConversation.this.patternCache.getNumber(), delay);


                    CompSound.CAT_MEOW.play(DelayConversation.this.player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                    (AdminMenu.this.new PatternEditMenu(DelayConversation.this.bondBase, DelayConversation.this.tierCache, DelayConversation.this.patternCache)).displayTo(AdminMenu.this.getViewer());
                }
            }
        }

        public class DurationConversation {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;
            private Player player;

            public DurationConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                (new BondDurationConversation()).show(player);
                this.player = player;
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
            }

            private final class BondDurationConversation extends SimplePrompt {
                public BondDurationConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type the amount of seconds the pattern will last. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    if (!Valid.isInteger(input))
                        return false;
                    else {
                        int level = Integer.parseInt(input);
                        return level >= 0 && level <= DurationConversation.this.patternCache.getDelay();
                    }
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    return "&cYour input &e" + invalidInput + " &cwas not a whole number between 0 and the current delay of " + DurationConversation.this.patternCache.getDelay() + " seconds. Try again! &7Type &oexit &r&7to leave.";
                }

                @Nullable
                protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                    int duration = Integer.parseInt(input);
                    DurationConversation.this.bondBase.setDuration(DurationConversation.this.tierCache.getTier(), DurationConversation.this.patternCache.getNumber(), duration);
                    CompSound.CAT_MEOW.play(DurationConversation.this.player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                    (AdminMenu.this.new PatternEditMenu(DurationConversation.this.bondBase, DurationConversation.this.tierCache, DurationConversation.this.patternCache)).displayTo(AdminMenu.this.getViewer());
                }
            }
        }

        private final class ActionListMenu extends MenuPagged<String> {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;
            private final Button backButton;
            private final Button currentActionButton;
            private final Button actionItemButton;

            protected ActionListMenu(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                super(ActionBase.getActionNames());
                this.setTitle("&3&lSelect a Action");
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            new PatternEditMenu(bondBase, tierCache, patternCache).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
                this.currentActionButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.FISHING_ROD, "&3&lAction", new String[]{"&eCurrent Action: &3" + patternCache.getActionName(), "&7The event that activates", "&7this particular pattern."}).make();
                    }
                };
                this.actionItemButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            //new ActionItemMenu(bondBase, tierCache, patternCache, patternCache.getActionName()).displayTo(player);
                        }
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.RABBIT_FOOT, "&3&lItem in Hand", new String[]{"&eCurrent Item: &3" + patternCache.getActionItemName(), "&7If the player undergoes", "&7the action " + patternCache.getActionName(), "&7and holds " + patternCache.getActionItemName(), "&7this pattern will be activated.", " ", "&f(Click to Change)"}).make();
                    }
                };
            }

            protected ItemStack convertToItemStack(String actionName) {
                ActionBase actionBase = ActionBase.getByName(actionName);
                return ItemCreator.of(CompMaterial.valueOf(actionBase.getIcon()), "&3&l" + actionName, new String[]{"&eType: &3" + actionBase.getType(), "&7" + (actionBase.getType().equals("Selective") ? "One entity is affected." : "Multiple entities are affected."), " ", "&f(Click to Apply)"}).glow(this.patternCache.getActionName().equals(actionName)).amount(actionBase.getAmount()).make();
            }

            protected void onPageClick(Player player, String actionName, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    this.bondBase.setAction(this.tierCache.getTier(), this.patternCache.getNumber(), actionName);
                    new ActionListMenu(this.bondBase, this.tierCache, this.patternCache).displayTo(player);
                }
            }

            public ItemStack getItemAt(int slot) {
                boolean multiplePages = this.getPages().size() > 1;
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else if (slot == this.getSize() - 4) {
                    return this.actionItemButton.getItem();
                } else if (slot == this.getSize() - 6) {
                    return this.currentActionButton.getItem();
                } else {
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        } else if (this.getSize() != 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }

        private final class PowerListMenu extends MenuPagged<String> {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;
            private final Button backButton;
            private final Button createPowerButton;
            private final Button strengthButton;
            private final Button currentPowerButton;

            protected PowerListMenu(BondBase bondBase, BondBase.TierCache tierCache, final BondBase.TierCache.PatternCache patternCache) {
                super(PowerBase.getPowerNames());
                this.setTitle("&3&lSelect a Power");
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            (AdminMenu.this.new PatternEditMenu(bondBase, tierCache, patternCache)).displayTo(player);
                        }

                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
                this.createPowerButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            new CustomPowerConversation(player, bondBase, tierCache, patternCache);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.WRITABLE_BOOK, "&3&lCreate Power", new String[]{"&7Make your own power", "&7with a command of your", "&7choosing.", " ", "&f(Click to Create)"}).make();
                    }
                };
                this.strengthButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        String powerName = patternCache.getPowerName();
                        if (clickType.isLeftClick())
                            new StrengthConversation(player, bondBase, tierCache, patternCache);
                    }

                    public ItemStack getItem() {
                        PowerBase powerBase = PowerBase.getPower(patternCache.getPowerName());
                        return ItemCreator.of(CompMaterial.SPAWNER, "&3&lStrength", new String[]{"&eCurrent Strength: &3" + patternCache.getStrength() + " " + powerBase.getUnits(), "&7The amount of vigor", "&7this power has.", "&7Used to customize powers", "&7to your liking.", " ", "&f(Click to Change)"}).make();
                    }
                };
                this.currentPowerButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.IRON_AXE, "&3&lPower", new String[]{"&eCurrent Power: &3" + patternCache.getPowerName(), "&7The ability of the", "&7activated pattern."}).make();
                    }
                };
            }

            protected ItemStack convertToItemStack(String powerName) {
                PowerBase powerBase = PowerBase.getPower(powerName);
                ArrayList<String> convertingLore = new ArrayList(powerBase.getLore());
                if (powerBase.usesStrength()) {
                    convertingLore.add(0, "&eStrength: &3" + this.patternCache.getStrength() + " " + powerBase.getUnits());
                }

                convertingLore.add(" ");
                convertingLore.add("&f(Left Click to Apply)");
                if (PowerBase.customPowerNames.contains(powerName)) {
                    convertingLore.add("&f(Right Click to Edit)");
                }

                return ItemCreator.of(CompMaterial.valueOf(powerBase.getIcon()), "&3&l" + powerName, convertingLore).glow(this.patternCache.getPowerName().equals(powerName)).make();
            }

            protected void onPageClick(Player player, String powerName, ClickType clickType) {
                if (clickType.isRightClick()) {
                    if (PowerBase.customPowerNames.contains(powerName)) {
                        new PowerEditMenu(this.bondBase, this.tierCache, this.patternCache, powerName).displayTo(player);
                    } else {
                        this.restartMenu("&c&lCannot Edit!");
                        CompSound.VILLAGER_NO.play(player);
                    }
                }

                if (clickType.isLeftClick()) {
                    this.bondBase.setPower(this.tierCache.getTier(), this.patternCache.getNumber(), powerName);
                    new PowerListMenu(this.bondBase, this.tierCache, this.patternCache).displayTo(player);
                }
            }

            public ItemStack getItemAt(int slot) {
                PowerBase powerBase = PowerBase.getPower(this.patternCache.getPowerName());
                boolean multiplePages = this.getPages().size() > 1;
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else if (slot == this.getSize() - 3 && (true)) {//|| powerBase.getCommand().contains("%STRENGTH%") || powerBase.getExitCommand().contains("%STRENGTH%"))) { //todo
                    return powerBase.usesStrength() ? this.strengthButton.getItem() : ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                } else if (slot == this.getSize() - 5) {
                    return this.currentPowerButton.getItem();
                } else if (slot == this.getSize() - 7) {
                    return this.createPowerButton.getItem();
                } else {
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 1) {
                        if (powerBase.usesStrength()) { //todo
                            if (slot == this.getSize() - 5) {
                                return super.getItemAt(slot);
                            }
                        } else if (slot == this.getSize() - 3) {
                            return super.getItemAt(slot);
                        }

                        if (slot != this.getSize() - 5 && slot != this.getSize() - 7) {
                            if (multiplePages) {
                                if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                    return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                                }
                            } else if (this.getSize() != 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }

        public class CustomPowerConversation {
            private Player player;
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;

            public CustomPowerConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                this.player = player;
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
                (new NewPowerConversation()).show(player);
            }

            private final class NewPowerConversation extends SimplePrompt {
                public NewPowerConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type the name for this new Power. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    for (String powerName : PowerBase.getPowerNames())
                        if (input.equals(powerName))
                            return false;

                    return true;
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    return "&cThe power &e" + invalidInput + " &calready exists! Try again! &7Type &oexit &r&7to leave.";
                }

                @Nullable
                protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                    PowerBase.createPower(input);
                    CompSound.CAT_MEOW.play(CustomPowerConversation.this.player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                    new PowerListMenu(bondBase, tierCache, patternCache).displayTo(AdminMenu.this.getViewer());
                }
            }
        }

        public class StrengthConversation {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;
            private Player player;

            public StrengthConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                (new PowerStrengthConversation()).show(player);
                this.player = player;
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
            }

            private final class PowerStrengthConversation extends SimplePrompt {
                public PowerStrengthConversation() {
                    super(false);
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aPlease type the numerical value to be the strength of the power. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    if (!Valid.isInteger(input)) {
                        return false;
                    } else if (input.contains(".")) {
                        return false;
                    } else {
                        int strength = Integer.parseInt(input);
                        return strength >= 1 && strength <= 60;
                    }
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    return "&cYour input &e" + invalidInput + " &cwas not a whole number less than between 1 and 60. Try again! &7Type &oexit &r&7to leave.";
                }

                @Nullable
                protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                    int strength = Integer.parseInt(input);
                    StrengthConversation.this.bondBase.setStrength(StrengthConversation.this.tierCache.getTier(), StrengthConversation.this.patternCache.getNumber(), strength);
                    CompSound.CAT_MEOW.play(StrengthConversation.this.player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                    new PowerListMenu(bondBase, tierCache, patternCache).displayTo(AdminMenu.this.getViewer());
                }
            }
        }

        private final class PowerEditMenu extends Menu {
            private final Button backButton;
            private final Button powerRemovalButton;
            private final Button iconButton;
            private final Button loreButton;
            private final Button senderButton;
            private final Button commandButton;
            private final Button hasCommandButton;
            private final Button hasCommandPane;
            private final Button exitCommandButton;
            private final Button hasExitCommandButton;
            private final Button hasExitCommandPane;

            public PowerEditMenu(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String powerName) {
                this.setTitle("&3&l" + powerName);
                this.setSize(36);
                final PowerBase powerBase = PowerBase.getPower(powerName);
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            new PowerListMenu(bondBase, tierCache, patternCache).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
                this.powerRemovalButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            new PowerRemoveConversation(bondBase, tierCache, patternCache, powerName).show(player);
                            displayTo(player);
                        }
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.TNT, "&c&lDelete Power", new String[]{"&7Removes the power from", "&7all bonds and deletes the", "&7power from the power list.", " ", "&f(Click to Remove)"}).make();
                    }
                };
                this.iconButton = new Button() { //todo Item List...
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            //  (AdminMenu.this.new PowerSelectIconMenu(bondBase, tierCache, patternCache, powerName)).displayTo(player);
                        }
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.valueOf(powerBase.getIcon()), "&3&lIcon", new String[]{"&eCurrent Icon: &3" + powerBase.getIcon(), "&7The material that represents", "&7this power in the menus.", " ", "&f(Click to Change)"}).make();
                    }
                };
                this.loreButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            new PowerLoreConversation(player, bondBase, tierCache, patternCache, powerName);
                    }

                    public ItemStack getItem() {
                        PowerBase powerBase = PowerBase.getPower(powerName);
                        ArrayList<String> convertingLore = new ArrayList(powerBase.getLore());
                        convertingLore.add(0, "&eCurrent Lore: ");
                        convertingLore.add(" ");
                        convertingLore.add("&f(Click to change)");
                        return ItemCreator.of(CompMaterial.BOOK, "&3&lLore", convertingLore).make();
                    }
                };
                this.senderButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            if (powerBase.getSender().equals("Console")) {
                                powerBase.setSender("Player");
                            } else {
                                powerBase.setSender("Console");
                            }
                            new PowerEditMenu(bondBase, tierCache, patternCache, powerName).displayTo(player);
                        }

                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(powerBase.getSender().equals("Console") ? CompMaterial.WITHER_SKELETON_SKULL : CompMaterial.PLAYER_HEAD, "&3&lSender", new String[]{"&eCurrent Sender: &3" + powerBase.getSender(), "&7The entity that runs", "&7these commands.", " ", "&f(Click to Switch to " + (powerBase.getSender().equals("Console") ? "Player)" : "Console)")}).make();
                    }
                };
                this.commandButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            if (!powerBase.getCommand().equals("NONE")) {
                                new PowerCommandConversation(player, bondBase, tierCache, patternCache, powerName);
                            } else {
                                PowerEditMenu.this.restartMenu("&c&lCannot Edit!");
                                CompSound.VILLAGER_NO.play(player);
                            }
                        }

                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.COMMAND_BLOCK, "&3&lCommand", new String[]{"&eCurrent Command: &3/" + powerBase.getCommand(), "&7Once the pattern is activated,", "&7this command will run.", " ", "&7If you want this power", "&7to use strength for easy", "&7customization, input", "&7%STRENGTH% in your command.", "&7Strength can only", "&7be an Integer.", " ", "&f(Click to Change)"}).make();
                    }
                };
                this.hasCommandButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            if (powerBase.getCommand().equals("NONE")) {
                                powerBase.setCommand("say hi");
                            } else {
                                powerBase.setCommand("NONE");
                            }

                            new PowerEditMenu(bondBase, tierCache, patternCache, powerName).displayTo(player);
                        }

                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(powerBase.getCommand().equals("NONE") ? CompMaterial.RED_DYE : CompMaterial.LIME_DYE, "&3&l" + (powerBase.getCommand().equals("NONE") ? "Disabled" : "Enabled"), new String[]{"&7Whether or not this power", "&7will have a command", "&7sent from the sender", "&7when the power is activated.", " ", "&f(Click to " + (powerBase.getCommand().equals("NONE") ? "Enable" : "Disable") + ")"}).glow(!powerBase.getCommand().equals("NONE")).make();
                    }
                };
                this.hasCommandPane = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(powerBase.getCommand().equals("NONE") ? CompMaterial.RED_STAINED_GLASS_PANE : CompMaterial.LIME_STAINED_GLASS_PANE, "&3&l" + (powerBase.getCommand().equals("NONE") ? "Disabled" : "Enabled"), new String[0]).make();
                    }
                };
                this.exitCommandButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            if (!powerBase.getExitCommand().equals("NONE"))
                                new PowerExitCommandConversation(player, bondBase, tierCache, patternCache, powerName);
                            else {
                                PowerEditMenu.this.restartMenu("&c&lCannot Edit!");
                                CompSound.VILLAGER_NO.play(player);
                            }
                        }

                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.COMMAND_BLOCK_MINECART, "&3&lExit Command", new String[]{"&eCurrent Exit Command: &3/" + powerBase.getExitCommand(), "&7This command will run", "&7when the pattern's duration", "&7has finished.", " ", "&7If you want this power", "&7to use strength for easy", "&7customization, input", "&7%STRENGTH% in your command.", "&7Strength can only", "&7be an Integer.", " ", "&f(Click to Change)"}).make();
                    }
                };
                this.hasExitCommandButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            if (powerBase.getExitCommand().equals("NONE")) {
                                powerBase.setExitCommand("say hi");
                            } else {
                                powerBase.setExitCommand("NONE");
                            }

                            new PowerEditMenu(bondBase, tierCache, patternCache, powerName).displayTo(player);
                        }

                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(powerBase.getExitCommand().equals("NONE") ? CompMaterial.RED_DYE : CompMaterial.LIME_DYE, "&3&l" + (powerBase.getExitCommand().equals("NONE") ? "Disabled" : "Enabled"), new String[]{"&7Whether or not this power", "&7will have a command", "&7sent from the sender", "&7when the pattern's duration ends.", " ", "&f(Click to " + (powerBase.getExitCommand().equals("NONE") ? "Enable" : "Disable") + ")"}).glow(!powerBase.getExitCommand().equals("NONE")).make();
                    }
                };
                this.hasExitCommandPane = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(powerBase.getExitCommand().equals("NONE") ? CompMaterial.RED_STAINED_GLASS_PANE : CompMaterial.LIME_STAINED_GLASS_PANE, "&3&l" + (powerBase.getExitCommand().equals("NONE") ? "Disabled" : "Enabled"), new String[0]).make();
                    }
                };
            }

            public ItemStack getItemAt(int slot) {
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else if (slot == 10) {
                    return this.iconButton.getItem();
                } else if (slot == 11) {
                    return this.loreButton.getItem();
                } else if (slot == 12) {
                    return this.senderButton.getItem();
                } else if (slot == 14) {
                    return this.hasCommandPane.getItem();
                } else if (slot == 15) {
                    return this.commandButton.getItem();
                } else if (slot == 16) {
                    return this.hasCommandButton.getItem();
                } else if (slot == 20) {
                    return this.powerRemovalButton.getItem();
                } else if (slot == 23) {
                    return this.hasExitCommandPane.getItem();
                } else if (slot == 24) {
                    return this.exitCommandButton.getItem();
                } else {
                    return slot == 25 ? this.hasExitCommandButton.getItem() : ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                }
            }

            public class PowerRemoveConversation extends SimplePrompt {
                private BondBase bondBase;
                private BondBase.TierCache tierCache;
                private BondBase.TierCache.PatternCache patternCache;
                private String powerName;

                public PowerRemoveConversation(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String powerName) {
                    super(false);
                    this.bondBase = bondBase;
                    this.tierCache = tierCache;
                    this.patternCache = patternCache;
                    this.powerName = powerName;
                }

                protected String getPrompt(ConversationContext conversationContext) {
                    return "&aTo remove the power &2" + powerName + " &atype &oremove. &7Type &oexit &r&7to leave.";
                }

                protected boolean isInputValid(ConversationContext context, String input) {
                    if (input.equalsIgnoreCase("remove"))
                        return true;

                    return false;
                }

                protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                    return "&cThe input &e" + invalidInput + "&c is invalid! Try again! &7Type &oremove &r&7to remove " + powerName + " &7or &oexit &r&7to leave.";
                }

                protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                    Player player = this.getPlayer(context);
                    PowerBase.removePower(powerName);
                    CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                    return null;
                }

                public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                    new PowerListMenu(bondBase, tierCache, patternCache).displayTo(AdminMenu.this.getViewer());
                }
            }

            public class PowerLoreConversation {
                private BondBase bondBase;
                private Player player;

                public PowerLoreConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String powerName) {
                    this.player = player;
                    this.bondBase = bondBase;
                    new PowerSetLoreConversation(bondBase, tierCache, patternCache, powerName, player).show(player);
                }

                public class PowerSetLoreConversation extends SimplePrompt {
                    private BondBase bondBase;
                    private BondBase.TierCache tierCache;
                    private BondBase.TierCache.PatternCache patternCache;
                    private String powerName;
                    private Player player;

                    public PowerSetLoreConversation(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String powerName, Player player) {
                        super(false);
                        this.bondBase = bondBase;
                        this.tierCache = tierCache;
                        this.patternCache = patternCache;
                        this.powerName = powerName;
                        this.player = player;
                    }

                    @Override
                    protected String getPrompt(ConversationContext conversationContext) {
                        return "&aPlease type the lore of the power &e" + powerName + "&a. To go to the next line, input &e/n&a. You can also use Color Codes and PlaceHolders. &7Type &oexit &r&7to leave.";
                    }

                    protected boolean isInputValid(ConversationContext context, String input) {
                        return true;
                    }

                    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                        return "&cSomething went wrong. Try again! &7Type &oexit &r&7to leave.";
                    }

                    protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                        List<String> lore = Arrays.asList(input.split("/n"));
                        PowerBase powerBase = PowerBase.getPower(powerName);
                        powerBase.setLore(lore);
                        CompSound.CAT_MEOW.play(player, 5.0F, 0.3F);
                        return null;
                    }

                    public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                        new PowerEditMenu(bondBase, tierCache, patternCache, powerName).displayTo(PowerLoreConversation.this.player);
                    }
                }
            }

            public class PowerCommandConversation {
                private Player player;
                private BondBase bondBase;
                private BondBase.TierCache tierCache;
                private BondBase.TierCache.PatternCache patternCache;
                private String powerName;

                public PowerCommandConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String powerName) {
                    this.player = player;
                    this.bondBase = bondBase;
                    this.tierCache = tierCache;
                    this.patternCache = patternCache;
                    this.powerName = powerName;
                    (new NewCommandConversation()).show(player);
                }

                private final class NewCommandConversation extends SimplePrompt {
                    public NewCommandConversation() {
                        super(false);
                    }

                    protected String getPrompt(ConversationContext conversationContext) {
                        return "&aPlease type the command for the power &e" + PowerCommandConversation.this.powerName + "&a. Don't use the /. &7Type &oexit &r&7to leave.";
                    }

                    protected boolean isInputValid(ConversationContext context, String input) {
                        return true;
                    }

                    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                        return "&cSomething went wrong! Try again! &7Type &oexit &r&7to leave.";
                    }

                    @Nullable
                    protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                        PowerBase powerBase = PowerBase.getPower(PowerCommandConversation.this.powerName);
                        powerBase.setCommand(input);
                        CompSound.CAT_MEOW.play(PowerCommandConversation.this.player, 5.0F, 0.3F);
                        return null;
                    }

                    public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                        new PowerEditMenu(PowerCommandConversation.this.bondBase, PowerCommandConversation.this.tierCache, PowerCommandConversation.this.patternCache, PowerCommandConversation.this.powerName).displayTo(AdminMenu.this.getViewer());
                    }
                }
            }

            public class PowerExitCommandConversation {
                private Player player;
                private BondBase bondBase;
                private BondBase.TierCache tierCache;
                private BondBase.TierCache.PatternCache patternCache;
                private String powerName;

                public PowerExitCommandConversation(Player player, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String powerName) {
                    this.player = player;
                    this.bondBase = bondBase;
                    this.tierCache = tierCache;
                    this.patternCache = patternCache;
                    this.powerName = powerName;
                    (new NewExitCommandConversation()).show(player);
                }

                private final class NewExitCommandConversation extends SimplePrompt {
                    public NewExitCommandConversation() {
                        super(false);
                    }

                    protected String getPrompt(ConversationContext conversationContext) {
                        return "&aPlease type the exit command for the power &e" + PowerExitCommandConversation.this.powerName + "&a. Don't use the /. &7Type &oexit &r&7to leave.";
                    }

                    protected boolean isInputValid(ConversationContext context, String input) {
                        return true;
                    }

                    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
                        return "&cSomething went wrong! Try again! &7Type &oexit &r&7to leave.";
                    }

                    @Nullable
                    protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                        PowerBase powerBase = PowerBase.getPower(PowerExitCommandConversation.this.powerName);
                        powerBase.setExitCommand(input);
                        CompSound.CAT_MEOW.play(PowerExitCommandConversation.this.player, 5.0F, 0.3F);
                        return null;
                    }

                    public void onConversationEnd(SimpleConversation conversation, ConversationAbandonedEvent event) {
                        new PowerEditMenu(PowerExitCommandConversation.this.bondBase, PowerExitCommandConversation.this.tierCache, PowerExitCommandConversation.this.patternCache, PowerExitCommandConversation.this.powerName).displayTo(AdminMenu.this.getViewer());
                    }
                }
            }
        }

        private final class ParticleListMenu extends MenuPagged<CompParticle> {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;
            private final Button backButton;
            private final Button currentParticleButton;
            private final Button shapeButton;
            private final Button positionButton;

            protected ParticleListMenu(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                super((Iterable) Arrays.asList(CompParticle.values()).stream().filter((particle) -> {
                    return (particle.name().contains("EXPLOSION") || particle.name().contains("FIREWORKS_SPARK") || particle.name().contains("SMOKE_NORMAL") || particle.name().contains("SMOKE_LARGE")
                            || particle.name().contains("SPELL") || particle.name().contains("DRIP_WATER") || particle.name().contains("DRIP_LAVA") || particle.name().contains("VILLIAGER")
                            || particle.name().contains("TOWN_AURA") || particle.name().contains("NOTE") || particle.name().contains("PORTAL") || particle.name().contains("ENCHANTMENT_TABLE") || particle.name().contains("FLAME") || particle.name().contains("LAVA")
                            || particle.name().contains("CLOUD") || particle.name().contains("SNOWBALL") || particle.name().contains("SNOW_SHOVEL") || particle.name().contains("SLIME") || particle.name().contains("HEART") || particle.name().contains("HEART") || particle.name().contains("WATER_BUBBLE") || particle.name().contains("FOOTSTEP") || particle.name().contains("SUSPENDED_DEPTH")
                    ) && !particle.name().contains("SOUL_FIRE_FLAME") && !particle.name().contains("DRIPSTONE") && !particle.name().contains("REVERSE") && !particle.name().contains("LANDING") && !particle.name().contains("FALLING") && !particle.name().contains("SMALL_FLAME");
                }).collect(Collectors.toList()));

                //return particle != CompParticle.WATER_BUBBLE && particle != CompParticle.WATER_BUBBLE && particle != CompParticle.WATER_WAKE && particle != CompParticle.SUSPENDED && particle != CompParticle.SWEEP_ATTACK && particle != CompParticle.SUSPENDED_DEPTH && particle != CompParticle.CRIT && particle != CompParticle.CRIT_MAGIC && particle != CompParticle.FOOTSTEP && particle != CompParticle.REDSTONE && particle != CompParticle.BARRIER && particle != CompParticle.ITEM_CRACK && particle != CompParticle.ITEM_TAKE && particle != CompParticle.MOB_APPEARANCE && particle != CompParticle.BLOCK_CRACK && particle != CompParticle.BLOCK_DUST && particle != CompParticle.FALLING_DUST && particle != CompParticle.BUBBLE_POP && particle != CompParticle.BUBBLE_COLUMN_UP && particle != CompParticle.CURRENT_DOWN && particle != CompParticle.DUST_COLOR_TRANSITION && particle != CompParticle.VIBRATION && particle != CompParticle.DRIPPING_DRIPSTONE_LAVA;

                this.setTitle("&3&lSelect a Particle");
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            (AdminMenu.this.new PatternEditMenu(bondBase, tierCache, patternCache)).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
                this.currentParticleButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.NETHER_STAR, "&3&lParticle", new String[]{"&eCurrent Particle: &3" + patternCache.getParticleName(), "&7The particle that is played", "&7in the given position", "&7and shape."}).make();
                    }
                };
                this.shapeButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick() && !patternCache.getShapeName().equals("NONE"))
                            new ShapeListMenu(bondBase, tierCache, patternCache).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_STAIRS, "&3&lShape", new String[]{"&eCurrent Shape: &3" + patternCache.getShapeName(), "&7The shape of the", "&7particles when played.", " ", "&f(" + (patternCache.getShapeName().equals("NONE") ? "The Shape is Disabled)" : "Click to Change)")}).make();
                    }
                };
                this.positionButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick() && !patternCache.getShapeName().equals("NONE"))
                            new PositionListMenu(bondBase, tierCache, patternCache).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.FILLED_MAP, "&3&lPosition", new String[]{"&eCurrent Position: &3" + patternCache.getPositionName(), "&7The location of the", "&7particles when played.", " ", "&f(Click to Change)"}).make();
                    }
                };
            }

            protected ItemStack convertToItemStack(CompParticle particle) {
                String particleName = particle.name();
                CompMaterial material = CompMaterial.BARRIER;
                int amount = 1;
                if (particle.name().equals("FOOTSTEP")) {
                    material = CompMaterial.LEATHER_BOOTS;
                }
                if (particle.name().equals("WATER_BUBBLE")) {
                    material = CompMaterial.FISHING_ROD;
                }
                if (particle.name().equals("SUSPENDED_DEPTH")) {
                    material = CompMaterial.BEDROCK;
                }
                if (particle.name().equals("EXPLOSION_NORMAL")) {
                    material = CompMaterial.TNT;
                }

                if (particle.name().equals("EXPLOSION_LARGE")) {
                    material = CompMaterial.TNT;
                    amount = 2;
                }

                if (particle.name().equals("EXPLOSION_HUGE")) {
                    material = CompMaterial.TNT;
                    amount = 3;
                }

                if (particle.name().equals("FIREWORKS_SPARK")) {
                    material = CompMaterial.FIREWORK_ROCKET;
                }

                if (particle.name().equals("WATER_SPLASH")) {
                    material = CompMaterial.WET_SPONGE;
                }

                if (particle.name().equals("SMOKE_NORMAL")) {
                    material = CompMaterial.GRAY_DYE;
                }

                if (particle.name().equals("SMOKE_LARGE")) {
                    material = CompMaterial.GRAY_DYE;
                    amount = 2;
                }

                if (particle.name().equals("SPELL")) {
                    material = CompMaterial.POTION;
                }

                if (particle.name().equals("SPELL_INSTANT")) {
                    material = CompMaterial.POTION;
                    amount = 2;
                }

                if (particle.name().equals("SPELL_MOB")) {
                    material = CompMaterial.WITHER_SKELETON_SKULL;
                }

                if (particle.name().equals("SPELL_MOB_AMBIENT")) {
                    material = CompMaterial.WITHER_SKELETON_SKULL;
                    amount = 2;
                }

                if (particle.name().equals("SPELL_WITCH")) {
                    material = CompMaterial.BREWING_STAND;
                }

                if (particle.name().equals("DRIP_WATER")) {
                    material = CompMaterial.WATER_BUCKET;
                }

                if (particle.name().equals("DRIP_LAVA")) {
                    material = CompMaterial.LAVA_BUCKET;
                }

                if (particle.name().equals("VILLAGER_ANGRY")) {
                    material = CompMaterial.YELLOW_DYE;
                }

                if (particle.name().equals("VILLAGER_HAPPY")) {
                    material = CompMaterial.EMERALD;
                }

                if (particle.name().equals("TOWN_AURA")) {
                    material = CompMaterial.IRON_SWORD;
                }

                if (particle.name().equals("NOTE")) {
                    material = CompMaterial.MUSIC_DISC_CAT;
                }

                if (particle.name().equals("PORTAL")) {
                    material = CompMaterial.OBSIDIAN;
                }

                if (particle.name().equals("ENCHANTMENT_TABLE")) {
                    material = CompMaterial.ENCHANTING_TABLE;
                }

                if (particle.name().equals("FLAME")) {
                    material = CompMaterial.FLINT_AND_STEEL;
                }

                if (particle.name().equals("LAVA")) {
                    material = CompMaterial.LAVA_BUCKET;
                    amount = 2;
                }

                if (particle.name().equals("CLOUD")) {
                    material = CompMaterial.BONE_MEAL;
                }

                if (particle.name().equals("SNOWBALL")) {
                    material = CompMaterial.SNOWBALL;
                }

                if (particle.name().equals("SNOW_SHOVEL")) {
                    material = CompMaterial.IRON_SHOVEL;
                }

                if (particle.name().equals("SLIME")) {
                    material = CompMaterial.SLIME_BALL;
                }

                if (particle.name().equals("HEART")) {
                    material = CompMaterial.POPPY;
                }

                if (particle.name().equals("WATER_DROP")) {
                    material = CompMaterial.FISHING_ROD;
                }

                if (particle.name().equals("DRAGON_BREATH")) {
                    material = CompMaterial.DRAGON_EGG;
                }

                if (particle.name().equals("END_ROD")) {
                    material = CompMaterial.END_ROD;
                }

                if (particle.name().equals("DAMAGE_INDICATOR")) {
                    material = CompMaterial.NETHER_WART;
                }

                if (particle.name().equals("TOTEM")) {
                    material = CompMaterial.TOTEM_OF_UNDYING;
                }

                if (particle.name().equals("SPIT")) {
                    material = CompMaterial.LIGHT_GRAY_DYE;
                }

                if (particle.name().equals("SQUID_INK")) {
                    material = CompMaterial.INK_SAC;
                }

                if (particle.name().equals("NAUTILUS")) {
                    material = CompMaterial.NAUTILUS_SHELL;
                }

                if (particle.name().equals("DOLPHIN")) {
                    material = CompMaterial.DOLPHIN_SPAWN_EGG;
                }

                if (particle.name().equals("SNEEZE")) {
                    material = CompMaterial.SLIME_BLOCK;
                }

                if (particle.name().equals("CAMPFIRE_COSY_SMOKE")) {
                    material = CompMaterial.CAMPFIRE;
                    amount = 1;
                }

                if (particle.name().equals("CAMPFIRE_SIGNAL_SMOKE")) {
                    material = CompMaterial.CAMPFIRE;
                    amount = 2;
                }

                if (particle.name().equals("COMPOSTER")) {
                    material = CompMaterial.COMPOSTER;
                }

                if (particle.name().equals("FLASH")) {
                    material = CompMaterial.LIGHTNING_ROD;
                }

                if (particle.name().equals("FALLING_LAVA")) {
                    material = CompMaterial.LAVA_BUCKET;
                    amount = 2;
                }

                if (particle.name().equals("LANDING_LAVA")) {
                    material = CompMaterial.LAVA_BUCKET;
                    amount = 3;
                }

                if (particle.name().equals("FALLING_WATER")) {
                    material = CompMaterial.WATER_BUCKET;
                    amount = 2;
                }

                if (particle.name().equals("DRIPPING_HONEY")) {
                    material = CompMaterial.HONEYCOMB;
                    amount = 1;
                }

                if (particle.name().equals("FALLING_HONEY")) {
                    material = CompMaterial.HONEYCOMB;
                    amount = 2;
                }

                if (particle.name().equals("LANDING_HONEY")) {
                    material = CompMaterial.HONEYCOMB;
                    amount = 3;
                }

                if (particle.name().equals("FALLING_NECTAR")) {
                    material = CompMaterial.HONEYCOMB_BLOCK;
                }

                if (particle.name().equals("SOUL_FIRE_FLAME")) {
                    material = CompMaterial.SOUL_CAMPFIRE;
                }

                if (particle.name().equals("ASH")) {
                    material = CompMaterial.COAL;
                }

                if (particle.name().equals("CRIMSON_SPORE")) {
                    material = CompMaterial.CRIMSON_FUNGUS;
                }

                if (particle.name().equals("WARPED_SPORE")) {
                    material = CompMaterial.CRIMSON_ROOTS;
                }

                if (particle.name().equals("SOUL")) {
                    material = CompMaterial.SOUL_SAND;
                }

                if (particle.name().equals("DRIPPING_OBSIDIAN_TEAR")) {
                    material = CompMaterial.OBSIDIAN;
                    amount = 1;
                }

                if (particle.name().equals("LANDING_OBSIDIAN_TEAR")) {
                    material = CompMaterial.OBSIDIAN;
                    amount = 2;
                }

                if (particle.name().equals("FALLING_OBSIDIAN_TEAR")) {
                    material = CompMaterial.OBSIDIAN;
                    amount = 3;
                }

                if (particle.name().equals("REVERSE_PORTAL")) {
                    material = CompMaterial.PURPLE_DYE;
                }

                if (particle.name().equals("WHITE_ASH")) {
                    material = CompMaterial.WHITE_DYE;
                }

                if (particle.name().equals("LIGHT")) {
                    material = CompMaterial.GLOWSTONE;
                }

                if (particle.name().equals("FALLING_SPORE_BLOSSOM")) {
                    material = CompMaterial.SPORE_BLOSSOM;
                    amount = 1;
                }

                if (particle.name().equals("SPORE_BLOSSOM_AIR")) {
                    material = CompMaterial.SPORE_BLOSSOM;
                    amount = 2;
                }

                if (particle.name().equals("SMALL_FLAME")) {
                    material = CompMaterial.FLINT_AND_STEEL;
                }

                if (particle.name().equals("SNOWFLAKE")) {
                    material = CompMaterial.POWDER_SNOW_BUCKET;
                }

                if (particle.name().equals("FALLING_DRIPSTONE_LAVA")) {
                    material = CompMaterial.DRIPSTONE_BLOCK;
                    amount = 1;
                }

                if (particle.name().equals("FALLING_DRIPSTONE_WATER")) {
                    material = CompMaterial.DRIPSTONE_BLOCK;
                    amount = 2;
                }

                if (particle.name().equals("DRIPPING_DRIPSTONE_WATER")) {
                    material = CompMaterial.DRIPSTONE_BLOCK;
                    amount = 3;
                }

                if (particle.name().equals("GLOW_SQUID_INK")) {
                    material = CompMaterial.GLOW_INK_SAC;
                }

                if (particle.name().equals("GLOW")) {
                    material = CompMaterial.SEA_LANTERN;
                }

                if (particle.name().equals("WAX_ON")) {
                    material = CompMaterial.GREEN_CONCRETE_POWDER;
                }

                if (particle.name().equals("WAX_OFF")) {
                    material = CompMaterial.WHITE_CONCRETE_POWDER;
                }

                if (particle.name().equals("ELECTRIC_SPARK")) {
                    material = CompMaterial.LIGHTNING_ROD;
                }

                if (particle.name().equals("SCRAPE")) {
                    material = CompMaterial.GREEN_CANDLE;
                }

                return ItemCreator.of(material, "&3&l" + particleName, new String[]{"&7This can be the particle", "&7that will be played.", " ", "&f(Click to Apply)"}).amount(amount).glow(this.patternCache.getParticleName().equals(particle.name())).make();
            }

            protected void onPageClick(Player player, CompParticle particle, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    this.bondBase.setParticle(this.tierCache.getTier(), this.patternCache.getNumber(), particle.name());
                    new ParticleListMenu(this.bondBase, this.tierCache, this.patternCache).displayTo(player);
                }
            }

            public ItemStack getItemAt(int slot) {
                boolean multiplePages = this.getPages().size() > 1;
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else if (slot == this.getSize() - 3) {
                    return this.shapeButton.getItem();
                } else if (slot == this.getSize() - 5) {
                    return this.currentParticleButton.getItem();
                } else if (slot == this.getSize() - 7) {
                    return this.positionButton.getItem();
                } else {
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 7 && slot != this.getSize() - 5 && slot != this.getSize() - 3 && slot != this.getSize() - 1) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        } else if (this.getSize() != 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }

        private final class ShapeListMenu extends MenuPagged<String> {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;
            private final Button backButton;
            private final Button currentShapeButton;

            protected ShapeListMenu(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                super(ShapeBase.getShapeNames());
                this.setTitle("&3&lSelect a Shape");
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            new ParticleListMenu(bondBase, tierCache, patternCache).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
                this.currentShapeButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_STAIRS, "&3&lShape", new String[]{"&eCurrent Shape: &3" + patternCache.getShapeName(), "&7The shape of the", "&7particles when played."}).make();
                    }
                };
            }

            protected ItemStack convertToItemStack(String shapeName) {
                ShapeBase shapeBase = ShapeBase.getShape(shapeName);
                return ItemCreator.of(CompMaterial.valueOf(shapeBase.getIcon()), "&3&l" + shapeName, new String[]{"&7The selected particle", "&7will play in this formation.", " ", "&f(Click to Apply)"}).glow(this.patternCache.getShapeName().equals(shapeName)).make();
            }

            protected void onPageClick(Player player, String shapeName, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    this.bondBase.setShape(this.tierCache.getTier(), this.patternCache.getNumber(), shapeName);
                    new ParticleListMenu(this.bondBase, this.tierCache, this.patternCache).displayTo(player);
                }
            }

            public ItemStack getItemAt(int slot) {
                boolean multiplePages = this.getPages().size() > 1;
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else if (slot == this.getSize() - 5) {
                    return this.currentShapeButton.getItem();
                } else {
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 5 && slot != this.getSize() - 1) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        } else if (this.getSize() != 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }

        private final class PositionListMenu extends MenuPagged<String> {
            private BondBase bondBase;
            private BondBase.TierCache tierCache;
            private BondBase.TierCache.PatternCache patternCache;
            private final Button backButton;
            private final Button currentShapeButton;

            protected PositionListMenu(BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache) {
                super(PositionBase.getPositionNames());
                this.setTitle("&3&lSelect a Position");
                this.bondBase = bondBase;
                this.tierCache = tierCache;
                this.patternCache = patternCache;
                this.backButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                        if (clickType.isLeftClick())
                            new ParticleListMenu(bondBase, tierCache, patternCache).displayTo(player);
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.OAK_DOOR, "&c&lReturn", new String[]{" ", "&7Return Back."}).make();
                    }
                };
                this.currentShapeButton = new Button() {
                    public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    }

                    public ItemStack getItem() {
                        return ItemCreator.of(CompMaterial.FILLED_MAP, "&3&lPosition", new String[]{"&eCurrent Position: &3" + patternCache.getPositionName(), "&7The position of the", "&7particles when played."}).make();
                    }
                };
            }

            protected ItemStack convertToItemStack(String positionName) {
                PositionBase positionBase = PositionBase.getPosition(positionName);
                return ItemCreator.of(CompMaterial.valueOf(positionBase.getIcon()), "&3&l" + positionName, new String[]{"&7The desired particles will", "&7play at this location.", " ", "&f(Click to Apply)"}).glow(this.patternCache.getPositionName().equals(positionName)).make();
            }

            protected void onPageClick(Player player, String positionName, ClickType clickType) {
                if (clickType.isLeftClick()) {
                    this.bondBase.setPosition(this.tierCache.getTier(), this.patternCache.getNumber(), positionName);
                    new ParticleListMenu(this.bondBase, this.tierCache, this.patternCache).displayTo(player);
                }
            }

            public ItemStack getItemAt(int slot) {
                boolean multiplePages = this.getPages().size() > 1;
                if (slot == this.getSize() - 1) {
                    return this.backButton.getItem();
                } else if (slot == this.getSize() - 5) {
                    return this.currentShapeButton.getItem();
                } else {
                    if (slot >= this.getSize() - 9 && slot != this.getSize() - 5 && slot != this.getSize() - 1) {
                        if (multiplePages) {
                            if (slot != this.getSize() - 6 && slot != this.getSize() - 4) {
                                return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                            }
                        } else if (this.getSize() != 4) {
                            return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ", new String[0]).make();
                        }
                    }

                    return super.getItemAt(slot);
                }
            }
        }
    }
}
