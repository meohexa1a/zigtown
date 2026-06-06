package com.mdt.mindustry.popup;

import java.util.*;
import java.util.function.Function;

import javax.inject.Singleton;

import org.jetbrains.annotations.NotNull;

import lombok.Locked;
import lombok.extern.slf4j.Slf4j;

import arc.util.Timer;

import mindustry.gen.*;

@Slf4j
@Singleton
public final class PopupRegisterService {
    private final Map<String, Function<Player, List<PopupContent>>> registered = new HashMap<>();

    // !----------------------------------------------------------------!

    public PopupRegisterService() {
        Timer.schedule(() -> arc.Core.app.post(this::applyProviders), 0, 1);
    }

    @Locked.Write
    public void register(@NotNull String group, @NotNull Function<Player, List<PopupContent>> provider) {
        registered.put(group, provider);
    }

    @Locked.Write
    public void unregister(@NotNull String group) {
        registered.remove(group);
    }

    // !----------------------------------------------------------------!

    @Locked.Read
    private Set<Function<Player, List<PopupContent>>> copyProviders() {
        return new HashSet<>(registered.values());
    }

    private void applyProviders() {
        var providers = copyProviders();
        if (providers.isEmpty()) return;

        for (var player : Groups.player) {
            for (var provider : providers) {
                try {
                    for (var content : provider.apply(player)) {
                        var margin = content.zone().getMargin(player);

                        Call.infoPopupReliable(player.con, content.content(), 1.05f,
                            content.zone().getAlignFlag(),
                            margin.top(), margin.left(), margin.bottom(), margin.right());
                    }
                } catch (Exception e) {
                    log.error("Provider {} failed for player {}", provider.getClass().getSimpleName(), player.name, e);
                }
            }
        }
    }
}
