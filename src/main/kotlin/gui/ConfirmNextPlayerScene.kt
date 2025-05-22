package gui

import entity.KombiCard
import entity.KombiPlayer
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * Scene that appears between turns to show the next player and both scores.
 *
 * @property application Reference to SopraApplication for scene switching.
 */
class ConfirmNextPlayerScene(
    private val application: SopraApplication
) : MenuScene(width = 1280, height = 720), Refreshable {

    /** Headline with next player name. */
    private val titleLabel = Label(
        width = 600, height = 50,
        posX = 340, posY = 150,
        text = "",
        font = Font(size = 28, fontWeight = Font.FontWeight.BOLD, color = Color.BLACK)
    )

    /** Score label for player 1 (oben). */
    private val player1ScoreLabel = Label(
        width = 600, height = 40,
        posX = 340, posY = 220,
        text = "",
        font = Font(size = 22, color = Color(50, 50, 50))
    )

    /** Score label for player 2 (darunter). */
    private val player2ScoreLabel = Label(
        width = 600, height = 40,
        posX = 340, posY = 270,
        text = "",
        font = Font(size = 22, color = Color(50, 50, 50))
    )

    /** "Weiter" button. */
    private val continueButton = Button(
        width = 150, height = 50,
        posX = 400, posY = 340,
        text = "Weiter",
        font = Font(size = 18, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual(144, 238, 144)
    )

    /** "Beenden" button. */
    private val quitButton = Button(
        width = 150, height = 50,
        posX = 600, posY = 340,
        text = "Beenden",
        font = Font(size = 18, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual(250, 128, 114)
    )

    init {
        background = ColorVisual(240, 240, 240)

        addComponents(
            titleLabel,
            player1ScoreLabel,
            player2ScoreLabel,
            continueButton,
            quitButton
        )

        continueButton.onMouseClicked = {
            application.hideMenuScene()
            application.showGameSceneView()
        }

        quitButton.onMouseClicked = {
            application.exit()
        }
    }

    /**
     * Called when a turn ends. Updates next player and both scores.
     */
    override fun refreshAfterTurnEnd() {

        val game = application.rootService.currentGame ?: return
        val index1 = game.currentPlayerIndex
        val nextPlayer = game.players[index1]
        val p1 = game.players[0]
        val p2 = game.players[1]

        titleLabel.text = "Nächster Spieler ist: ${nextPlayer.name}"
        player1ScoreLabel.text = "${p1.name}: ${p1.score} Punkte"
        player2ScoreLabel.text = "${p2.name}: ${p2.score} Punkte"
    }


    // All other refresh methods are unused in this scene
    override fun refreshAfterStart(players: List<KombiPlayer>) {}
    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {}
    override fun refreshAfterGameEnd(winner: KombiPlayer?) {}
    override fun refreshAfterCardDrawn(card: KombiCard) {}
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {}
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {}
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {}
    override fun showMessage(message: String) {}
}
