package service


/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the GUI classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * GUI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 */
interface Refreshable{
    /**
     * Called after a new game has been started.
     * Should reset the UI to the initial state for the first player.
     */
    fun refreshAfterStart() {}

    /**
     * Called when a new turn begins.
     * Should show the active player's hand and current turn information.
     */
    fun refreshAfterTurnStart() {}

    /**
     * Called after a player's turn ends.
     * Should hide that player's hand and wait for the next player's confirmation.
     */
    fun refreshAfterTurnEnd() {}

    /**
     * Called after the game ends.
     * Should show final scores and announce the winner.
     */
    fun refreshAfterGameEnd() {}

    /**
     * Called after the active player draws a card.
     * UI can update the hand and draw pile visually.
     */
    fun refreshAfterCardDrawn() {}

    /**
     * Called after a card was exchanged with the exchange area.
     * Should update both hand and exchange pool in the UI.
     */
    fun refreshAfterCardSwapped() {}

    /**
     * Called after the player successfully played a valid combination.
     * Updates the discard pile, hand, and score.
     */
    fun refreshAfterCombinationPlayed() {}

    /**
     * Called when a card is selected (optional, for UI feedback).
     */
    fun refreshAfterCardSelected() {}



}