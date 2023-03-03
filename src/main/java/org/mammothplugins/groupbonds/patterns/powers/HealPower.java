package org.mammothplugins.groupbonds.patterns.powers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mineacademy.fo.remain.CompSound;

import java.util.Arrays;

public class HealPower extends PowerBase {
    public HealPower() {
        super("Heal");
        this.setUnits("(1/2) Hearts");
        this.setIcon("REDSTONE");
        this.setLore(Arrays.asList("&eSelective:", "&7The Player and Friend will heal half", "&7of the life stolen from the target.", "&7Friend: Cannot be the Target", "&eGeneral:", "&7The Player and Friend will heal", "&7the amount of hearts given."));
    }

    @Override
    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if (actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) {
            if (player.getMaxHealth() > player.getHealth() + (double) strength) {
                player.setHealth(player.getHealth() + (double) strength);
            } else
                player.setHealth(player.getMaxHealth());
            if (friend != null)
                if (friend.getMaxHealth() > friend.getHealth() + (double) strength) {
                    friend.setHealth(friend.getHealth() + (double) strength);
                } else
                    friend.setHealth(friend.getMaxHealth());
            if (friend != null)
                CompSound.NOTE_PIANO.play(friend.getLocation());
            CompSound.NOTE_PIANO.play(player.getLocation());
        }
    }

    @Override
    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && (friend != null && !target.getUniqueId().equals(friend.getUniqueId()) || friend == null)) {
            int halfHealth = strength / 2;
            if (target.getHealth() > (double) strength)
                target.damage((double) strength);
            else
                target.setHealth(0.0);
            if (player.getMaxHealth() > player.getHealth() + (double) halfHealth)
                player.setHealth(player.getHealth() + (double) halfHealth);
            else
                player.setHealth(player.getMaxHealth());
            if (friend != null)
                if (friend.getMaxHealth() > friend.getHealth() + (double) strength) {
                    friend.setHealth(friend.getHealth() + (double) strength);
                } else
                    friend.setHealth(friend.getMaxHealth());
            if (friend != null)
                CompSound.NOTE_PIANO.play(friend.getLocation());
            CompSound.NOTE_PIANO.play(player.getLocation());
        }

    }
}
