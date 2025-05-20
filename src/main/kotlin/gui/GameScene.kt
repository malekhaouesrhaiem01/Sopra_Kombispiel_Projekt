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
 * Main scene where the game is played. Supports drawing, exchanging, playing combinations, and passing.
 * Cards are shown face-up for the current player. Exchange area is always visible.
 * Buttons are positioned clearly for user interaction.
 *
 * @property rootService Access to game logic and services
 * @property cardImageLoader Utility for loading card images
 * @property application Reference to SopraApplication for scene switching and messaging
 */
class GameScene(
    private val rootService: RootService,
    private val cardImageLoader: CardImageLoader,
    private val application: SopraApplication
) : BoardGameScene(1920, 1080), Refreshable {

    private var exchangeMode = false
    private var playMode = false
    private var selectedHandIndex: Int? = null
    private var selectedExchangeIndex: Int? = null
    private val selectedCards = mutableListOf<Int>()

    private val playerHand = LinearLayout<GameComponentView>(
        posX = 750, posY = 700, width = 400, height = 200,
        spacing = 20,
        visual = ColorVisual.TRANSPARENT,
        orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER
    )

    private val opponentHand = LinearLayout<GameComponentView>(
        posX = 750, posY = 80, width = 400, height = 200,
        spacing = 20,
        visual = ColorVisual.TRANSPARENT,
        orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER
    )

    private val drawPile = CardStack<CardView>(
        posX = 100, posY = 400, width = 130, height = 180,
        alignment = Alignment.CENTER,
        visual = ColorVisual(200, 200, 200)
    )

    private val discardPile = CardStack<CardView>(
        posX = 300, posY = 400, width = 130, height = 180,
        alignment = Alignment.CENTER,
        visual = ColorVisual(200, 200, 200)
    )

    private val exchangeArea = LinearLayout<CardView>(
        posX = 750, posY = 400, width = 400, height = 180,
        spacing = 20,
        orientation = Orientation.HORIZONTAL,
        alignment = Alignment.CENTER,
        visual = ColorVisual.TRANSPARENT
    )

    private val actionLabel = Label(
        posX = 1200, posY = 640,
        width = 600, height = 40,
        text = "",
        font = Font(size = 25),
        isWrapText = true,
        alignment = Alignment.TOP_LEFT
    )

    private val drawButton = Button(
        posX = 1200, posY = 400, width = 180, height = 50,
        text = "Draw Card",
        font = Font(size = 18),
        visual = ColorVisual(173, 216, 230)
    ).apply {
        onMouseClicked = {
            try {
                rootService.playerActionService.drawCard()
            } catch (e: Exception) {
                application.showMessageToUser(e.message ?: "Draw failed.")
            }
        }
    }

    private val playButton = Button(
        posX = 1200, posY = 460, width = 180, height = 50,
        text = "Play Combination",
        font = Font(size = 18),
        visual = ColorVisual(144, 238, 144)
    ).apply {
        onMouseClicked = {
            if (!playMode) {
                playMode = true
                selectedCards.clear()
            } else {
                val game = rootService.currentGame
                if (game != null) {
                    val player = game.players[game.currentPlayerIndex]
                    val selected = selectedCards.mapNotNull { player.hand.getOrNull(it) }
                    try {
                        if (selected.isNotEmpty()) {
                            rootService.playerActionService.playCombinations(listOf(selected))
                        }
                    } catch (e: Exception) {
                        application.showMessageToUser(e.message ?: "Play failed.")
                    }
                    playMode = false
                    selectedCards.clear()
                }
            }
        }
    }
    private val exchangeButton = Button(
        posX = 1200, posY = 520, width = 180, height = 50,
        text = "Exchange Card",
        font = Font(size = 18),
        visual = ColorVisual(255, 228, 181)
    ).apply {
        onMouseClicked = {
            if (!exchangeMode) {
                exchangeMode = true
                selectedHandIndex = null
                selectedExchangeIndex = null
            } else {
                try {
                    if (selectedHandIndex != null && selectedExchangeIndex != null) {
                        rootService.playerActionService.tradeCard(selectedHandIndex!!, selectedExchangeIndex!!)
                    }
                } catch (e: Exception) {
                    application.showMessageToUser(e.message ?: "Exchange failed.")
                }
                exchangeMode = false
            }
        }
    }

    private val passButton = Button(
        posX = 1200, posY = 580, width = 180, height = 50,
        text = "Pass",
        font = Font(size = 18),
        visual = ColorVisual(255, 160, 122)
    ).apply {
        onMouseClicked = {
            try {
                rootService.playerActionService.passed()
            } catch (e: Exception) {
                application.showMessageToUser(e.message ?: "Pass failed.")
            }
        }
    }

    init {
        addComponents(
            playerHand,
            opponentHand,
            drawPile,
            discardPile,
            exchangeArea,
            drawButton,
            playButton,
            exchangeButton,
            passButton,
            actionLabel
        )
    }

    /**
     * Refreshes all card areas including player hands, exchange area, and discard pile.
     */
    fun refreshDisplay() {
        val game = rootService.currentGame ?: return
        val currentPlayer = game.players[game.currentPlayerIndex]
        val opponent = game.players[(game.currentPlayerIndex + 1) % 2]

        playerHand.clear()
        opponentHand.clear()
        exchangeArea.clear()

        playerHand.clear()
        currentPlayer.hand.forEachIndexed { index, card ->
            val view = CardView(
                width = 80, height = 120,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply {
                showFront()
                onMouseClicked = {
                    if (playMode) selectedCards.add(index)
                    if (exchangeMode) selectedHandIndex = index
                }
            }
            playerHand.add(view)
        }

        opponentHand.clear()
        repeat(opponent.hand.size) {
            val view = CardView(
                width = 80, height = 120,
                front = cardImageLoader.blankImage,
                back = cardImageLoader.backImage
            ).apply { showBack() }
            opponentHand.add(view)
        }

        exchangeArea.clear()
        game.exchangeArea.forEachIndexed { index, card ->
            val view = CardView(
                width = 80, height = 120,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply {
                showFront()
                onMouseClicked = {
                    if (exchangeMode) selectedExchangeIndex = index
                }
            }
            exchangeArea.add(view)
        }

        actionLabel.text = "Actions: ${currentPlayer.performedActions.joinToString()}"
    }

    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) = refreshDisplay()
    override fun refreshAfterTurnEnd(finishedPlayer: KombiPlayer) {
        refreshDisplay()
        application.showConfirmNextPlayerScene()
    }
    override fun refreshAfterCardDrawn(card: KombiCard) = refreshDisplay()
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) = refreshDisplay()
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {
        combination.forEach { card ->
            val view = CardView(
                width = 80, height = 120,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            ).apply {
                showFront()
            }
            discardPile.add(view)
        }
        refreshDisplay()
    }

    override fun refreshAfterStart(players: List<KombiPlayer>) {}
    override fun refreshAfterGameEnd(winner: KombiPlayer, loser: KombiPlayer) {}
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {}
    override fun showMessage(message: String) {
        actionLabel.text = message
    }
}
