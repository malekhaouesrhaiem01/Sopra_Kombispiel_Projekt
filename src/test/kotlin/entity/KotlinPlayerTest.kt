package entity

import kotlin.test.*

/**
 * Test class for KombiPlayer.
 * Verifies card management, scoring, turn status, and reset logic.
 */
class KombiPlayerTest {

    private lateinit var player: KombiPlayer

    /**
     * Sets up a fresh player before each test.
     */
    @BeforeTest
    fun setUp() {
        player = KombiPlayer("TestPlayer")
    }

    /**
     * Tests that a card can be drawn when the hand size is valid.
     */
    @Test
    fun testDrawCard() {
        val card = KombiCard(CardSuit.HEARTS, CardValue.ACE)
        player.drawCard(card)
        assertEquals(1, player.handCards.size)
        assertTrue(player.handCards.contains(card))
    }

    /**
     * Tests that playCombination correctly updates the discard pile, removes cards, and adds points.
     */
    @Test
    fun testPlayCombinationWithAction() {
        val combo = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.FIVE),
            KombiCard(CardSuit.HEARTS, CardValue.SIX),
            KombiCard(CardSuit.HEARTS, CardValue.SEVEN)
        )
        player.handCards.addAll(combo)

        val result = player.playCombination(combo, Action.COMBINATION)

        assertTrue(result, "Combination should be played successfully.")
        assertTrue(player.discardPile.contains(combo), "Combination should be added to discard pile.")
        assertEquals(6, player.points, "COMBINATION should give 2 points per card.")
        assertEquals(Action.COMBINATION, player.lastAction, "Last action should be COMBINATION.")
        assertTrue(player.handCards.isEmpty(), "Played cards should be removed from hand.")
    }

    /**
     * Tests that passing a turn updates the hasPassed flag.
     */
    @Test
    fun testPassTurn() {
        player.passTurn()
        assertTrue(player.hasPassed, "Player should be marked as passed.")
    }

    /**
     * Tests that reset clears all relevant player state.
     */
    @Test
    fun testReset() {
        player.handCards.add(KombiCard(CardSuit.SPADES, CardValue.SEVEN))
        player.points = 10
        player.passTurn()
        player.discardPile.add(listOf(KombiCard(CardSuit.CLUBS, CardValue.THREE)))
        player.lastAction = Action.TRIPLE

        player.reset()

        assertTrue(player.handCards.isEmpty(), "Hand should be empty after reset.")
        assertTrue(player.discardPile.isEmpty(), "Discard pile should be empty after reset.")
        assertEquals(0, player.points, "Points should reset to 0.")
        assertFalse(player.hasPassed, "Player should not be marked as passed after reset.")
        assertEquals(Action.NOACTION, player.lastAction, "Last action should reset to NOACTION.")
    }

    /**
     * Tests that players with the same name are considered equal by name check.
     */
    @Test
    fun testHasSameNameAs() {
        val playerA1 = KombiPlayer("Alex")
        val playerA2 = KombiPlayer("Alex")
        val playerB = KombiPlayer("Blake")

        assertTrue(playerA1.hasSameNameAs(playerA2), "Players with the same name should match.")
        assertFalse(playerA1.hasSameNameAs(playerB), "Players with different names should not match.")
    }
}
