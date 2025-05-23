package service

import entity.*

/**
 * Handles all player-specific actions during a turn in the Kombi-Duel game.
 *
 * Enforces turn-based rules:
 * - Max. 2 different actions per turn (only PLAY_COMBINATION can be repeated)
 * - No actions allowed after PASS
 * - Multiple valid combinations (triples, sequences, etc.) can be played per turn
 *
 * Notifies the UI through [Refreshable] whenever a valid action occurs.
 *
 * @property rootService Provides access to game state and other services
 */
class PlayerActionService(
    private val rootService: RootService
) : AbstractRefreshingService() {

    /**
     * Draws one card from the draw pile and adds it to the player's hand.
     *
     * Validates action rules, enforces hand size limits, and ends the turn
     * if this is the second distinct non-combination action.
     *
     * @throws IllegalStateException if no game is active, draw pile is empty, or hand is full.
     */
    fun drawCard() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]
        checkActionRules(player, Action.DRAW_CARD)

        if (game.drawPile.isEmpty()) throw IllegalStateException("Draw pile is empty.")
        if (player.hand.size >= 10) throw IllegalStateException("Hand is full (max 10 cards).")

        val card = game.drawPile.removeFirst()
        player.hand.add(card)
        player.performedActions.add(Action.DRAW_CARD)
        onAllRefreshables { refreshAfterCardDrawn(card) }

        val distinctNonPlay = player.performedActions.filter { it != Action.PLAY_COMBINATION }.toSet()
        if (distinctNonPlay.size >= 2) {
            rootService.gameService.endTurn()
        }
    }

    /**
     * Trades one card from the player's hand with one from the exchange area.
     *
     * @param handIndex Index of the card in the player's hand.
     * @param exchangeIndex Index of the card in the exchange area.
     * @throws IllegalStateException if no game is active.
     * @throws IllegalArgumentException if any index is out of range or action already performed.
     */
    fun tradeCard(handIndex: Int, exchangeIndex: Int) {
        val game = rootService.currentGame ?: return
        val player = game.players[game.currentPlayerIndex]
        checkActionRules(player, Action.EXCHANGE_CARD)

        if (handIndex in player.hand.indices && exchangeIndex !in game.exchangeArea.indices) {

            val handCard = player.hand[handIndex]
            val exchangeCard = game.exchangeArea[exchangeIndex]

            player.hand[handIndex] = exchangeCard
            game.exchangeArea[exchangeIndex] = handCard
            player.performedActions.add(Action.EXCHANGE_CARD)
            onAllRefreshables { refreshAfterCardSwapped(handCard, exchangeCard) }

            val distinctNonPlay = player.performedActions.filter { it != Action.PLAY_COMBINATION }.toSet()
            if (distinctNonPlay.size >= 2) {
                rootService.gameService.endTurn()
            }
        }
    }

    /**
     * Plays a valid card combination from the player's hand and adds it to their discard pile.
     *
     * @param selectedCards The cards the player wants to play.
     * @throws IllegalStateException if no game is active or turn rules are violated.
     * @throws IllegalArgumentException if the combination is invalid or cards not in hand.
     */
    fun playCombination(selectedCards: List<KombiCard>) {
        val game = rootService.currentGame ?: return
        val player = game.players[game.currentPlayerIndex]

        checkActionRules(player, Action.PLAY_COMBINATION)

        if (!player.hand.containsAll(selectedCards)) {
            throw IllegalArgumentException("Player does not have all cards for combination.")
        }

        val type = determineCombinationType(selectedCards)
        val points = when (type) {
            "TRIPLE" -> 10
            "QUADRUPLE" -> 15
            "SEQUENCE" -> selectedCards.size * 2
            else -> throw IllegalArgumentException("Unknown combination type.")
        }

        player.hand.removeAll(selectedCards)
        player.discardPile.addAll(selectedCards)
        player.score += points

        if (Action.PLAY_COMBINATION !in player.performedActions) {
            player.performedActions.add(Action.PLAY_COMBINATION)
        }

        onAllRefreshables { refreshAfterCombinationPlayed(player, selectedCards) }

        val distinctNonPlayActions = player.performedActions.filter { it != Action.PLAY_COMBINATION }.toSet()
        if (distinctNonPlayActions.size >= 2 || Action.PASS in player.performedActions) {
            rootService.gameService.endTurn()
        }
    }

    /**
     * Ends the player's turn by passing. If both players pass in succession, ends the game.
     *
     * @throws IllegalStateException if player already passed or no game active.
     */
    fun passed() {
        val game = rootService.currentGame ?: return
        val player = game.players[game.currentPlayerIndex]

        if (Action.PASS in player.performedActions) {
            throw IllegalStateException("You already passed this turn.")
        }

        val distinctNonPlayActions = player.performedActions.filter { it != Action.PLAY_COMBINATION }.toSet()
        if (distinctNonPlayActions.size >= 2) {
            player.performedActions.add(Action.PASS)
            rootService.gameService.endTurn()
            return
        }

        player.performedActions.add(Action.PASS)

        val otherPlayer = game.players[(game.currentPlayerIndex + 1) % 2]
        if (
            otherPlayer.performedActions.size == 1 &&
            player.performedActions.size == 1 &&
            otherPlayer.performedActions[0] == Action.PASS &&
            player.performedActions[0] == Action.PASS
        ) {
            rootService.gameService.endGame()
        } else {
            rootService.gameService.endTurn()
        }
    }

    /**
     * Validates whether the given action is allowed based on the player's current turn state.
     *
     * @param player The player whose action is being validated.
     * @param action The action the player wants to perform.
     * @throws IllegalStateException if 2 distinct actions already performed or PASS already made.
     * @throws IllegalArgumentException if the action was already performed (except PLAY_COMBINATION).
     */
    fun checkActionRules(player: KombiPlayer, action: Action) {
        if (action == Action.PASS) return

        if (Action.PASS in player.performedActions) return

        if (action in player.performedActions && action != Action.PLAY_COMBINATION) {
            throw IllegalArgumentException("Action $action already performed.")
        }

        val distinctActions = player.performedActions.toMutableSet()
        if (action != Action.PLAY_COMBINATION || Action.PLAY_COMBINATION !in distinctActions) {
            distinctActions.add(action)
        }

        if (distinctActions.size > 2) {
            throw IllegalStateException("You have already performed 2 different actions this turn.")
        }
    }

    /**
     * Determines the type of  combination based on the given cards.
     *
     * @param cards The cards the player attempts to play.
     * @return "TRIPLE", "QUADRUPLE", or "SEQUENCE" if valid.
     * @throws IllegalArgumentException if the combination is invalid.
     */
    fun determineCombinationType(cards: List<KombiCard>): String {
        if (cards.size == 3 && cards.all { it.value == cards[0].value }) return "TRIPLE"
        if (cards.size == 4 && cards.all { it.value == cards[0].value }) return "QUADRUPLE"

        if (cards.size >= 3 && cards.all { it.suit == cards[0].suit }) {
            if (isNormalSequence(cards) || isWrapAroundSequence(cards)) return "SEQUENCE"
        }

        throw IllegalArgumentException("Invalid combination.")
    }

    /**
     * Checks if the given cards form a normal straight sequence.
     *
     * @param cards Cards to check.
     * @return True if the cards form a normal sequence.
     */
    private fun isNormalSequence(cards: List<KombiCard>): Boolean {
        val sorted = cards.map { it.value.ordinal }.sorted()
        for (i in 0 until sorted.size - 1) {
            if (sorted[i + 1] != sorted[i] + 1) return false
        }
        return true
    }

    /**
     * Checks if the given cards form a wrap-around straight (like K-A-2-3-4).
     *
     * @param cards Cards to check.
     * @return True if the cards form a wrap-around sequence.
     */
    private fun isWrapAroundSequence(cards: List<KombiCard>): Boolean {
        val ordinals = cards.map { it.value.ordinal }
        if (CardValue.ACE.ordinal !in ordinals || CardValue.TWO.ordinal !in ordinals) return false

        val max = ordinals.maxOrNull() ?: 12
        val shifted = ordinals.map { if (it < max - 3) it + 13 else it }.sorted()
        for (i in 0 until shifted.size - 1) {
            if (shifted[i + 1] != shifted[i] + 1) return false
        }
        return true
    }
}