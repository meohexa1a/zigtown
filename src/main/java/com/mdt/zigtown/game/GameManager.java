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
        Events.on(PlayerJoin.class, e -> {
            if (session == null) return;
            showWelcomeMessage(e.player);
        });

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

    private void showWelcomeMessage(Player player) {
        String msg = "[accent]Chào mừng bạn đến với Zigtown SSC (Sector Survival Combat)![]\n\n" +
            "[lightgray]Luật chơi cho Defenders (Đội Phòng Thủ):[]\n" +
            "1. [accent]Xây dựng:[] Chỉ được phép xây dựng Turrets, Walls, Menders, và Force Projectors.\n" +
            "2. [accent]Phạm vi:[] Chỉ được xây dựng bên trong Phân khu Hoạt động (Active Sector) - có đường viền màu bao quanh.\n" +
            "3. [accent]Supply:[] Mỗi block xây dựng tốn Supply (Giới hạn hiển thị trên HUD). Đạt giới hạn sẽ không thể xây thêm.\n" +
            "4. [accent]Mục tiêu:[] Chiếm các Core ở Active Sector để mở khóa các phân khu tiếp theo.\n\n" +
            "[lightgray]Luật chơi cho Attackers (Đội Tấn Công):[]\n" +
            "- Không được phép xây dựng hoặc phá hủy block.\n" +
            "- Quân lính tự động spawn từ Phân khu Kẻ tấn công và mạnh dần theo thời gian (Escalation).\n\n" +
            "[green]Chúc bạn chơi game vui vẻ và giành chiến thắng![]";

        Call.infoMessage(player.con, msg);
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
        Vars.state.rules.infiniteResources = true;

        log.info("Zigtown active! {} sectors loaded", config.sectors().size());
        Call.infoToast("Zigtown SSC Active! Sector: " + config.sectors().getFirst().name(), 10f);
    }
}
