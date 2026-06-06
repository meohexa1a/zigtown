package com.mdt.zigtown.game.manager;

import java.util.*;

import javax.inject.*;

import lombok.*;

import mindustry.*;
import mindustry.gen.*;

import com.mdt.mindustry.popup.*;
import com.mdt.zigtown.game.*;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ZigtownHudService {

    private static final String GROUP_TOP_RIGHT = "zigtown-top-right";
    private static final String GROUP_TOP_LEFT  = "zigtown-top-left";

    private final PopupRegisterService popupRegisterService;
    private final Provider<GameManager> gameManagerProvider;

    // !----------------------------------------------------------------!

    public void init() {
        popupRegisterService.register(GROUP_TOP_RIGHT, this::buildTopRight);
        popupRegisterService.register(GROUP_TOP_LEFT,  this::buildTopLeft);
    }

    // !----------------------------------------------------------------!

    private List<PopupContent> buildTopRight(Player player) {
        var session = gameManagerProvider.get().getSession();
        if (session == null) return List.of();

        var mapName        = Vars.state.map.name();
        var activeSector   = session.getActiveSector();
        var sectorIndex    = session.getActiveSectorIndex() + 1;
        var totalSectors   = session.getConfig().sectors().size();
        var elapsed        = session.getElapsedSeconds();
        var minutes        = elapsed / 60;
        var seconds        = elapsed % 60;
        var timeStr        = minutes > 0
            ? String.format("%dm %02ds", minutes, seconds)
            : String.format("%ds", seconds);

        var content = PopupContent.builder()
            .zone(DisplayZone.MINIMAP_TOP_RIGHT)

            .append("[accent]" + mapName + "[]\n")
            .append("\n")

            .append("[cyan]" + activeSector.name() + "[][]\n")
            .append("[lightgray]" + sectorIndex + "/" + totalSectors + "[]\n")
            .append("[lightgray]Time elapsed: [yellow]" + timeStr + "[]")

            .completeContent()
            .build();

        return List.of(content);
    }

    private List<PopupContent> buildTopLeft(Player player) {
        var session = gameManagerProvider.get().getSession();
        if (session == null) return List.of();

        var config        = session.getConfig();
        var escalation    = session.getEscalationCount();
        var maxEscalation = config.maxEscalationWaves();
        var atkUsed       = (int) session.getAttackerPointsUsed();
        var atkMax        = (int) session.getMaxAttackerPoints();
        var defUsed       = (int) session.getDefenderSupplyUsed();
        var defMax        = (int) session.getMaxDefenderSupply();

        var content = PopupContent.builder()
            .zone(DisplayZone.POPUP_TOP_LEFT)

            .append("[accent]⚡ Escalation [white]" + escalation + "[lightgray]/" + maxEscalation + "[]\n")
            .append("[scarlet]● Attacker  [white]" + atkUsed + "[lightgray]/" + atkMax + "[] pts\n")
            .append("[cyan]● Defender  [white]" + defUsed + "[lightgray]/" + defMax + "[] supply")

            .completeContent()
            .build();

        return List.of(content);
    }
}
