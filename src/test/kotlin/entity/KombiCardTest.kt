package entity

import kotlin.test.*

/**
 * Test class for KombiCard.
 * Tests creation, equality, inequality, and string representation.
 */
class KombiCardTest {

    /**
     * Tests the creation of a KombiCard and its properties.
     */
    @Test
    fun testCardCreation() {
        val card = KombiCard(CardSuit.HEARTS, CardValue.ACE)
        assertEquals(CardSuit.HEARTS, card.suit, "The card suit should be HEARTS.")
        assertEquals(CardValue.ACE, card.value, "The card value should be ACE.")
    }

    /**
     * Tests the equality of two KombiCards with the same suit and value.
     */
    @Test
    fun testCardEquality() {
        val card1 = KombiCard(CardSuit.SPADES, CardValue.KING)
        val card2 = KombiCard(CardSuit.SPADES, CardValue.KING)
        assertEquals(card1, card2, "Cards with the same suit and value should be equal.")
    }

    /**
     * Tests the inequality of two KombiCards with different suits or values.
     */
    @Test
    fun testCardInequality() {
        val card1 = KombiCard(CardSuit.CLUBS, CardValue.QUEEN)
        val card2 = KombiCard(CardSuit.DIAMONDS, CardValue.QUEEN)
        val card3 = KombiCard(CardSuit.CLUBS, CardValue.JACK)

        assertNotEquals(card1, card2, "Cards with different suits should not be equal.")
        assertNotEquals(card1, card3, "Cards with different values should not be equal.")
    }

    /**
     * Tests the string representation of a KombiCard.
     * For example: "♣10", "♥Q", "♠A"
     */
    @Test
    fun testCardToString() {
        val card1 = KombiCard(CardSuit.HEARTS, CardValue.ACE)
        val card2 = KombiCard(CardSuit.CLUBS, CardValue.TEN)
        val card3 = KombiCard(CardSuit.SPADES, CardValue.QUEEN)

        assertEquals("♥A", card1.toString())
        assertEquals("♣10", card2.toString())
        assertEquals("♠Q", card3.toString())
    }

    /**
     * Tests that the string representation of a card has the correct length.
     */
    @Test
    fun testCardToStringLength() {
        val card10 = KombiCard(CardSuit.CLUBS, CardValue.TEN)
        val cardA = KombiCard(CardSuit.HEARTS, CardValue.ACE)

        assertEquals(3, card10.toString().length, "10♣ should have length 3")
        assertEquals(2, cardA.toString().length, "A♥ should have length 2")
    }
}
