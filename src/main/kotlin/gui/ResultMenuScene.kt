package gui

import entity.KombiCard
import entity.KombiPlayer
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene

/**
 * Scene displayed at the end of the game to show the result, scores of both players,
 * and offer options to restart or exit the game.
 *
 * @property application Reference to the main [SopraApplication] to switch scenes or exit.
 */
class ResultMenuScene(
    private val application: SopraApplication
) : MenuScene(width = 1280, height = 720), Refreshable {

    private val titleLabel = Label(
        width = 300, height = 30,
        posX = 490, posY = 120,
        text = "GEWINNER",
        font = Font(size = 18, color = Color.LIGHT_GRAY, fontWeight = Font.FontWeight.BOLD)
    )

    private val winnerNameLabel = Label(
        width = 300, height = 60,
        posX = 490, posY = 170,
        text = "",
        font = Font(size = 36, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual(80, 80, 80)
    )

    private val player1ScoreLabel = Label(
        width = 300, height = 25,
        posX = 490, posY = 250,
        text = "",
        font = Font(size = 16, color = Color.LIGHT_GRAY)
    )

    private val player2ScoreLabel = Label(
        width = 300, height = 25,
        posX = 490, posY = 290,
        text = "",
        font = Font(size = 16, color = Color.LIGHT_GRAY)
    )

    private val restartButton = Button(
        width = 200, height = 50,
        posX = 540, posY = 360,
        text = "NEUSTART",
        font = Font(size = 18, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual(100, 100, 100)
    ).apply {
        onMouseClicked = {
            application.showNewGameMenuScene()
        }
    }

    private val exitButton = Button(
        width = 200, height = 50,
        posX = 540, posY = 430,
        text = "SPIEL BEENDEN",
        font = Font(size = 18, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual(120, 80, 80)
    ).apply {
        onMouseClicked = {
            application.exitApp()
        }
    }

    init {
        background = ColorVisual(211, 211, 211) // Light gray

        addComponents(
            titleLabel,
            winnerNameLabel,
            player1ScoreLabel,
            player2ScoreLabel,
            restartButton,
            exitButton
        )
    }

    /**
     * Updates the UI to show the winner or tie, and displays player scores.
     *
     * @param winner The player who won, or null in case of tie.
     * @param player1 First player.
     * @param player2 Second player.
     * @param tieMessage Optional message in case of tie.
     */
    fun updateContent(winner: KombiPlayer?, player1: KombiPlayer, player2: KombiPlayer, tieMessage: String? = null) {
        if (tieMessage != null) {
            titleLabel.text = ""
            winnerNameLabel.text = tieMessage
            winnerNameLabel.font = Font(size = 30, color = Color.YELLOW, fontWeight = Font.FontWeight.BOLD)
            winnerNameLabel.visual = ColorVisual(45, 45, 55)
        } else if (winner != null) {
            titleLabel.text = "GEWINNER"
            winnerNameLabel.text = winner.name.uppercase()
            winnerNameLabel.font = Font(size = 36, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
            winnerNameLabel.visual = ColorVisual(80, 80, 80)
        } else {
            titleLabel.text = "SPIELENDE"
            winnerNameLabel.text = "Ergebnis unklar"
        }

        player1ScoreLabel.text = "${player1.name}: ${player1.score}"
        player2ScoreLabel.text = "${player2.name}: ${player2.score}"
    }

    /**
     * Called when the game ends to determine the winner or tie and update the result view.
     */
    override fun refreshAfterGameEnd(winner: KombiPlayer, loser: KombiPlayer) {
        val actualWinner: KombiPlayer?
        val tieMsg: String?

        when {
            winner.score > loser.score -> {
                actualWinner = winner
                tieMsg = null
            }
            loser.score > winner.score -> {
                actualWinner = loser
                tieMsg = null
            }
            else -> {
                actualWinner = null
                tieMsg = "UNENTSCHIEDEN!"
            }
        }

        updateContent(actualWinner, winner, loser, tieMsg)
    }

    // Unused refresh methods from Refreshable (required by interface)
    override fun refreshAfterStart(players: List<KombiPlayer>) {}
    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {}
    override fun refreshAfterTurnEnd(finishedPlayer: KombiPlayer) {}
    override fun refreshAfterCardDrawn(card: KombiCard) {}
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {}
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {}
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {}
    override fun showMessage(message: String) {}
}
