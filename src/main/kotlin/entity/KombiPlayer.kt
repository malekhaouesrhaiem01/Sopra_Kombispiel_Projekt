package entity

/**
 * Represents a player in the Kombi-Duel game.
 *
 * Stores all player-related data including name, hand cards, discard pile,
 * current score, whether the player has passed this turn, and the first action performed in the current turn.
 *
 * @property name the player's name
 * @property hand the cards currently in the player's hand
 * @property discardPile the pile where valid played combinations are placed
 * @property score the player's current score
 * @property performedActions keeps track of the action performed in the current turn
 */
data class KombiPlayer(
    val name: String,
    val hand: MutableList<KombiCard> = mutableListOf(),
    val discardPile: MutableList<KombiCard> = mutableListOf(),
    var score: Int = 0,
    val performedActions: MutableList<Action> = mutableListOf()
)

