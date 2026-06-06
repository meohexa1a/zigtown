package com.mdt.mindustry.popup;

import java.util.function.Supplier;

import lombok.*;

@Builder(toBuilder = true)
public record PopupContent(
    @NonNull DisplayZone zone,
    @NonNull String content) {

    @Generated
    public static class PopupContentBuilder {
        private @Generated DisplayZone displayZone = DisplayZone.POPUP_TOP_LEFT;

        private final StringBuilder stringBuilder = new StringBuilder();

        // !------------------------------------------------------!

        public PopupContentBuilder append(String content) {
            stringBuilder.append(content);

            return this;
        }

        public PopupContentBuilder appendIf(boolean condition, Supplier<String> content) {
            if (condition) return append(content.get());

            return this;
        }

        public PopupContentBuilder completeContent() {
            this.content(stringBuilder.toString());
            return this;
        }
    }
}
