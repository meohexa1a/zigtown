package com.mdt.zigtown.game.config;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import arc.util.serialization.Json;

import mindustry.game.Team;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Slf4j
public class ZigtownConfig {

    @Builder.Default
    private List<SectorConfig> sectors = new ArrayList<>();

    private SectorConfig attackerSector;

    @Builder.Default
    private float initialMaxAttackerPoints = 120f;
    @Builder.Default
    private float initialMaxDefenderSupply = 150f;
    @Builder.Default
    private int attackerGrowthPerEscalation = 25;
    @Builder.Default
    private int defenderGrowthPerEscalation = 30;
    @Builder.Default
    private int attackerSpawnIntervalSec = 3;
    @Builder.Default
    private int attackerMaxUnitsPerWave = 10;
    @Builder.Default
    private int escalationIntervalSec = 20;
    @Builder.Default
    private int maxEscalationWaves = 30;

    @Builder.Default
    private int attackerTeamId = Team.sharded.id;
    @Builder.Default
    private int defenderTeamId = Team.crux.id;

    public static ZigtownConfig parse(String desc) {
        if (desc == null || desc.isEmpty()) return null;
        if (!desc.contains("[zigtown]") || !desc.contains("[/zigtown]")) return null;

        int start = desc.indexOf("[zigtown]") + "[zigtown]".length();
        int end = desc.indexOf("[/zigtown]");

        try {
            Json json = new Json();
            return json.fromJson(ZigtownConfig.class, desc.substring(start, end).trim());
        } catch (Throwable e) {
            log.error("Failed to parse Zigtown configuration from map description", e);
            return null;
        }
    }
}
