package entity

import kotlin.test.*

/**
 * Test class for ExchangeArea.
 * Tests card swapping functionality and ensures state consistency.
 */
class ExchangeAreaTest {

    private lateinit var exchangeArea: ExchangeArea
    private val card1 = KombiCard(CardSuit.HEARTS, CardValue.ACE)
    private val card2 = KombiCard(CardSuit.SPADES, CardValue.KING)
    private val card3 = KombiCard(CardSuit.CLUBS, CardValue.QUEEN)
    private val initialCards = listOf(card1, card2, card3)

    private val playerCard = KombiCard(CardSuit.DIAMONDS, CardValue.TEN)
    private val unknownCard = KombiCard(CardSuit.HEARTS, CardValue.TWO)

    /**
     * Initializes the ExchangeArea with 3 cards before each test.
     */
    @BeforeTest
    fun setUp() {
        exchangeArea = ExchangeArea(initialCards)
    }

    /**
     * Tests that a successful exchange replaces an existing card with the player's card.
     */
    @Test
    fun testExchangeSuccess() {
        val result = exchangeArea.exchange(playerCard, card1)

        assertTrue(result, "The exchange should be successful.")
        assertTrue(exchangeArea.cards.contains(playerCard), "The player's card should be added.")
        assertFalse(exchangeArea.cards.contains(card1), "The old card should be removed.")
        assertEquals(3, exchangeArea.cards.size, "Exchange area should still contain 3 cards.")
    }

    /**
     * Tests that an exchange fails if the selected area card does not exist.
     */
    @Test
    fun testExchangeFailure() {
        val result = exchangeArea.exchange(playerCard, unknownCard)

        assertFalse(result, "The exchange should fail if the card is not in the area.")
        assertFalse(exchangeArea.cards.contains(playerCard), "The player's card should not be added.")
        assertEquals(3, exchangeArea.cards.size, "Exchange area should still contain 3 cards.")
    }

    /**
     * Verifies that the number of cards in the exchange area always remains three.
     */
    @Test
    fun testExchangeAreaCardCount() {
        exchangeArea.exchange(playerCard, card2)
        assertEquals(3, exchangeArea.cards.size, "Exchange area should always contain exactly 3 cards.")
    }
}
