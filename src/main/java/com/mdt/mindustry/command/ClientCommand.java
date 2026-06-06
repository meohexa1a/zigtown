package com.mdt.mindustry.command;

import java.util.Set;
import java.util.function.BiConsumer;

import lombok.*;

import mindustry.gen.Player;

@Builder(toBuilder = true)
public record ClientCommand(
    @NonNull @Singular Set<String> prefixes,
    @NonNull String description,
    @NonNull String args,
    @NonNull BiConsumer<String[], Player> action) {

    public static class ClientCommandBuilder {
        private @Generated String description = "";
        private @Generated String args = "";
    }
}
