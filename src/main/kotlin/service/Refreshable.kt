package service

import entity.KombiCard
import entity.KombiPlayer

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * with the user interface (GUI) when changes happen in the game state.
 *
 * Default (empty) implementations are provided for each method, so GUI implementations
 * only need to override the methods relevant to their display logic.
 *
 * @see AbstractRefreshingService
 */
interface Refreshable {

    /**
     * Called after a new game has been started.
     * Should initialize the UI for the first turn.
     *
     * @param players The list of players in the new game
     */
    fun refreshAfterStart(players: List<KombiPlayer>) {}

    /**
     * Called when a new turn begins.
     * Should show the current player's hand and allow them to act.
     *
     * @param activePlayer The player whose turn it is
     */
    fun refreshAfterTurnStart(activePlayer: KombiPlayer) {}

    /**
     * Called after a player's turn ends.
     * Should hide the current player's hand and prepare the UI for the next player.
     *
     * @param finishedPlayer The player who just ended their turn
     */
    fun refreshAfterTurnEnd() {}

    /**
     * Called after the game ends.
     * Should display the winner (or a draw) and final scores.
     *
     * @param winner The player who won the game, or null if it's a draw.
     */
    fun refreshAfterGameEnd(winner: KombiPlayer?) {}

    /**
     * Called after the active player draws a card.
     * The UI should update the draw pile and the hand.
     *
     * @param card The card that was drawn
     */
    fun refreshAfterCardDrawn(card: KombiCard) {}

    /**
     * Called after a player swaps a card with the exchange area.
     * Should update both the player's hand and the exchange area in the UI.
     *
     * @param handCard The card from the player's hand
     * @param exchangedCard The card taken from the exchange area
     */
    fun refreshAfterCardSwapped(handCard: KombiCard, exchangedCard: KombiCard) {}

    /**
     * Called after the player plays a valid combination of cards.
     * Should update the discard pile, player's hand, and score display.
     *
     * @param player The player who played the combination
     * @param combination The list of cards played
     */
    fun refreshAfterCombinationPlayed(player: KombiPlayer, combination: List<KombiCard>) {}

    /**
     * Called when a player selects a card (e.g., for highlighting or interaction).
     *
     * @param selectedCard The card that was selected
     */
    fun refreshAfterCardSelected(selectedCard: KombiCard) {}

    /**
     * Displays a message to the user, such as announcing the winner or showing alerts.
     *
     * @param message The message text to display
     */
    fun showMessage(message: String) {}
}
