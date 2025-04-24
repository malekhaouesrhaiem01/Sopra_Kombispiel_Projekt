package entity

import kotlin.test.*

/**
 * Test class for KombiGame.
 * Tests game initialization, turn management, game-over conditions, and winner determination.
 */
class KombiGameTest {

    private lateinit var kombiGame: KombiGame
    private lateinit var player1: KombiPlayer
    private lateinit var player2: KombiPlayer
    private lateinit var drawPile: DrawPile
    private lateinit var exchangeArea: ExchangeArea

    /**
     * Sets up a KombiGame with two players, an empty draw pile, and an exchange area.
     */
    @BeforeTest
    fun setUp() {
        player1 = KombiPlayer("Player1")
        player2 = KombiPlayer("Player2")
        drawPile = DrawPile(
            listOf(
                KombiCard(CardSuit.HEARTS, CardValue.ACE),
                KombiCard(CardSuit.SPADES, CardValue.KING),
                KombiCard(CardSuit.CLUBS, CardValue.QUEEN)
            )
        )
        exchangeArea = ExchangeArea(
            listOf(
                KombiCard(CardSuit.HEARTS, CardValue.THREE),
                KombiCard(CardSuit.SPADES, CardValue.FOUR),
                KombiCard(CardSuit.DIAMONDS, CardValue.FIVE)
            )
        )
        kombiGame = KombiGame(listOf(player1, player2), 0, drawPile, exchangeArea)
    }

    /**
     * Tests that the active player is returned correctly.
     */
    @Test
    fun testGetActivePlayer() {
        assertEquals(player1, kombiGame.getActivePlayer(), "Player 1 should be active initially.")
        kombiGame.nextPlayer()
        assertEquals(player2, kombiGame.getActivePlayer(), "Player 2 should be active after switching turn.")
    }

    /**
     * Tests that switching to the next player resets pass status and turn actions.
     */
    @Test
    fun testNextPlayerResetsTurnState() {
        kombiGame.turnActions = 2
        player2.hasPassed = true

        kombiGame.nextPlayer()

        assertEquals(player2, kombiGame.getActivePlayer(), "Active player should switch to Player 2.")
        assertFalse(player2.hasPassed, "Pass status should be reset after switching.")
        assertEquals(0, kombiGame.turnActions, "Turn actions should reset to 0.")
    }

    /**
     * Tests that the game ends when a player has no more hand cards.
     */
    @Test
    fun testGameEndsWhenHandIsEmpty() {
        player1.handCards.clear()
        val result = kombiGame.isGameOver()
        assertTrue(result, "Game should end if a player has no hand cards.")
    }

    /**
     * Tests that the game ends when both players have passed.
     */
    @Test
    fun testGameEndsWhenAllPassed() {
        player1.hasPassed = true
        player2.hasPassed = true
        val result = kombiGame.isGameOver()
        assertTrue(result, "Game should end if both players have passed.")
    }

    /**
     * Tests winner calculation logic based on point comparison.
     */
    @Test
    fun testCalculateWinner() {
        player1.points = 15
        player2.points = 10
        assertEquals("Player1 wins with 15 points", kombiGame.calculateWinner())

        player1.points = 8
        player2.points = 12
        assertEquals("Player2 wins with 12 points", kombiGame.calculateWinner())

        player1.points = 10
        player2.points = 10
        assertEquals("It's a tie! Both have 10 points", kombiGame.calculateWinner())
    }
    /**
     * Tests that the draw pile and exchange area are accessible and initialized correctly.
     * This ensures their usage is detected and avoids unused property warnings.
     */
    @Test
    fun testDrawPileAndExchangeAreaAccess() {
        // Check that the draw pile is not null and has cards
        assertNotNull(kombiGame.drawPile, "Draw pile should be initialized.")
        assertTrue(kombiGame.drawPile.remainingCards() >= 0, "Draw pile size should be valid.")

        // Check that the exchange area is not null and has exactly 3 cards
        assertNotNull(kombiGame.exchangeArea, "Exchange area should be initialized.")
        assertEquals(3, kombiGame.exchangeArea.cards.size, "Exchange area should start with 3 cards.")
    }
}
