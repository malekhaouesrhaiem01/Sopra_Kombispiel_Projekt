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
    }

    fun tradeCard(handIndex: Int, exchangeIndex: Int) {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]
        checkActionRules(player, Action.EXCHANGE_CARD)

        if (handIndex !in player.hand.indices) throw IllegalArgumentException("Invalid hand index.")
        if (exchangeIndex !in game.exchangeArea.indices) throw IllegalArgumentException("Invalid exchange index.")

        val handCard = player.hand[handIndex]
        val exchangeCard = game.exchangeArea[exchangeIndex]

        player.hand[handIndex] = exchangeCard
        game.exchangeArea[exchangeIndex] = handCard
        player.performedActions.add(Action.EXCHANGE_CARD)
        onAllRefreshables { refreshAfterCardSwapped(handCard, exchangeCard) }
    }

    fun playCombinations(combos: List<List<KombiCard>>) {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]
        checkActionRules(player, Action.PLAY_COMBINATION)

        var totalPoints = 0
        val allCards = mutableListOf<KombiCard>()

        for (combo in combos) {
            if (!player.hand.containsAll(combo)) {
                throw IllegalArgumentException("Player does not have all cards for combination.")
            }

            val type = determineCombinationType(combo)
            val points = when (type) {
                "TRIPLE" -> 10
                "QUADRUPLE" -> 15
                "SEQUENCE" -> combo.size * 2
                else -> throw IllegalArgumentException("Unknown combination type.")
            }

            totalPoints += points
            allCards.addAll(combo)
        }

        player.hand.removeAll(allCards)
        player.discardPile.addAll(allCards)
        player.score += totalPoints

        if (Action.PLAY_COMBINATION !in player.performedActions) {
            player.performedActions.add(Action.PLAY_COMBINATION)
        }

        onAllRefreshables { refreshAfterCombinationPlayed(player, allCards) }
    }
    fun passed() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]

        if (player.performedActions.size >= 2) {
            throw IllegalStateException("You cannot perform more than 2 actions per turn.")
        }
        if (Action.PASS in player.performedActions) throw IllegalStateException("Player already passed this turn.")

        player.performedActions.add(Action.PASS)
        val otherPlayer = game.players[(game.currentPlayerIndex + 1) % 2]

        if (otherPlayer.performedActions.size == 1 &&
            player.performedActions.size == 1 &&
            otherPlayer.performedActions[0] == Action.PASS &&
            player.performedActions[0] == Action.PASS
        ) {
            // Beide haben genau 1x PASS → Spiel beenden
            rootService.gameService.endGame()
        } else {
            // Sonst ganz normal den Zug beenden
            rootService.gameService.endTurn()
        }
    }


    private fun checkActionRules(player: KombiPlayer, action: Action) {
        if (Action.PASS in player.performedActions) throw IllegalStateException("No actions allowed after passing.")
        if (action != Action.PLAY_COMBINATION && action in player.performedActions) throw IllegalArgumentException("Action $action already performed.")
        if (player.performedActions.size >= 2 && action !in player.performedActions) throw IllegalStateException("Max two different actions per turn.")
    }

    private fun determineCombinationType(cards: List<KombiCard>): String {
        if (cards.size == 3 && cards.all { it.value == cards[0].value }) return "TRIPLE"
        if (cards.size == 4 && cards.all { it.value == cards[0].value }) return "QUADRUPLE"

        if (cards.size >= 3 && cards.all { it.suit == cards[0].suit }) {
            val sorted = cards.map { it.value.ordinal }.sorted()
            for (i in 0 until sorted.size - 1) {
                val current = sorted[i]
                val next = sorted[i + 1]
                if ((current + 1) % 13 != next) throw IllegalArgumentException("Not a valid sequence.")
            }
            return "SEQUENCE"
        }

        throw IllegalArgumentException("Invalid combination.")
    }
}
