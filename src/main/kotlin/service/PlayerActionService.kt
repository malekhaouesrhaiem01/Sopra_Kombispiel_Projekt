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
        val distinctNonPlay = player.performedActions.filter { it != Action.PLAY_COMBINATION }.toSet()
        if (distinctNonPlay.size >= 2) {
            rootService.gameService.endTurn()
        }

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
        val distinctNonPlay = player.performedActions.filter { it != Action.PLAY_COMBINATION }.toSet()
        if (distinctNonPlay.size >= 2) {
            rootService.gameService.endTurn()
        }
    }

    fun playCombination(selectedCards: List<KombiCard>) {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
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

    fun passed() {
        val game = rootService.currentGame ?: throw IllegalStateException("No game active.")
        val player = game.players[game.currentPlayerIndex]

        //  Already passed this turn
        if (Action.PASS in player.performedActions) {
            throw IllegalStateException("You already passed this turn.")
        }

        //  Allow PASS as third action(PLAYS THE ROLE OF ENDTURN HERE), and end turn immediately if 2 distinct actions already done
        val distinctNonPlayActions = player.performedActions.filter { it != Action.PLAY_COMBINATION }.toSet()
        if (distinctNonPlayActions.size >= 2) {
            player.performedActions.add(Action.PASS)
            rootService.gameService.endTurn()
            return
        }

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



     fun checkActionRules(player: KombiPlayer, action: Action) {
        if (action == Action.PASS) return

        if (Action.PASS in player.performedActions) {
            throw IllegalStateException("No actions allowed after passing.")
        }

        if (action in player.performedActions && action != Action.PLAY_COMBINATION) {
            throw IllegalArgumentException("Action $action already performed.")
        }

        // Determine how many distinct actions (including PLAY_COMBINATION once)
        //this is done with intention  to allow the player to play multiple combinations in one turn
        //playCombination -> exchngeCard -> drawCard is not allowed
        //but drawcard  -> playCombination -> playcombination  is allowed
        // can be evaluated threw tests like i done in playeractionservice or simply threw the application when playing :))
        val distinctActions = player.performedActions.toMutableSet()
        if (action != Action.PLAY_COMBINATION || Action.PLAY_COMBINATION !in distinctActions) {
            distinctActions.add(action)
        }

        if (distinctActions.size > 2) {
            throw IllegalStateException("You have already performed 2 different actions this turn.")
        }
    }




    fun determineCombinationType(cards: List<KombiCard>): String {
        if (cards.size == 3 && cards.all { it.value == cards[0].value }) return "TRIPLE"
        if (cards.size == 4 && cards.all { it.value == cards[0].value }) return "QUADRUPLE"

        if (cards.size >= 3 && cards.all { it.suit == cards[0].suit }) {
            val ordinals = cards.map { it.value.ordinal }
            val sorted = ordinals.sorted()

            // Normal straight (2-3-4-5 etc.)
            var isNormal = true
            for (i in 0 until sorted.size - 1) {
                if (sorted[i + 1] != sorted[i] + 1) {
                    isNormal = false
                    break
                }
            }

            // Wrap-around straight (K-A-2-3-4-5 etc.)
            val hasAce = ordinals.contains(CardValue.ACE.ordinal)
            val hasTwo = ordinals.contains(CardValue.TWO.ordinal)

            var isWrap = false
            if (hasAce && hasTwo) {
                val max = ordinals.maxOrNull() ?: 12
                val shifted = ordinals.map { if (it < max - 3) it + 13 else it }.sorted()
                isWrap = true
                for (i in 0 until shifted.size - 1) {
                    if (shifted[i + 1] != shifted[i] + 1) {
                        isWrap = false
                        break
                    }
                }
            }

            if (isNormal || isWrap) return "SEQUENCE"
        }

        throw IllegalArgumentException("Invalid combination.")
    }


}

