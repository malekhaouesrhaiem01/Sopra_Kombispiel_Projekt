package service

import entity.*

/**
 * Handles game-level operations such as starting the game, managing turns, and ending the game.
 * Game rules are enforced here. It interacts with the [KombiGame] entity and updates the UI through [Refreshable].
 *
 * @property rootService The [RootService] providing access to the current game state and other services.
 */
class GameService(
    private val rootService: RootService
) : AbstractRefreshingService() {

    /**
     * Starts a new Kombi-Duel game with the given player names.
     */
    fun startGame(player1Name: String, player2Name: String) {
        require(player1Name.isNotBlank() && player2Name.isNotBlank()) {
            "Player names must not be blank."
        }
        require(player1Name != player2Name) {
            "Player names must be different."
        }

        val player1 = KombiPlayer(name = player1Name)
        val player2 = KombiPlayer(name = player2Name)

        val fullDeck = createFullDeck()
        val handCardsP1 = fullDeck.take(7)
        val handCardsP2 = fullDeck.drop(7).take(7)
        val exchangeCards = fullDeck.drop(14).take(3)
        val drawPile = fullDeck.drop(17).toMutableList()

        player1.hand.addAll(handCardsP1)
        player2.hand.addAll(handCardsP2)

        val newGame = KombiGame(
            players = listOf(player1, player2),
            drawPile = drawPile,
            exchangeArea = exchangeCards.toMutableList(),
            currentPlayerIndex = 0
        )

        rootService.currentGame = newGame
        onAllRefreshables { refreshAfterStart(newGame.players) }
    }

    /**
     * Prepares the current player's turn.
     */
    fun startTurn() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game is active.")
        val activePlayer = game.players[game.currentPlayerIndex]
        onAllRefreshables { refreshAfterTurnStart(activePlayer) }
    }

    /**
     * Ends the current player's turn and switches to the next player.
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
        nextPlayer.performedActions.clear()
        onAllRefreshables { refreshAfterTurnEnd(pastPlayer) }
    }

    /**
     * Ends the game and notifies refreshables of the result.
     */
    fun endGame() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game is currently active.")
        val player1 = game.players[0]
        val player2 = game.players[1]

        val winner: KombiPlayer?
        val loser: KombiPlayer?
        val message: String

        when {
            player1.score > player2.score -> {
                winner = player1
                loser = player2
                message = "${player1.name} wins!"
            }
            player2.score > player1.score -> {
                winner = player2
                loser = player1
                message = "${player2.name} wins!"
            }
            else -> {
                rootService.currentGame = null
                onAllRefreshables {
                    refreshAfterGameEnd(player1, player2)
                    showMessage("It's a tie!")
                }
                return
            }
        }

        rootService.currentGame = null
        onAllRefreshables {
            refreshAfterGameEnd(winner, loser)
            showMessage(message)
        }
    }

    /**
     * Creates a full 52-card deck, one of each suit and value.
     */
    private fun createFullDeck(): List<KombiCard> {
        val deck = mutableListOf<KombiCard>()
        for (suit in CardSuit.entries) {
            for (value in CardValue.entries) {
                deck.add(KombiCard(suit, value))
            }
        }
        return deck.shuffled()
    }
}
