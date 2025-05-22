package gui
import tools.aqua.bgw.visual.ImageVisual
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
        this.background = ImageVisual("confirmmenuscene.jpg")


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

    override fun refreshAfterTurnEnd() {
        val game = application.rootService.currentGame ?: return
        val index1 = game.currentPlayerIndex
        val nextPlayer = game.players[index1]
        val p1 = game.players[0]
        val p2 = game.players[1]

        val boldFont = Font(size = 22, fontWeight = Font.FontWeight.BOLD)
        val whiteVisual = ColorVisual(255, 255, 255, 0) // texte blanc, fond transparent

        titleLabel.apply {
            text = "Nächster Spieler ist: ${nextPlayer.name}"
            font = boldFont
            visual = whiteVisual
        }

        player1ScoreLabel.apply {
            text = "${p1.name}: ${p1.score} Punkte"
            font = boldFont
            visual = whiteVisual
        }

        player2ScoreLabel.apply {
            text = "${p2.name}: ${p2.score} Punkte"
            font = boldFont
            visual = whiteVisual
        }
    }

}
