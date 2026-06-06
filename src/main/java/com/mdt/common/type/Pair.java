package com.mdt.common.type;

import org.jetbrains.annotations.*;

public record Pair<A, B>(A first, B second) {

    @Contract("_, _ -> new")
    public static <A, B> @NotNull Pair<A, B> of(@NotNull A first, @NotNull B second) {
        return new Pair<>(first, second);
    }
}
