package com.mdt.zigtown.game.manager;

import java.util.*;
import java.util.concurrent.*;

import javax.inject.*;

import lombok.*;
import net.jodah.expiringmap.*;

import mindustry.gen.*;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WarningRateLimiter {

    private final Map<String, Long> rateLimits = ExpiringMap.builder()
        .expiration(2, TimeUnit.SECONDS)
        .build();

    public void sendWarning(Player player, String message) {
        if (player == null) return;
        var compositeKey = "warning:" + player.uuid();
        if (rateLimits.containsKey(compositeKey)) return;

        rateLimits.put(compositeKey, System.currentTimeMillis());
        player.sendMessage(message);
    }
}
