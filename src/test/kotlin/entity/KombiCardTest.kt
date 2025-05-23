package entity

import kotlin.test.*

/**
 * Unit tests for [KombiCard].
 */
class KombiCardTest {

    private val card1 = KombiCard(CardSuit.SPADES, CardValue.ACE)
    private val card2 = KombiCard(CardSuit.SPADES, CardValue.ACE)
    private val card3 = KombiCard(CardSuit.HEARTS, CardValue.ACE)
    private val card4 = KombiCard(CardSuit.SPADES, CardValue.TWO)

    /**
     * Tests that two cards with the same suit and value are equal.
     */
    @Test
    fun testCardEquality() {
        assertEquals(card1, card2, "Cards with same suit and value should be equal.")
    }

    /**
     * Tests that cards with different suits or values are not equal.
     */
    @Test
    fun testCardInequality() {
        assertNotEquals(card1, card3, "Cards with different suits should not be equal.")
        assertNotEquals(card1, card4, "Cards with different values should not be equal.")
    }

    /**
     * Tests the string representation of the card.
     */
    @Test
    fun testCardToString() {
        val card = KombiCard(CardSuit.HEARTS, CardValue.SEVEN)
        assertEquals("7♥", card.toString(), "String representation of card is incorrect.")
    }
    /**
     * Tests the [KombiCard.toString] method to ensure it returns
     * the correct string format combining card value and suit.
     */
    @Test
    fun testToStringReturnsCorrectFormat() {
        val card = KombiCard(CardSuit.SPADES, CardValue.ACE)
        assertEquals("A♠", card.toString())
    }

    /**
     * Tests that [CardValue.toString] returns the correct string representation
     * for each card value, matching typical card notation.
     */
    @Test
    fun testCardValueToStringFormat() {
        assertEquals("2", CardValue.TWO.toString())
        assertEquals("3", CardValue.THREE.toString())
        assertEquals("4", CardValue.FOUR.toString())
        assertEquals("5", CardValue.FIVE.toString())
        assertEquals("6", CardValue.SIX.toString())
        assertEquals("7", CardValue.SEVEN.toString())
        assertEquals("8", CardValue.EIGHT.toString())
        assertEquals("9", CardValue.NINE.toString())
        assertEquals("10", CardValue.TEN.toString())
        assertEquals("J", CardValue.JACK.toString())
        assertEquals("Q", CardValue.QUEEN.toString())
        assertEquals("K", CardValue.KING.toString())
        assertEquals("A", CardValue.ACE.toString())
    }

    /**
     * Tests that [CardSuit.toString] returns the correct Unicode symbol
     * for each card suit.
     *
     * This ensures that card suits are correctly represented in UI or logs.
     */
    @Test
    fun testCardSuitToStringFormat() {
        assertEquals("♣", CardSuit.CLUBS.toString())
        assertEquals("♠", CardSuit.SPADES.toString())
        assertEquals("♥", CardSuit.HEARTS.toString())
        assertEquals("♦", CardSuit.DIAMONDS.toString())
    }
}

