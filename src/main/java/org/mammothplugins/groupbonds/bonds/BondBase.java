package org.mammothplugins.groupbonds.bonds;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.PlayerCache;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.*;

@Getter
public class BondBase extends YamlConfig {

    private final static Map<String, BondBase> bonds = new HashMap();

    private String name;

    private boolean enabled;
    private boolean hasTiers;
    private boolean soldInShop;

    private String icon;
    private List<String> lore;
    private List<String> disabledWorlds;

    @Getter(AccessLevel.NONE)
    private Set<TierCache> tiers;
    @Getter(AccessLevel.NONE)
    private List<TierCache> sortedTiers;

    public BondBase(String name) {
        this.name = removeYmlExtension(name);

        this.setHeader("GroupBonds\nBond: " + this.name);
        this.loadConfiguration(NO_DEFAULT, "Bonds/" + name + ".yml");
        this.save();
    }

    @Override
    protected void onLoad() {
        this.enabled = this.getBoolean("Enabled", true);
        this.hasTiers = this.getBoolean("Has Tiers", false);
        this.soldInShop = this.getBoolean("Sold In Shop", true);

        this.icon = this.getString("Icon", "GLOWSTONE_DUST");
        this.lore = this.getStringList("Lore");
        this.disabledWorlds = this.getStringList("Disabled Worlds");

        this.tiers = this.getSet("Tiers", TierCache.class);
        this.sortTierList();
    }

    @Override
    protected void onSave() {
        this.set("Enabled", this.enabled);
        this.set("Has Tiers", this.hasTiers);
        this.set("Sold In Shop", this.soldInShop);

        this.set("Icon", this.icon);
        this.set("Lore", this.lore);
        this.set("Disabled Worlds", this.disabledWorlds);
        this.set("Tiers", this.tiers);
    }

    //TODO DONT FORGET
    public void duplicateBond(String newName) {
        createBond(newName);
        BondBase bondBase = getByName(newName);

        bondBase.setEnabled(this.isEnabled());
        bondBase.setHasTiers(this.isHasTiers());
        bondBase.setSoldInShop(this.isSoldInShop());

        bondBase.setIcon(this.getIcon());
        bondBase.setLore(this.getLore());
        bondBase.setDisabledWorlds(this.getDisabledWorlds());
        bondBase.setTiersSet(this.getTiersSet());
        bondBase.setTiers(this.getTiers());

        this.save();
    }

