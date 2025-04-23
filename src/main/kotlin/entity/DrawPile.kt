package entity


class DrawPile(cards: List<KombiCard>) {
    private val stack: ArrayDeque<KombiCard> = ArrayDeque(cards.shuffled())

    fun draw(): KombiCard? = if (stack.isNotEmpty()) stack.removeFirst() else null
    fun isEmpty(): Boolean = stack.isEmpty()
    fun remainingCards(): Int = stack.size

}
