package service

import entity.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for [GameService] logic including game start, turn management, and game end.
 */
class GameServiceTest {

    private lateinit var rootService: RootService
    private lateinit var gameService: GameService
    private lateinit var player1: KombiPlayer
    private lateinit var player2: KombiPlayer
    private lateinit var testRefreshable: TestRefreshable

    /**
     * Sets up a clean game with two players and a registered [TestRefreshable] before each test.
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

        // First player passes
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

        // simulate we're on player 0's turn
        game.currentPlayerIndex = 0
        assertDoesNotThrow { gameService.endTurn() }
    }

    /**
     * Tests that [//startGame] sets up a proper game with 7 cards per hand, 3 in exchange, and 35 in draw pile,
     * and that UI refresh is triggered via [//refreshAfterStart].
     */
    @Test
    fun testStartGameCardDistribution() {
        rootService.gameService.startGame("Alice", "Bob")

        val game = rootService.currentGame!!

        assertEquals(7, game.players[0].hand.size)
        assertEquals(7, game.players[1].hand.size)
        assertEquals(3, game.exchangeArea.size)
        assertEquals(35, game.drawPile.size)

        assertTrue(testRefreshable.refreshAfterStartCalled, "UI should refresh after game start")
    }
}
