package entity

class ExchangeArea (initialCards: List<KombiCard>) {
    val cards: MutableList<KombiCard> = initialCards.toMutableList()

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
