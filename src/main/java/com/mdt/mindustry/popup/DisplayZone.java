package com.mdt.mindustry.popup;

import lombok.*;

import arc.util.Align;

import mindustry.gen.Player;

@Getter
@RequiredArgsConstructor
public enum DisplayZone {
    POPUP_TOP_LEFT(Align.topLeft,
        new PopupMargin(120, 5, 0, 0),
        new PopupMargin(160, 5, 0, 0)),

    RIGHT_MIDDLE(Align.right | Align.center,
        new PopupMargin(0, 0, 0, 0),
        new PopupMargin(0, 0, 0, 0)),

    MISSION(Align.topLeft,
        new PopupMargin(0, 120, 0, 0),
        new PopupMargin(70, 120, 0, 0)),

    TOP_MIDDLE(Align.top | Align.center,
        new PopupMargin(0, 0, 0, 0),
        new PopupMargin(0, 0, 0, 0)),

    MINIMAP_TOP_RIGHT(Align.topRight,
        new PopupMargin(0, 0, 0, 0),
        new PopupMargin(0, 0, 0, 0)),

    BOT_RIGHT(Align.bottomRight,
        new PopupMargin(0, 0, 0, 0),
        new PopupMargin(0, 0, 0, 0));

    private final int alignFlag;
    private final PopupMargin desktopMargin;
    private final PopupMargin mobileMargin;

    // !----------------------------------------------------------------!

    public PopupMargin getMargin(Player player) {
        return (player.con != null && player.con().mobile) ? mobileMargin : desktopMargin;
    }

    // !----------------------------------------------------------------!

    public record PopupMargin(int top, int left, int right, int bottom) {

    }
}
