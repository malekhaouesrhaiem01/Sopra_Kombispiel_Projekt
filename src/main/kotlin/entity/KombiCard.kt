package entity



data class KombiCard(val suit: CardSuit, val value: CardValue) {
    /**
     * compares two [KombiCard]s according to the [Enum.ordinal] value of their [CardSuit]
     * (i.e., the order in which the suits are declared in the enum class)
     */
    operator fun compareTo(other: KombiCard) = this.value.ordinal - other.value.ordinal
    /**
     * Returns the representation of the card as a character string,
     * e.g. "A♥"
     *
     * The output combines the symbol of the card value with the Unicode character of the card shape.
     *
     * @return String representation of the card in the format <value><form>, e.g. "Q♦"
     */
    override fun toString() = "$suit$value"

}