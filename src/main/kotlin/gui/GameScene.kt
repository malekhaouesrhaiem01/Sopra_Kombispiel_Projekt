package gui

import entity.KombiCard
import service.RootService
import service.Refreshable
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * The main game scene for Kombi-Duell.
 * Displays player hands, exchange area, draw pile, and action buttons.
 * Handles card selection and interaction logic per turn.
 *
 * @property rootService Provides access to the game logic and services.
 */
class GameScene(
    private val rootService: RootService
) : BoardGameScene(1920.0, 1080.0), Refreshable {

    /**
     * Loads visuals for card fronts and backs.
     */
    private val cardImageLoader = CardImageLoader()

    /**
     * Cards currently selected by the player for an action (exchange or play).
     */
    private val selectedCards = mutableListOf<KombiCard>()

    /**
     * Maximum number of actions allowed per player turn.
     */
    private val maxActionsPerTurn = 2

    /**
     * Current number of actions taken this turn.
     */
    private var actionsThisTurn = 0

    /**
     * Layout for opponent's hand (top of screen).
     */
    private val opponentHand = LinearLayout<CardView>(
        width = 1200.0, height = 200.0,
        spacing = 10.0, posX = 360.0, posY = 50.0
    )

    /**
     * Layout for player's hand (bottom of screen).
     */
    private val playerHand = LinearLayout<CardView>(
        width = 1200.0, height = 200.0,
        spacing = 10.0, posX = 360.0, posY = 780.0
    )

    /**
     * Layout for exchange area (middle of screen).
     */
    private val exchangeArea = LinearLayout<CardView>(
        width = 400.0, height = 200.0,
        spacing = 10.0, posX = 760.0, posY = 440.0
    )

    /**
     * Label displaying "Draw Pile".
     */
    private val drawPileLabel = Label(
        width = 200.0, height = 40.0,
        posX = 860.0, posY = 360.0,
        text = "Draw Pile",
        font = Font(size = 20)
    )

    /**
     * Button to draw a card from the draw pile.
     */
    private val drawButton = Button(
        width = 180.0, height = 40.0,
        posX = 150.0, posY = 850.0,
        text = "Draw Card"
    ).apply {
        onMouseClicked = {
            rootService.playerActionService.drawCard()
            incrementAction()
        }
    }

    /**
     * Button to exchange one card from hand with one from exchange area.
     */
    private val exchangeButton = Button(
        width = 180.0, height = 40.0,
        posX = 360.0, posY = 850.0,
        text = "Exchange Card"
    ).apply {
        onMouseClicked = {
            if (selectedCards.size == 2) {
                val handIndex = rootService.currentGame?.players?.first()?.hand?.indexOf(selectedCards[0]) ?: -1
                val exchangeIndex = rootService.currentGame?.exchangeArea?.indexOf(selectedCards[1]) ?: -1
                if (handIndex != -1 && exchangeIndex != -1) {
                    rootService.playerActionService.tradeCard(handIndex, exchangeIndex)
                    incrementAction()
                    selectedCards.clear()
                }
            }
        }
    }

    /**
     * Button to play a selected combination of cards.
     */
    private val playButton = Button(
        width = 180.0, height = 40.0,
        posX = 570.0, posY = 850.0,
        text = "Play Combination"
    ).apply {
        onMouseClicked = {
            rootService.playerActionService.playCombinations(listOf(selectedCards.toList()))
            incrementAction()
            selectedCards.clear()
        }
    }

    /**
     * Button to pass the player's turn.
     */
    private val passButton = Button(
        width = 180.0, height = 40.0,
        posX = 780.0, posY = 850.0,
        text = "Pass"
    ).apply {
        onMouseClicked = {
            rootService.playerActionService.passed()
            triggerNextPlayer()
        }
    }

    /**
     * Initializes the game scene with default layout and buttons.
     */
    init {
        background = ColorVisual(34, 139, 34)
        addComponents(
            opponentHand,
            drawPileLabel,
            exchangeArea,
            playerHand,
            drawButton,
            exchangeButton,
            playButton,
            passButton
        )
    }

    /**
     * Fills the scene with card views after game start.
     * Displays player hand, opponent hand, and exchange area.
     */
    fun refreshAfterStartNewGame() {
        val game = rootService.currentGame ?: return

        opponentHand.clear()
        game.players[1].hand.forEach { _ ->
            opponentHand.add(
                CardView(
                    front = cardImageLoader.backImage,
                    height = 200,
                    width = 130
                )
            )
        }


        playerHand.clear()
        game.players[0].hand.forEach { card ->
            val view = createCardView(card)
            playerHand.add(view)
        }

        exchangeArea.clear()
        game.exchangeArea.forEach { card ->
            val view = createCardView(card)
            exchangeArea.add(view)
        }
    }

    /**
     * Creates a clickable CardView for the given [card].
     * Clicking toggles its selection and refreshes state.
     *
     * @param card The [KombiCard] to create a visual for.
     * @return A configured [CardView] component.
     */
    private fun createCardView(card: KombiCard): CardView {
        val view = CardView(
            front = cardImageLoader.frontImageFor(card.suit, card.value),
            back = cardImageLoader.backImage,
            height = 200, width = 130
        )
        view.onMouseClicked = {
            toggleCardSelection(card)
            refreshAfterCardSelected(selectedCard = card)
        }
        return view
    }

    /**
     * Toggles card selection for user interaction.
     *
     * @param card The card to select or deselect.
     */
    private fun toggleCardSelection(card: KombiCard) {
        if (selectedCards.contains(card)) {
            selectedCards.remove(card)
        } else {
            selectedCards.add(card)
        }
    }

    /**
     * Called when a card is clicked by the player.
     * Can be used to update GUI or log info.
     *
     * @param selectedCard The card that was selected or deselected.
     */
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {
        println("Selected cards: ${selectedCards.size}")
    }

    /**
     * Increases action counter and ends turn after max actions are taken.
     */
    private fun incrementAction() {
        actionsThisTurn++
        if (actionsThisTurn >= maxActionsPerTurn) {
            passButton.isVisible = false
            triggerNextPlayer()
        }
    }

    /**
     * Ends the current turn and shows the [ConfirmNextPlayerScene] for the next player.
     */
    private fun triggerNextPlayer() {
        val game = rootService.currentGame ?: return
        val nextPlayer = game.players[(game.currentPlayerIndex + 1) % 2]
        rootService.viewSwitcher?.showConfirmNextPlayerScene(nextPlayer.name)
        actionsThisTurn = 0
        passButton.isVisible = true
    }
}
