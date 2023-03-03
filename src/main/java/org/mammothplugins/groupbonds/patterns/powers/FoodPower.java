package org.mammothplugins.groupbonds.patterns.powers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mineacademy.fo.remain.CompSound;

import java.util.Arrays;

public class FoodPower extends PowerBase {
    public FoodPower() {
        super("Food");
        this.setUnits("Saturation");
        this.setIcon("COOKED_BEEF");
        this.setLore(Arrays.asList("&eSelective:", "&7The Target will get", "&7a Saturation boost.", "&7Friend: Cannot be selected.", "&eGeneral:", "&7The player will get", "&7a Saturation boost."));
    }

    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if (actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) {
            if (20 - player.getFoodLevel() >= 0) {
                player.setFoodLevel(player.getFoodLevel() + strength);
            } else
                player.setFoodLevel(20);

            CompSound.BURP.play(player.getLocation(), 10.0F, -50.0F);
        }

    }

    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && (friend != null && !target.getUniqueId().equals(friend.getUniqueId()) || friend == null) && target instanceof Player) {
            Player targetPlayer = (Player) target;
            if (strength + player.getFoodLevel() <= 20) {
                targetPlayer.setFoodLevel(strength);
            } else
                player.setFoodLevel(20);

            CompSound.BURP.play(player.getLocation(), 10.0F, -50.0F);
        }

    }
}
