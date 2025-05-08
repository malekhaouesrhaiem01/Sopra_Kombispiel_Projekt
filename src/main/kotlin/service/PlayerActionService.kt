package service

import entity.*

/**
 * Handles all player-specific actions during a turn.
 * Supports drawing cards, trading, playing combinations, and passing.
 * Enforces all action rules using [Action] and the player's [//performedActions] list.
 *
 * @property rootService The central access point to game state and services
 */
class PlayerActionService(
    private val rootService: RootService
) : AbstractRefreshingService() {

    /**
     * Player draws a card from the draw pile.
     */
    fun drawCard() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]

        checkActionRules(player, Action.DRAW_CARD)

        if (game.drawPile.isEmpty())
            throw IllegalStateException("Draw pile is empty.")
        if (player.hand.size >= 10)
            throw IllegalStateException("Hand is full (10 cards).")

        val card = game.drawPile.removeFirst()
        player.hand.add(card)
        player.performedActions.add(Action.DRAW_CARD)

        onAllRefreshables { refreshAfterCardDrawn() }
    }

    /**
     * Player trades a hand card with one from the exchange area.
     */
    fun tradeCard(handIndex: Int, exchangeIndex: Int) {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]

        checkActionRules(player, Action.EXCHANGE_CARD)

        if (handIndex !in player.hand.indices)
            throw IllegalArgumentException("Invalid hand card index.")
        if (exchangeIndex !in 0..2 || exchangeIndex >= game.exchangeArea.size)
            throw IllegalArgumentException("Invalid exchange area index.")

        val handCard = player.hand[handIndex]
        val exchangeCard = game.exchangeArea[exchangeIndex]

        player.hand[handIndex] = exchangeCard
        game.exchangeArea[exchangeIndex] = handCard

        player.performedActions.add(Action.EXCHANGE_CARD)

        onAllRefreshables { refreshAfterCardSwapped() }
    }

    /**
     * Player plays a valid card combination from hand.
     */
    fun playCombination(cards: List<KombiCard>) {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]

        val actionType = determineCombinationType(cards)
        checkActionRules(player, actionType)

        if (!player.hand.containsAll(cards))
            throw IllegalArgumentException("Not all selected cards are in hand.")

        player.hand.removeAll(cards)
        player.discardPile.addAll(cards)
        player.score += scoreCombination(cards)
        player.performedActions.add(actionType)

        onAllRefreshables { refreshAfterCombinationPlayed() }
    }

    /**
     * Player chooses to pass the turn.
     */
    fun passed() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]

        if (player.performedActions.isNotEmpty())
            throw IllegalStateException("Cannot pass after performing an action.")

        player.performedActions.add(Action.PASS)

        onAllRefreshables { refreshAfterTurnEnd() }
    }

    /**
     * Ensures the action can be legally performed.
     */
    private fun checkActionRules(player: KombiPlayer, action: Action) {
        if (Action.PASS in player.performedActions)
            throw IllegalStateException("Cannot perform action after passing.")
        if (action in player.performedActions)
            throw IllegalArgumentException("Action $action already performed.")
        if (player.performedActions.size >= 2)
            throw IllegalStateException("Only 2 actions allowed per turn.")
    }

    /**
     * Determines the combination type.
     */
    private fun determineCombinationType(cards: List<KombiCard>): Action {
        if (cards.size == 3 && cards.all { it.value == cards[0].value }) return Action.PLAY_TRIPLE
        if (cards.size == 4 && cards.all { it.value == cards[0].value }) return Action.PLAY_QUADRUPLE

        if (cards.size >= 3 && cards.all { it.suit == cards[0].suit }) {
            val ordinals = cards.map { it.value.ordinal }.sorted()
            val isSequential = ordinals.zipWithNext().all { it.second == it.first + 1 }
            if (isSequential) return Action.PLAY_SEQUENCE
        }

        throw IllegalArgumentException("Invalid combination.")
    }

    /**
     * Calculates the score for a played combination.
     */
    private fun scoreCombination(cards: List<KombiCard>): Int {
        return when (determineCombinationType(cards)) {
            Action.PLAY_TRIPLE -> 10
            Action.PLAY_QUADRUPLE -> 15
            Action.PLAY_SEQUENCE -> 2 * cards.size
            else -> 0
        }
    }
}
