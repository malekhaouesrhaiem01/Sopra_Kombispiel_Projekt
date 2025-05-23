package service

import entity.KombiCard
import entity.KombiPlayer

/**
 * Dummy [Refreshable] implementation used for testing whether refresh methods are triggered.
 *
 * Each refresh method sets a corresponding boolean flag, which can be checked in assertions.
 * The [reset] function clears all flags, making the instance reusable across multiple test cases.
 */
class TestRefreshable : Refreshable {

    /** True if [refreshAfterStart] was called. */
    var refreshAfterStartCalled = false
        private set

    /** True if [refreshAfterTurnStart] was called. */
    var refreshAfterTurnStartCalled = false
        private set

    /** True if [refreshAfterTurnEnd] was called. */
    var refreshAfterTurnEndCalled = false
        private set

    /** True if [refreshAfterCardDrawn] was called. */
    var refreshAfterCardDrawnCalled = false
        private set

    /** True if [refreshAfterCardSwapped] was called. */
    var refreshAfterCardSwappedCalled = false
        private set

    /** True if [refreshAfterCombinationPlayed] was called. */
    var refreshAfterCombinationPlayedCalled = false
        private set

    /** True if [refreshAfterGameEnd] was called. */
    var refreshAfterGameEndCalled = false
        private set

    /** True if [refreshAfterCardSelected] was called. */
    var refreshAfterCardSelectedCalled = false
        private set

    /** True if [showMessage] was called. */
    var showMessageCalled = false
        private set

    /** Stores the last message received in [showMessage], or null if none. */
    var lastMessage: String? = null

    /**
     * Resets all refresh flags and the last message to their default state.
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

    /**
     * Called when a game starts.
     * @param players The list of participating players.
     */
    override fun refreshAfterStart(players: List<KombiPlayer>) {
        refreshAfterStartCalled = true
    }

    /**
     * Called when a new turn starts.
     * @param activePlayer The player whose turn it is.
     */
    override fun refreshAfterTurnStart(activePlayer: KombiPlayer) {
        refreshAfterTurnStartCalled = true
    }

    /**
     * Called when a turn ends.
     */
    override fun refreshAfterTurnEnd() {
        refreshAfterTurnEndCalled = true
    }

    /**
     * Called when a card is drawn by a player.
     * @param card The card that was drawn.
     */
    override fun refreshAfterCardDrawn(card: KombiCard) {
        refreshAfterCardDrawnCalled = true
    }

    /**
     * Called when two cards are swapped (hand ↔ exchange area).
     * @param handCard The card from the player's hand.
     * @param exchangedCard The card from the exchange area.
     */
    override fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {
        refreshAfterCardSwappedCalled = true
    }

    /**
     * Called when a valid combination is played by a player.
     * @param player The player who played the combination.
     * @param combination The list of cards forming the combination.
     */
    override fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {
        refreshAfterCombinationPlayedCalled = true
    }

    /**
     * Called when the game ends.
     * @param winner The winning player, or null if it's a draw.
     */
    override fun refreshAfterGameEnd(winner: KombiPlayer?) {
        refreshAfterGameEndCalled = true
    }

    /**
     * Called when a card is selected in the UI.
     * @param selectedCard The card that was selected.
     */
    override fun refreshAfterCardSelected(selectedCard: KombiCard) {
        refreshAfterCardSelectedCalled = true
    }

    /**
     * Called to display a message to the user.
     * @param message The message text.
     */
    override fun showMessage(message: String) {
        showMessageCalled = true
        lastMessage = message
    }
}
