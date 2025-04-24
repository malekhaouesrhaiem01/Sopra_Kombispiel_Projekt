package entity

/**
 * Represents the types of card combinations a player can play in Kombi-Duell.
 *
 * - [TRIPLE]: A set of three cards with the same value (e.g., 7♠, 7♥, 7♦) → 10 points
 * - [QUADRUPLE]: A set of four cards with the same value (e.g., K♠, K♥, K♣, K♦) → 15 points
 * - [COMBINATION]: A sequence of three or more cards of the same suit (e.g., 5♥, 6♥, 7♥) → 2 points per card
 * - [NOACTION]: Indicates that the player hasn't played a combination yet (0 points)
 *
 * This enum is used to track the type of combo a player played and to calculate scoring accordingly.
 *
 * @property flatPoints Fixed points for TRIPLE or QUADRUPLE combinations
 * @property pointsPerCard Points per card used in COMBINATION
 */
enum class Action(val flatPoints: Int = 0, val pointsPerCard: Int = 0) {

    /**
     * Three cards of the same value.
     */
    TRIPLE(flatPoints = 10),

    /**
     * Four cards of the same value.
     */
    QUADRUPLE(flatPoints = 15),

    /**
     * A sequence of 3 or more cards of the same suit.
     */
    COMBINATION(pointsPerCard = 2),

    /**
     * Default state if no combo was played.
     */
    NOACTION();

    /**
     * Calculates the points awarded for this action.
     *
     * @param size The number of cards in the combination (used only for COMBINATION)
     * @return The total points earned
     */
    fun calculatePoints(size: Int = 0): Int {
        return if (this == COMBINATION) pointsPerCard * size else flatPoints
    }
}
