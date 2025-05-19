package entity

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Unit tests for the [KombiPlayer] data class.
 * Ensures correct initialization, data manipulation, and internal state tracking.
 */
class KombiPlayerTest {

    /**
     * Tests the default values and initial state of a newly created player.
     */
    @Test
    fun testInitialState() {
        val player = KombiPlayer("TestPlayer")

        assertEquals("TestPlayer", player.name)
        assertTrue(player.hand.isEmpty())
        assertTrue(player.discardPile.isEmpty())
        assertEquals(0, player.score)
        assertTrue(player.performedActions.isEmpty())
    }

    /**
     * Tests that hand and discard pile accept cards.
     */
    @Test
    fun testHandAndDiscardPileModification() {
        val player = KombiPlayer("TestPlayer")
        val card1 = KombiCard(CardSuit.HEARTS, CardValue.TEN)
        val card2 = KombiCard(CardSuit.SPADES, CardValue.ACE)

        player.hand.add(card1)
        player.discardPile.add(card2)

        assertEquals(1, player.hand.size)
        assertEquals(card1, player.hand[0])

        assertEquals(1, player.discardPile.size)
        assertEquals(card2, player.discardPile[0])
    }

    /**
     * Tests score tracking and mutation.
     */
    @Test
    fun testScoreUpdate() {
        val player = KombiPlayer("TestPlayer")
        assertEquals(0, player.score)
        player.score += 15
        assertEquals(15, player.score)
    }

    /**
     * Tests that actions can be added to performedActions and verified.
     */
    @Test
    fun testPerformedActions() {
        val player = KombiPlayer("TestPlayer")
        assertTrue(player.performedActions.isEmpty())

        player.performedActions.add(Action.DRAW_CARD)
        player.performedActions.add(Action.EXCHANGE_CARD)

        assertEquals(2, player.performedActions.size)
        assertTrue(Action.DRAW_CARD in player.performedActions)
        assertTrue(Action.EXCHANGE_CARD in player.performedActions)
        assertFalse(Action.PASS in player.performedActions)
    }
}
