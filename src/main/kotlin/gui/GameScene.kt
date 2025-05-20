package gui

import entity.KombiCard
import entity.KombiGame
import service.Refreshable
import service.RootService
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.util.Font

/**
 * The main game scene displaying the current state of the Kombi-Duell game.
 * It includes the opponent's hand, the exchange pile, draw pile, discard pile,
 * current player’s hand, and action buttons.
 *
 * @property rootService Reference to the [RootService], giving access to services and game state.
 * @property cardImageLoader Utility to load images for cards.
 */
class GameScene(
    private val rootService: RootService,
    private val cardImageLoader: CardImageLoader
) : BoardGameScene(1920, 1080), Refreshable {

    private val currentPlayerHandView = CardArea<KombiCard>(
        posX = 100, posY = 800, width = 1720, height = 200,
        layout = CardArea.Layout.HORIZONTAL
    )

    private val opponentHandView = CardArea<KombiCard>(
        posX = 100, posY = 40, width = 1720, height = 200,
        layout = CardArea.Layout.HORIZONTAL
    )

    private val exchangePileView = CardArea<KombiCard>(
        posX = 860, posY = 440, width = 200, height = 200,
        layout = CardArea.Layout.STACKED
    )

    private val drawPileCountLabel = Label(
        text = "Draw pile: 0",
        posX = 1160, posY = 500,
        width = 200, height = 50,
        font = Font.defaultFont(24)
    )

    private val discardPileView = CardArea<KombiCard>(
        posX = 1460, posY = 440, width = 300, height = 200,
        layout = CardArea.Layout.HORIZONTAL
    )

    private val drawButton = Button("Draw Card", posX = 100, posY = 700, width = 200, height = 50).apply {
        onMouseClicked = {
            rootService.playerActionService.drawCard()
        }
    }

    private val tradeButton = Button("Trade Card", posX = 320, posY = 700, width = 200, height = 50).apply {
        onMouseClicked = {
            // selection handled via selectedCard
            selectedCard?.let { selected ->
                rootService.playerActionService.tradeCard(selected)
                selectedCard = null
            }
        }
    }

    private val playButton = Button("Play Combination", posX = 540, posY = 700, width = 200, height = 50).apply {
        onMouseClicked = {
            if (selectedCards.isNotEmpty()) {
                rootService.playerActionService.playCombinations(listOf(selectedCards.toList()))
                selectedCards.clear()
            }
        }
    }

    private val passButton = Button("Pass", posX = 760, posY = 700, width = 200, height = 50).apply {
        onMouseClicked = {
            rootService.playerActionService.passed()
        }
    }

    private val selectedCards: MutableSet<KombiCard> = mutableSetOf()
    private var selectedCard: KombiCard? = null

    init {
        background = ColorVisual.LIGHT_GRAY

        addComponents(
            currentPlayerHandView,
            opponentHandView,
            exchangePileView,
            discardPileView,
            drawPileCountLabel,
            drawButton,
            tradeButton,
            playButton,
            passButton
        )

        currentPlayerHandView.onCardClicked = { card ->
            if (selectedCards.contains(card)) {
                selectedCards.remove(card)
                currentPlayerHandView.unhighlightCard(card)
            } else {
                selectedCards.add(card)
                currentPlayerHandView.highlightCard(card)
            }
            rootService.refreshables.forEach { it.refreshAfterCardSelected(card) }
        }
    }

    /**
     * Refresh the scene after a new game starts or turn begins.
     * Updates all visual components.
     *
     * @param game The current [KombiGame] instance.
     */
    override fun refreshAfterStartGame(game: KombiGame) {
        refreshGameState(game)
    }

    /**
     * Refreshes visual state after a card is selected.
     *
     * @param card The [KombiCard] that was clicked.
     */
    override fun refreshAfterCardSelected(card: KombiCard) {
        // optional visual feedback or validation logic
    }

    /**
     * Refreshes the GUI with updated game state.
     *
     * @param game The [KombiGame] to use for updates.
     */
    private fun refreshGameState(game: KombiGame) {
        val currentPlayer = game.players[game.currentPlayerIndex]
        val opponent = game.players[(game.currentPlayerIndex + 1) % 2]

        currentPlayerHandView.clear()
        currentPlayer.hand.forEach { card ->
            currentPlayerHandView.add(card, cardImageLoader.frontImageFor(card))
        }

        opponentHandView.clear()
        repeat(opponent.hand.size) {
            opponentHandView.add(KombiCard.DUMMY, cardImageLoader.backImage())
        }

        exchangePileView.clear()
        game.exchangePile.forEach {
            exchangePileView.add(it, cardImageLoader.frontImageFor(it))
        }

        discardPileView.clear()
        game.discardPile.forEach { combo ->
            combo.forEach { card ->
                discardPileView.add(card, cardImageLoader.frontImageFor(card))
            }
        }

        drawPileCountLabel.text = "Draw pile: ${game.drawPile.size}"
    }

    /**
     * Refreshes the discard pile after a combination is played.
     *
     * @param player The player who played the combination.
     * @param cards The cards that were played.
     */
    override fun refreshAfterCombinationPlayed(player: entity.KombiPlayer, cards: List<KombiCard>) {
        rootService.currentGame?.let { refreshGameState(it) }
    }

    /**
     * Refresh the whole board after a new turn.
     *
     * @param game The [KombiGame] instance.
     */
    override fun refreshAfterStartTurn(game: KombiGame) {
        refreshGameState(game)
    }

    /**
     * Triggered at the end of a turn.
     *
     * @param game The [KombiGame] state after ending the turn.
     */
    override fun refreshAfterEndTurn(game: KombiGame) {
        refreshGameState(game)
    }

    /**
     * When the game ends, transition to ResultScene.
     *
     * @param winner The winning [KombiPlayer].
     * @param loser The losing [KombiPlayer].
     */
    override fun refreshAfterGameEnd(winner: entity.KombiPlayer, loser: entity.KombiPlayer) {
        rootService.currentGame = null
        rootService.refreshables.forEach {
            if (it is MenuScene) {
                rootService.currentMenuScene = it
            }
        }
    }
}