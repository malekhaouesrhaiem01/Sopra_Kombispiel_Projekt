package gui

import service.Refreshable
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.util.Font

/**
 * Confirmation scene shown between player turns.
 * Displays a message indicating the turn has ended and announces the next player.
 *
 * @param nextPlayerName The name of the player whose turn is next.
 */
class ConfirmNextPlayerScene(
    nextPlayerName: String
) : MenuScene(width = 500, height = 300), Refreshable {

    /**
     * Label displaying the title "Turn Ended!"
     */
    private val titleLabel = Label(
        width = 500, height = 50,
        posX = 0, posY = 40,
        text = "Turn Ended!",
        font = Font(size = 24)
    )

    /**
     * Label announcing the next player's turn.
     */
    private val infoLabel = Label(
        width = 500, height = 40,
        posX = 0, posY = 100,
        text = "Next up: $nextPlayerName",
        font = Font(size = 18)
    )

    /**
     * Button to continue the game.
     */
    val continueButton = Button(
        width = 120, height = 40,
        posX = 130, posY = 200,
        text = "Continue"
    ).apply {
        visual = ColorVisual(136, 221, 136)
    }

    /**
     * Button to quit the game.
     */
    val quitButton = Button(
        width = 120, height = 40,
        posX = 250, posY = 200,
        text = "Quit"
    ).apply {
        visual = ColorVisual(221, 136, 136)
        onMouseClicked = { kotlin.system.exitProcess(0) }
    }

    init {
        opacity = 1.0
        addComponents(titleLabel, infoLabel, continueButton, quitButton)
    }
}

