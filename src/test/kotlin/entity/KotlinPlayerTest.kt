package entity

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for the [KombiPlayer] class.
 * Verifies correct initialization, mutability, and action tracking behavior.
 */
class KombiPlayerTest {

    private lateinit var player: KombiPlayer

    /**
     * Initializes a fresh [KombiPlayer] before each test.
     */
    @BeforeEach
    fun setUp() {
        player = KombiPlayer("TestPlayer")
    }

    /**
     * Tests that the player's name is stored correctly.
     */
    @Test
    fun testPlayerNameIsSet() {
        assertEquals("TestPlayer", player.name)
    }

    /**
     * Tests that the player's hand and discard pile are initially empty.
     */
    @Test
    fun testInitialHandAndDiscardAreEmpty() {
        assertTrue(player.hand.isEmpty(), "Hand should be empty initially.")
        assertTrue(player.discardPile.isEmpty(), "Discard pile should be empty initially.")
    }

    /**
     * Tests that performedActions is initially empty and can be updated.
     */
    @Test
    fun testPerformedActionsTracking() {
        assertTrue(player.performedActions.isEmpty(), "No actions should be performed at start.")
        player.performedActions.add(Action.DRAW_CARD)
        assertEquals(1, player.performedActions.size)
        assertTrue(player.performedActions.contains(Action.DRAW_CARD))
    }

    /**
     * Tests that score is initialized to 0 and can be incremented.
     */
    @Test
    fun testScoreInitializationAndIncrement() {
        assertEquals(0, player.score)
        player.score += 10
        assertEquals(10, player.score)
    }

    /**
     * Tests that equalsByName returns true for players with same name.
     */
    @Test
    fun testHasSameNameAs() {
        val other = KombiPlayer("TestPlayer")
        assertTrue(player.hasSameNameAs(other))
    }

    /**
     * Tests that hasEmptyName returns true for blank names.
     */
    @Test
    fun testHasEmptyName() {
        val empty = KombiPlayer("")
        val blank = KombiPlayer("   ")
        assertTrue(empty.hasEmptyName())
        assertTrue(blank.hasEmptyName())
        assertFalse(player.hasEmptyName())
    }

    /**
     * Tests that the performedActions list can be cleared after endTurn.
     */
    @Test
    fun testClearPerformedActions() {
        player.performedActions.add(Action.EXCHANGE_CARD)
        player.performedActions.add(Action.DRAW_CARD)
        player.performedActions.clear()
        assertTrue(player.performedActions.isEmpty(), "Performed actions should be cleared.")
    }
}
