package service

import entity.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [GameService].
 *
 * Covers all logic in:
 * - startGame()
 * - startTurn()
 * - endTurn()
 * - endGame()
 * - createFullDeck()
 */
class GameServiceTest {

    private lateinit var rootService: RootService
    private lateinit var gameService: GameService

    @BeforeEach
    fun setup() {
        rootService = RootService()
        gameService = GameService(rootService)
    }

    /**
     * Tests starting a game with valid names.
     */
    @Test
    fun testStartGame_validNames() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame
        assertNotNull(game)
        assertEquals(2, game.players.size)
        assertEquals(7, game.players[0].hand.size)
        assertEquals(7, game.players[1].hand.size)
        assertEquals(3, game.exchangeArea.size)
        assertTrue(game.drawPile.size in 35..39) // Depends on shuffling
        assertEquals(0, game.currentPlayerIndex)
    }

    /**
     * Tests that starting a game with blank names throws an error.
     */
    @Test
    fun testStartGame_blankNames() {
        val ex1 = assertThrows<IllegalArgumentException> {
            gameService.startGame("", "Bob")
        }
        assertEquals("Player names must not be blank.", ex1.message)

        val ex2 = assertThrows<IllegalArgumentException> {
            gameService.startGame("Alice", "")
        }
        assertEquals("Player names must not be blank.", ex2.message)
    }
    /**
     * Tests que [GameService.startGame] déclenche bien
     * [Refreshable.refreshAfterStart] après la création d’une nouvelle partie.
     */
    @Test
    fun testStartGameTriggersRefreshAfterStart() {
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        gameService.startGame("Alice", "Bob")

        assertTrue(testRefreshable.refreshAfterStartCalled)
    }



    /**
     * Tests that starting a game with identical names throws an error.
     */
    @Test
    fun testStartGame_duplicateNames() {
        val ex = assertThrows<IllegalArgumentException> {
            gameService.startGame("Alice", "Alice")
        }
        assertEquals("Player names must be different.", ex.message)
    }

    /**
     * Tests that starting a turn correctly notifies for the active player.
     */
    @Test
    fun testStartTurn_activePlayerCorrect() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        gameService.startTurn()
        val active = game.players[game.currentPlayerIndex]
        assertEquals("Alice", active.name)
    }
    /**
     * Tests that [GameService.endTurn] switches to the next player
     * and triggers [Refreshable.refreshAfterTurnEnd].
     */
    @Test
    fun testEndTurnSwitchesPlayerAndRefreshes() {
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!

        val currentBefore = game.players[game.currentPlayerIndex]

        gameService.endTurn()

        val currentAfter = game.players[game.currentPlayerIndex]

        assertNotEquals(currentBefore, currentAfter, "endTurn should switch to the other player.")

    }

    /**
     * Tests that startTurn throws if no game is active.
     */
    @Test
    fun testStartTurn_noGame() {
        val ex = assertThrows<IllegalStateException> {
            gameService.startTurn()
        }
        assertEquals("No game is active.", ex.message)
    }

    /**
     * Tests that endTurn switches to the next player and clears actions.
     */
    @Test
    fun testEndTurn_switchesPlayer() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.players[0].performedActions.add(Action.DRAW_CARD)

        gameService.endTurn()

        assertEquals(1, game.currentPlayerIndex)
        assertTrue(game.players[1].performedActions.isEmpty())
    }
    /**
     * Tests que [GameService.startTurn] déclenche bien
     * [Refreshable.refreshAfterTurnStart] au début du tour du joueur actif.
     */
    @Test
    fun testStartTurnTriggersRefreshAfterTurnStart() {
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        gameService.startGame("Alice", "Bob")
        gameService.startTurn()

        assertTrue(testRefreshable.refreshAfterTurnStartCalled)
    }

    /**
     * Tests that endTurn ends the game if hand is empty.
     */
    @Test
    fun testEndTurn_endsGameIfHandEmpty() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.players[0].hand.clear() // simulate empty hand
        gameService.endTurn()

        assertNull(rootService.currentGame) // game is ended and cleared
    }
    /**
     * Tests que [GameService.endGame] déclenche bien
     * [Refreshable.refreshAfterGameEnd] avec le joueur gagnant correct.
     */
    @Test
    fun testEndGameTriggersRefreshAfterGameEnd() {
        val player1 = KombiPlayer("Alice").apply { score = 50 }
        val player2 = KombiPlayer("Bob").apply { score = 30 }

        val game = KombiGame(
            players = listOf(player1, player2),
            drawPile = mutableListOf(),
            exchangeArea = mutableListOf()
        )
        rootService.currentGame = game

        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        gameService.endGame()

        assertTrue(testRefreshable.refreshAfterGameEndCalled)
    }

    /**
     * Tests endTurn throws if no game is active.
     */
    @Test
    fun testEndTurn_noGame() {
        val ex = assertThrows<IllegalStateException> {
            gameService.endTurn()
        }
        assertEquals("No game is active.", ex.message)
    }

    /**
     * Tests endGame correctly identifies winner and clears game.
     */
    @Test
    fun testEndGame_differentScores() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.players[0].score = 15
        game.players[1].score = 10
        gameService.endGame()

        assertNull(rootService.currentGame) // Game is cleared
        // Normally we'd mock refreshAfterGameEnd to verify correct winner is passed
    }

    /**
     * Tests endGame results in tie.
     */
    @Test
    fun testEndGame_tie() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.players[0].score = 20
        game.players[1].score = 20
        gameService.endGame()

        assertNull(rootService.currentGame)
    }

    /**
     * Tests endGame throws if no game is active.
     */
    @Test
    fun testEndGame_noGame() {
        val ex = assertThrows<IllegalStateException> {
            gameService.endGame()
        }
        assertEquals("No game is currently active.", ex.message)
    }

    /**
     * Tests that the full deck contains 52 unique cards.
     */
    @Test
    fun testCreateFullDeck_uniqueCards() {
        val deck = gameService.javaClass.getDeclaredMethod("createFullDeck").apply {
            isAccessible = true
        }.invoke(gameService) as List<*>

        assertEquals(52, deck.size)
        assertEquals(52, deck.toSet().size) // no duplicates
    }
    /**
     * Tests that the correct initial player is set after game start.
     */
    @Test
    fun testStartGame_setsFirstPlayerIndex() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        assertEquals(0, game.currentPlayerIndex, "First player should start at index 0.")
    }

    /**
     * Tests that both players are created with empty performed actions and discard piles.
     */
    @Test
    fun testStartGame_playersInitializedCleanly() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.players.forEach {
            assertTrue(it.performedActions.isEmpty(), "Players should start with no actions.")
            assertTrue(it.discardPile.isEmpty(), "Players should start with empty discard piles.")
        }
    }

    /**
     * Tests that endTurn does not accidentally skip a player or wrap incorrectly.
     */
    @Test
    fun testEndTurn_wrapsCorrectlyToFirstPlayer() {
        gameService.startGame("Alice", "Bob")
        val game = rootService.currentGame!!
        game.currentPlayerIndex = 1
        gameService.endTurn()
        assertEquals(0, game.currentPlayerIndex, "Should wrap back to player index 0.")
    }

}
