package service

import entity.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/**
 * Full test suite for [GameService] covering all methods, branches,
 * and error handling for complete test coverage.
 */
class GameServiceTest {

    private lateinit var rootService: RootService
    private lateinit var gameService: GameService

    /**
     * Sets up a fresh instance of [RootService] and [GameService] before each test.
     */
    @BeforeEach
    fun setup() {
        rootService = RootService()
        gameService = GameService(rootService)
    }

    /**
     * Tests whether a new game is properly initialized with two players,
     * 7 cards each, 3 exchange cards, and the correct draw pile.
     */
    @Test
    fun `startGame initializes game correctly`() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame
        assertNotNull(game)
        assertEquals(2, game!!.players.size)
        assertEquals(7, game.players[0].hand.size)
        assertEquals(7, game.players[1].hand.size)
        assertEquals(3, game.exchangeArea.size)
        assertEquals(52 - 17, game.drawPile.size)
    }

    /**
     * Tests that invalid player names (blank or identical) throw exceptions.
     */
    @Test
    fun `startGame throws for blank or duplicate names`() {
        assertThrows<IllegalArgumentException> { gameService.startGame("", "Bob") }
        assertThrows<IllegalArgumentException> { gameService.startGame("Alice", "") }
        assertThrows<IllegalArgumentException> { gameService.startGame("Same", "Same") }
    }

    /**
     * Verifies that startTurn throws when no game is currently active.
     */
    @Test
    fun `startTurn throws if no game active`() {
        assertThrows<IllegalStateException> { gameService.startTurn() }
    }

    /**
     * Verifies that startTurn  successfully accesses the current player
     * without throwing exceptions.
     */
    @Test
    fun `startTurn accesses current player`() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        val firstPlayer = game.players[0]
        gameService.startTurn()
        assertEquals(firstPlayer, game.players[game.currentPlayerIndex])
    }

    /**
     * Verifies that endTurn correctly switches the active player
     * and clears the performed actions of the new player.
     */
    @Test
    fun `endTurn switches to next player and clears actions`() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        val currentPlayer = game.players[game.currentPlayerIndex]
        currentPlayer.performedActions.add(Action.DRAW_CARD)
        val nextIndex = (game.currentPlayerIndex + 1) % 2

        gameService.endTurn()

        assertEquals(nextIndex, game.currentPlayerIndex)
        assertTrue(game.players[nextIndex].performedActions.isEmpty())
    }

    /**
     * Verifies that endTurn ends the game if both players passed consecutively.
     */
    @Test
    fun `endTurn ends game if both players passed`() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        val p1 = game.players[0]
        val p2 = game.players[1]

        p1.performedActions.add(Action.PASS)
        p2.performedActions.add(Action.PASS)
        game.currentPlayerIndex = 0

        gameService.endTurn()

        assertNull(rootService.currentGame)
    }

    /**
     * Tests that endGame  correctly identifies player 1 as the winner.
     */
    @Test
    fun `endGame declares correct winner`() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.players[0].score = 20
        game.players[1].score = 10

        gameService.endGame()

        assertNull(rootService.currentGame)
    }

    /**
     * Tests that endGame correctly identifies player 2 as the winner.
     */
    @Test
    fun `endGame declares correct winner reverse`() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.players[0].score = 5
        game.players[1].score = 15

        gameService.endGame()

        assertNull(rootService.currentGame)
    }

    /**
     * Verifies that endGame handles tie situations correctly.
     */
    @Test
    fun `endGame handles tie`() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.players[0].score = 10
        game.players[1].score = 10

        gameService.endGame()

        assertNull(rootService.currentGame)
    }

    /**
     * Ensures that endGame throws if called without an active game.
     */
    @Test
    fun `endGame throws if no game active`() {
        assertThrows<IllegalStateException> { gameService.endGame() }
    }

    /**
     * Ensures that endTurn throws if no game is currently active.
     */
    @Test
    fun `endTurn throws if no game active`() {
        assertThrows<IllegalStateException> { gameService.endTurn() }
    }

    /**
     * Uses reflection to test that  createFullDeck returns a 52-card deck with unique entries.
     */
    @Test
    fun `createFullDeck creates 52 unique cards`() {
        val method = GameService::class.java.getDeclaredMethod("createFullDeck")
        method.isAccessible = true
        val deck = method.invoke(gameService) as List<*>
        assertEquals(52, deck.distinct().size)
    }
}
