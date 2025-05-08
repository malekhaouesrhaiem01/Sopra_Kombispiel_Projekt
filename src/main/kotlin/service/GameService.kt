package service

import entity.*

/**
 * Handles game-level operations such as starting the game, managing turns, and ending the game.
 * Game rules are enforced here. It interacts with the [KombiGame] entity and updates the UI through [Refreshable].
 *
 * @property rootService The [RootService] providing access to current game state and other services
 */
class GameService(
    private val rootService: RootService
) : AbstractRefreshingService() {

    /**
     * Starts the game with the provided player names.
     * Initializes hand cards, draw pile, and exchange area.
     *
     * @throws IllegalArgumentException if names are empty, blank, or identical
     * @throws IllegalStateException if no game instance exists
     */
    fun startGame(player1Name: String, player2Name: String) {
        require(player1Name.isNotBlank() && player2Name.isNotBlank())
        require(player1Name != player2Name)

        val fullDeck = createFullDeck()
        val handCardsP1 = fullDeck.take(7).toMutableList()
        val handCardsP2 = fullDeck.drop(7).take(7).toMutableList()
        val exchangeCards = fullDeck.drop(14).take(3).toMutableList()
        val remainingDrawPile = fullDeck.drop(17).toMutableList()

        val game = rootService.currentGame ?: throw IllegalStateException("No game instance.")

        game.players[0].hand.addAll(handCardsP1)
        game.players[1].hand.addAll(handCardsP2)
        game.exchangeArea.addAll(exchangeCards)
        game.drawPile.addAll(remainingDrawPile)

        onAllRefreshables { refreshAfterStart() }
    }

    /**
     * Prepares the current player's turn by resetting their temporary state.
     * Triggers the UI to update for the new active player.
     *
     * @throws IllegalStateException if no game is active
     */
    fun startTurn() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game is active.")

        val activePlayer = game.players[game.currentPlayerIndex]
        activePlayer.performedActions.clear()

        onAllRefreshables { refreshAfterTurnStart() }
    }

    /**
     * Ends the current player's turn. If both players have passed, ends the game.
     * Otherwise, switches to the next player and resets their performed actions list.
     *
     * @throws IllegalStateException if no game is active
     */
    fun endTurn() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game is active.")

        val pastIndex = game.currentPlayerIndex
        val pastPlayer = game.players[pastIndex]

        val nextIndex = (pastIndex + 1) % 2
        val nextPlayer = game.players[nextIndex]

        if (Action.PASS in pastPlayer.performedActions && Action.PASS in nextPlayer.performedActions) {
            endGame()
            return
        }

        game.currentPlayerIndex = nextIndex
        game.players[nextIndex].performedActions.clear()

        onAllRefreshables { refreshAfterTurnEnd() }
    }

    /**
     * Ends the current Kombi-Duel game.
     *
     * Determines the winner based on player scores, prints results,
     * and triggers final UI update.
     *
     * @throws IllegalStateException if no game is active
     */
    fun endGame() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game is active.")

        val player1 = game.players[0]
        val player2 = game.players[1]

        val winner = when {
            player1.score > player2.score -> player1.name
            player2.score > player1.score -> player2.name
            else -> "tie"
        }

        println("Game Over! Winner: $winner")
        println("${player1.name}: ${player1.score} Points")
        println("${player2.name}: ${player2.score} Points")

        onAllRefreshables { refreshAfterGameEnd() }
    }

    /**
     * Generates and returns a full shuffled deck of 52 unique KombiCards.
     */
    private fun createFullDeck(): List<KombiCard> {
        val deck = mutableListOf<KombiCard>()
        for (suit in CardSuit.values()) {
            for (value in CardValue.values()) {
                deck.add(KombiCard(suit, value))
            }
        }
        deck.shuffle()
        return deck
    }
}
