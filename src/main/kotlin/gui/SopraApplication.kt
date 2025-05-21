package gui
import entity.KombiPlayer
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import service.Refreshable

/**
 * Main application class for the Kombi-Duell game.
 * Initializes all scenes and manages scene transitions.
 */
class SopraApplication : BoardGameApplication("Kombi-Duell"), Refreshable {

    /** Loader for card graphics */
    val cardImageLoader = CardImageLoader()

    /** Root service providing access to all game logic and models */
    val rootService = RootService()

    /** All game scenes */
    private val newGameMenuScene = NewGameMenuScene(this)
    private val gameScene = GameScene(rootService,cardImageLoader,this)
    private val confirmNextPlayerScene = ConfirmNextPlayerScene(this)
    private val resultMenuScene = ResultMenuScene(rootService,this)

    init {
        // Register scenes as refreshables
        rootService.addRefreshables(
            this,
            newGameMenuScene,
            gameScene,
            confirmNextPlayerScene,
            resultMenuScene
        )
        showMenuScene(resultMenuScene)
        // Start with the main menu
        showMenuScene(newGameMenuScene)
    }

    /** Shows the main game scene */
    fun  showGameSceneView() {
        if (rootService.currentGame != null) {
            rootService.gameService.startTurn()
            showGameScene(gameScene)
        } else {
            println("[WARN] Tried to start turn but no game is active.")
        }
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

    /** Shows the result menu after the game ends */
    fun showResultMenuScene() {
        println("DEBUG: showResultMenuScene() called")

        showMenuScene(resultMenuScene)
    }
    /** Exits the game application */
    fun exitApp() {
        super.exit()
    }

    override fun refreshAfterGameEnd(winner: KombiPlayer?) {
        resultMenuScene.refreshAfterGameEnd(winner)
        this.showMenuScene(resultMenuScene)
    }
}
