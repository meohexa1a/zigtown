package com.mdt.zigtown;

import javax.inject.Named;

import lombok.RequiredArgsConstructor;

import org.codejargon.feather.Provides;

import arc.util.CommandHandler;

@RequiredArgsConstructor
public final class ZigtownModule {

    private final CommandHandler clientHandler;
    private final CommandHandler serverHandler;

    // !----------------------------------------------------------------!

    @Provides
    @Named("client")
    public CommandHandler provideClientHandler() {
        return clientHandler;
    }

    @Provides
    @Named("server")
    public CommandHandler provideServerHandler() {
        return serverHandler;
    }
}
