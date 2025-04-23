package entity

class KombiPlayer(val name: String,
                   val handCards: MutableList<KombiCard> = mutableListOf(),
                   val discardPile: MutableList<List<KombiCard>> = mutableListOf(),
                   var points: Int = 0,
                   var hasPassed: Boolean = false
) {

    fun drawCard(card: KombiCard) {
        if (handCards.size < 10) handCards.add(card)
    }

}