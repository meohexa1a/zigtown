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
            // --- English ---
            "[accent]═══ ENGLISH ═══[]\n" +
            "[white]Defenders:\n" +
            "  1. [accent]Building:[] Only Turrets, Walls, Menders & Force Projectors.\n" +
            "  2. [accent]Zone:[] Build only inside the Active Sector (colored border).\n" +
            "  3. [accent]Supply:[] Each block costs Supply (shown on HUD). At the limit you cannot build.\n" +
            "  4. [accent]Objective:[] Capture Cores in the Active Sector to unlock the next one.\n" +
            "[white]Attackers:\n" +
            "  - No building or destroying blocks.\n" +
            "  - Units auto-spawn from the Attacker base and escalate over time.\n" +
            "\n" +
            // --- Vietnamese ---
            "[accent]═══ TIẾNG VIỆT ═══[]\n" +
            "[white]Defenders (Đội Phòng Thủ):\n" +
            "  1. [accent]Xây dựng:[] Chỉ được xây Turrets, Walls, Menders và Force Projectors.\n" +
            "  2. [accent]Phạm vi:[] Chỉ xây trong Phân khu Hoạt động (đường viền màu).\n" +
            "  3. [accent]Supply:[] Mỗi block tốn Supply (hiển thị trên HUD). Hết giới hạn = không xây thêm.\n" +
            "  4. [accent]Mục tiêu:[] Chiếm Core ở Phân khu Hoạt động để mở khóa phân khu tiếp theo.\n" +
            "[white]Attackers (Đội Tấn Công):\n" +
            "  - Không được xây hoặc phá block.\n" +
            "  - Quân tự động spawn từ căn cứ và mạnh dần theo thời gian (Escalation).\n" +
            "\n" +
            "[green]Good luck! — Chúc bạn chơi vui vẻ![]";

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
