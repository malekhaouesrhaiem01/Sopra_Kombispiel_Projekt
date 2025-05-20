package gui

import entity.KombiPlayer
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Central class responsible for switching between all scenes in the application.
 *
 * Handles transitions between:
 * - [NewGameMenuScene] for entering player names
 * - [GameScene] for gameplay
 * - [ConfirmNextPlayerScene] between turns
 * - [ResultMenuScene] after the game ends
 *
 * @property application The main [BoardGameApplication] instance
 * @property rootService The shared [RootService] containing game logic and state
 */
class ViewSwitcher(
    private val application: BoardGameApplication,
    private val rootService: RootService
) {

    /**
     * Displays the menu scene to start a new game.
     */
    fun showNewGameMenu() {
        val newGameScene = NewGameMenuScene(rootService)

        newGameScene.quitButton.onMouseClicked = {
            kotlin.system.exitProcess(0)
        }

        newGameScene.startButton.onMouseClicked = {
            val name1 = newGameScene.p1Input.text.trim()
            val name2 = newGameScene.p2Input.text.trim()

            if (name1.isNotEmpty() && name2.isNotEmpty() && name1 != name2) {
                rootService.gameService.startGame(name1, name2)
                application.hideMenuScene() // ✅ Hides the menu before showing game
                showGameScene()
            }
        }

        application.showMenuScene(newGameScene)
    }

    /**
     * Displays the main game scene.
     */
    fun showGameScene() {
        val gameScene = GameScene(rootService)
        rootService.addRefreshable(gameScene)
        application.showGameScene(gameScene)
        gameScene.refreshAfterStartNewGame()
    }

    /**
     * Displays the confirmation screen for switching players.
     *
     * @param playerName The name of the next player.
     */
    fun showConfirmNextPlayerScene(playerName: String) {
        val confirmScene = ConfirmNextPlayerScene(playerName)
        confirmScene.continueButton.onMouseClicked = { showGameScene() }
        confirmScene.quitButton.onMouseClicked = { kotlin.system.exitProcess(0) }
        application.showMenuScene(confirmScene)
    }

    /**
     * Displays the result screen with final scores and winner.
     *
     * @param winner The player who won the game.
     * @param loser The player who lost the game.
     */
    fun showResultScene(winner: KombiPlayer, loser: KombiPlayer) {
        val resultScene = ResultMenuScene(rootService)
        resultScene.refreshAfterGameEnd(winner, loser)

        resultScene.newGameButton.onMouseClicked = {
            showNewGameMenu()
        }

        resultScene.quitButton.onMouseClicked = {
            kotlin.system.exitProcess(0)
        }

        application.showMenuScene(resultScene)
    }
}
