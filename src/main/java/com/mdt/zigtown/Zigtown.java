package com.mdt.zigtown;

import lombok.extern.slf4j.Slf4j;

import org.codejargon.feather.Feather;

import arc.util.CommandHandler;

import mindustry.mod.Plugin;

import com.mdt.zigtown.game.GameManager;

@Slf4j
public final class Zigtown extends Plugin {

    private CommandHandler clientHandler;
    private CommandHandler serverHandler;

    // !----------------------------------------------------------------!

    @Override
    public void registerClientCommands(CommandHandler handler) {
        this.clientHandler = handler;
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        this.serverHandler = handler;
    }

    // !----------------------------------------------------------------!

    @Override
    public void init() {
        log.info("Zigtown - initializing...");

        for (var wall : new mindustry.world.Block[]{
            mindustry.content.Blocks.copperWall,
            mindustry.content.Blocks.copperWallLarge,
            mindustry.content.Blocks.titaniumWall,
            mindustry.content.Blocks.titaniumWallLarge,
            mindustry.content.Blocks.thoriumWall,
            mindustry.content.Blocks.thoriumWallLarge,
            mindustry.content.Blocks.plastaniumWall,
            mindustry.content.Blocks.plastaniumWallLarge,
            mindustry.content.Blocks.phaseWall,
            mindustry.content.Blocks.phaseWallLarge,
            mindustry.content.Blocks.surgeWall,
            mindustry.content.Blocks.surgeWallLarge
        }) {
            wall.update = true;
        }

        var feather = Feather.with(new ZigtownModule(clientHandler, serverHandler));
        feather.instance(GameManager.class).init();

        log.info("Zigtown - ready.");
    }
}
