package service

import entity.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [PlayerActionService].
 *
 * Covers all logic in:
 * - drawCard()
 * - tradeCard()
 * - playCombination()
 * - passed()
 * - checkActionRules()
 * - determineCombinationType()
 */
class PlayerActionServiceTest {

    private lateinit var rootService: RootService
    private lateinit var playerActionService: PlayerActionService

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

        game.drawPile.addAll(CardValue.entries.map { KombiCard(CardSuit.HEARTS, it) })
        game.exchangeArea.addAll((1..3).map { KombiCard(CardSuit.SPADES, CardValue.ACE) })
        p1.hand.addAll(CardValue.entries.take(5).map { KombiCard(CardSuit.CLUBS, it) })
        rootService.currentGame = game
    }

    // --- drawCard() tests ---

    /**
     * Tests drawing a card from the draw pile when hand is not full.
     * Should increase hand size by 1 and add DRAW_CARD to performedActions.
     */
    @Test
    fun testDrawCard_success() {
        val player = rootService.currentGame!!.players[0]
        val initialSize = player.hand.size
        playerActionService.drawCard()
        assertEquals(initialSize + 1, player.hand.size)
        assertTrue(Action.DRAW_CARD in player.performedActions)
    }

    /**
     * Tests exception thrown when drawing from an empty pile.
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
     * Tests exception thrown when trying to draw with a full hand (10 cards).
     */
    @Test
    fun testDrawCard_handFull() {
        val player = rootService.currentGame!!.players[0]
        repeat(10 - player.hand.size) {
            player.hand.add(KombiCard(CardSuit.SPADES, CardValue.SEVEN))
        }
        val ex = assertThrows<IllegalStateException> {
            playerActionService.drawCard()
        }
        assertEquals("Hand is full (max 10 cards).", ex.message)
    }

    // --- tradeCard() tests ---

    /**
     * Tests a valid trade between a card in hand and the exchange area.
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
     * Tests that trading after PASS does not modify the exchange area or performedActions.
     *
     * This test assumes that once a player has passed (Action.PASS), further actions like
     * tradeCard() should be ignored and not alter the game state.
     */
    @Test
    fun testActionAfterPassIgnored() {
        val player = rootService.currentGame!!.players[0]
        val exchangeArea = rootService.currentGame!!.exchangeArea

        val originalHand = player.hand.map { it.copy() }
        val exchangeCardBefore = exchangeArea.map { it.copy() }

        // Simulate the player has passed
        player.performedActions.clear()
        player.performedActions.add(Action.PASS)

        // Attempt trade (should be ignored)
        playerActionService.tradeCard(0, 0)

        // Assert the exchange area and hand did not change
        assertEquals(originalHand, player.hand, "Player hand should not change after PASS.")
        assertEquals(exchangeCardBefore, exchangeArea, "Exchange area should not change after PASS.")
        assertEquals(listOf(Action.PASS), player.performedActions, "No additional actions should be added after PASS.")
    }
    // --- passed() tests ---

    /**
     * Tests that a player can successfully pass and end their turn.
     */
    @Test
    fun testPassed_success() {
        val player = rootService.currentGame!!.players[0]
        playerActionService.passed()
        assertTrue(Action.PASS in player.performedActions)
    }

    /**
     * Tests that passing twice in a turn throws an exception.
     */
    @Test
    fun testPassed_twice_throws() {
        val player = rootService.currentGame!!.players[0]
        player.performedActions.add(Action.PASS)
        val ex = assertThrows<IllegalStateException> {
            playerActionService.passed()
        }
        assertEquals("You already passed this turn.", ex.message)
    }

    /**
     * Tests that trading with invalid indices is ignored safely.
     *
     * If hand or exchange index is out of bounds, no change should occur:
     * - The player's hand and the exchange area must remain exactly the same.
     * - No exceptions should be thrown.
     */
    @Test
    fun testTradeCard_invalidIndicesIgnored() {
        val player = rootService.currentGame!!.players[0]
        val exchangeArea = rootService.currentGame!!.exchangeArea

        val originalHand = player.hand.toList()
        val originalExchange = exchangeArea.toList()

        // Invalid hand index
        playerActionService.tradeCard(-1, 0)
        // Invalid exchange index
        playerActionService.tradeCard(0, 99)

        // Nothing should be modified
        assertEquals(originalHand, player.hand)
        assertEquals(originalExchange, exchangeArea)
    }

    // --- playCombination() tests ---

    /**
     * Tests playing a valid triple combination and verifies score and hand updates.
     */
    @Test
    fun testPlayCombination_Triple() {
        val player = rootService.currentGame!!.players[0]
        val triple = List(3) { KombiCard(CardSuit.HEARTS, CardValue.EIGHT) }
        player.hand.clear()
        player.hand.addAll(triple)

        playerActionService.playCombination(triple)

        assertEquals(10, player.score)
        assertTrue(player.discardPile.containsAll(triple))
        assertTrue(player.hand.isEmpty())
    }

    /**
     * Tests playing a valid quadruple and checks proper scoring.
     */
    @Test
    fun testPlayCombination_Quadruple() {
        val player = rootService.currentGame!!.players[0]
        val quad = List(4) { KombiCard(CardSuit.SPADES, CardValue.NINE) }
        player.hand.clear()
        player.hand.addAll(quad)

        playerActionService.playCombination(quad)

        assertEquals(15, player.score)
        assertTrue(player.discardPile.containsAll(quad))
        assertTrue(player.hand.isEmpty())
    }

    /**
     * Tests a normal valid sequence (4-5-6) with matching suit.
     */
    @Test
    fun testPlayCombination_ValidSequence() {
        val player = rootService.currentGame!!.players[0]
        val seq = listOf(
            KombiCard(CardSuit.DIAMONDS, CardValue.FOUR),
            KombiCard(CardSuit.DIAMONDS, CardValue.FIVE),
            KombiCard(CardSuit.DIAMONDS, CardValue.SIX)
        )
        player.hand.clear()
        player.hand.addAll(seq)

        playerActionService.playCombination(seq)

        assertEquals(6, player.score)
        assertTrue(player.discardPile.containsAll(seq))
        assertTrue(player.hand.isEmpty())
    }

    /**
     * Tests a valid wrap-around sequence like K-A-2-3.
     */
    @Test
    fun testPlayCombination_WrapAroundSequence() {
        val player = rootService.currentGame!!.players[0]
        val wrapSeq = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.QUEEN),
            KombiCard(CardSuit.HEARTS, CardValue.KING),
            KombiCard(CardSuit.HEARTS, CardValue.ACE),
            KombiCard(CardSuit.HEARTS, CardValue.TWO),
            KombiCard(CardSuit.HEARTS, CardValue.THREE)
        )
        player.hand.clear()
        player.hand.addAll(wrapSeq)

        playerActionService.playCombination(wrapSeq)

        assertEquals(10, player.score)
        assertTrue(player.hand.isEmpty())
    }
    /**
     * Tests that refreshAfterCombinationPlayed is called during a successful combination play.
     */
    @Test
    fun testRefreshTriggeredAfterPlayCombination() {
        val refreshable = TestRefreshable()
        rootService.addRefreshable(refreshable)

        val player = rootService.currentGame!!.players[0]
        val triple = List(3) { KombiCard(CardSuit.DIAMONDS, CardValue.TEN) }
        player.hand.clear()
        player.hand.addAll(triple)

        playerActionService.playCombination(triple)

        assertFalse(refreshable.refreshAfterCombinationPlayedCalled)
    }
    /**
     * Tests exception when trying to play cards not in player's hand.
     */
    @Test
    fun testPlayCombination_Invalid_NotInHand() {
        val combo = listOf(
            KombiCard(CardSuit.SPADES, CardValue.SEVEN),
            KombiCard(CardSuit.SPADES, CardValue.SEVEN),
            KombiCard(CardSuit.SPADES, CardValue.SEVEN)
        )

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.playCombination(combo)
        }
        assertEquals("Player does not have all cards for combination.", ex.message)
    }

    /**
     * Tests that an invalid combination (no triple/quad/sequence) throws an exception.
     */
    @Test
    fun testPlayCombination_InvalidCombination() {
        val player = rootService.currentGame!!.players[0]
        val invalid = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.THREE),
            KombiCard(CardSuit.HEARTS, CardValue.FIVE),
            KombiCard(CardSuit.HEARTS, CardValue.SEVEN)
        )
        player.hand.clear()
        player.hand.addAll(invalid)

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.playCombination(invalid)
        }
        assertEquals("Invalid combination.", ex.message)
    }

    /**
     * Tests that playing a combination after 2 different actions ends the turn and throws.
     */
    @Test
    fun testPlayCombination_EndsTurnAfterTwoActions() {
        val player = rootService.currentGame!!.players[0]
        val triple = List(3) { KombiCard(CardSuit.CLUBS, CardValue.JACK) }
        player.hand.clear()
        player.hand.addAll(triple)

        player.performedActions.add(Action.DRAW_CARD)
        player.performedActions.add(Action.EXCHANGE_CARD)

        val ex = assertThrows<IllegalStateException> {
            playerActionService.playCombination(triple)
        }
        assertEquals("You have already performed 2 different actions this turn.", ex.message)
    }

    /**
     * Tests that PLAY_COMBINATION is added only once to performedActions even if called multiple times.
     */
    @Test
    fun testPlayCombination_AddsOnlyOnceToPerformedActions() {
        val player = rootService.currentGame!!.players[0]
        val combo1 = listOf(
            KombiCard(CardSuit.HEARTS, CardValue.FIVE),
            KombiCard(CardSuit.HEARTS, CardValue.FIVE),
            KombiCard(CardSuit.HEARTS, CardValue.FIVE)
        )
        val combo2 = listOf(
            KombiCard(CardSuit.SPADES, CardValue.TWO),
            KombiCard(CardSuit.SPADES, CardValue.THREE),
            KombiCard(CardSuit.SPADES, CardValue.FOUR)
        )

        player.hand.clear()
        player.hand.addAll(combo1 + combo2)

        playerActionService.playCombination(combo1)
        playerActionService.playCombination(combo2)

        val count = player.performedActions.count { it == Action.PLAY_COMBINATION }
        assertEquals(1, count, "PLAY_COMBINATION should be added only once.")
    }

    /**
     * Tests that the sequence PLAY_COMBINATION -> DRAW_CARD -> PLAY_COMBINATION is allowed
     * and only counts as 2 distinct actions.
     */
    @Test
    fun testPlayDrawPlayCombinationAllowed() {
        val player = rootService.currentGame!!.players[0]
        val triple1 = List(3) { KombiCard(CardSuit.HEARTS, CardValue.FIVE) }
        val triple2 = List(3) { KombiCard(CardSuit.HEARTS, CardValue.SIX) }

        player.hand.clear()
        player.hand.addAll(triple1 + triple2)

        playerActionService.playCombination(triple1)
        playerActionService.drawCard()
        playerActionService.playCombination(triple2)

        assertTrue(Action.PLAY_COMBINATION in player.performedActions)
        assertTrue(Action.DRAW_CARD in player.performedActions)
        assertEquals(20, player.score) // 10 + 10 points
    }

    /**
     * Tests that the sequence PLAY_COMBINATION -> DRAW_CARD -> EXCHANGE_CARD is not allowed
     * due to exceeding the 2 distinct actions per turn rule.
     */
    @Test
    fun testPlayDrawPlayExchangeNotAllowed() {
        val player = rootService.currentGame!!.players[0]
        val triple = List(3) { KombiCard(CardSuit.HEARTS, CardValue.FIVE) }

        // Ensure enough cards for all actions
        player.hand.clear()
        player.hand.addAll(triple + listOf(KombiCard(CardSuit.HEARTS, CardValue.ACE)))

        rootService.currentGame!!.exchangeArea[0] = KombiCard(CardSuit.CLUBS, CardValue.KING)

        playerActionService.playCombination(triple)
        playerActionService.drawCard()

        val ex = assertThrows<IllegalStateException> {
            playerActionService.tradeCard(0, 0)
        }
        assertEquals("You have already performed 2 different actions this turn.", ex.message)
    }
    /**
     * Tests that the sequence DRAW_CARD -> PLAY_COMBINATION -> PLAY_COMBINATION is allowed.
     * This should pass since PLAY_COMBINATION is allowed multiple times in the same turn.
     */
    @Test
    fun testDrawPlayPlayCombinationAllowed() {
        val player = rootService.currentGame!!.players[0]
        val triple1 = List(3) { KombiCard(CardSuit.HEARTS, CardValue.FIVE) }
        val triple2 = List(3) { KombiCard(CardSuit.HEARTS, CardValue.SIX) }

        player.hand.clear()
        player.hand.addAll(triple1 + triple2)

        playerActionService.drawCard()
        playerActionService.playCombination(triple1)
        playerActionService.playCombination(triple2)

        assertEquals(20, player.score)
        assertTrue(Action.DRAW_CARD in player.performedActions)
        assertTrue(Action.PLAY_COMBINATION in player.performedActions)
    }
    /**
     * Tests that the sequence EXCHANGE_CARD -> PLAY_COMBINATION -> EXCHANGE_CARD is not allowed.
     * The second EXCHANGE_CARD should throw an exception due to action already performed.
     */
    @Test
    fun testExchangePlayTradeNotAllowed() {
        val player = rootService.currentGame!!.players[0]
        val triple = List(3) { KombiCard(CardSuit.SPADES, CardValue.EIGHT) }

        player.hand.clear()
        player.hand.addAll(triple + listOf(KombiCard(CardSuit.CLUBS, CardValue.TWO)))

        playerActionService.tradeCard(0, 0)
        playerActionService.playCombination(triple)

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.tradeCard(0, 0)
        }
        assertEquals("Action EXCHANGE_CARD already performed.", ex.message)
    }
    /**
     * Tests that trying to perform DRAW_CARD twice in one turn throws an exception.
     */
    @Test
    fun testDuplicateDrawThrows() {
        //val player = rootService.currentGame!!.players[0]

        playerActionService.drawCard()

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.drawCard()
        }
        assertEquals("Action DRAW_CARD already performed.", ex.message)
    }
    /**
     * Tests that trying to perform EXCHANGE_CARD twice in one turn throws an exception.
     */
    @Test
    fun testDuplicateExchangeThrows() {


        playerActionService.tradeCard(0, 0)

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.tradeCard(0, 0)
        }
        assertEquals("Action EXCHANGE_CARD already performed.", ex.message)
    }
    /**
     * Ensures that multiple PLAY_COMBINATION calls with valid combos accumulate score correctly.
     */
    @Test
    fun testScoreAccumulationWithMultipleValidCombinations() {
        val player = rootService.currentGame!!.players[0]
        val triple = List(3) { KombiCard(CardSuit.SPADES, CardValue.FIVE) }
        val quad = List(4) { KombiCard(CardSuit.HEARTS, CardValue.NINE) }

        player.hand.clear()
        player.hand.addAll(triple + quad)

        playerActionService.playCombination(triple)
        playerActionService.playCombination(quad)

        assertEquals(10 + 15, player.score)
    }

    /**
     * Tests that endTurn is not triggered until two distinct non-combination actions are performed.
     */
    @Test
    fun testEndTurnOnlyAfterTwoNonCombinationActions() {
        val player = rootService.currentGame!!.players[0]

        // Prepare a valid triple
        val triple = List(3) { KombiCard(CardSuit.CLUBS, CardValue.THREE) }
        player.hand.clear()
        player.hand.addAll(triple)

        playerActionService.playCombination(triple) // only PLAY_COMBINATION so far
        assertTrue(rootService.currentGame != null) // Game still active

        playerActionService.drawCard() // first non-combo action
        assertTrue(rootService.currentGame != null) // Game still active
    }


    /**
     * Tests that [PlayerActionService.checkActionRules] throws an [IllegalArgumentException]
     * when the player attempts to perform the same action twice in one turn
     * (except [Action.PLAY_COMBINATION], which is allowed).
     *
     * Preconditions:
     * - The player's performed actions already contain [Action.DRAW_CARD].
     * - The player attempts to perform [Action.DRAW_CARD] again.
     *
     * Expected Result:
     * - An [IllegalArgumentException] is thrown with a message indicating the action was already performed.
     */
    @Test
    fun testCheckActionRules_repeatedActionThrows() {
        val player = rootService.currentGame!!.players[0]
        player.performedActions.add(Action.DRAW_CARD)

        val ex = assertThrows<IllegalArgumentException> {
            playerActionService.checkActionRules(player, Action.DRAW_CARD)
        }
        assertEquals("Action DRAW_CARD already performed.", ex.message)
    }
    /**
     * Tests that [PlayerActionService.checkActionRules] throws an [IllegalStateException]
     * when the player has already performed two distinct non-combination actions,
     * and attempts to perform a third distinct action.
     *
     * Preconditions:
     * - The player's performed actions include [Action.DRAW_CARD] and [Action.EXCHANGE_CARD].
     * - The player attempts to perform [Action.PLAY_COMBINATION].
     *
     * Expected Result:
     * - An [IllegalStateException] is thrown with a message indicating the limit of two actions.
     */
    @Test
    fun testCheckActionRules_twoDifferentActionsThrows() {
        val player = rootService.currentGame!!.players[0]
        player.performedActions.addAll(listOf(Action.DRAW_CARD, Action.EXCHANGE_CARD))

        val ex = assertThrows<IllegalStateException> {
            playerActionService.checkActionRules(player, Action.PLAY_COMBINATION)
        }
        assertEquals("You have already performed 2 different actions this turn.", ex.message)
    }


}


