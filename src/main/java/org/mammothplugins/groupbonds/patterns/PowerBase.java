package org.mammothplugins.groupbonds.patterns;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mammothplugins.groupbonds.GroupBonds;
import org.mammothplugins.groupbonds.PlayerCache;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.powers.BlindPower;
import org.mammothplugins.groupbonds.patterns.powers.TossPower;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.*;

public class PowerBase extends YamlConfig {
    private static final Map<String, PowerBase> powers = new HashMap();
    public static final List<String> customPowerNames = new ArrayList();
    public static final List<String> doesNotUseStrength = new ArrayList();
    public static Map<Player, PowerBase> thoseWhoLeftEarly = new HashMap();

    //When Adding a new Power, Make sure to edit getPower()...
    public static final PowerBase TOSS = new TossPower();
    public static final PowerBase BLIND = new BlindPower();

    private String name;
    private String icon;
    private String units;
    private String sender;
    private String command;
    private String exitCommand;
    private List<String> lore;
    public double strength;

    protected PowerBase(String powerName) {
        this.name = removeYmlExtension(powerName);

        this.loadConfiguration(NO_DEFAULT, "BondCharacteristics/Powers/" + name + ".yml");
        this.setHeader("GroupBonds\nPower: " + name);
        this.save();

        if (!name.equals("Toss") && !name.equals("Heal") && !name.equals("Speed") && !name.equals("Strength") && !name.equals("Summon Wolves") && !name.equals("Jump") && !name.equals("Food") && !name.equals("Poison") && !name.equals("Teleport") && !name.equals("Fireball") && !name.equals("Health Equalizer") && !name.equals("Invisibility") && !name.equals("Damage Resistance") && !name.equals("Leap") && !name.equals("Wither") && !name.equals("Miner Fatigue") && !name.equals("TNT") && !name.equals("Blind")) {
            if (!powers.containsKey(name) && !customPowerNames.contains(name)) {
                customPowerNames.add(name);
            }
        } else if (name.equals("Teleport") || name.equals("Fireball") || name.equals("Health Equalizer") || name.equals("Invisibility") || name.equals("Blind")) {
            if (!doesNotUseStrength.contains(name))
                doesNotUseStrength.add(name);
        }
    }

    @Override
    protected void onSave() {
        this.set("Icon", this.icon);
        this.set("Units", this.units);
        if (!this.name.equals("Toss") || !this.name.equals("Heal") || !this.name.equals("Speed") || !this.name.equals("Strength") || !this.name.equals("Summon Wolves") || !this.name.equals("Food") || !this.name.equals("Jump") || !this.name.equals("Poison") || !this.name.equals("Teleport") || !this.name.equals("Fireball") || !this.name.equals("Health Equalizer") || !this.name.equals("Invisibility") || !this.name.equals("Damage Resistance") || !this.name.equals("Leap") || !this.name.equals("Wither") || !this.name.equals("Miner Fatigue") || !this.name.equals("TNT") || !this.name.equals("Blind")) {
            //CUSTOM COMMANDS
            this.set("Lore", this.lore);
            this.set("Sender", this.sender);
            this.set("Command", this.command);
            this.set("ExitCommand", this.exitCommand);
        }
    }

    @Override
    protected void onLoad() {
        this.icon = this.getString("Icon", "BOWL");
        this.units = this.getString("Units", "Units");
        if (!name.equals("Toss") || !this.name.equals("Heal") || !this.name.equals("Speed") || !this.name.equals("Strength") || !this.name.equals("Summon Wolves") || !this.name.equals("Food") || !this.name.equals("Jump") || !this.name.equals("Poison") || !this.name.equals("Teleport") || !this.name.equals("Fireball") || !this.name.equals("Health Equalizer") || !this.name.equals("Invisibility") || !this.name.equals("Damage Resistance") || !this.name.equals("Leap") || !this.name.equals("Wither") || !this.name.equals("Miner Fatigue") || !this.name.equals("TNT") || !this.name.equals("Blind")) {
            this.lore = this.getStringList("Lore");
            this.sender = this.getString("Sender");
            this.command = this.getString("Command");
            this.exitCommand = this.getString("ExitCommand");
        }
    }

    public static void onStart() {
        File[] files = FileUtil.getFiles("BondCharacteristics/Powers/", "yml");
        for (File power : files) {
            try {
                PowerBase powerBase = new PowerBase(PowerBase.removeYmlExtension(power.getName()));
                String powerName = PowerBase.removeYmlExtension(power.getName());
                if (!powers.containsKey(powerName))
                    powers.put(powerName, powerBase);

            } catch (FoException exception) {
            }
        }
    }

    private static String removeYmlExtension(String string) {
        if (string.endsWith(".yml"))
            return string.substring(0, string.length() - 4);
        else return string;
    }

    public static void createPower(String powerName) {
        PowerBase powerBase = new PowerBase(powerName);
        customPowerNames.add(powerName);
        powers.put(powerName, powerBase);

        powerBase.setSender("Console");
        powerBase.setCommand("say hi");
        powerBase.setExitCommand("say bye");
        powerBase.setUnits("Units");
        powerBase.save();
    }

    public static void removePower(String powerName) {
        for (BondBase bondBase : BondBase.getBonds())
            for (BondBase.TierCache tierCache : bondBase.getTiers())
                for (BondBase.TierCache.PatternCache patternCache : tierCache.getPatterns())
                    if (patternCache.getPowerName().equals(powerName))
                        bondBase.setPower(tierCache.getTier(), patternCache.getNumber(), "Toss");

        PowerBase powerBase = getPower(powerName);
        powerBase.deleteFile();
        customPowerNames.remove(powerName);
        powers.remove(powerName);
    }

