package entity



/**
 * Represents a player in the Kombi-Duell game.
 *
 * Tracks the player’s hand, points, played combinations, and whether they passed.
 */
data class KombiPlayer(
    val name: String,
    val handCards: MutableList<KombiCard> = mutableListOf(),
    val discardPile: MutableList<List<KombiCard>> = mutableListOf(),
    var points: Int = 0,
    var hasPassed: Boolean = false,
    var lastAction: Action = Action.NOACTION
) {
    /**
     * Adds a card to the player's hand if the hand has fewer than 10 cards.
     */
    fun drawCard(card: KombiCard) {
        if (handCards.size < 10) handCards.add(card)
    }

    /**
     * Plays a combination of cards, updating points and lastAction if valid.
     *
     * @param cards The cards to play
     * @param actionType The type of combination being played
     * @return True if successful, false if cards are not in hand
     */
    fun playCombination(cards: List<KombiCard>, actionType: Action): Boolean {
        return if (handCards.containsAll(cards)) {
            discardPile.add(cards)
            handCards.removeAll(cards)
            points += actionType.calculatePoints(cards.size)
            lastAction = actionType
            true
        } else false
    }

    /**
     * Marks the player as having passed their turn.
     */
    fun passTurn() {
        hasPassed = true
    }

    /**
     * Resets the player's state for a new game.
     */
    fun reset() {
        handCards.clear()
        discardPile.clear()
        points = 0
        hasPassed = false
        lastAction = Action.NOACTION
    }

    /**
     * Checks if this player has the same name as another player.
     */
    fun hasSameNameAs(other: KombiPlayer): Boolean = this.name == other.name
}

