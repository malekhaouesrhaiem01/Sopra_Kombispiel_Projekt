package entity
/**
 * Represents the draw pile from which players can draw cards.
 *
 * Cards are stored internally in a shuffled stack (LIFO order).
 */

class DrawPile(cards: List<KombiCard>) {
    private val stack: ArrayDeque<KombiCard> = ArrayDeque(cards.shuffled())
    /**
     * Draws a card from the draw pile.
     *
     * @return the drawn card, or null if the pile is empty.
     */
    fun draw(): KombiCard? = if (stack.isNotEmpty()) stack.removeFirst() else null
    /**
     * Checks if the draw pile is empty.
     *
     * @return true if the pile is empty, false otherwise.
     */
    fun isEmpty(): Boolean = stack.isEmpty()
    /**
     * Returns the number of remaining cards in the draw pile.
     *
     * @return the count of cards left in the pile.
     */
    fun remainingCards(): Int = stack.size

    /**
     * Shuffles the draw pile.
     *
     * This method randomizes the order of cards in the draw pile(useful when the game is restarted ).
     */

    fun shuffle() {
        val shuffled = stack.shuffled()
        stack.clear()
        stack.addAll(shuffled)
    }
    /**
     * Returns the current list of remaining cards in the draw pile.
     *
     * This method provides a snapshot of the cards still available,
     * without modifying the actual pile.
     *
     * @return A list of KombiCards currently in the draw pile.
     */
    fun remaining(): List<KombiCard> = stack.toList()

}
