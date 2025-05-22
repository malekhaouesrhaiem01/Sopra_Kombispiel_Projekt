package gui

import entity.KombiCard
import entity.KombiPlayer
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene

/**
 * This is the menu that shows up at the beginning of the game.
 * Players can enter their names and hit the "Start Game" button.
 * If something is invalid, we show an error message.
 *
 * @param application Reference to the main [SopraApplication], so we can start the game and switch scenes.
 */
class NewGameMenuScene(
    private val application: SopraApplication
) : MenuScene(width = 1280, height = 720), Refreshable {

    /** Big headline at the top of the screen. */
    private val headlineLabel = Label(
        posX = 440, posY = 80,
        width = 400, height = 50,
        text = "Kombi-Duell",
        font = Font(size = 32, fontWeight = Font.FontWeight.BOLD)
    )

    /** Subtext asking players to enter their names. */
    private val subHeadlineLabel = Label(
        posX = 440, posY = 140,
        width = 400, height = 30,
        text = "Spieler-Namen eingeben",
        font = Font(size = 18, color = Color(80, 80, 80))
    )

    /** Label for player 1 input field. */
    private val player1Label = Label(
        posX = 440, posY = 200,
        width = 150, height = 30,
        text = "Spieler 1:",
        font = Font(size = 16, fontWeight = Font.FontWeight.BOLD)
    )

    /** Text field where player 1 types their name. */
    val player1NameField = TextField(
        posX = 600, posY = 200,
        width = 300, height = 40,
        prompt = "Name Spieler 1",
        font = Font(size = 16)
    )

    /** Label for player 2 input field. */
    private val player2Label = Label(
        posX = 440, posY = 260,
        width = 150, height = 30,
        text = "Spieler 2:",
        font = Font(size = 16, fontWeight = Font.FontWeight.BOLD)
    )

    /** Text field where player 2 types their name. */
    val player2NameField = TextField(
        posX = 600, posY = 260,
        width = 300, height = 40,
        prompt = "Spieler 2",
        font = Font(size = 16)
    )

    /** This label is used to show any validation or exception errors. */
    private val errorLabel = Label(
        posX = 465, posY = 320,
        width = 350, height = 30,
        text = "",
        font = Font(size = 14, color = Color.RED)
    )

    /** The "Start Game" button. When clicked, we try to start the game. */
    val startButton = Button(
        posX = 540, posY = 380,
        width = 200, height = 50,
        text = "Spiel starten",
        font = Font(size = 18, fontWeight = Font.FontWeight.BOLD, color = Color.WHITE),
        visual = ColorVisual(60, 179, 113)
    )

    init {
        background = ColorVisual(235, 235, 235)

        addComponents(
            headlineLabel,
            subHeadlineLabel,
            player1Label,
            player1NameField,
            player2Label,
            player2NameField,
            errorLabel,
            startButton
        )

        // When you click "Spiel starten"
        startButton.onMouseClicked = {
            val p1 = player1NameField.text.trim()
            val p2 = player2NameField.text.trim()

            try {
                // Try to start the game
                errorLabel.text = ""
                application.rootService.gameService.startGame(p1, p2)
                application.showConfirmNextPlayerScene()
            } catch (e: IllegalArgumentException) {
                // Probably empty names or same name twice
                errorLabel.text = e.message ?: "Ein ungültiger Name wurde eingegeben."
            } catch (e: Exception) {
                // Something else went wrong
                errorLabel.text = "Fehler: ${e.localizedMessage}"
                e.printStackTrace()
            }
        }
    }

    // These are unused here, but needed to implement Refreshable
    override fun refreshAfterStart(players: List<KombiPlayer>) {}
    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {}
    override fun refreshAfterTurnEnd() {}
    override fun refreshAfterGameEnd(winner: KombiPlayer?) {}
    override fun refreshAfterCardDrawn(card: KombiCard) {}
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {}
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {}
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {}

    /**
     * If something goes wrong elsewhere ,and we need to show a message,
     * this function will put it into the red error label.
     */
    override fun showMessage(message: String) {
        errorLabel.text = message
    }
}
