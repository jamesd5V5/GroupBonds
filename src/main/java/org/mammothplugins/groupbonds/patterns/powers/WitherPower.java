package org.mammothplugins.groupbonds.patterns.powers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mineacademy.fo.remain.CompSound;

import java.util.Arrays;

public class WitherPower extends PowerBase {
    public WitherPower() {
        super("Wither");
        this.setUnits("Strength");
        this.setIcon("WITHER_SKELETON_SKULL");
        this.setLore(Arrays.asList("&eSelective:", "&7The Target will get", "&7awithered.", "&7Friend: Cannot be selected.", "&eGeneral:", "&7The player will get", "&7withered."));
    }

    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if (actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, patternCache.getDuration() * 20, strength));
            CompSound.WITHER_IDLE.play(player.getLocation(), 10.0F, -50.0F);
        }

    }

    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && (friend != null && !target.getUniqueId().equals(friend.getUniqueId()) || friend == null) && target instanceof Player) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, patternCache.getDuration() * 20, strength));
            CompSound.WITHER_IDLE.play(player.getLocation(), 10.0F, -50.0F);
        }

    }
}
