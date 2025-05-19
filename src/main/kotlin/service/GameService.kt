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
     * Initializes hands, exchange area, draw pile, and sets the first player.
     * Also triggers a refresh of the UI for game start.
     *
     * @param player1Name Name of the first player (must not be blank or equal to player2Name)
     * @param player2Name Name of the second player
     * @throws IllegalArgumentException if names are blank or identical
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
     * Ensures the game is active and triggers the UI update for the start of the turn.
     *
     * @throws IllegalStateException if no game is active
     */
    fun startTurn() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game is active.")
        val activePlayer = game.players[game.currentPlayerIndex]
        onAllRefreshables { refreshAfterTurnStart(activePlayer) }
    }

    /**
     * Ends the current player's turn.
     * If both players have passed consecutively, the game ends.
     * Otherwise, switches to the next player and resets their performed actions.
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
        nextPlayer.performedActions.clear()

        onAllRefreshables { refreshAfterTurnEnd(pastPlayer) }
    }

    /**
     * Ends the current Kombi-Duel game.
     * Determines the winner based on player scores and notifies the UI.
     *
     * @throws IllegalStateException if no game is currently active
     */
    fun endGame() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game is currently active.")
        val player1 = game.players[0]
        val player2 = game.players[1]

        val winner: KombiPlayer
        val loser: KombiPlayer
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
        rootService.viewSwitcher?.showResultScene(winner, loser)


        onAllRefreshables {
            refreshAfterGameEnd(winner, loser)
            showMessage(message)
        }
    }

    /**
     * Generates and returns a full shuffled deck of 52 unique KombiCards.
     *
     * @return A list of shuffled [KombiCard]s
     */
    private fun createFullDeck(): List<KombiCard> {
        val deck = mutableListOf<KombiCard>()
        for (suit in CardSuit.entries) {
            for (value in CardValue.entries) {
                deck.add(KombiCard(suit, value))
            }
        }
        return deck.shuffled().toMutableList()
    }
}
