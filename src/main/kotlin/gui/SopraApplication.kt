package gui

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Entry point of the SoPra board game application.
 * This class initializes the [RootService], the [ViewSwitcher],
 * and displays the start scene.
 */
class SopraApplication : BoardGameApplication("Kombi-Duell") {

    /**
     * The central game logic and entity handler.
     */
    private val rootService = RootService()

    /**
     * The view switcher that controls all GUI transitions.
     */
    private val viewSwitcher = ViewSwitcher(this, rootService)

    /**
     * Initializes the application by assigning the [viewSwitcher] to the [rootService]
     * and showing the start menu scene.
     */
    init {
        println("🟢 SopraApplication init started")
        rootService.viewSwitcher = viewSwitcher
    }

    /**
     * Starts the application by showing the initial scene.
     */
     fun start() {
        viewSwitcher.showNewGameMenu()
    }
}
