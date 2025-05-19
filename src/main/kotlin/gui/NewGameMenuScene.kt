package gui

import service.RootService
import service.Refreshable
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.core.Color
import kotlin.system.exitProcess

/**
 * The start menu scene for Kombi-Duell.
 * Lets users input two player names and start the game.
 *
 * @property rootService Central service layer access.
 */
class NewGameMenuScene(
    private val rootService: RootService
) : MenuScene(width = 600, height = 400), Refreshable {

    val titleLabel = Label(
        width = 600, height = 50,
        posX = 0, posY = 30,
        text = "Kombi-Duell",
        font = Font(size = 30)
    )

    val p1Label = Label(
        width = 100, height = 35,
        posX = 100, posY = 100,
        text = "Player 1:"
    )

    val p2Label = Label(
        width = 100, height = 35,
        posX = 100, posY = 150,
        text = "Player 2:"
    )

    /**
     * Input field for Player 1.
     */
    val p1Input: TextField = TextField(
        width = 200, height = 35,
        posX = 220, posY = 100,
        text = ""
    )

    /**
     * Input field for Player 2.
     */
    val p2Input: TextField = TextField(
        width = 200, height = 35,
        posX = 220, posY = 150,
        text = ""
    )

    /**
     * Button to quit the application.
     */
    val quitButton = Button(
        width = 140, height = 40,
        posX = 100, posY = 250,
        text = "Quit"
    ).apply {
        visual = ColorVisual(221, 136, 136)
        onMouseClicked = { exitProcess(0) }
    }

    /**
     * Button to start the game after validating inputs.
     */
    val startButton = Button(
        width = 140, height = 40,
        posX = 280, posY = 250,
        text = "Start"
    ).apply {
        visual = ColorVisual(136, 221, 136)
        isDisabled = true

        onMouseClicked = {
            val name1 = p1Input.text.trim()
            val name2 = p2Input.text.trim()

            try {
                rootService.gameService.startGame(name1, name2)
                val nextPlayerName = rootService.currentGame
                    ?.players
                    ?.get(rootService.currentGame!!.currentPlayerIndex)
                    ?.name ?: "Player"
                rootService.viewSwitcher?.showConfirmNextPlayerScene(nextPlayerName)
            } catch (e: IllegalArgumentException) {
                println("Invalid input: ${e.message}")
            }
        }
    }

    init {
        println("🟢 NewGameMenuScene created")

        // Event listeners (after both inputs are declared)
        p1Input.onKeyPressed = {
            startButton.isDisabled = p1Input.text.isBlank() || p2Input.text.isBlank()
        }

        p2Input.onKeyPressed = {
            startButton.isDisabled = p1Input.text.isBlank() || p2Input.text.isBlank()
        }

        opacity = 1.0
        background = ColorVisual(Color.WHITE) // Optional: makes sure screen isn't black
        addComponents(
            titleLabel,
            p1Label, p1Input,
            p2Label, p2Input,
            startButton, quitButton
        )
    }
}
