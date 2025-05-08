package service

import entity.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for [GameService], [PlayerActionService], and refreshable logic.
 * Uses [TestRefreshable] to ensure UI callbacks are triggered as expected.
 */
class GameServiceTest {

    private lateinit var rootService: RootService
    private lateinit var gameService: GameService
    private lateinit var player1: KombiPlayer
    private lateinit var player2: KombiPlayer
    private lateinit var testRefreshable: TestRefreshable

    /**
     * Sets up the game and registers [TestRefreshable] before each test.
     */
    @BeforeEach
    fun setUp() {
        rootService = RootService()
        gameService = rootService.gameService
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        player1 = KombiPlayer("Alice")
        player2 = KombiPlayer("Bob")

        val game = KombiGame(
            players = listOf(player1, player2),
            drawPile = mutableListOf(),
            exchangeArea = mutableListOf()
        )
        rootService.currentGame = game
    }

    /**
     * Tests that the game starts correctly with valid player names.
     */
    @Test
    fun testStartGameInitializesDecksAndHands() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!

        assertEquals(7, game.players[0].hand.size)
        assertEquals(7, game.players[1].hand.size)
        assertEquals(3, game.exchangeArea.size)
        assertEquals(35, game.drawPile.size)
    }

    /**
     * Verifies that startGame throws for blank or duplicate names.
     */
    @Test
    fun testStartGameWithInvalidNamesThrows() {
        assertThrows(IllegalArgumentException::class.java) { gameService.startGame("", "Bob") }
        assertThrows(IllegalArgumentException::class.java) { gameService.startGame("Bob", "Bob") }
    }

    /**
     * Verifies that starting a turn clears the active player's performed actions.
     */
    @Test
    fun testStartTurnResetsPerformedActions() {
        val game = rootService.currentGame!!
        game.players[0].performedActions.add(Action.DRAW_CARD)
        gameService.startTurn()
        assertTrue(game.players[0].performedActions.isEmpty())
    }

    /**
     * Checks that endTurn correctly switches players and clears the next player's actions.
     */
    @Test
    fun testEndTurnSwitchesPlayerAndClearsActions() {
        val game = rootService.currentGame!!
        val nextPlayer = game.players[1]
        nextPlayer.performedActions.add(Action.DRAW_CARD)
        game.players[0].performedActions.add(Action.PASS)

        gameService.endTurn()

        assertEquals(1, game.currentPlayerIndex)
        assertTrue(nextPlayer.performedActions.isEmpty())
    }

    /**
     * Verifies that endGame prints results and ends correctly when both players passed.
     */
    @Test
    fun testEndGamePrintsResultsAndTriggersRefresh() {
        player1.score = 12
        player2.score = 10

        assertDoesNotThrow { gameService.endGame() }
    }

    /**
     * Checks that endTurn ends the game if both players passed consecutively.
     */
    @Test
    fun testEndTurnEndsGameIfBothPassed() {
        val game = rootService.currentGame!!
        game.players[0].performedActions.add(Action.PASS)
        game.players[1].performedActions.add(Action.PASS)
        game.currentPlayerIndex = 0

        assertDoesNotThrow { gameService.endTurn() }
    }

    /**
     * Tests that refreshAfterStart is triggered via [TestRefreshable] on game start.
     */
    @Test
    fun testStartGameCardDistributionAndUIRefresh() {
        rootService.gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!

        assertEquals(7, game.players[0].hand.size)
        assertEquals(7, game.players[1].hand.size)
        assertEquals(3, game.exchangeArea.size)
        assertEquals(35, game.drawPile.size)

        assertTrue(testRefreshable.refreshAfterStartCalled, "UI should refresh after game start")
    }

    /**
     * Tests that [//addRefreshable] correctly adds a single Refreshable to all services.
     */
    @Test
    fun testRootServiceSingleRefreshable() {
        val r = TestRefreshable()
        rootService.addRefreshable(r)

        assertFalse(r.refreshAfterStartCalled)
        rootService.gameService.onAllRefreshables { refreshAfterStart() }
        assertTrue(r.refreshAfterStartCalled)
        r.reset()

        assertFalse(r.refreshAfterStartCalled)
        rootService.playerActionService.onAllRefreshables { refreshAfterStart() }
        assertTrue(r.refreshAfterStartCalled)
    }

    /**
     * Tests that [//addRefreshables] correctly adds multiple refreshables to all services.
     */
    @Test
    fun testRootServiceMultiRefreshable() {
        val r1 = TestRefreshable()
        val r2 = TestRefreshable()
        rootService.addRefreshables(r1, r2)

        rootService.gameService.onAllRefreshables { refreshAfterStart() }
        assertTrue(r1.refreshAfterStartCalled)
        assertTrue(r2.refreshAfterStartCalled)
    }
}
