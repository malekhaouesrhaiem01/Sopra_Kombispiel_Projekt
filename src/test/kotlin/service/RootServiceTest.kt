package service

import entity.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.BeforeTest
import kotlin.test.Test


/**
 * Unit tests for the [RootService] class.
 *
 * These tests verify the correct instantiation of service components
 * and validate the default and manual assignment behavior of the game state.
 */
class RootServiceTest {

    /**
     * The root service to be initialized before each test.
     */
    private lateinit var rootService: RootService

    /**
     * Sets up a fresh [RootService] instance before every test case.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
    }

    /**
     * Tests whether the [RootService] and its sub-services [GameService] and [PlayerActionService]
     * are correctly initialized without throwing any exceptions.
     */
    @Test
    fun testServiceInitialization() {
        assertNotNull(rootService.gameService, "gameService should be initialized.")
        assertNotNull(rootService.playerActionService, "playerActionService should be initialized.")
    }

    /**
     * Verifies that [//currentGame] is initially null before any game has been started or assigned.
     */
    @Test
    fun testCurrentGameInitiallyNull() {
        assertNull(rootService.currentGame, "currentGame should be null initially.")
    }

    /**
     * Tests manual assignment of a [KombiGame] to [RootService.currentGame]
     * and verifies that the assignment persists correctly.
     */
    @Test
    fun testAssignGameToRootService() {
        val players = listOf(
            KombiPlayer("Alice"),
            KombiPlayer("Bob")
        )
        val drawPile = mutableListOf<KombiCard>()
        val exchangeArea = mutableListOf<KombiCard>()
        val game = KombiGame(players, drawPile, exchangeArea)
        rootService.currentGame = game

        assertSame(game, rootService.currentGame, "Assigned game should match currentGame.")
        assertEquals(2, rootService.currentGame?.players?.size, "Game should have 2 players.")
    }
    /**
     * Tests that multiple refreshables can be added using addRefreshables().
     */
    @Test
    fun testAddMultipleRefreshables() {
        val r1 = object : Refreshable {}
        val r2 = object : Refreshable {}

        // Should not throw
        assertDoesNotThrow {
            rootService.addRefreshables(r1, r2)
        }
    }
}
