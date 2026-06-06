package com.mdt.zigtown.game;

import javax.inject.*;

import arc.*;

import lombok.*;
import lombok.extern.slf4j.*;

import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;

import com.mdt.zigtown.game.config.*;
import com.mdt.zigtown.game.manager.*;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class GameManager {
    private final CommonGameService commonGameService;
    private final ActionRestrictionService actionRestrictionService;
    private final ZigtownHudService zigtownHudService;

    @Getter
    private ZigtownSession session = null;

    public void init() {
        log.info("Zigtown GameManager - initializing...");

        zigtownHudService.init();
        Events.on(PlayEvent.class, e -> onPlay());

        arc.util.Timer.schedule(() -> {
            if (session == null) return;

            commonGameService.update();
            actionRestrictionService.update();
        }, 0f, 1f);

        Vars.netServer.admins.addActionFilter(action -> {
            if (session == null) return true;

            return actionRestrictionService.filterAction(action);
        });

        log.info("Zigtown GameManager - ready.");
    }

    private void onPlay() {
        log.info("PlayEvent received - scanning map description...");
        session = null;

        var config = ZigtownConfig.parse(Vars.state.map.description());
        if (config == null || config.sectors().isEmpty()) {
            log.info("Zigtown disabled: no valid config in map description.");
            return;
        }

        session = new ZigtownSession(config);
        Vars.state.rules.buildCostMultiplier = 0f;
        Vars.state.rules.coreCapture = true;
        Vars.state.rules.attackMode = true;
        Vars.state.rules.pvp = true;
        Vars.state.rules.pvpAutoPause = false;
        Vars.state.rules.disableUnitCap = true;

        log.info("Zigtown active! {} sectors loaded", config.sectors().size());
        Call.infoToast("Zigtown SSC Active! Sector: " + config.sectors().getFirst().name(), 10f);
    }
}
