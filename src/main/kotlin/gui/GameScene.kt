package gui

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
 * This is the main scene where the actual game takes place.
 * We display both hands (player + opponent), the draw pile, the discard pile,
 * the exchange area, and all action buttons (play, draw, trade, skip).
 *
 * It also handles most of the click logic like selecting cards or triggering trades.
 *
 * @param rootService Reference to the game logic (backend services).
 * @param cardImageLoader Helper class to load card images from the sprite sheet.
 */
class GameScene(
    private val rootService: RootService,
    private val cardImageLoader: CardImageLoader
) : BoardGameScene(1920, 1080), Refreshable {

    /** Whether we’re currently trading a card. */
    private var tradeMode = false

    /** Whether the player is currently selecting cards to play a combination. */
    private var playMode = false

    /** Index of selected hand card for trading. */
    private var selectedHandIndex: Int? = null

    /** Index of selected exchange area card for trading. */
    private var selectedExchangeIndex: Int? = null

    /** Indices of cards selected to play as a combination. */
    private val selectedCards = mutableListOf<Int>()

    /** Horizontal layout for the current player’s hand (bottom of the screen). */
    val playerHand = LinearLayout<GameComponentView>(
        posX = 830, posY = 700, width = 300, height = 200,
        spacing = -60,
        visual = ColorVisual(Color.WHITE).apply { transparency = 0.0 },
        orientation = Orientation.HORIZONTAL,
        alignment = Alignment.TOP_CENTER
    )

    /** Hidden layout for the opponent’s hand (top of the screen). */
    val opponentHand = LinearLayout<GameComponentView>(
        posX = 830, posY = 100, width = 300, height = 200,
        spacing = 0,
        visual = ColorVisual(Color.WHITE).apply { transparency = 0.0 },
        orientation = Orientation.HORIZONTAL,
        alignment = Alignment.TOP_LEFT
    )

    /** Button to skip the turn (does nothing here yet). */
    val skipButton = Button(
        posX = 1400, posY = 750, width = 110, height = 45,
        text = "Skip",
        font = Font(size = 20, color = Color.BLACK, family = "Arial"),
        alignment = Alignment.CENTER,
        isWrapText = false,
        visual = ColorVisual(Color.WHITE)
    )

    /** Button to enter play mode and start selecting cards for a combination. */
    val playCombinationButton = Button(
        posX = 1400, posY = 840, width = 110, height = 45,
        text = "Play",
        font = Font(size = 20, color = Color.BLACK, family = "Arial"),
        alignment = Alignment.CENTER,
        isWrapText = false,
        visual = ColorVisual(Color.WHITE)
    ).apply {
        onMouseClicked = {
            playMode = true
            selectedCards.clear()
        }
    }

    /** Button to confirm the selected cards and actually play the combination. */
    val confirmPlayButton = Button(
        posX = 1400, posY = 900, width = 110, height = 45,
        text = "Confirm",
        font = Font(size = 20, color = Color.BLACK, family = "Arial"),
        alignment = Alignment.CENTER,
        isWrapText = false,
        visual = ColorVisual(Color(0xCCCCCC))
    ).apply {
        onMouseClicked = {
            val game = rootService.currentGame
            if (game != null) {
                val player = game.players[game.currentPlayerIndex]
                if (playMode && selectedCards.isNotEmpty()) {
                    val selectedCardList = selectedCards.mapNotNull { index -> player.hand.getOrNull(index) }
                    rootService.playerActionService.playCombinations(listOf(selectedCardList))
                    selectedCards.clear()
                    playMode = false
                }
            }
        }
    }

    /** Button to start the trade process (select one from hand, then from exchange). */
    val tradeButton = Button(
        posX = 1400, posY = 660, width = 110, height = 45,
        text = "Trade",
        font = Font(size = 20, color = Color.BLACK, family = "Arial"),
        alignment = Alignment.CENTER,
        isWrapText = false,
        visual = ColorVisual(Color.WHITE)
    ).apply {
        onMouseClicked = {
            tradeMode = true
            selectedHandIndex = null
            selectedExchangeIndex = null
        }
    }

    /** Stack for the draw pile (cards still to be drawn). */
    val drawPile = CardStack<CardView>(
        posX = 1600, posY = 420, width = 130, height = 200,
        alignment = Alignment.CENTER,
        visual = ColorVisual(Color.WHITE).apply { transparency = 0.0 }
    )

    /** Stack for played combinations (shown face up). */
    val discardPile = CardStack<CardView>(
        posX = 300, posY = 600, width = 130, height = 200,
        alignment = Alignment.CENTER,
        visual = ColorVisual(Color.WHITE).apply { transparency = 0.0 }
    )

    /** The three exchange cards (visible and clickable for trading). */
    val tradeAreas = listOf(
        CardView(posX = 600, posY = 400, width = 130, height = 200,
            front = ColorVisual(Color(0xFFFEFE)), back = ColorVisual(Color(0xB4B4B4))),
        CardView(posX = 900, posY = 400, width = 130, height = 200,
            front = ColorVisual(Color(0xF3F3F3)), back = ColorVisual(Color(0xB4B4B4))),
        CardView(posX = 1200, posY = 400, width = 130, height = 200,
            front = ColorVisual(Color(0xEBEBEB)), back = ColorVisual(Color(0xB4B4B4)))
    )

    init {
        // Connect exchange card clicks
        tradeAreas.forEachIndexed { index, view ->
            view.onMouseClicked = {
                if (tradeMode) {
                    selectedExchangeIndex = index
                    tryTrade()
                }
            }
        }

        addComponents(
            playerHand,
            opponentHand,
            skipButton,
            playCombinationButton,
            confirmPlayButton,
            tradeButton,
            drawPile,
            discardPile,
            *tradeAreas.toTypedArray()
        )
    }

    /**
     * Called when both a hand card and an exchange card are selected.
     * Performs the trade and resets selection.
     */
    private fun tryTrade() {
        val game = rootService.currentGame ?: return
        if (selectedHandIndex != null && selectedExchangeIndex != null) {
            rootService.playerActionService.tradeCard(
                handIndex = selectedHandIndex!!,
                exchangeIndex = selectedExchangeIndex!!
            )
            tradeMode = false
            selectedHandIndex = null
            selectedExchangeIndex = null
        }
    }

    /**
     * Completely rebuilds the player hand and opponent hand visually.
     * Also clears and redraws the selected card visuals.
     */
    fun refreshDisplay() {
        println(" refreshDisplay() called")
        val game = rootService.currentGame ?: return
        val currentPlayer = game.players[game.currentPlayerIndex]
        playerHand.clear()
        opponentHand.clear()

        currentPlayer.hand.forEachIndexed { index, card ->
            val cardView = CardView(
                width = 80,
                height = 120,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply {
                onMouseClicked = {
                    if (playMode) {
                        selectedCards.add(index)
                    } else if (tradeMode) {
                        selectedHandIndex = index
                        tryTrade()
                    }
                }
            }
            playerHand.add(cardView)
        }

        val opponent = game.players[(game.currentPlayerIndex + 1) % 2]
        repeat(opponent.hand.size) {
            val hiddenCard = CardView(
                width = 80,
                height = 120,
                front = cardImageLoader.blankImage,
                back = cardImageLoader.backImage
            )
            opponentHand.add(hiddenCard)
        }
    }

    // Below: refresh methods from the Refreshable interface.
    // Most of them just call refreshDisplay() to update the view.

    override fun refreshAfterStart(players: List<KombiPlayer>) {}
    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {
        refreshDisplay()
    }
    override fun refreshAfterTurnEnd(finishedPlayer: KombiPlayer) {
        refreshDisplay()
    }
    override fun refreshAfterGameEnd(winner: KombiPlayer, loser: KombiPlayer) {}
    override fun refreshAfterCardDrawn(card: KombiCard) {
        refreshDisplay()
    }
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {
        refreshDisplay()
    }
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {
        refreshDisplay()
    }
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {}
    override fun showMessage(message: String) {}
}
