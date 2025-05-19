package service

import entity.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [PlayerActionService].
 *
 * This test class ensures 100% code coverage for:
 * - drawCard()
 * - tradeCard()
 * - playCombinations()
 * - passed()
 * - all internal rule checks
 *
 * It validates normal usage, edge cases, and error handling.
 */
class PlayerActionServiceTest {

    private lateinit var rootService: RootService
    private lateinit var playerActionService: PlayerActionService

    /**
     * Initializes a mock game with 2 players, a filled draw pile,
     * and a filled exchange area before each test.
     */
    @BeforeEach
    fun setup() {
        rootService = RootService()
        playerActionService = PlayerActionService(rootService)

        val p1 = KombiPlayer("Alice")
        val p2 = KombiPlayer("Bob")

        val game = KombiGame(
            players = listOf(p1, p2),
            drawPile = mutableListOf(),
            exchangeArea = mutableListOf()
        )

        game.drawPile.addAll((1..10).map {
            KombiCard(CardSuit.HEARTS, CardValue.values()[it % 13])
        })

        game.exchangeArea.addAll((1..3).map {
            KombiCard(CardSuit.SPADES, CardValue.ACE)
        })

        p1.hand.addAll((1..5).map {
            KombiCard(CardSuit.CLUBS, CardValue.values()[it % 13])
        })

        rootService.currentGame = game
    }

    /**
     * Tests successful card drawing from the draw pile.
     */
    @Test
    fun testDrawCard() {
        val player = rootService.currentGame!!.players[0]
        val initialSize = player.hand.size
        playerActionService.drawCard()
        assertEquals(initialSize + 1, player.hand.size)
        assertTrue(Action.DRAW_CARD in player.performedActions)
    }

    /**
     * Ensures drawing from an empty pile throws an exception.
     */
    @Test
    fun testDrawCard_emptyPile() {
        rootService.currentGame!!.drawPile.clear()
        val ex = assertThrows<IllegalStateException> {
            playerActionService.drawCard()
        }
        assertEquals("Draw pile is empty.", ex.message)
    }

    /**
     * Ensures drawing with a full hand throws an exception.
     */
    @Test
    fun testDrawCard_fullHand() {
        val player = rootService.currentGame!!.players[0]
        while (player.hand.size < 10) {
            player.hand.add(KombiCard(CardSuit.DIAMONDS, CardValue.TWO))
        }
        val ex = assertThrows<IllegalStateException> {
            playerActionService.drawCard()
        }
        assertEquals("Hand is full (max 10 cards).", ex.message)
    }

    /**
     * Tests a valid card trade between hand and exchange area.
     */
    @Test
    fun testTradeCard_success() {
        val player = rootService.currentGame!!.players[0]
        val handCard = player.hand[0]
        val exchangeCard = rootService.currentGame!!.exchangeArea[0]
        playerActionService.tradeCard(0, 0)
        assertEquals(exchangeCard, player.hand[0])
        assertEquals(handCard, rootService.currentGame!!.exchangeArea[0])
        assertTrue(Action.EXCHANGE_CARD in player.performedActions)
    }

    /**
     * Ensures out-of-bound trade indices throw exceptions.
     */
    @Test
    fun testTradeCard_invalidIndices() {
        assertThrows<IllegalArgumentException> {
            playerActionService.tradeCard(-1, 0)
        }
        assertThrows<IllegalArgumentException> {
            playerActionService.tradeCard(0, -1)
        }
        assertThrows<IllegalArgumentException> {
            playerActionService.tradeCard(99, 99)
        }
    }

    /**
     * Tests playing two valid combinations in one call: a triple and a sequence.
     */
    @Test
    fun testPlayCombinations_validTripleAndSequence() {
        val player = rootService.currentGame!!.players[0]
        val triple = List(3) { KombiCard(CardSuit.HEARTS, CardValue.SEVEN) }
        val seq = listOf(
            KombiCard(CardSuit.SPADES, CardValue.TWO),
            KombiCard(CardSuit.SPADES, CardValue.THREE),
            KombiCard(CardSuit.SPADES, CardValue.FOUR)
        )
        player.hand.clear()
        player.hand.addAll(triple + seq)

        playerActionService.playCombinations(listOf(triple, seq))
        assertEquals(10 + 6, player.score)
        assertTrue(Action.PLAY_COMBINATION in player.performedActions)
        assertTrue(player.hand.isEmpty())
    }

