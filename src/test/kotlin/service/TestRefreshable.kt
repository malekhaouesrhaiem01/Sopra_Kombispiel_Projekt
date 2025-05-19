package service

import entity.KombiCard
import entity.KombiPlayer

/**
 * Dummy [Refreshable] implementation to test whether refresh methods are triggered.
 * All flags are resettable via [reset].
 */
class TestRefreshable : Refreshable {

    var refreshAfterStartCalled = false
        private set

    var refreshAfterTurnStartCalled = false
        private set

    var refreshAfterTurnEndCalled = false
        private set

    var refreshAfterCardDrawnCalled = false
        private set

    var refreshAfterCardSwappedCalled = false
        private set

    var refreshAfterCombinationPlayedCalled = false
        private set

    var refreshAfterGameEndCalled = false
        private set

    var refreshAfterCardSelectedCalled = false
        private set

    var showMessageCalled = false
        private set

    var lastMessage: String? = null

    /**
     * Resets all refresh flags to false.
     */
    fun reset() {
        refreshAfterStartCalled = false
        refreshAfterTurnStartCalled = false
        refreshAfterTurnEndCalled = false
        refreshAfterCardDrawnCalled = false
        refreshAfterCardSwappedCalled = false
        refreshAfterCombinationPlayedCalled = false
        refreshAfterGameEndCalled = false
        refreshAfterCardSelectedCalled = false
        showMessageCalled = false
        lastMessage = null
    }

    override fun refreshAfterStart(players: List<KombiPlayer>) {
        refreshAfterStartCalled = true
    }

    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {
        refreshAfterTurnStartCalled = true
    }

    override fun refreshAfterTurnEnd(finishedPlayer: KombiPlayer) {
        refreshAfterTurnEndCalled = true
    }

    override fun refreshAfterCardDrawn(card: KombiCard) {
        refreshAfterCardDrawnCalled = true
    }

    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {
        refreshAfterCardSwappedCalled = true
    }

    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {
        refreshAfterCombinationPlayedCalled = true
    }

    override fun refreshAfterGameEnd(winner: KombiPlayer, loser: KombiPlayer) {
        refreshAfterGameEndCalled = true
    }

    override fun refreshAfterCardSelected(selectedCard: KombiCard) {
        refreshAfterCardSelectedCalled = true
    }

    override fun showMessage(message: String) {
        showMessageCalled = true
        lastMessage = message
    }
}

