package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test cases for [KombiGame].
 *
 * Verifies that player list, draw pile, and exchange area are initialized correctly,
 * and that the current player index behaves as expected.
 */
class KombiGameTest {

    private val player1 = KombiPlayer("Alice")
    private val player2 = KombiPlayer("Bob")
    private val deck = mutableListOf(
        KombiCard(CardSuit.CLUBS, CardValue.TWO),
        KombiCard(CardSuit.HEARTS, CardValue.ACE)
    )
    private val exchange = mutableListOf(
        KombiCard(CardSuit.SPADES, CardValue.FIVE),
        KombiCard(CardSuit.HEARTS, CardValue.SIX),
        KombiCard(CardSuit.DIAMONDS, CardValue.SEVEN)
    )

    private val game = KombiGame(
        players = listOf(player1, player2),
        drawPile = deck,
        exchangeArea = exchange,
        currentPlayerIndex = 0
    )

    /**
     * Tests whether the players are initialized and accessible by index.
     */
    @Test
    fun testPlayerAccess() {
        assertEquals(2, game.players.size, "There should be exactly two players.")
        assertSame(player1, game.players[0], "First player should be Alice.")
        assertSame(player2, game.players[1], "Second player should be Bob.")
    }

    /**
     * Tests that the draw pile is stored correctly.
     */
    @Test
    fun testDrawPileInitialization() {
        assertEquals(2, game.drawPile.size, "Draw pile should contain 2 cards.")
    }

    /**
     * Tests that the exchange area is stored correctly.
     */
    @Test
    fun testExchangeAreaInitialization() {
        assertEquals(3, game.exchangeArea.size, "Exchange area should contain 3 cards.")
    }

    /**
     * Tests that the currentPlayerIndex is set correctly.
     */
    @Test
    fun testCurrentPlayerIndex() {
        assertEquals(0, game.currentPlayerIndex, "Initial active player should be at index 0.")
    }
}
