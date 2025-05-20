package gui

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Entry point of the Kombi-Duell application.
 * Initializes the game logic and opens the main menu.
 */
class SopraApplication : BoardGameApplication("Kombi-Duell") {

    private val rootService = RootService()
    private val viewSwitcher = ViewSwitcher(this, rootService)

    init {
        println("🟢 SopraApplication initialized")
        rootService.viewSwitcher = viewSwitcher
        viewSwitcher.showNewGameMenu()
    }
}