    //BOOLEANS=====================================================================================
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.save();
    }

    public void setHasTiers(boolean hasTiers) {
        this.hasTiers = hasTiers;
        this.save();
    }

    public void setSoldInShop(boolean soldInShop) {
        this.soldInShop = soldInShop;
        this.save();
    }

    //STRINGS======================================================================================
    public void setIcon(String legacyName) {
        this.icon = legacyName;
        this.save();
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
        this.save();
    }

    public List<String> getDisabledWorlds() {
        List<String> sortedWorlds = disabledWorlds;
        Collections.sort(sortedWorlds);
        return sortedWorlds;
    }

    public void setDisabledWorlds(List<String> disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
        this.save();
    }

    //INTEGERS=====================================================================================

    //TIERS========================================================================================
    private Set<TierCache> getTiersSet() {
        return this.tiers;
    }

    private void setTiersSet(Set<TierCache> tiers) {
        this.tiers = tiers;
        this.save();
    }

    public List<TierCache> getTiers() {
        List<Integer> sortedTiers = new ArrayList<>();
        for (TierCache tierCache : tiers)
            sortedTiers.add(tierCache.getTier());

        Collections.sort(sortedTiers);

        List<TierCache> sortedTiers2 = new ArrayList<>();
        for (int tier : sortedTiers) {
            sortedTiers2.add(getTierCache(tier));
        }
        this.sortedTiers = sortedTiers2;
        return this.sortedTiers;
    }

    private void setTiers(List<TierCache> tiers) {
        this.sortedTiers = sortedTiers;
        this.sortTierList();
    }

    public boolean hasTier(int tier) {
        return getTierCache(tier) == null ? false : true;
    }

    public void addTier(int tier, int range, int price) {
        List<String> emptyLore = new ArrayList<>();
        List<TierCache.PatternCache> emptyPatterns = new ArrayList<>();
        this.tiers.add(new TierCache(tier, range, price, emptyLore, emptyPatterns));
        this.sortTierList();
    }

    public void addTier(int tier, int range, int price, List<String> lore, List<TierCache.PatternCache> patterns) {
        this.tiers.add(new TierCache(tier, range, price, lore, patterns));
        this.sortTierList();
    }

    public void removeTier(int tier) {
        TierCache tierCache = this.getTierCache(tier);
        this.tiers.remove(tierCache);
        this.sortTierList();
    }

    public void clearTiers() {
        //Removes all tiers, except tier 1 for storage purposes
        Set<TierCache> newSet = new HashSet<>();
        List<TierCache> newSort = new ArrayList<>();
        for (TierCache tierCache : this.tiers) {
            if (tierCache.getTier() == 1) {
                newSet.add(tierCache);
                newSort.add(tierCache);
            }
        }
        this.tiers = newSet;
        this.sortedTiers = newSort;
        this.save();
    }

    public TierCache getTierCache(int tier) {
        for (TierCache tierCache : this.tiers) {
            if (tierCache.getTier() == tier)
                return tierCache;
        }
        return null;
    }

    public TierCache getTierCache(Player player) {
        PlayerCache playerCache = PlayerCache.from(player);
        int playerTier = playerCache.getBondTier(this);
        TierCache appliedTierCache = null;
        this.sortTierList();

        for (TierCache tierCache : this.getTiers())
            if (playerTier >= tierCache.getTier())
                appliedTierCache = tierCache;

        return appliedTierCache;
    }

    public void sortTierList() {
        this.save("Tiers", this.sortedTiers);
    }

    //SECONDARY TIER METHODS=======================================================================
    public int getMaxTier() {
        return sortedTiers.get(sortedTiers.size() - 1).getTier();
    }

    public List<String> getTierLore(int tier) {
        TierCache cache = this.getTierCache(tier);
        List<String> lore = new ArrayList();
        return (cache != null ? cache.getLore() : lore);
    }

    public int getPatternNumber(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getNumber() : 1;
    }

    public String getPowerName(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getPowerName() : "Toss";
    }

    public double getStrength(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? (double) cache.getStrength() : 2.0;
    }

    public String getShapeName(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getShapeName() : "Dot";
    }

    public String getParticleName(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getParticleName() : "VILLAGER_HAPPY";
    }

    public String getPositionName(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getPositionName() : "Center";
    }

    public boolean isInside(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.isInside() : false;
    }

    public String getActionName(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getActionName() : "Shift";
    }

    public String getActionItemName(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getActionItemName() : "Anything";
    }

    public int getDuration(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getDuration() : 1;
    }

    public int getDelay(int tier, int number) {
        TierCache.PatternCache cache = this.getPattern(tier, number);
        return cache != null ? cache.getDelay() : 3;
    }

    public void duplicateTierLvl(int duplicateTier, int toDuplicatedTierLvl) {
        TierCache tierCache = this.getTierCache(duplicateTier);
        this.addTier(toDuplicatedTierLvl, tierCache.getRange(), tierCache.getPrice(), tierCache.getLore(), tierCache.getPatterns());
    }

    public void setTierLore(int tier, List<String> lore) {
        TierCache tierCache = this.getTierCache(tier);
        tierCache.setLore(lore);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setTierPrice(int tier, int price) {
        TierCache tierCache = this.getTierCache(tier);
        tierCache.setPrice(price);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setRange(int tier, int range) {
        TierCache tierCache = this.getTierCache(tier);
        tierCache.setRange(range);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }


    //PATTERN TIER METHODS=========================================================================
    public List<TierCache.PatternCache> getPatterns(int tier) {
        TierCache tierCache = this.getTierCache(tier);
        return tierCache.getPatterns();
    }

    public List<Integer> getPatternNumbers(int tier) {
        List<Integer> orderedList = new ArrayList();
        int numberSize = this.getPatterns(tier).size();

        for (int i = 0; i < numberSize; ++i) {
            orderedList.add(i + 1);
        }

        return orderedList;
    }

    public TierCache.PatternCache getPattern(int tier, int number) {
        for (TierCache.PatternCache patternCache : getPatterns(tier)) {
            if (number == patternCache.getNumber())
                return patternCache;
        }
        return null;
    }

    public void addPattern(int tier) {
        TierCache tierCache = this.getTierCache(tier);
        int nextNumber = tierCache.getPatterns().size() + 1;
        tierCache.getPatterns().add(new TierCache.PatternCache(nextNumber, "Toss", 2, "VILLAGER_HAPPY", "Dot", "Center", 3, 5, false, "LeftClick", "Anything"));
        this.sortTierList();
    }

    public void removePattern(int tier, int number) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        tierCache.getPatterns().remove(patternCache);
        Iterator var5 = tierCache.getPatterns().iterator();

        while (var5.hasNext()) {
            TierCache.PatternCache patternCache1 = (TierCache.PatternCache) var5.next();
            if (patternCache1.getNumber() > number) {
                patternCache1.setNumber(patternCache1.getNumber() - 1);
            }
        }

        this.sortTierList();
    }

    public void duplicatePattern(int tier, int duplicatePattern) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, duplicatePattern);
        tierCache.getPatterns().add(new TierCache.PatternCache(tierCache.getPatterns().size() + 1, patternCache.getPowerName(), patternCache.getStrength(), patternCache.getParticleName(), patternCache.getShapeName(), patternCache.getPositionName(), patternCache.getDuration(), patternCache.getDelay(), patternCache.isInside(), patternCache.getActionName(), patternCache.actionItemName));
        this.sortTierList();
    }

    public void clearPatterns(int tier) {
        TierCache tierCache = this.getTierCache(tier);
        tierCache.getPatterns().clear();
        this.sortTierList();
    }

    public void setPower(int tier, int number, String powerName) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setPowerName(powerName);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setStrength(int tier, int number, int strength) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setStrength(strength);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setShape(int tier, int number, String shapeName) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setShapeName(shapeName);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setParticle(int tier, int number, String particleName) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setParticleName(particleName);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setPosition(int tier, int number, String positionName) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setPositionName(positionName);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setAction(int tier, int number, String action) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setActionName(action);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setActionItem(int tier, int number, String legacyName) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setActionItemName(legacyName);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setDuration(int tier, int number, int duration) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setDuration(duration);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setDelay(int tier, int number, int delay) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setDelay(delay);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    public void setInside(int tier, int number, boolean isInside) {
        TierCache tierCache = this.getTierCache(tier);
        TierCache.PatternCache patternCache = this.getPattern(tier, number);
        patternCache.setInside(isInside);
        this.save("Tiers", tierCache);
        this.sortTierList();
    }

    //==========================================================================================
    //STATIC
    //==========================================================================================
    public static void createBond(String name) {
        BondBase bondBase = new BondBase(name);
        bonds.put(removeYmlExtension(name), bondBase);
    }

    public static void removeBond(String name) {
        for (BondBase bondBase : getBonds()) {
            if (bondBase.getName().equals(removeYmlExtension(name))) {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerCache playerCache = PlayerCache.from(player);
                    playerCache.removeBond(bondBase);
                }
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    PlayerCache playerCache = PlayerCache.from(offlinePlayer.getUniqueId());
                    playerCache.removeBond(bondBase);
                }

                bonds.remove(bondBase.getName());
                bondBase.deleteFile();
            }
        }
    }

    public static void clearBonds() {
        for (BondBase bondBase : getBonds())
            BondBase.removeBond(bondBase.getName());
    }

    public static BondBase getByName(String name) {
        Iterator group = bonds.values().iterator();

        BondBase loadedBond;
        do {
            if (!group.hasNext()) {
                return null;
            }

            loadedBond = (BondBase) group.next();
        } while (!loadedBond.getName().equals(name));

        return loadedBond;
    }

    public static List<BondBase> getBonds() {
        List<String> sortedList = new ArrayList<>();
        for (BondBase bondBase : bonds.values())
            sortedList.add(bondBase.getName());

        Collections.sort(sortedList);
        List<BondBase> sortedBonds = new ArrayList<>();
        for (String bondName : sortedList)
            sortedBonds.add(bonds.get(bondName));

        return sortedBonds;
    }

    private static String removeYmlExtension(String string) {
        if (string.endsWith(".yml"))
            return string.substring(0, string.length() - 4);
        else return string;
    }

    public static void onStart() {
        File[] files = FileUtil.getFiles("Bonds", "yml");
        for (File group : files) {
            try {
                BondBase bondBase = new BondBase(BondBase.removeYmlExtension(group.getName()));
                String groupName = BondBase.removeYmlExtension(bondBase.getName());
                if (!bonds.containsKey(groupName))
                    bonds.put(groupName, bondBase);

            } catch (FoException exception) {
            }
        }
    }

    //==========================================================================================
    //CACHES
    //==========================================================================================
    public static final class TierCache implements ConfigSerializable {
        private int tier;
        private int range;
        private int price;
        private List<String> lore;
        private List<PatternCache> patterns;

        public TierCache(int tier, int range, int price, List<String> lore, List<PatternCache> patterns) {
            this.tier = tier;
            this.range = range;
            this.price = price;
            this.lore = lore;
            this.patterns = patterns;
        }

        public static TierCache deserialize(SerializedMap map) {
            int tier = map.getInteger("Tier");
            Valid.checkNotNull(tier, "A Bond lacks a tier Key!");
            int range = map.getInteger("Ranges");
            Valid.checkNotNull(range, "A Bond lacks a range Key!");
            int price = map.getInteger("Price");
            Valid.checkNotNull(price, "A Bond lacks a price Key!");
            List<String> lore = map.getStringList("Lore");
            Valid.checkNotNull(lore, "A Bond lacks Lore!");
            List<PatternCache> patterns = map.getList("Patterns", PatternCache.class);
            Valid.checkNotNull(patterns, "A Bond lacks Patterns!");
            return new TierCache(tier, range, price, lore, patterns);
        }

        public SerializedMap serialize() {
            SerializedMap map = new SerializedMap();
            map.put("Tier", this.tier);
            map.put("Ranges", this.range);
            map.put("Price", this.price);
            map.put("Lore", this.lore);
            map.put("Patterns", this.patterns);
            return map;
        }

        public int getTier() {
            return this.tier;
        }

        public int getRange() {
            return this.range;
        }

        public int getPrice() {
            return this.price;
        }

        public List<String> getLore() {
            return this.lore;
        }

        public List<PatternCache> getPatterns() {
            return this.patterns;
        }

        private void setTier(int tier) {
            this.tier = tier;
        }

        private void setRange(int range) {
            this.range = range;
        }

        private void setPrice(int price) {
            this.price = price;
        }

        private void setLore(List<String> lore) {
            this.lore = lore;
        }

        private void setPatterns(List<PatternCache> patterns) {
            this.patterns = patterns;
        }

        public static final class PatternCache implements ConfigSerializable {
            private int number;
            private String powerName;
            private int strength;
            private String particleName;
            private String shapeName;
            private String positionName;
            private int duration;
            private int delay;
            private boolean inside;
            private String actionName;
            private String actionItemName;

            private PatternCache(int number, String powerName, int strength, String particleName, String shapeName, String positionName, int duration, int delay, boolean inside, String actionName, String actionItemName, int otherThing) {
                this(number, powerName, strength, particleName, shapeName, positionName, duration, delay, inside, actionName, actionItemName);
            }

            public static PatternCache deserialize(SerializedMap map) {
                int number = map.getInteger("Number");
                Valid.checkNotNull(number, "A Bond lacks a Number!");
                String powerName = map.getString("Power");
                Valid.checkNotNull(powerName, "A Bond lacks a power!");
                int strength = map.getInteger("Strength");
                Valid.checkNotNull(strength, "A Bond lacks strength!");
                String particleName = map.getString("Particle");
                Valid.checkNotNull(particleName, "A Bond lacks a particle!");
                String shapeName = map.getString("Shape");
                Valid.checkNotNull(shapeName, "A Bond lacks a shape!");
                String positionName = map.getString("Position");
                Valid.checkNotNull(positionName, "A Bond lacks a Position!");
                int duration = map.getInteger("Duration");
                Valid.checkNotNull(duration, "A Bond lacks a Duration!");
                int delay = map.getInteger("Delay");
                Valid.checkNotNull(delay, "A Bond lacks a delay!");
                boolean inside = map.getBoolean("Inside");
                Valid.checkNotNull(inside, "A Bond lacks a boolean!");
                String actionName = map.getString("Action");
                Valid.checkNotNull(actionName, "A Bond lacks an Action value!");
                String actionItem = map.getString("Action Item");
                Valid.checkNotNull(actionName, "A Bond lacks an Action Item!");
                return new PatternCache(number, powerName, strength, particleName, shapeName, positionName, duration, delay, inside, actionName, actionItem);
            }

            public SerializedMap serialize() {
                SerializedMap map = new SerializedMap();
                map.put("Number", this.number);
                map.put("Action", this.actionName);
                map.put("Action Item", this.actionItemName);
                map.put("Power", this.powerName);
                map.put("Strength", this.strength);
                map.put("Particle", this.particleName);
                map.put("Shape", this.shapeName);
                map.put("Duration", this.duration);
                map.put("Position", this.positionName);
                map.put("Delay", this.delay);
                map.put("Inside", this.inside);
                return map;
            }

            public int getNumber() {
                return this.number;
            }

            public String getPowerName() {
                return this.powerName;
            }

            public boolean hasPower() {
                if (this.getPowerName().equals("NONE"))
                    return false;
                else
                    return true;
            }

            public int getStrength() {
                return this.strength;
            }

            public String getParticleName() {
                return this.particleName;
            }

            public String getShapeName() {
                return this.shapeName;
            }

            public String getPositionName() {
                return this.positionName;
            }

            public int getDuration() {
                return this.duration;
            }

            public int getDelay() {
                return this.delay;
            }

            public boolean isInside() {
                return this.inside;
            }

            public String getActionName() {
                return this.actionName;
            }

            public String getActionItemName() {
                return this.actionItemName;
            }

            public PatternCache(int number, String powerName, int strength, String particleName, String shapeName, String positionName, int duration, int delay, boolean inside, String actionName, String actionItemName) {
                this.number = number;
                this.powerName = powerName;
                this.strength = strength;
                this.particleName = particleName;
                this.shapeName = shapeName;
                this.positionName = positionName;
                this.duration = duration;
                this.delay = delay;
                this.inside = inside;
                this.actionName = actionName;
                this.actionItemName = actionItemName;
            }

            private void setNumber(int number) {
                this.number = number;
            }

            private void setPowerName(String powerName) {
                this.powerName = powerName;
            }

            private void setStrength(int strength) {
                this.strength = strength;
            }

            private void setParticleName(String particleName) {
                this.particleName = particleName;
            }

            private void setShapeName(String shapeName) {
                this.shapeName = shapeName;
            }

            private void setPositionName(String positionName) {
                this.positionName = positionName;
            }

            private void setDuration(int duration) {
                this.duration = duration;
            }

            private void setDelay(int delay) {
                this.delay = delay;
            }

            private void setInside(boolean inside) {
                this.inside = inside;
            }

            private void setActionName(String actionName) {
                this.actionName = actionName;
            }

            private void setActionItemName(String actionItemName) {
                this.actionItemName = actionItemName;
            }
        }
    }
}