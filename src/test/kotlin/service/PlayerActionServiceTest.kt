package service

import entity.*
import org.junit.jupiter.api.*
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for [PlayerActionService].
 *
 * This test class covers all legal and illegal scenarios for:
 * - drawCard()
 * - tradeCard()
 * - playCombination()
 * - passed()
 */
class PlayerActionServiceTest {

    /**
     * RootService instance for initializing game state and service access.
     */
    private lateinit var rootService: RootService

    /**
     * PlayerActionService under test.
     */
    private lateinit var service: PlayerActionService

    /**
     * Reference to the active player used in tests.
     */
    private lateinit var player: KombiPlayer

    /**
     * Initializes a test-ready game with two players before each test.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        service = rootService.playerActionService

        val p1 = KombiPlayer("Alice")
        val p2 = KombiPlayer("Bob")
        val game = KombiGame(listOf(p1, p2), mutableListOf(), mutableListOf())
        rootService.currentGame = game
        game.currentPlayerIndex = 0
        player = game.players[0]
    }

    /**
     * Verifies that drawing a card from a non-empty pile increases hand size.
     */
    @Test
    fun testDrawCardValid() {
        val game = rootService.currentGame!!
        game.drawPile.add(KombiCard(CardSuit.HEARTS, CardValue.FIVE))
        service.drawCard()
        assertEquals(1, player.hand.size)
        assertTrue(Action.DRAW_CARD in player.performedActions)
    }

    /**
     * Ensures drawing fails when the draw pile is empty.
     */
    @Test
    fun testDrawCardFailsIfPileEmpty() {
        assertFailsWith<IllegalStateException> { service.drawCard() }
    }

    /**
     * Ensures drawing fails when the player's hand already contains 10 cards.
     */
    @Test
    fun testDrawCardFailsIfHandFull() {
        val game = rootService.currentGame!!
        game.drawPile.add(KombiCard(CardSuit.SPADES, CardValue.TWO))
        repeat(10) {
            player.hand.add(KombiCard(CardSuit.CLUBS, CardValue.values()[it % 13]))
        }
        assertFailsWith<IllegalStateException> { service.drawCard() }
    }

    /**
     * Validates that passing with no prior action is allowed.
     */
    @Test
    fun testPassedValid() {
        service.passed()
        assertTrue(Action.PASS in player.performedActions)
    }

    /**
     * Ensures passing is not allowed after performing another action.
     */
    @Test
    fun testPassedAfterOtherActionFails() {
        val game = rootService.currentGame!!
        game.drawPile.add(KombiCard(CardSuit.SPADES, CardValue.ACE))
        service.drawCard()
        assertFailsWith<IllegalStateException> { service.passed() }
    }

    /**
     * Validates a legal card trade between hand and exchange area.
     */
    @Test
    fun testTradeCardValid() {
        val game = rootService.currentGame!!
        player.hand.add(KombiCard(CardSuit.SPADES, CardValue.KING))
        game.exchangeArea.addAll(
            listOf(
                KombiCard(CardSuit.HEARTS, CardValue.TWO),
                KombiCard(CardSuit.DIAMONDS, CardValue.THREE),
                KombiCard(CardSuit.CLUBS, CardValue.FOUR)
            )
        )
        service.tradeCard(0, 1)
        assertEquals(1, player.performedActions.size)
        assertTrue(Action.EXCHANGE_CARD in player.performedActions)
    }

    /**
     * Ensures tradeCard throws exception for invalid indices.
     */
    @Test
    fun testTradeCardFailsWithBadIndex() {
        val game = rootService.currentGame!!
        player.hand.add(KombiCard(CardSuit.SPADES, CardValue.SIX))
        game.exchangeArea.add(KombiCard(CardSuit.HEARTS, CardValue.FOUR))
        assertFailsWith<IllegalArgumentException> { service.tradeCard(2, 0) }
        assertFailsWith<IllegalArgumentException> { service.tradeCard(0, 3) }
    }

    /**
     * Ensures playing a valid triple correctly adds score and logs the action.
     */
    @Test
    fun testPlayTripleValid() {
        val card1 = KombiCard(CardSuit.SPADES, CardValue.FIVE)
        val card2 = KombiCard(CardSuit.CLUBS, CardValue.FIVE)
        val card3 = KombiCard(CardSuit.HEARTS, CardValue.FIVE)
        player.hand.addAll(listOf(card1, card2, card3))
        service.playCombination(listOf(card1, card2, card3))
        assertTrue(Action.PLAY_TRIPLE in player.performedActions)
        assertEquals(10, player.score)
    }

    /**
     * Ensures invalid combinations (not triple/quad/sequence) are rejected.
     */
    @Test
    fun testPlayInvalidCombinationFails() {
        val c1 = KombiCard(CardSuit.SPADES, CardValue.FIVE)
        val c2 = KombiCard(CardSuit.HEARTS, CardValue.SIX)
        val c3 = KombiCard(CardSuit.DIAMONDS, CardValue.EIGHT)
        player.hand.addAll(listOf(c1, c2, c3))
        assertFailsWith<IllegalArgumentException> { service.playCombination(listOf(c1, c2, c3)) }
    }

    /**
     * Ensures playCombination fails if cards are not in the player's hand.
     */
    @Test
    fun testPlayNotInHandFails() {
        val c1 = KombiCard(CardSuit.SPADES, CardValue.NINE)
        assertFailsWith<IllegalArgumentException> { service.playCombination(listOf(c1)) }
    }
}
