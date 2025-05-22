package gui
import tools.aqua.bgw.visual.ImageVisual
import entity.Action
import entity.KombiCard
import entity.KombiPlayer
import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.GameComponentView
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * Main scene where the game is played. Handles all UI logic such as drawing, playing, exchanging, passing,
 * visual feedback (rotation on selection), and automatic turn-end triggering.
 * Updated to support two discard piles, enlarged cards, proper layout usage, and button positioning.
 *
 * @property rootService Access to game logic and state.
 * @property cardImageLoader Provides card visuals.
 * @property application For scene switching and messaging.
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
    private val selectedCards = mutableListOf<Int>()
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

    private val drawPileCountLabel = Label(
        posX = 100.0, posY = 590.0,
        width = 130.0, height = 50.0,
        text = "",
        font = Font(size = 22, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER,
        visual = ColorVisual(220, 250, 220)
    )

    private val playerHand = LinearLayout<GameComponentView>(
        posX = 460.0, posY = 850.0, width = 1000.0, height = 200.0,
        spacing = 25.0, orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER, visual = ColorVisual.TRANSPARENT
    )

    private val opponentHand = LinearLayout<GameComponentView>(
        posX = 460.0, posY = 80.0, width = 1000.0, height = 200.0,
        spacing = 25.0, orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER, visual = ColorVisual.TRANSPARENT
    )

    private val drawPile = CardStack<CardView>(
        posX = 100.0, posY = 400.0, width = 130.0, height = 180.0,
        alignment = Alignment.CENTER, visual = ColorVisual(200, 200, 200)
    )

    private val playerDiscardPile = CardStack<CardView>(
        posX = 260.0, posY = 850.0, width = 130.0, height = 180.0,
        alignment = Alignment.CENTER, visual = ColorVisual(200, 200, 200)
    )

    private val opponentDiscardPile = CardStack<CardView>(
        posX = 260.0, posY = 80.0, width = 130.0, height = 180.0,
        alignment = Alignment.CENTER, visual = ColorVisual(200, 200, 200)
    )

    private val exchangeArea = LinearLayout<CardView>(
        posX = 460.0, posY = 400.0, width = 1000.0, height = 180.0,
        spacing = 25.0, orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER, visual = ColorVisual.TRANSPARENT
    )

    private val actionLabel = Label(
        posX = 1450.0, posY = 950.0, width = 400.0, height = 50.0,
        text = "", font = Font(size = 24), isWrapText = true,
        alignment = Alignment.TOP_LEFT
    )

    private val drawButton = Button(
        posX = 1450.0, posY = 700.0, width = 200.0, height = 50.0,
        text = "Draw Card", font = Font(size = 18),
        visual = ColorVisual(173, 216, 230)
    ).apply {
        onMouseClicked = {
            try {
                rootService.playerActionService.drawCard()
                checkAndEndTurn()
            } catch (e: Exception) {
                application.showMessageToUser(e.message ?: "Draw failed.")
            }
        }
    }

    private val playButton = Button(
        posX = 1450.0, posY = 760.0, width = 200.0, height = 50.0,
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
                            rootService.playerActionService.playCombinations(listOf(selected))
                            checkAndEndTurn()
                        }
                    } catch (e: Exception) {
                        application.showMessageToUser(e.message ?: "Play failed.")
                        clearHandSelection()
                    }
                    playMode = false
                }
            }
        }
    }

    private val exchangeButton = Button(
        posX = 1450.0, posY = 820.0, width = 200.0, height = 50.0,
        text = "Exchange Card", font = Font(size = 18),
        visual = ColorVisual(255, 228, 181)
    ).apply {
        onMouseClicked = {
            if (!exchangeMode) {
                exchangeMode = true
                clearHandSelection()
                clearExchangeSelection()
            } else {
                try {
                    if (selectedHandIndex != null && selectedExchangeIndex != null) {
                        rootService.playerActionService.tradeCard(selectedHandIndex!!, selectedExchangeIndex!!)
                        checkAndEndTurn()
                    }
                } catch (e: Exception) {
                    application.showMessageToUser(e.message ?: "Exchange failed.")
                }
                exchangeMode = false
                clearHandSelection()
                clearExchangeSelection()
            }
        }
    }

    private val passButton = Button(
        posX = 1450.0, posY = 880.0, width = 200.0, height = 50.0,
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
            } catch (e: Exception) {
                application.showMessageToUser(e.message ?: "Pass failed.")
            }
        }
    }

    init {
        this.background = ImageVisual("gamescene.jpg")
        addComponents(
            playerHand, opponentHand, drawPile, playerDiscardPile, opponentDiscardPile, exchangeArea,
            drawButton, playButton, exchangeButton, passButton, actionLabel, drawPileCountLabel, playerScoreLabel, opponentScoreLabel,
        )
    }

    private fun updateCardRotation(view: CardView, selected: Boolean) {
        view.rotation = if (selected) 6.0 else 0.0
    }

    private fun clearHandSelection() {
        selectedCards.clear()
        selectedHandIndex = null
        playerHand.forEach { if (it is CardView) updateCardRotation(it, false) }
    }

    private fun clearExchangeSelection() {
        selectedExchangeIndex = null
        exchangeArea.forEach { if (it is CardView) updateCardRotation(it, false) }
    }

    private fun checkAndEndTurn() {
        val game = rootService.currentGame ?: return
        val currentPlayer = game.players[game.currentPlayerIndex]
        if (currentPlayer.performedActions.size == 2) {
            rootService.gameService.endTurn()
            application.showMenuScene(ConfirmNextPlayerScene(application))
        }
    }

    fun refreshDisplay() {
        val game = rootService.currentGame ?: return
        val currentPlayer = game.players[game.currentPlayerIndex]
        val opponent = game.players[(game.currentPlayerIndex + 1) % 2]
        playerDiscardPile.clear()
        opponentDiscardPile.clear()
        playerHand.clear()
        currentPlayer.hand.forEachIndexed { index, card ->
            val view = CardView(
                width = 100.0, height = 150.0,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply {
                showFront()
                onMouseClicked = {
                    if (playMode) {
                        if (selectedCards.contains(index)) {
                            selectedCards.remove(index)
                            updateCardRotation(this, false)
                        } else {
                            selectedCards.add(index)
                            updateCardRotation(this, true)
                        }
                    }
                    if (exchangeMode) {
                        selectedHandIndex = index
                        playerHand.forEachIndexed { i, card ->
                            if (card is CardView) updateCardRotation(card, i == index)
                        }
                    }
                }
            }
            playerHand.add(view)
        }

        opponentHand.clear()
        repeat(opponent.hand.size) {
            val view = CardView(
                width = 100.0, height = 150.0,
                front = cardImageLoader.blankImage,
                back = cardImageLoader.backImage
            ).apply { showBack() }
            opponentHand.add(view)
        }

        exchangeArea.clear()
        game.exchangeArea.forEachIndexed { index, card ->
            val view = CardView(
                width = 100.0, height = 150.0,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply {
                showFront()
                onMouseClicked = {
                    if (exchangeMode) {
                        selectedExchangeIndex = index
                        exchangeArea.forEachIndexed { i, card ->
                            if (card is CardView) updateCardRotation(card, i == index)
                        }
                    }
                }
            }
            exchangeArea.add(view)
        }

        drawPileCountLabel.text = "${game.drawPile.size}\nKarten"
        actionLabel.text = "Actions: ${currentPlayer.performedActions.joinToString()}"
        playerScoreLabel.text = "${game.players[0].name}: ${game.players[0].score}"
        opponentScoreLabel.text = "${game.players[1].name}: ${game.players[1].score}"
    }

    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) { refreshDisplay()}
    override fun refreshAfterTurnEnd() {
        refreshDisplay()
       application.showConfirmNextPlayerScene()
    }

    override fun refreshAfterCardDrawn(card: KombiCard) = refreshDisplay()
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) = refreshDisplay()

    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {
        val game = rootService.currentGame ?: return
        val isCurrentPlayer = player == game.players[game.currentPlayerIndex]
        val targetPile = if (isCurrentPlayer) playerDiscardPile else opponentDiscardPile

        combination.forEach { card ->
            val view = CardView(
                width = 100.0, height = 150.0,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply { showFront() }
            targetPile.add(view)
        }
        refreshDisplay()
    }

    override fun refreshAfterStart(players: List<KombiPlayer>) {}
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {}
    override fun showMessage(message: String) {
        actionLabel.text = message
    }
}