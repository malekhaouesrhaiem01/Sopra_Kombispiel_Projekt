package gui

import entity.Action
import entity.KombiCard
import entity.KombiPlayer
import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.GameComponentView
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
/**
 * GameScene class representing the main game interface for the Kombi game.
 * It extends BoardGameScene and implements Refreshable to update the UI based on game state changes.
 *
 * @property rootService The service managing the game state and player actions.
 * @property cardImageLoader The service responsible for loading card images.
 * @property application The main application instance for scene management.
 */
class GameScene(
    private val rootService: RootService,
    private val cardImageLoader: CardImageLoader,
    private val application: SopraApplication
) : BoardGameScene(1920.0, 1080.0), Refreshable {

    private var exchangeMode = false
    private var playMode = false
    private var selectedHandIndex: Int? = null
    private var selectedExchangeIndex: Int? = null
    private val selectedCards = mutableSetOf<Int>()

    private val drawPileCard = CardView(
        width = 120.0, height = 180.0,
        front = cardImageLoader.backImage,
        back = cardImageLoader.backImage
    ).apply {
        showBack()
        posX = 100.0
        posY = 400.0
        onMouseClicked = {
            try {
                rootService.playerActionService.drawCard()
                checkAndEndTurn()
            } catch (e: IllegalStateException) {
                application.showMessageToUser(e.message ?: "Draw failed due to game state.")
            }
        }
    }
    private val playerDiscardLabel = Label(
        posX = 50.0, posY = 880.0,  // place just above player discard
        width = 300.0, height = 30.0,
        text = "Discard Pile Player 1",
        font = Font(size = 24, fontWeight = Font.FontWeight.BOLD,color = Color(255, 140, 0)),
        alignment = Alignment.TOP_LEFT,
        visual = ColorVisual(255, 255, 255, 0)
    )

    private val opponentDiscardLabel = Label(
        posX = 50.0, posY = 150.0,  // place just above opponent discard
        width = 300.0, height = 30.0,
        text = "Discard Pile Player 2",
        font = Font(size = 24, fontWeight = Font.FontWeight.BOLD, color = Color(30, 144, 255)),
        alignment = Alignment.TOP_LEFT,
        visual = ColorVisual(255, 255, 255, 0)
    )
    private val playerDiscardArea = LinearLayout<CardView>(
        posX = 50.0, posY = 910.0, // directly under label
        width = 400.0, height = 80.0,
        spacing = -25.0,
        orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER_LEFT,
        visual = ColorVisual.TRANSPARENT
    )

    private val opponentDiscardArea = LinearLayout<CardView>(
        posX = 50.0, posY = 180.0,  // directly under label
        width = 400.0, height = 80.0,
        spacing = -25.0,
        orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER_LEFT,
        visual = ColorVisual.TRANSPARENT
    )


    private val drawPileCountLabel = Label(
        posX = 100.0, posY = 580.0,
        width = 130.0, height = 50.0,
        text = "",
        font = Font(size = 22, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER,
        visual = ColorVisual(220, 250, 220, 255)
    )

    private val playerHand = LinearLayout<GameComponentView>(
        posX = 440.0, posY = 880.0, width = 1200.0, height = 200.0,
        spacing = -25.0, orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER,
        visual = ColorVisual.TRANSPARENT
    )

    private val opponentHand = LinearLayout<GameComponentView>(
        posX = 440.0, posY = 80.0, width = 1200.0, height = 200.0,
        spacing = -25.0, orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER,
        visual = ColorVisual.TRANSPARENT
    )

    private val exchangeArea = LinearLayout<CardView>(
        posX = 850.0, posY = 400.0, width = 1000.0, height = 180.0,
        spacing = 10.0, orientation = Orientation.HORIZONTAL,
        visual = ColorVisual.TRANSPARENT
    )

    private val playerScoreLabel = Label(
        posX = 100.0, posY = 20.0, width = 300.0, height = 50.0,
        text = "", font = Font(size = 26, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER,
        visual = ColorVisual(240, 255, 240)
    )

    private val opponentScoreLabel = Label(
        posX = 1520.0, posY = 20.0, width = 300.0, height = 50.0,
        text = "", font = Font(size = 26, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER,
        visual = ColorVisual(240, 255, 240)
    )

    private val actionLabel = Label(
        posX = 1500.0,
        posY = 960.0,
        width = 320.0,
        height = 90.0,
        text = "",
        font = Font(
            size = 26, fontWeight = Font.FontWeight.BOLD, color = Color.RED,),
        alignment = Alignment.TOP_LEFT,
        isWrapText = true

    )

    private val playButton = Button(
        posX = 1650.0, posY = 720.0, width = 200.0, height = 50.0,
        text = "Play Combination", font = Font(size = 18),
        visual = ColorVisual(144, 238, 144)
    ).apply {
        onMouseClicked = {
            if (!playMode) {
                playMode = true
                clearHandSelection()
            } else {
                val game = rootService.currentGame
                if (game != null) {
                    val player = game.players[game.currentPlayerIndex]
                    val selected = selectedCards.mapNotNull { player.hand.getOrNull(it) }
                    try {
                        if (selected.isNotEmpty()) {
                            rootService.playerActionService.playCombination(selected)
                            checkAndEndTurn()
                        }
                    } catch (e: IllegalArgumentException) {
                        application.showMessageToUser(e.message ?: "Invalid combination.")
                        clearHandSelection()
                    } catch (e: IllegalStateException) {
                        application.showMessageToUser(e.message ?: "Play failed due to game state.")
                        clearHandSelection()
                    }
                    playMode = false
                }
            }
        }
    }


    private val exchangeButton = Button(
        posX = 1650.0, posY = 790.0, width = 200.0, height = 50.0,
        text = "Exchange Card", font = Font(size = 18),
        visual = ColorVisual(255, 228, 181)
    ).apply {
        onMouseClicked = {
            if (!exchangeMode) {
                exchangeMode = true
                clearHandSelection()
                clearExchangeSelection()
            } else {
                val game = rootService.currentGame
                if (game != null && selectedHandIndex != null && selectedExchangeIndex != null) {
                    try {
                        rootService.playerActionService.tradeCard(selectedHandIndex!!, selectedExchangeIndex!!)
                        checkAndEndTurn()
                    } catch (e: IllegalArgumentException) {
                        application.showMessageToUser(e.message ?: "Invalid exchange selection.")
                    } catch (e: IllegalStateException) {
                        application.showMessageToUser(e.message ?: "Exchange failed due to game state.")
                    }
                    exchangeMode = false
                    clearHandSelection()
                    clearExchangeSelection()
                }
            }
        }
    }


    private val passButton = Button(
        posX = 1650.0, posY = 860.0, width = 200.0, height = 50.0,
        text = "Pass", font = Font(size = 18),
        visual = ColorVisual(255, 160, 122)
    ).apply {
        onMouseClicked = {
            try {
                rootService.playerActionService.passed()
                val game = rootService.currentGame
                val currentPlayer = game?.players?.get(game.currentPlayerIndex)
                if (currentPlayer != null &&
                    (currentPlayer.performedActions.size >= 2 || Action.PASS in currentPlayer.performedActions)) {
                    rootService.gameService.endTurn()
                    application.showMenuScene(ConfirmNextPlayerScene(application))
                }
            } catch (e: IllegalStateException) {
                application.showMessageToUser(e.message ?: "Pass failed due to invalid state.")
            }
        }
    }


    init {
        this.background = ImageVisual("gamescene.jpg")
        addComponents(
            playerHand, opponentHand, drawPileCard, drawPileCountLabel, exchangeArea,
            playButton, exchangeButton, passButton,playerDiscardArea,
            opponentDiscardArea,opponentDiscardLabel,playerDiscardLabel,
                    actionLabel, playerScoreLabel, opponentScoreLabel
        )
    }

    private fun updateCardRotation(view: CardView, selected: Boolean) {
        view.rotation = if (selected) 8.0 else 0.0
    }

    private fun clearHandSelection() {
        selectedCards.clear()
        selectedHandIndex = null
        playerHand.forEachIndexed { index, comp ->
            if (comp is CardView) updateCardRotation(comp, false)
        }
    }

    private fun clearExchangeSelection() {
        selectedExchangeIndex = null
        exchangeArea.forEachIndexed { index, comp ->
            if (comp is CardView) updateCardRotation(comp, false)
        }
    }

    private fun checkAndEndTurn() {
        val game = rootService.currentGame ?: return
        val player = game.players[game.currentPlayerIndex]

        val distinctNonPlayActions = player.performedActions.filter { it != Action.PLAY_COMBINATION }.toSet()

        if (Action.PASS in player.performedActions || distinctNonPlayActions.size >= 2) {
            rootService.gameService.endTurn()
            val confirmScene = ConfirmNextPlayerScene(application)
            confirmScene.refreshAfterTurnEnd()
            application.showMenuScene(confirmScene)
        }
    }




    /**
     * Updates the entire visual state of the GameScene to reflect the current game state.
     *
     * This includes:
     * - Updating the draw pile visibility and remaining count
     * - Rebuilding the current player's hand (face-up, selectable)
     * - Rebuilding the opponent's hand (face-down)
     * - Rebuilding the exchange area (face-up, selectable)
     * - Rendering each player's discard pile with smaller overlapping cards
     * - Updating action and score labels for both players
     *
     * Additionally:
     * - If the current player’s hand is empty, the game is automatically ended
     *   by calling gameservice endgame which then triggers the result scene.
     */

    fun refreshDisplay() {
        val game = rootService.currentGame ?: return
        val currentPlayer = game.players[game.currentPlayerIndex]
        val opponent = game.players[(game.currentPlayerIndex + 1) % 2]

        drawPileCard.isVisible = game.drawPile.isNotEmpty()
        drawPileCountLabel.text = if (game.drawPile.isNotEmpty()) "${game.drawPile.size}\nKarten" else "0\nKarten"

        playerHand.clear()
        currentPlayer.hand.forEachIndexed { index, card ->
            val view = CardView(
                width = 120.0, height = 180.0,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply {
                showFront()
                onMouseClicked = {
                    if (playMode || exchangeMode) {
                        if (selectedCards.contains(index)) {
                            selectedCards.remove(index)
                            updateCardRotation(this, false)
                        } else {
                            selectedCards.add(index)
                            updateCardRotation(this, true)
                        }
                        if (exchangeMode) {
                            selectedHandIndex = index
                            if (selectedExchangeIndex != null) {
                                performExchange()
                            } else {
                                playerHand.forEachIndexed { i, card ->
                                    if (card is CardView) updateCardRotation(card, i == index)
                                }
                            }
                        }
                    }
                }
            }
            playerHand.add(view)
        }

        opponentHand.clear()
        repeat(opponent.hand.size) {
            val view = CardView(
                width = 120.0, height = 180.0,
                front = cardImageLoader.blankImage,
                back = cardImageLoader.backImage
            ).apply { showBack() }
            opponentHand.add(view)
        }
        playerDiscardArea.clear()
        opponentDiscardArea.clear()

        currentPlayer.discardPile.forEach {
            val view = CardView(
                width = 65.0, height = 95.0,
                front = cardImageLoader.frontImageFor(it.suit, it.value),
                back = cardImageLoader.backImage
            ).apply { showFront() }
            playerDiscardArea.add(view)
        }

        opponent.discardPile.forEach {
            val view = CardView(
                width = 65.0, height = 95.0,
                front = cardImageLoader.frontImageFor(it.suit, it.value),
                back = cardImageLoader.backImage
            ).apply { showFront() }
            opponentDiscardArea.add(view)
        }

        exchangeArea.clear()
        game.exchangeArea.forEachIndexed { index, card ->
            val view = CardView(
                width = 120.0, height = 180.0,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply {
                showFront()
                onMouseClicked = {
                    if (exchangeMode) {
                        selectedExchangeIndex = index
                        if (selectedHandIndex != null) {
                            performExchange()
                        } else {
                            exchangeArea.forEachIndexed { i, comp ->
                                if (comp is CardView) updateCardRotation(comp, i == index)
                            }
                        }
                    }
                }
            }
            exchangeArea.add(view)

        }
        if (currentPlayer.hand.isEmpty()) {
            rootService.gameService.endGame()
            return
        }

        actionLabel.text = "Actions: ${currentPlayer.performedActions.joinToString()}"
        playerScoreLabel.text = "${game.players[0].name}: ${game.players[0].score}"
        opponentScoreLabel.text = "${game.players[1].name}: ${game.players[1].score}"
    }
    /**
     * Executes the exchange of a selected hand card with a selected card from the exchange area.
     *
     * This function is called automatically once both `selectedHandIndex` and `selectedExchangeIndex`
     * are set (regardless of selection order). It performs the swap by calling the service layer,
     * then resets the exchange mode and visual selection state.
     *
     * If the exchange fails (invalid indices or rule violation), an error message is shown.
     *
     * After a successful exchange, it checks whether the player's turn should end.
     */
    private fun performExchange() {
        try {
            rootService.playerActionService.tradeCard(
                handIndex = selectedHandIndex!!,
                exchangeIndex = selectedExchangeIndex!!
            )
            exchangeMode = false
            clearHandSelection()
            clearExchangeSelection()
            checkAndEndTurn()
        } catch (e: IllegalArgumentException) {
            application.showMessageToUser(e.message ?: "Invalid selection.")
        } catch (e: IllegalStateException) {
            application.showMessageToUser(e.message ?: "Exchange failed due to game state.")
        }
    }

    /**
     * Clears all selected card indices and disables play/exchange modes.
     *
     * Should be called at the start of each turn or when switching actions.
     */
    private fun resetSelections() {
        selectedHandIndex = null
        selectedExchangeIndex = null
        selectedCards.clear()
        playMode = false
        exchangeMode = false
        clearHandSelection()
        clearExchangeSelection()
    }


    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {
        resetSelections()
        refreshDisplay()
    }
    override fun refreshAfterTurnEnd() {
        refreshDisplay()
        application.showConfirmNextPlayerScene()
    }

    override fun refreshAfterCardDrawn(card: KombiCard) = refreshDisplay()
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) = refreshDisplay()
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) = refreshDisplay()
    override fun showMessage(message: String) {
        actionLabel.text = message
    }
}