package entity

/**
 * Represents the specific types of actions a player can perform in a turn.
 *
 * This enum is used in the `performedActions` list of [KombiPlayer] to track
 * what the player has done in the current turn. It ensures that:
 * - No action is repeated
 * - No more than two actions are performed
 * - No action is allowed after [PASS]
 *
 * @property DRAW_CARD the player draws one card from the draw pile
 * @property EXCHANGE_CARD the player swaps a hand card with one from the exchange area
 * @property PLAY_SEQUENCE the player plays a valid sequence of cards (same suit, ordered)
 * @property PLAY_TRIPLE the player plays three cards of the same value
 * @property PLAY_QUADRUPLE the player plays four cards of the same value
 * @property PASS the player voluntarily skips the turn; no further actions allowed
 */
enum class Action {
    DRAW_CARD,
    EXCHANGE_CARD,
    PLAY_SEQUENCE,
    PLAY_TRIPLE,
    PLAY_QUADRUPLE,
    PASS
}

