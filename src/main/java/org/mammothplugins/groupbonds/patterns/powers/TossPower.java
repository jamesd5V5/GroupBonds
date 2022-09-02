package org.mammothplugins.groupbonds.patterns.powers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.mammothplugins.groupbonds.bonds.BondBase;
import org.mammothplugins.groupbonds.patterns.ActionBase;
import org.mammothplugins.groupbonds.patterns.PowerBase;
import org.mineacademy.fo.remain.CompSound;

import java.util.Arrays;

public class TossPower extends PowerBase {
    public TossPower() {
        super("Toss");
        this.setUnits("Toss Power");
        this.setIcon("IRON_BLOCK");
        this.setLore(Arrays.asList("&eSelective:", "&7The Target will be tossed", "&7high in the sky.", "&7Friend: Cannot be tossed.", "&eGeneral:", "&7All living entities in range will be tossed.", "&7Friend: Cannot be tossed."));
    }

    @Override
    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength) {
        if (actionName.equals("AUTO") || ActionBase.getByName(actionName).getType().equals("General")) {
            int range = tierCache.getRange();
            for (Entity entity : player.getNearbyEntities(range, range, range))
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(friend.getUniqueId())) {
                    entity.setVelocity(new Vector(0, strength, 0));
                    CompSound.IRONGOLEM_WALK.play(player.getLocation(), 10.0F, 50.0F);
                }
        }
    }

    @Override
    public void playPower(Player player, Player friend, BondBase bondBase, BondBase.TierCache tierCache, BondBase.TierCache.PatternCache patternCache, String actionName, int strength, LivingEntity target) {
        if (ActionBase.getByName(actionName).getType().equals("Selective") && (friend != null && !target.getUniqueId().equals(friend.getUniqueId()) || friend == null)) {
            target.setVelocity(new Vector(0, strength, 0));
            CompSound.IRONGOLEM_WALK.play(player.getLocation(), 10.0F, 50.0F);
        }
    }
}
