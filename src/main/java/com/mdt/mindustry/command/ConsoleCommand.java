package com.mdt.mindustry.command;

import java.util.Set;
import java.util.function.Consumer;

import lombok.*;

@Builder(toBuilder = true)
public record ConsoleCommand(
    @NonNull @Singular Set<String> prefixes,
    @NonNull String description,
    @NonNull String args,
    @NonNull Consumer<String[]> action) {

    public static class ConsoleCommandBuilder {
        private @Generated String description = "";
        private @Generated String args = "";
    }
}
