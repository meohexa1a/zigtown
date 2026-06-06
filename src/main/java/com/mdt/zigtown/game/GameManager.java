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
import com.mdt.mindustry.menu.MenuService;
import com.mdt.mindustry.menu.MenuOption;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class GameManager {
    private final CommonGameService commonGameService;
    private final ActionRestrictionService actionRestrictionService;
    private final ZigtownHudService zigtownHudService;
    private final MenuService menuService;

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
        String title = "[accent]⚔ Zigtown SSC — Sector Survival Combat[]";

        String msg =
            "[accent]═══ DEFENDERS (Đội Phòng Thủ) ═══[]\n" +
            "[white]1. [accent]Building / Xây dựng:[]\n" +
            "   [lightgray]EN:[white] Only Turrets, Walls, Menders & Force Projectors allowed.\n" +
            "   [lightgray]VI:[white] Chỉ được xây Turrets, Walls, Menders và Force Projectors.\n" +
            "[white]2. [accent]Zone / Phạm vi:[]\n" +
            "   [lightgray]EN:[white] Build only inside the Active Sector (colored border).\n" +
            "   [lightgray]VI:[white] Chỉ xây trong Phân khu Hoạt động (đường viền màu).\n" +
            "[white]3. [accent]Supply:[]\n" +
            "   [lightgray]EN:[white] Each block costs Supply (shown on HUD). Limit = no more building.\n" +
            "   [lightgray]VI:[white] Mỗi block tốn Supply (HUD hiển thị). Hết giới hạn = không xây thêm.\n" +
            "[white]4. [accent]Objective / Mục tiêu:[]\n" +
            "   [lightgray]EN:[white] Capture Cores in the Active Sector to unlock the next one.\n" +
            "   [lightgray]VI:[white] Chiếm Core ở Phân khu Hoạt động để mở khóa phân khu tiếp theo.\n" +
            "\n" +
            "[scarlet]═══ ATTACKERS (Đội Tấn Công) ═══[]\n" +
            "   [lightgray]EN:[white] No building or destroying. Units auto-spawn and escalate over time.\n" +
            "   [lightgray]VI:[white] Không xây hoặc phá block. Quân tự spawn và mạnh dần (Escalation).\n" +
            "\n" +
            "[green]Good luck & have fun! — Chúc bạn chơi vui vẻ![]";

        var menu = MenuOption.builder()
            .title(title)
            .message(msg)
            .button("OK — Đã hiểu!", p -> {})
            .completeContent()
            .build();

        menuService.showMenu(player, menu);
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
