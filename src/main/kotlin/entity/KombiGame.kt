package entity

/**
 * Represents the full game state of a Kombi-Duel match.
 *
 * This entity class stores the players, draw pile, and exchange area.
 * Game logic (e.g., switching players, checking combinations) should be handled in the service layer.
 *
 * @property players A list of exactly two players (index 0 and 1)
 * @property drawPile The face-down stack of cards to draw from
 * @property exchangeArea The pool of 3 cards used for swapping
 * @property currentPlayerIndex Index of the currently active player
 */
data class KombiGame(
    val players: List<KombiPlayer>,
    val drawPile: MutableList<KombiCard>,
    val exchangeArea: MutableList<KombiCard>,
    var currentPlayerIndex: Int = 0
)
