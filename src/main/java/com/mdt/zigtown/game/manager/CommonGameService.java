package com.mdt.zigtown.game.manager;

import javax.inject.*;

import lombok.*;

import mindustry.gen.*;
import mindustry.net.Administration.PlayerAction;

import com.mdt.zigtown.game.*;
import com.mdt.zigtown.game.config.*;
import com.mdt.zigtown.game.util.ZigtownCosts;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommonGameService {

    private final WarningRateLimiter warningRateLimiter;
    private final Provider<GameManager> gameManagerProvider;

    public void update() {
        var session = gameManagerProvider.get().getSession();
        if (session == null) return;

        var now = System.currentTimeMillis();

        checkSectorProgress(session);
        handleEscalation(session, now);
        handleUnitSpawning(session, now);
    }

    public boolean checkSupplyLimit(PlayerAction action) {
        var session = gameManagerProvider.get().getSession();
        if (session == null) return true;

        var player = action.player;
        if (player == null) return true;

        var cost = ZigtownCosts.getBlockCost(action.block);
        if (session.canAfford(cost)) return true;

        warningRateLimiter.sendWarning(player, String.format(
            "[red]Not enough supply! Required: %d | Used: %d/%d",
            (int) cost, (int) session.getDefenderSupplyUsed(), (int) session.getMaxDefenderSupply()));
        return false;
    }

    private void checkSectorProgress(ZigtownSession session) {
        if (!ZigtownWorldHelper.isSectorFullyCaptured(session.getActiveSector(), session.getDefenderTeam())) return;
        if (!session.tryAdvanceSector()) return;

        Call.infoToast("Sector captured! Next active sector: " + session.getActiveSector().name(), 4f);
    }

    private void handleEscalation(ZigtownSession session, long now) {
        if (!session.checkAndRecordEscalation(now)) return;

        var config = session.getConfig();
        Call.infoToast(String.format(
            "[accent]⚡ Escalation [white]%d[lightgray]/%d[] — new limits applied!",
            session.getEscalationCount(),
            config.maxEscalationWaves()
        ), 3f);
    }

    private void handleUnitSpawning(ZigtownSession session, long now) {
        if (!session.checkAndRecordSpawn(now)) return;

        var config = session.getConfig();
        var attackerSector = config.attackerSector();

        var remaining = session.getMaxAttackerPoints() - session.getAttackerPointsUsed();
        var attackerTeam = session.getAttackerTeam();

        for (var i = 0; i < config.attackerMaxUnitsPerWave() && remaining > 0; i++) {
            var type = ZigtownCosts.selectUnitRandomly(session.getEscalationCount(), remaining);
            if (type == null) break;

            ZigtownWorldHelper.spawnUnit(type, attackerTeam, attackerSector);
            remaining -= ZigtownCosts.getUnitCost(type);
        }
    }
}