    public static final PowerBase getPower(String name) {
        for (PowerBase powerBase : powers.values()) {
            if (powerBase.getName().equals(name)) {
                if (customPowerNames.contains(powerBase))
                    return powerBase;
                else {
                    if (name.equals("Blind"))
                        return new BlindPower();

                    if (name.equals("Toss"))
                        return new TossPower();
                }
            }
        }
        return powers.get(name);
    }

    public static List<String> getPowerNames() {
        List<String> names = new ArrayList(powers.keySet());
        Collections.sort(names);
        return names;
    }

    public boolean equals(PowerBase powerBase) {
        return this.getName().equals(powerBase.name);
    }

    public void setIcon(String legacyName) {
        this.icon = legacyName;
        // if (customPowerNames.contains(this.name)) {
        this.save();
        // }
    }

    public void setSender(String sender) {
        if (customPowerNames.contains(this.name)) {
            this.sender = sender;
            this.save();
        }
    }

    public boolean hasCommand() {
        return this.getCommand() == null ? false : true;
    }

    public boolean hasExitCommand() {
        return this.getExitCommand() == null ? false : true;
    }

    public boolean usesStrength() {
        if (!customPowerNames.contains(this.name) && !doesNotUseStrength.contains(this.name)) //premade strengths
            return true;

        boolean hasStrength = false;
        if (customPowerNames.contains(this.name)) {
            if (this.hasCommand() && this.getCommand().contains("%STRENGTH%"))
                hasStrength = true;
            if (this.hasExitCommand() && this.getExitCommand().contains("%STRENGTH%"))
                hasStrength = true;
        }
        return hasStrength;
    }

    public void setCommand(String command) {
        if (customPowerNames.contains(this.name)) {
            this.command = command;
            this.save();
        }
    }

    public void setExitCommand(String exitCommand) {
        if (customPowerNames.contains(this.name)) {
            this.exitCommand = exitCommand;
            this.save();
        }
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
        this.save();
    }

    public void setUnits(String units) {
        this.units = units;
        this.save();
    }

    public double getStrength() {
        return this.strength;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getUnits() {
        return this.units;
    }

    public String getSender() {
        return this.sender;
    }

    public String getCommand() {
        return this.command;
    }

    public String getExitCommand() {
        return this.exitCommand;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if ((actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) && customPowerNames.contains(this.name)) {
            if (!this.command.equals("NONE")) {
                if (this.command.startsWith("/"))
                    this.command = this.command.substring(1);
                Bukkit.getServer().dispatchCommand((CommandSender) (this.sender.equals("Console") ? Bukkit.getServer().getConsoleSender() : Bukkit.getPlayer(player.getUniqueId())), this.formCommand(this.command, (double) strength));
                CompSound.NOTE_PIANO.play(player.getLocation());
            }
            if (!this.exitCommand.equals("NONE")) {
                if (player.isOnline()) {
                    if (this.exitCommand.startsWith("/"))
                        this.exitCommand = this.exitCommand.substring(1);
                    this.runExitCommand(player, bondBase, patternCache, (double) strength);
                } else {
                    thoseWhoLeftEarly.put(player, this);
                    this.strength = (double) strength;
                }
            }
        }

    }

    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && customPowerNames.contains(this.name)) {
            if (!this.command.equals("NONE")) {
                if (this.command.startsWith("/"))
                    this.command = this.command.substring(1);
                Bukkit.getServer().dispatchCommand((CommandSender) (this.sender.equals("Console") ? Bukkit.getServer().getConsoleSender() : Bukkit.getPlayer(player.getUniqueId())), this.formCommand(this.command, (double) strength));
                CompSound.NOTE_PIANO.play(player.getLocation());
            }

            if (!this.exitCommand.equals("NONE")) {
                if (player.isOnline()) {
                    if (this.exitCommand.startsWith("/"))
                        this.exitCommand = this.exitCommand.substring(1);
                    this.runExitCommand(player, bondBase, patternCache, (double) strength);
                } else {
                    thoseWhoLeftEarly.put(player, this);
                    this.strength = (double) strength;
                }
            }
        }

    }

    private String formCommand(String commandString, double strength) {
        String strengthString = String.valueOf(strength);
        String playedCommand = commandString;
        if (strengthString.contains(".0")) {
            strengthString = strengthString.replace(".0", "");
            playedCommand = commandString.replace("%STRENGTH%", strengthString);
        } else if (strengthString.contains(".5")) {
            playedCommand = commandString.replace("%STRENGTH%", "" + strength);
        }

        return playedCommand;
    }

    private void runExitCommand(Player player, BondBase bondBase, BondBase.TierCache.PatternCache patternCache, double strength) {
        (new BukkitRunnable() {
            public void run() {
                if (player.isOnline()) {
                    Bukkit.getServer().dispatchCommand((CommandSender) (PowerBase.this.sender.equals("Console") ? Bukkit.getServer().getConsoleSender() : Bukkit.getPlayer(player.getUniqueId())), PowerBase.this.formCommand(PowerBase.this.exitCommand, strength));
                    CompSound.NOTE_PIANO.play(player.getLocation());
                } else {
                    PlayerCache playerCache = PlayerCache.from(player);
                    playerCache.setHasLeftBeforeExitCommand(bondBase, true);
                }

            }
        }).runTaskLater(GroupBonds.getInstance(), (long) (patternCache.getDuration() * 20));
    }

    public void runExitCommandWithOutDelay(Player player, double strength) {
        Bukkit.getServer().dispatchCommand((CommandSender) (this.sender.equals("Console") ? Bukkit.getServer().getConsoleSender() : Bukkit.getPlayer(player.getUniqueId())), this.formCommand(this.exitCommand, strength));
        CompSound.NOTE_PIANO.play(player.getLocation());
    }

}
