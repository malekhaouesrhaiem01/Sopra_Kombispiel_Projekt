package entity
/**
 * Represents the exchange area containing exactly three cards available for trading.
 *
 * The area always maintains three cards: when one is removed, a new one must be added.
 *
 * @property cards The current cards visible in the exchange area.
 */
class ExchangeArea (initialCards: List<KombiCard>) {
    val cards: MutableList<KombiCard> = initialCards.toMutableList()

    /**
     * Swaps a card from the player's hand with a card from the exchange area.
     *
     * Removes [areaCard] from the exchange area and inserts [playerCard] in its place.
     * The total number of cards in the exchange area remains unchanged.
     *
     * @param playerCard The card to be added to the exchange area from the player's hand.
     * @param areaCard The card to be taken from the exchange area.
     * @return `true` if the exchange was successful, otherwise `false`.
     */
    fun exchange(playerCard: KombiCard, areaCard: KombiCard): Boolean {
        return if (cards.contains(areaCard)) {
            cards.remove(areaCard)
            cards.add(playerCard)
            true
        } else {
            false
        }
    }
}
