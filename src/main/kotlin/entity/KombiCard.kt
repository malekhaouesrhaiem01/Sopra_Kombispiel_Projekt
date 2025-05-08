package entity

/**
 * Represents a card in the Kombi-Duel game.
 *
 * Each card has a [CardSuit] (e.g. CLUBS, SPADES) and a [CardValue] (e.g. TWO, JACK).
 * Cards are immutable and used to form combinations such as triples, quadruples, and sequences.
 *
 * @property suit the suit of the card (♣, ♥, ♠, ♦)
 * @property value the value of the card (2–10, J, Q, K, A)
 */
data class KombiCard(
    val suit: CardSuit,
    val value: CardValue
) {
    /**
     * Returns a string representation of the card using its value and suit.
     * Example: "7♠", "A♦"
     */
    override fun toString(): String = "${value}${suit}"
}