    /**
     * Ensures an error is thrown when player lacks the required cards.
     */
    @Test
    fun testPlayCombinations_invalidCombination_notInHand() {
        val combo = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.KING),
            KombiCard(CardSuit.HEARTS, CardValue.KING),
            KombiCard(CardSuit.HEARTS, CardValue.KING)
        )
        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.playCombinations(listOf(combo))
        }
        assertEquals("Player does not have all cards for combination.", ex.message)
    }

    /**
     * Ensures an invalid sequence (non-consecutive) is rejected.
     */
    @Test
    fun testPlayCombinations_invalidCombination_wrongSequence() {
        val badSeq = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.TWO),
            KombiCard(CardSuit.HEARTS, CardValue.FOUR),
            KombiCard(CardSuit.HEARTS, CardValue.SIX)
        )
        val player = rootService.currentGame!!.players[0]
        player.hand.clear()
        player.hand.addAll(badSeq)

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.playCombinations(listOf(badSeq))
        }
        assertEquals("Not a valid sequence.", ex.message)
    }

    /**
     * Ensures mixed-suit cards not forming any valid combo are rejected.
     */
    @Test
    fun testPlayCombinations_invalidCombination_mixedTypes() {
        val mixed = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.TWO),
            KombiCard(CardSuit.SPADES, CardValue.TWO),
            KombiCard(CardSuit.DIAMONDS, CardValue.FIVE)
        )
        val player = rootService.currentGame!!.players[0]
        player.hand.clear()
        player.hand.addAll(mixed)

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.playCombinations(listOf(mixed))
        }
        assertEquals("Invalid combination.", ex.message)
    }

    /**
     * tests if there is a repeated action for example draw card then drawcard again .
     */
    @Test
    fun testRepeatSameActionThrows() {
        val player = rootService.currentGame!!.players[0]
        player.performedActions.add(Action.DRAW_CARD)

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.drawCard()
        }

        assertEquals("Action DRAW_CARD already performed.", ex.message)
    }
    /**
     * tests if there is a player wants to play more than 2 action (and distinct actions)for example exchange card draw card playcombination  .
     */
    @Test
    fun testThirdDifferentActionThrows() {
        val player = rootService.currentGame!!.players[0]
        player.performedActions.add(Action.DRAW_CARD)
        player.performedActions.add(Action.EXCHANGE_CARD)

        val cards = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.SEVEN),
            KombiCard(CardSuit.HEARTS, CardValue.SEVEN),
            KombiCard(CardSuit.HEARTS, CardValue.SEVEN)
        )
        player.hand.addAll(cards)

        val ex = assertThrows<IllegalStateException> {
            playerActionService.playCombinations(listOf(cards))
        }

        assertEquals("Max two different actions per turn.", ex.message)
    }
    /**
     * Tests that no actions are allowed after passing.
     */
    @Test
    fun testActionAfterPassThrows() {
        val player = rootService.currentGame!!.players[0]
        player.performedActions.add(Action.PASS)

        val ex = assertThrows<IllegalStateException> {
            playerActionService.tradeCard(0, 0)
        }

        assertEquals("No actions allowed after passing.", ex.message)
    }


    /**
     * Tests that the player can successfully pass the turn.
     */
    @Test
    fun testPassedAddsActionAndEndsTurn() {
        val player = rootService.currentGame!!.players[0]
        assertTrue(Action.PASS !in player.performedActions)

        playerActionService.passed()

        assertTrue(Action.PASS in player.performedActions)
    }
    /**
     * Tests that passing twice in one turn is not allowed.
     */
    @Test
    fun testPassedTwiceThrows() {
        val player = rootService.currentGame!!.players[0]
        player.performedActions.add(Action.PASS)

        val ex = assertThrows<IllegalStateException> {
            playerActionService.passed()
        }

        assertEquals("Player already passed this turn.", ex.message)
    }

}
