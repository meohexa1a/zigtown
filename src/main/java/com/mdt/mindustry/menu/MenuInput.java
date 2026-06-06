package com.mdt.mindustry.menu;

import java.util.function.*;

import lombok.*;

import mindustry.gen.Player;

import com.mdt.common.utils.CommonUtils;

@Builder(toBuilder = true)
public record MenuInput(
    @NonNull String title,
    @NonNull String message,
    @NonNull String holder,
    boolean isNumber,

    @NonNull BiConsumer<Player, String> action,
    @NonNull Consumer<Player> userCloseAction) {

    public static class MenuInputBuilder {
        private @Generated String holder = "";
        private @Generated BiConsumer<Player, String> action = CommonUtils::doNothing;
        private @Generated Consumer<Player> userCloseAction = CommonUtils::doNothing;
    }
}
