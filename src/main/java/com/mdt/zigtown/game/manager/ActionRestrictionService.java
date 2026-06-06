package com.mdt.zigtown.game.manager;

import javax.inject.*;

import lombok.*;

import mindustry.gen.*;
import mindustry.net.Administration.*;

import com.mdt.zigtown.game.*;
import com.mdt.zigtown.game.util.*;

import arc.graphics.Color;

import mindustry.content.Fx;
import mindustry.graphics.Pal;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ActionRestrictionService {

    private final WarningRateLimiter warningRateLimiter;
    private final CommonGameService commonGameService;
    private final Provider<GameManager> gameManagerProvider;

    public void update() {
        var session = gameManagerProvider.get().getSession();
        if (session == null) return;

        var activeSector = session.getActiveSector();
        var attackerTeam = session.getAttackerTeam();
        var defenderTeam = session.getDefenderTeam();

        var attackerSector = session.getConfig().attackerSector();
        ZigtownWorldHelper.killIntrudersInSector(attackerSector, defenderTeam);

        for (var sector : session.getConfig().sectors()) {
            if (sector.order() > activeSector.order()) {
                ZigtownWorldHelper.killIntrudersInSector(sector, attackerTeam);
            } else if (sector.order() < activeSector.order()) {
                ZigtownWorldHelper.killIntrudersInSector(sector, defenderTeam);
            }
        }

        ZigtownWorldHelper.drawSectorBorder(attackerSector, Fx.dynamicSpikes, Pal.accent);

        for (var sector : session.getConfig().sectors()) {
            if (sector.order() > activeSector.order()) {
                ZigtownWorldHelper.drawSectorBorder(sector, Fx.dynamicSpikes, Color.gray);
            } else if (sector.order() == activeSector.order()) {
                ZigtownWorldHelper.drawSectorBorder(sector, Fx.dynamicSpikes, Color.scarlet);
            } else {
                ZigtownWorldHelper.drawSectorBorder(sector, Fx.dynamicSpikes, Color.acid);
            }
        }
    }

    public boolean filterAction(PlayerAction action) {
        var session = gameManagerProvider.get().getSession();
        if (session == null) return true;

        if (action.type != ActionType.placeBlock) return true;

        var player = action.player;
        if (player == null) return true;

        var attackerTeam = session.getAttackerTeam();
        var defenderTeam = session.getDefenderTeam();

        if (player.team() == attackerTeam) {
            warningRateLimiter.sendWarning(player, "[red]Attackers are not allowed to build or destroy!");
            return false;
        }

        if (player.team() != defenderTeam) return true;

        if (!ZigtownCosts.isAllowed(action.block)) {
            warningRateLimiter.sendWarning(player, "[red]Only allowed: Turrets, Walls, Mender, Mend-Projector, Force-Projector!");
            return false;
        }

        var activeSector = session.getActiveSector();
        if (!activeSector.contains(action.tile.x, action.tile.y)) {
            warningRateLimiter.sendWarning(player, "[red]You can only build in the active sector: " + activeSector.name());
            return false;
        }

        return commonGameService.checkSupplyLimit(action);
    }
}
