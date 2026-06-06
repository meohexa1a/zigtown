package com.mdt.mindustry.menu;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

import javax.inject.*;

import org.jetbrains.annotations.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.jodah.expiringmap.ExpiringMap;

import mindustry.gen.*;
import mindustry.ui.Menus;

import com.mdt.common.type.Pair;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class MenuService {
    private final int menuId = Menus.registerMenu(this::handleMenuSelection);
    private final int inputId = Menus.registerTextInput(this::handleTextInput);

    private final ExpiringMap<@NotNull String, Pair<List<Consumer<Player>>, Consumer<Player>>> showedMenuOption =
        ExpiringMap.builder().expiration(5, TimeUnit.MINUTES).build();
    private final ExpiringMap<@NotNull String, Pair<BiConsumer<Player, String>, Consumer<Player>>> showedMenuInput =
        ExpiringMap.builder().expiration(5, TimeUnit.MINUTES).build();

    // !----------------------------------------------------------------!

    public void showMenu(@NotNull Player player, @NotNull MenuOption menuOption) {
        showedMenuOption.put(player.uuid(), Pair.of(menuOption.actions(), menuOption.userCloseAction()));

        Call.hideFollowUpMenu(player.con, menuId);
        Call.followUpMenu(player.con, menuId, menuOption.title(), menuOption.message(), menuOption.options());
    }

    public void showInput(@NotNull Player player, @NotNull MenuInput menuInput) {
        showedMenuInput.put(player.uuid(), Pair.of(menuInput.action(), menuInput.userCloseAction()));

        Call.textInput(player.con, inputId, menuInput.title(), menuInput.message(), 1024, menuInput.holder(), menuInput.isNumber());
    }

    // !----------------------------------------------------------------!

    private void handleMenuSelection(Player player, int option) {
        Call.hideFollowUpMenu(player.con, menuId);

        var menuOption = showedMenuOption.remove(player.uuid());
        if (menuOption == null) {
            log.warn("Menu actions not found/expired for player: {}", player.name);
            return;
        }

        try {
            if (option < 0) menuOption.second().accept(player); // userCloseAction
            else if (option < menuOption.first().size()) menuOption.first().get(option).accept(player);
            else log.warn("Invalid menu selection by player: {}, option: {}", player.name, option);
        } catch (Exception e) {
            log.error("Error executing menu action for player: {}, option: {}", player.name, option, e);
        }
    }

    private void handleTextInput(Player player, String text) {
        var menuInput = showedMenuInput.remove(player.uuid());
        if (menuInput == null) {
            log.warn("Menu input action not found/expired for player: {}", player.name);
            return;
        }

        try {
            if (text == null) menuInput.second().accept(player); // userCloseAction
            else menuInput.first().accept(player, text);
        } catch (Exception e) {
            log.error("Error executing text input action for player: {}, inputId: {}", player.name, inputId, e);
        }
    }
}
