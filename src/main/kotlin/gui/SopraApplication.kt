package gui

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication


/**
 * Main application class for the Kombi-Duell game.
 * Initializes all scenes and manages scene transitions.
 */
class SopraApplication : BoardGameApplication("Kombi-Duell") {

    /** Loader for card graphics */
    val cardImageLoader = CardImageLoader()

    /** Root service providing access to all game logic and models */
    val rootService = RootService()

    /** All game scenes */
    private val newGameMenuScene = NewGameMenuScene(this)
    private val gameScene = GameScene(rootService,cardImageLoader,this)
    private val confirmNextPlayerScene = ConfirmNextPlayerScene(this)
    private val resultMenuScene = ResultMenuScene(this)

    init {
        // Register scenes as refreshables
        rootService.addRefreshables(
            newGameMenuScene,
            gameScene,
            confirmNextPlayerScene,
            resultMenuScene
        )

        // Start with the main menu
        showMenuScene(newGameMenuScene)
    }

    /** Shows the main game scene */
    fun showGameSceneView() {
        rootService.gameService.startTurn()
        showGameScene(gameScene)
    }

    /** Shows the new game menu scene */
    fun showNewGameMenuScene() {
        showMenuScene(newGameMenuScene)
    }

    /** Shows the confirm next player scene */
    fun showConfirmNextPlayerScene() {
        showMenuScene(confirmNextPlayerScene)
    }
    /** Shows a message when called */
    fun showMessageToUser(msg: String) {
        println("[INFO] $msg")
        gameScene.showMessage(msg)
    }


    /** Exits the game application */
    fun exitApp() {
        super.exit()
    }
}
