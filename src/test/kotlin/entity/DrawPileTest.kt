package entity

import kotlin.test.*

/**
 * Test class for DrawPile.
 * Tests the functionality of drawing cards, checking emptiness, remaining cards, and shuffling.
 */
class DrawPileTest {

    private lateinit var drawPile: DrawPile
    private val cards = listOf(
        KombiCard(CardSuit.HEARTS, CardValue.ACE),
        KombiCard(CardSuit.SPADES, CardValue.KING),
        KombiCard(CardSuit.CLUBS, CardValue.QUEEN)
    )

    /**
     * Sets up the test environment by initializing the DrawPile with a list of cards.
     */
    @BeforeTest
    fun setUp() {
        drawPile = DrawPile(cards.toList()) // Make sure it's a copy
    }

    /**
     * Tests the draw method to ensure a card is drawn and the pile is updated correctly.
     */
    @Test
    fun testDraw() {
        val drawnCard = drawPile.draw()
        assertNotNull(drawnCard, "A card should be drawn from the pile.")
        assertEquals(2, drawPile.remainingCards(), "The pile should have one less card after drawing.")
    }

    /**
     * Tests the isEmpty method to ensure it correctly identifies an empty pile.
     */
    @Test
    fun testIsEmpty() {
        repeat(cards.size) { drawPile.draw() }
        assertTrue(drawPile.isEmpty(), "The pile should be empty after all cards are drawn.")
    }

    /**
     * Tests the remainingCards method to ensure it returns the correct count of cards.
     */
    @Test
    fun testRemainingCards() {
        assertEquals(3, drawPile.remainingCards(), "The pile should initially have 3 cards.")
        drawPile.draw()
        assertEquals(2, drawPile.remainingCards(), "The pile should have 2 cards after one draw.")
    }

    /**
     * Tests the shuffle method to ensure the pile is shuffled and cards are not lost.
     */
    @Test
    fun testShuffle() {
        val beforeShuffle = drawPile.remaining().toList()
        drawPile.shuffle()
        val afterShuffle = drawPile.remaining().toList()

        assertEquals(3, drawPile.remainingCards(), "Card count should stay the same after shuffle.")
        assertNotEquals(beforeShuffle, afterShuffle, "Card order should change after shuffle.")
    }
}
