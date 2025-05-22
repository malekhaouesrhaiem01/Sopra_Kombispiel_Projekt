package gui

import entity.KombiCard
import entity.KombiPlayer
import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * End-of-game result scene displaying the winner or a tie, along with both players' scores.
 *
 * @property rootService Provides access to the current game state.
 * @property application Reference to the SopraApplication to trigger scene transitions or exit.
 */
class ResultMenuScene(
    private val rootService: RootService,
    private val application: SopraApplication
) : MenuScene(width = 1280, height = 720), Refreshable {

    private val winnerTitleLabel = Label(
        posX = 540, posY = 120,
        width = 200, height = 30,
        text = "GEWINNER",
        font = Font(size = 16)
    )

    private val winnerNameLabel = Label(
        posX = 490, posY = 160,
        width = 300, height = 50,
        text = "",
        font = Font(size = 28, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual(60, 60, 70)
    )

    private val player1ScoreLabel = Label(
        posX = 540, posY = 230,
        width = 200, height = 30,
        text = "",
        font = Font(size = 18)
    )

    private val player2ScoreLabel = Label(
        posX = 540, posY = 270,
        width = 200, height = 30,
        text = "",
        font = Font(size = 18)
    )

    private val newGameButton = Button(
        posX = 540, posY = 340,
        width = 200, height = 50,
        text = "NEUSTART",
        font = Font(size = 18, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual(100, 100, 120)
    )

    private val quitButton = Button(
        posX = 540, posY = 410,
        width = 200, height = 50,
        text = "SPIEL BEENDEN",
        font = Font(size = 18, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual(120, 60, 60)
    )

    init {
        background = ColorVisual(170, 180, 190)
        addComponents(
            winnerTitleLabel,
            winnerNameLabel,
            player1ScoreLabel,
            player2ScoreLabel,
            newGameButton,
            quitButton
        )

        newGameButton.onMouseClicked = {
            application.showNewGameMenuScene()
        }

        quitButton.onMouseClicked = {
            application.exitApp()
        }
    }

    /**
     * Updates the result display with winner/tie message and scores.
     *
     * @param winner The winning player or null if tie.
     */
    override fun refreshAfterGameEnd(winner: KombiPlayer?) {
        winnerNameLabel.text = winner?.name?.let { "Gewinner: $it" } ?: "Unentschieden"

        val player1 = rootService.currentGame?.players?.getOrNull(0)
        val player2 = rootService.currentGame?.players?.getOrNull(1)

        if (player1 != null && player2 != null) {
            player1ScoreLabel.text = "${player1.name}: ${player1.score} Punkte"
            player2ScoreLabel.text = "${player2.name}: ${player2.score} Punkte"
        } else {
            player1ScoreLabel.text = ""
            player2ScoreLabel.text = ""
        }

        application.showResultMenuScene()
    }

    // Unused Refreshable methods
    override fun refreshAfterStart(players: List<KombiPlayer>) {}
    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {}
    override fun refreshAfterTurnEnd() {}
    override fun refreshAfterCardDrawn(card: KombiCard) {}
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {}
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {}
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {}
    override fun showMessage(message: String) {}
}
