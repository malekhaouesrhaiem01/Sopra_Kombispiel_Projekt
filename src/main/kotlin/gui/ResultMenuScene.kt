package gui

import entity.KombiGame
import entity.KombiPlayer
import service.Refreshable
import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.core.Color

/**
 * Scene that is displayed after the game ends.
 *
 * This scene shows the final scores of both players, announces the winner or a draw,
 * and provides options to either start a new game or exit the application.
 *
 * @property rootService Reference to the [RootService], which contains the current game state.
 */
class ResultMenuScene(
    private val rootService: RootService
) : MenuScene(width = 600, height = 400), Refreshable {

    /**
     * Label displaying the main headline.
     */
    private val headlineLabel = Label(
        width = 600, height = 50, posX = 0, posY = 30,
        text = "Game Over!",
        font = Font(size = 30, color = Color(64, 64, 64))
    )

    /**
     * Label displaying the final score of player 1.
     */
    private val p1ScoreLabel = Label(
        width = 600, height = 35, posX = 0, posY = 100,
        text = "",
        font = Font(size = 20)
    )

    /**
     * Label displaying the final score of player 2.
     */
    private val p2ScoreLabel = Label(
        width = 600, height = 35, posX = 0, posY = 140,
        text = "",
        font = Font(size = 20)
    )

    /**
     * Label showing the game result (winner or draw).
     */
    private val resultLabel = Label(
        width = 600, height = 40, posX = 0, posY = 200,
        text = "",
        font = Font(size = 22, color = Color(0, 102, 204))
    )

    /**
     * Button to exit the application.
     */
    val quitButton = Button(
        width = 160, height = 40,
        posX = 100, posY = 300,
        text = "Quit"
    ).apply {
        visual = ColorVisual(Color(221, 136, 136))
        onMouseClicked = { kotlin.system.exitProcess(0) }
    }

    /**
     * Button to start a new game. Must be handled externally.
     */
    val newGameButton = Button(
        width = 160, height = 40,
        posX = 340, posY = 300,
        text = "New Game"
    ).apply {
        visual = ColorVisual(Color(136, 221, 136))
    }

    /**
     * Adds all UI components to the scene on initialization.
     */
    init {
        background = ColorVisual(255, 255, 255)
        opacity = 0.95
        addComponents(
            headlineLabel,
            p1ScoreLabel,
            p2ScoreLabel,
            resultLabel,
            quitButton,
            newGameButton
        )
    }

    /**
     * Refreshes the scene after the game ends.
     * Displays the final scores and the winner or draw.
     *
     * @param winner The player who won the game (or either one if it's a draw).
     * @param loser The player who lost the game (or the other one if it's a draw).
     */
    override fun refreshAfterGameEnd(winner: KombiPlayer, loser: KombiPlayer) {
        val game: KombiGame = rootService.currentGame ?: return

        val player1 = game.players[0]
        val player2 = game.players[1]

        p1ScoreLabel.text = "${player1.name} scored ${player1.score} point(s)."
        p2ScoreLabel.text = "${player2.name} scored ${player2.score} point(s)."

        resultLabel.text = if (player1.score == player2.score) {
            "Draw"
        } else {
            "${winner.name} wins"
        }
    }
}
