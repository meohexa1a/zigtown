package com.mdt.zigtown.game.config;

import java.util.*;

import lombok.*;
import lombok.experimental.Accessors;

import arc.math.geom.Vec2;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class SectorConfig {
    @Builder.Default
    private String name = "Sector";
    private int order;
    private int x1, y1, x2, y2;

    @Builder.Default
    private List<Vec2> cores = new ArrayList<>();

    public boolean contains(int tx, int ty) {
        return tx >= x1 && tx <= x2 && ty >= y1 && ty <= y2;
    }
}
