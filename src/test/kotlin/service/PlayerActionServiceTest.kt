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
 * - combination types and rule violations
 */
class PlayerActionServiceTest {

    private lateinit var rootService: RootService
    private lateinit var service: PlayerActionService
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

    @Test
    fun testDrawCardValid() {
        val game = rootService.currentGame!!
        game.drawPile.add(KombiCard(CardSuit.HEARTS, CardValue.FIVE))
        service.drawCard()
        assertEquals(1, player.hand.size)
        assertTrue(Action.DRAW_CARD in player.performedActions)
    }

    @Test
    fun testDrawCardFailsIfPileEmpty() {
        assertFailsWith<IllegalStateException> { service.drawCard() }
    }

    @Test
    fun testDrawCardFailsIfHandFull() {
        val game = rootService.currentGame!!
        game.drawPile.add(KombiCard(CardSuit.SPADES, CardValue.TWO))
        repeat(10) {
            player.hand.add(KombiCard(CardSuit.CLUBS, CardValue.values()[it % 13]))
        }
        assertFailsWith<IllegalStateException> { service.drawCard() }
    }

    @Test
    fun testPassedValid() {
        service.passed()
        assertTrue(Action.PASS in player.performedActions)
    }

    @Test
    fun testPassedAfterOtherActionFails() {
        val game = rootService.currentGame!!
        game.drawPile.add(KombiCard(CardSuit.SPADES, CardValue.ACE))
        service.drawCard()
        assertFailsWith<IllegalStateException> { service.passed() }
    }

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

    @Test
    fun testTradeCardFailsWithBadIndex() {
        val game = rootService.currentGame!!
        player.hand.add(KombiCard(CardSuit.SPADES, CardValue.SIX))
        game.exchangeArea.add(KombiCard(CardSuit.HEARTS, CardValue.FOUR))
        assertFailsWith<IllegalArgumentException> { service.tradeCard(2, 0) }
        assertFailsWith<IllegalArgumentException> { service.tradeCard(0, 3) }
    }

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
     * Ensures playing a valid quadruple correctly adds score and logs the action.
     */
    @Test
    fun testPlayQuadrupleValid() {
        val cards = CardSuit.values().map { KombiCard(it, CardValue.EIGHT) }
        player.hand.addAll(cards)
        service.playCombination(cards)
        assertTrue(Action.PLAY_QUADRUPLE in player.performedActions)
        assertEquals(15, player.score)
    }

    /**
     * Ensures playing a valid sequence correctly adds score and logs the action.
     */
    @Test
    fun testPlaySequenceValid() {
        val cards = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.FIVE),
            KombiCard(CardSuit.HEARTS, CardValue.SIX),
            KombiCard(CardSuit.HEARTS, CardValue.SEVEN)
        )
        player.hand.addAll(cards)
        service.playCombination(cards)
        assertTrue(Action.PLAY_SEQUENCE in player.performedActions)
        assertEquals(6, player.score) // 3 cards * 2 points
    }

    @Test
    fun testPlayInvalidCombinationFails() {
        val c1 = KombiCard(CardSuit.SPADES, CardValue.FIVE)
        val c2 = KombiCard(CardSuit.HEARTS, CardValue.SIX)
        val c3 = KombiCard(CardSuit.DIAMONDS, CardValue.EIGHT)
        player.hand.addAll(listOf(c1, c2, c3))
        assertFailsWith<IllegalArgumentException> { service.playCombination(listOf(c1, c2, c3)) }
    }

    @Test
    fun testPlayNotInHandFails() {
        val c1 = KombiCard(CardSuit.SPADES, CardValue.NINE)
        assertFailsWith<IllegalArgumentException> { service.playCombination(listOf(c1)) }
    }

    /**
     * Ensures checkActionRules prevents more than two actions.
     */
    @Test
    fun testCannotDoMoreThanTwoActions() {
        player.performedActions.add(Action.DRAW_CARD)
        player.performedActions.add(Action.EXCHANGE_CARD)

        val c1 = KombiCard(CardSuit.HEARTS, CardValue.NINE)
        val c2 = KombiCard(CardSuit.SPADES, CardValue.NINE)
        val c3 = KombiCard(CardSuit.CLUBS, CardValue.NINE)
        player.hand.addAll(listOf(c1, c2, c3))

        assertFailsWith<IllegalStateException> {
            service.playCombination(listOf(c1, c2, c3))
        }
    }

    /**
     * Ensures checkActionRules prevents repeating the same action.
     */
    @Test
    fun testCannotRepeatSameAction() {
        val game = rootService.currentGame!!
        game.drawPile.add(KombiCard(CardSuit.SPADES, CardValue.SIX))
        player.performedActions.add(Action.DRAW_CARD)
        assertFailsWith<IllegalArgumentException> {
            service.drawCard()
        }
    }

    /**
     * Ensures checkActionRules prevents action after passing.
     */
    @Test
    fun testCannotActAfterPass() {
        player.performedActions.add(Action.PASS)
        assertFailsWith<IllegalStateException> {
            service.drawCard()
        }
    }
}
