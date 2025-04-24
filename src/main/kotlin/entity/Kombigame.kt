package entity

class KombiGame(val players: List<KombiPlayer>,
                var activePlayerIndex: Int = 0,
                val drawPile: DrawPile,
                val exchangeArea: ExchangeArea,
                var turnActions: Int = 0,
                var gameOver: Boolean = false

) {
    /**
     * Returns the currently active player.
     *
     * @return The player whose turn it is.
     */
    fun getActivePlayer(): KombiPlayer = players[activePlayerIndex]

    /**
     * Advances to the next player's turn.
     * Resets turn-related flags such as action count and pass status.
     */
    fun nextPlayer() {
        activePlayerIndex = (activePlayerIndex + 1) % 2
        turnActions = 0
        players[activePlayerIndex].hasPassed = false
    }

    /**
     * Checks whether any of the game end conditions are met:
     * - A player has no more cards in hand
     * - All players have passed in succession
     *
     * @return `true` if the game should end, otherwise `false`
     */
    fun isGameOver(): Boolean {
        val over = players.any { it.handCards.isEmpty() } || players.all { it.hasPassed }
        gameOver = over
        return over
    }

    /**
     * Determines which of the two players has the higher score.
     *
     * @return A string message stating the winner and their points, or a tie message if points are equal.
     */
    fun calculateWinner(): String {
        val player1 = players[0]
        val player2 = players[1]

        return when {
            player1.points > player2.points -> "${player1.name} wins with ${player1.points} points"
            player2.points > player1.points -> "${player2.name} wins with ${player2.points} points"
            else -> "It's a tie! Both have ${player1.points} points"
        }
    }

}

