package entity

import kotlin.test.*

/**
 * Test class for KombiPlayer.
 * Tests drawCard, playCombination, passTurn, and reset methods.
 */
class KombiPlayerTest {

    private lateinit var kombiPlayer: KombiPlayer
    private lateinit var drawPile: DrawPile

    /**
     * Sets up the test environment by initializing the KombiPlayer and DrawPile.
     */
    @BeforeTest
    fun setUp() {
        drawPile = DrawPile(
            listOf(
                KombiCard(CardSuit.HEARTS, CardValue.ACE),
                KombiCard(CardSuit.HEARTS, CardValue.TWO),
                KombiCard(CardSuit.HEARTS, CardValue.THREE)
            )
        )
        kombiPlayer = KombiPlayer("Player1")
    }

    /**
     * Tests the drawCard method to ensure a card is added to the player's hand.
     */
    @Test
    fun testDrawCard() {
        val card = drawPile.draw()
        assertNotNull(card, "Draw should not return null")
        kombiPlayer.drawCard(card)

        assertEquals(1, kombiPlayer.handCards.size, "The player should have 1 card in hand.")
        assertEquals(2, drawPile.remainingCards(), "The draw pile should have 2 cards left.")
    }

    /**
     * Tests the playCombination method to ensure the player can play a valid combination of cards.
     */
    @Test
    fun testPlayCombination() {
        val combination = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.ACE),
            KombiCard(CardSuit.HEARTS, CardValue.TWO)
        )
        kombiPlayer.handCards.addAll(combination)

        val played = kombiPlayer.playCombination(combination)

        assertTrue(played, "The player should be able to play the combination.")
        assertTrue(kombiPlayer.handCards.isEmpty(), "The player's hand should be empty after playing.")
        assertEquals(1, kombiPlayer.discardPile.size, "One combination should be in the discard pile.")
    }

    /**
     * Tests the passTurn method to ensure the player can pass their turn.
     */
    @Test
    fun testPassTurn() {
        kombiPlayer.passTurn()
        assertTrue(kombiPlayer.hasPassed, "The player should have passed their turn.")
    }

    /**
     * Tests the reset method to ensure the player's state is reset correctly.
     */
    @Test
    fun testReset() {
        kombiPlayer.handCards.add(KombiCard(CardSuit.SPADES, CardValue.SEVEN))
        kombiPlayer.points = 10
        kombiPlayer.passTurn()
        kombiPlayer.discardPile.add(listOf(KombiCard(CardSuit.CLUBS, CardValue.THREE)))

        kombiPlayer.reset()

        assertTrue(kombiPlayer.handCards.isEmpty(), "Hand should be empty after reset.")
        assertTrue(kombiPlayer.discardPile.isEmpty(), "Discard pile should be empty after reset.")
        assertEquals(0, kombiPlayer.points, "Points should be reset to 0.")
        assertFalse(kombiPlayer.hasPassed, "Player should not be marked as passed after reset.")
    }
    /**
     * Tests that hasSameNameAs returns true for players with identical names,
     * and false for players with different names.
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
