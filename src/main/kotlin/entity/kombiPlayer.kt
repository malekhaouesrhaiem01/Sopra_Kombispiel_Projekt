package entity
/**
 * Represents a player in the Kombi card game.
 *
 * Stores the player's name, hand, discard pile, score, and pass status.
 * Provides methods to draw cards, play combinations, pass turns, and reset state.
 */
class KombiPlayer(val name: String,
                   val handCards: MutableList<KombiCard> = mutableListOf(),
                   val discardPile: MutableList<List<KombiCard>> = mutableListOf(),
                   var points: Int = 0,
                   var hasPassed: Boolean = false
) {
    /**
     * Draws a card into the player's hand, if they have fewer than 10 cards.
     *
     * @param card The card to add to the player's hand.
     */
    fun drawCard(card: KombiCard) {
        if (handCards.size < 10) handCards.add(card)
    }
    /**
     * Attempts to play a combination of cards from the player's hand.
     * The combination is moved to the discard pile if all cards are in hand.
     *
     * @param cards The list of cards to play as a combination.
     * @return `true` if the combination was successfully played, otherwise `false`.
     */
    fun playCombination(cards: List<KombiCard>): Boolean {
        return if (handCards.containsAll(cards)) {
            discardPile.add(cards)
            handCards.removeAll(cards)
            true
        } else false
    }
    /**
     * Marks this player as having passed their turn.
     */
    fun passTurn() {
        hasPassed = true
    }
    /**
     * Checks whether this player has the same name as another player.
     *
     * @param other Another player to compare with.
     * @return `true` if both players have the same name, otherwise `false`.
     */
    fun hasSameNameAs(other: KombiPlayer): Boolean = this.name == other.name



    /**
     * Resets the player's state for a new game.
     *
     * This method:
     * - Clears all hand cards
     * - Clears all played combinations from the discard pile
     * - Resets the points to 0
     * - Sets the pass status to false
     */
    fun reset() {
        handCards.clear()
        discardPile.clear()
        points = 0
        hasPassed = false
    }

}
