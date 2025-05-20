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
 * Scene that appears between turns to prompt the next player to continue the game.
 * It ensures the current player is hidden before switching turns in hotseat mode.
 *
 * @property application Reference to [SopraApplication] for scene switching and control.
 */
class ConfirmNextPlayerScene(
    private val application: SopraApplication
) : MenuScene(width = 1280, height = 720), Refreshable {

    /** Flag to indicate whether this is the first turn of the game. */
    private var isInitialTurn: Boolean = false

    /** Label displaying the title text (e.g. "Spiel beginnt!", "Zug beendet!"). */
    private val dialogTitleLabel = Label(
        width = 400, height = 40,
        posX = 440, posY = 200,
        text = "",
        font = Font(size = 28, fontWeight = Font.FontWeight.BOLD, color = Color.BLACK)
    )

    /** Label displaying which player will play next. */
    private val nextPlayerInfoLabel = Label(
        width = 400, height = 30,
        posX = 440, posY = 260,
        text = "",
        font = Font(size = 18, color = Color(80, 80, 80))
    )

    /** Button that continues the game and switches to the GameScene. */
    private val continueButton = Button(
        width = 120, height = 45,
        posX = 490, posY = 330,
        text = "Weiter",
        font = Font(size = 16, fontWeight = Font.FontWeight.BOLD, color = Color.BLACK),
        visual = ColorVisual(144, 238, 144)
    )

    /** Button that exits the application. */
    private val quitButton = Button(
        width = 120, height = 45,
        posX = 670, posY = 330,
        text = "Beenden",
        font = Font(size = 16, fontWeight = Font.FontWeight.BOLD, color = Color.BLACK),
        visual = ColorVisual(250, 128, 114)
    )

    init {
        background = ColorVisual(0, 0, 0, 100)

        addComponents(
            dialogTitleLabel,
            nextPlayerInfoLabel,
            continueButton,
            quitButton
        )

        continueButton.onMouseClicked = {
            application.hideMenuScene() // <<< IMPORTANT
            application.rootService.gameService.startTurn()
            application.showGameSceneView()
        }

        quitButton.onMouseClicked = {
            application.exit()
        }
    }

    /**
     * Updates the dialog content to reflect the name of the next player and
     * whether it is the first turn or a regular turn.
     *
     * @param nextPlayerName Name of the player who will play next.
     * @param isFirstTurn Flag indicating if this is the first turn of the game.
     */
    fun updateContent(nextPlayerName: String, isFirstTurn: Boolean = false) {
        this.isInitialTurn = isFirstTurn
        if (isFirstTurn) {
            dialogTitleLabel.text = "Spiel beginnt!"
            nextPlayerInfoLabel.text = "$nextPlayerName, du beginnst das Spiel!"
        } else {
            dialogTitleLabel.text = "Zug beendet!"
            nextPlayerInfoLabel.text = "Als nächstes ist $nextPlayerName am Zug!"
        }
    }

    // --- Refreshable overrides (no custom logic needed here) ---


    override fun refreshAfterStart(players: List<KombiPlayer>) {}
    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {}
    override fun refreshAfterTurnEnd(finishedPlayer: KombiPlayer) {}
    override fun refreshAfterGameEnd(winner: KombiPlayer, loser: KombiPlayer) {}
    override fun refreshAfterCardDrawn(card: KombiCard) {}
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {}
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {}
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {}
    override fun showMessage(message: String) {}
}
