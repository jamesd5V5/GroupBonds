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

public class DamageResistancePower extends PowerBase {
    public DamageResistancePower() {
        super("Damage Resistance");
        this.setIcon("DIAMOND_CHESTPLATE");
        this.setUnits("Dmg Resistance Lvl");
        this.setLore(Arrays.asList("&eSelective:", "&7The Target will receive", "&7the Dmg Resistance boost.", "&7Friend: Cannot be selected.", "&eGeneral:", "&7The player will receive", "&7the Dmg Resistance boost."));
    }

    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if (actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, patternCache.getDuration() * 20, strength));
            CompSound.IRONGOLEM_ATTACK.play(player.getLocation(), 10.0F, -50.0F);
        }

    }

    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && (friend != null && !target.getUniqueId().equals(friend.getUniqueId()) || friend == null)) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, patternCache.getDuration() * 20, strength));
            CompSound.IRONGOLEM_ATTACK.play(player.getLocation(), 10.0F, -50.0F);
        }

    }
}
