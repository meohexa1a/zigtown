package com.mdt.zigtown.game.manager;

import lombok.experimental.UtilityClass;

import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.storage.*;

import arc.graphics.Color;

import mindustry.entities.Effect;

import com.mdt.zigtown.game.config.*;
import com.mdt.zigtown.game.util.*;

@UtilityClass
public class ZigtownWorldHelper {

    private float cachedDefenderSupply = 0f;
    private long lastDefenderSupplyTime = 0L;

    private float cachedAttackerPoints = 0f;
    private long lastAttackerPointsTime = 0L;

    public float countDefenderSupplyUsed(Team defenderTeam) {
        var now = System.currentTimeMillis();
        if (now - lastDefenderSupplyTime < 1000L) return cachedDefenderSupply;

        var supply = 0f;
        for (var build : Groups.build) {
            if (build.team == defenderTeam && ZigtownCosts.isAllowed(build.block)) {
                supply += ZigtownCosts.getBlockCost(build.block);
            }
        }

        cachedDefenderSupply = supply;
        lastDefenderSupplyTime = now;
        return supply;
    }

    public float countAttackerPointsUsed(Team attackerTeam) {
        var now = System.currentTimeMillis();
        if (now - lastAttackerPointsTime < 1000L) return cachedAttackerPoints;

        var pointsUsed = 0f;
        for (var unit : Groups.unit) {
            if (unit.team == attackerTeam && !unit.isPlayer()) {
                pointsUsed += ZigtownCosts.getUnitCost(unit.type);
            }
        }

        cachedAttackerPoints = pointsUsed;
        lastAttackerPointsTime = now;
        return pointsUsed;
    }

    public void killIntrudersInSector(SectorConfig sector, Team teamToKill) {
        for (var unit : Groups.unit) {
            if (unit.team == teamToKill && sector.contains(unit.tileX(), unit.tileY())) unit.kill();
        }
    }

    public boolean isSectorFullyCaptured(SectorConfig sector, Team defenderTeam) {
        for (var c : sector.cores()) {
            var tile = Vars.world.tile((int) c.x, (int) c.y);
            if (tile != null && tile.build instanceof CoreBlock.CoreBuild cb && cb.team == defenderTeam)
                return false;
        }
        return true;
    }

    public void spawnUnit(UnitType type, Team team, SectorConfig attackerSector) {
        var cores = attackerSector.cores();
        var corePos = cores.get(arc.math.Mathf.random(cores.size() - 1));

        var startX = (int) corePos.x;
        var startY = (int) corePos.y;

        for (var r = 1; r <= 10; r++) {
            for (var dx = -r; dx <= r; dx++) {
                for (var dy = -r; dy <= r; dy++) {
                    if (Math.abs(dx) != r && Math.abs(dy) != r) continue;

                    var tx = startX + dx;
                    var ty = startY + dy;
                    var tile = Vars.world.tile(tx, ty);
                    if (tile != null && !tile.solid()) {
                        type.spawn(team, tx * Vars.tilesize, ty * Vars.tilesize);
                        return;
                    }
                }
            }
        }
    }

    public void drawSectorBorder(SectorConfig sector, Effect effect, Color color) {
        int spacing = 15;
        int x1 = sector.x1();
        int y1 = sector.y1();
        int x2 = sector.x2();
        int y2 = sector.y2();

        playBorderEffect(x1, y1, effect, color);
        playBorderEffect(x1, y2, effect, color);
        playBorderEffect(x2, y1, effect, color);
        playBorderEffect(x2, y2, effect, color);

        for (int x = x1 + spacing; x < x2; x += spacing) {
            playBorderEffect(x, y1, effect, color);
            playBorderEffect(x, y2, effect, color);
        }

        for (int y = y1 + spacing; y < y2; y += spacing) {
            playBorderEffect(x1, y, effect, color);
            playBorderEffect(x2, y, effect, color);
        }
    }

    private void playBorderEffect(int tx, int ty, Effect effect, Color color) {
        float wx = tx * Vars.tilesize + Vars.tilesize / 2f;
        float wy = ty * Vars.tilesize + Vars.tilesize / 2f;
        Call.effect(effect, wx, wy, 2 * Vars.tilesize, color);
    }
}
