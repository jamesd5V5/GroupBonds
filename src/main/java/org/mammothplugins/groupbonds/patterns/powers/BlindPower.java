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

public class BlindPower extends PowerBase {
    public BlindPower() {
        super("Blind");
        this.setIcon("FERMENTED_SPIDER_EYE");
        this.setLore(Arrays.asList("&eSelective:", "&7The Target will become", "&7blind.", "&7Friend: Cannot be selected.", "&eGeneral:", "&7The player will become", "&7blind."));
    }

    @Override
    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if (actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, patternCache.getDuration() * 20, strength));
            CompSound.CAT_HISS.play(player.getLocation(), 10.0F, -50.0F);
        }
    }

    @Override
    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && (friend != null && !target.getUniqueId().equals(friend.getUniqueId()) || friend == null)) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, patternCache.getDuration() * 20, strength));
            CompSound.CAT_HISS.play(player.getLocation(), 10.0F, -50.0F);
        }
    }

}
