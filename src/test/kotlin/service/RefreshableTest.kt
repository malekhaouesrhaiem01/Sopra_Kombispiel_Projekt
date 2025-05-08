package service


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
    }

    override fun refreshAfterStart() {
        refreshAfterStartCalled = true
    }

    override fun refreshAfterTurnStart() {
        refreshAfterTurnStartCalled = true
    }

    override fun refreshAfterTurnEnd() {
        refreshAfterTurnEndCalled = true
    }

    override fun refreshAfterCardDrawn() {
        refreshAfterCardDrawnCalled = true
    }

    override fun refreshAfterCardSwapped() {
        refreshAfterCardSwappedCalled = true
    }

    override fun refreshAfterCombinationPlayed() {
        refreshAfterCombinationPlayedCalled = true
    }

    override fun refreshAfterGameEnd() {
        refreshAfterGameEndCalled = true
    }

    override fun refreshAfterCardSelected() {
        refreshAfterCardSelectedCalled = true
    }
}
