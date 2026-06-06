package com.mdt.zigtown.game;

import lombok.Getter;

import mindustry.game.Team;

import com.mdt.zigtown.game.config.*;
import com.mdt.zigtown.game.manager.*;

@Getter
public class ZigtownSession {

    private final ZigtownConfig config;
    private final Team attackerTeam;
    private final Team defenderTeam;

    private float maxDefenderSupply;
    private float maxAttackerPoints;
    private final long sessionStartMs = System.currentTimeMillis();
    private long lastSpawnTimeMs = System.currentTimeMillis();

    private int activeSectorIndex = 0;
    private int escalationCount = 0;
    private long lastEscalationTimeMs = System.currentTimeMillis();

    public ZigtownSession(ZigtownConfig config) {
        this.config = config;
        this.attackerTeam = Team.get(config.attackerTeamId());
        this.defenderTeam = Team.get(config.defenderTeamId());
        this.maxDefenderSupply = config.initialMaxDefenderSupply();
        this.maxAttackerPoints = config.initialMaxAttackerPoints();
    }


    public float getDefenderSupplyUsed() {
        return ZigtownWorldHelper.countDefenderSupplyUsed(defenderTeam);
    }

    public float getAttackerPointsUsed() {
        return ZigtownWorldHelper.countAttackerPointsUsed(attackerTeam);
    }

    public SectorConfig getActiveSector() {
        return config.sectors().get(activeSectorIndex);
    }

    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - sessionStartMs) / 1000L;
    }

    public boolean canAfford(float cost) {
        return getDefenderSupplyUsed() + cost <= maxDefenderSupply;
    }


    public boolean checkAndRecordSpawn(long now) {
        if (now - lastSpawnTimeMs < config.attackerSpawnIntervalSec() * 1000L) return false;

        lastSpawnTimeMs = now;
        return true;
    }

    public boolean tryAdvanceSector() {
        if (activeSectorIndex + 1 >= config.sectors().size()) return false;

        activeSectorIndex++;
        return true;
    }

    public boolean checkAndRecordEscalation(long now) {
        if (now - lastEscalationTimeMs < config.escalationIntervalSec() * 1000L) return false;
        if (escalationCount >= config.maxEscalationWaves()) return false;

        escalationCount++;
        lastEscalationTimeMs = now;
        maxDefenderSupply = config.initialMaxDefenderSupply() + escalationCount * config.defenderGrowthPerEscalation();
        maxAttackerPoints = config.initialMaxAttackerPoints() + escalationCount * config.attackerGrowthPerEscalation();
        return true;
    }
}
