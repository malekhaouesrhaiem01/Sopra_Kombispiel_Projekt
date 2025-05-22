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
}

