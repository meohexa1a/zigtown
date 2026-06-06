package com.mdt.zigtown.game.util;

import java.util.*;

import lombok.experimental.UtilityClass;

import arc.math.*;

import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.*;

@UtilityClass
public class ZigtownCosts {

    private final Map<Block, Float> BLOCK_COSTS = Map.ofEntries(
        Map.entry(Blocks.duo, 2.0f),
        Map.entry(Blocks.scatter, 3.0f),
        Map.entry(Blocks.scorch, 3.0f),
        Map.entry(Blocks.hail, 4.0f),
        Map.entry(Blocks.wave, 8.0f),
        Map.entry(Blocks.lancer, 5.0f),
        Map.entry(Blocks.arc, 4.0f),
        Map.entry(Blocks.parallax, 15.0f),
        Map.entry(Blocks.swarmer, 10.0f),
        Map.entry(Blocks.salvo, 6.0f),
        Map.entry(Blocks.segment, 15.0f),
        Map.entry(Blocks.tsunami, 8.0f),
        Map.entry(Blocks.fuse, 12.0f),
        Map.entry(Blocks.ripple, 12.0f),
        Map.entry(Blocks.cyclone, 12.0f),
        Map.entry(Blocks.spectre, 25.0f),
        Map.entry(Blocks.meltdown, 25.0f),
        Map.entry(Blocks.foreshadow, 35.0f),

        Map.entry(Blocks.mender, 3.0f),
        Map.entry(Blocks.mendProjector, 10.0f),
        Map.entry(Blocks.forceProjector, 15.0f),

        Map.entry(Blocks.itemSource, 0.0f),
        Map.entry(Blocks.liquidSource, 0.0f),
        Map.entry(Blocks.powerSource, 0.0f),

        Map.entry(Blocks.copperWall, 0.5f),
        Map.entry(Blocks.copperWallLarge, 1.5f),
        Map.entry(Blocks.titaniumWall, 1.0f),
        Map.entry(Blocks.titaniumWallLarge, 3.0f),
        Map.entry(Blocks.thoriumWall, 1.5f),
        Map.entry(Blocks.thoriumWallLarge, 4.5f),
        Map.entry(Blocks.plastaniumWall, 1.5f),
        Map.entry(Blocks.plastaniumWallLarge, 4.5f),
        Map.entry(Blocks.phaseWall, 2.0f),
        Map.entry(Blocks.phaseWallLarge, 6.0f),
        Map.entry(Blocks.surgeWall, 3.0f),
        Map.entry(Blocks.surgeWallLarge, 9.0f)
    );

    public boolean isAllowed(Block block) {
        return block != null && BLOCK_COSTS.containsKey(block);
    }

    public float getBlockCost(Block block) {
        return block == null ? 0f : BLOCK_COSTS.getOrDefault(block, 0f);
    }


    private final Map<UnitType, Float> UNIT_COSTS = Map.ofEntries(
        Map.entry(UnitTypes.dagger, 1.0f),
        Map.entry(UnitTypes.nova, 1.0f),
        Map.entry(UnitTypes.crawler, 1.0f),
        Map.entry(UnitTypes.flare, 3.0f),

        Map.entry(UnitTypes.mace, 2.0f),
        Map.entry(UnitTypes.pulsar, 2.0f),
        Map.entry(UnitTypes.atrax, 2.0f),
        Map.entry(UnitTypes.horizon, 6.0f),

        Map.entry(UnitTypes.fortress, 4.0f),
        Map.entry(UnitTypes.quasar, 4.0f),
        Map.entry(UnitTypes.spiroct, 4.0f),
        Map.entry(UnitTypes.zenith, 10.0f),

        Map.entry(UnitTypes.scepter, 8.0f),
        Map.entry(UnitTypes.vela, 8.0f),
        Map.entry(UnitTypes.arkyid, 8.0f),
        Map.entry(UnitTypes.antumbra, 20.0f),
        Map.entry(UnitTypes.quad, 20.0f),

        Map.entry(UnitTypes.reign, 16.0f),
        Map.entry(UnitTypes.corvus, 16.0f),
        Map.entry(UnitTypes.toxopid, 16.0f),
        Map.entry(UnitTypes.eclipse, 30.0f),
        Map.entry(UnitTypes.oct, 30.0f)
    );

    public float getUnitCost(UnitType type) {
        return type == null ? 0f : UNIT_COSTS.getOrDefault(type, 16f);
    }

    private final UnitType[][] TIER_POOLS = {
        {UnitTypes.dagger, UnitTypes.flare, UnitTypes.crawler, UnitTypes.nova},
        {UnitTypes.mace, UnitTypes.horizon, UnitTypes.pulsar, UnitTypes.atrax},
        {UnitTypes.fortress, UnitTypes.zenith, UnitTypes.spiroct, UnitTypes.quasar},
        {UnitTypes.scepter, UnitTypes.vela, UnitTypes.arkyid, UnitTypes.antumbra, UnitTypes.quad},
        {UnitTypes.reign, UnitTypes.corvus, UnitTypes.toxopid, UnitTypes.eclipse, UnitTypes.oct}
    };

    public UnitType selectUnitRandomly(int escalationCount, float remaining) {
        var roll = Mathf.random(99);
        int targetPool;

        if (escalationCount < 7) {
            if (roll < 70) targetPool = 0;
            else if (roll < 95) targetPool = 1;
            else targetPool = 2;
        } else if (escalationCount < 15) {
            if (roll < 30) targetPool = 0;
            else if (roll < 70) targetPool = 1;
            else if (roll < 90) targetPool = 2;
            else targetPool = 3;
        } else if (escalationCount < 25) {
            if (roll < 10) targetPool = 0;
            else if (roll < 30) targetPool = 1;
            else if (roll < 70) targetPool = 2;
            else if (roll < 95) targetPool = 3;
            else targetPool = 4;
        } else {
            if (roll < 10) targetPool = 1;
            else if (roll < 40) targetPool = 2;
            else if (roll < 80) targetPool = 3;
            else targetPool = 4;
        }

        for (var p = targetPool; p >= 0; p--) {
            var affordable = new ArrayList<UnitType>();
            for (var type : TIER_POOLS[p]) {
                if (getUnitCost(type) > remaining) continue;
                affordable.add(type);
            }
            if (affordable.isEmpty()) continue;

            return affordable.get(Mathf.random(affordable.size() - 1));
        }

        return UnitTypes.dagger;
    }
}
