package entity

class Kombigame(val players: List<KombiPlayer>,
                var activePlayerIndex: Int = 0,
                val drawPile: DrawPile,
                val exchangeArea: ExchangeArea,
                var turnActions: Int = 0,
                var gameOver: Boolean = false
) {
    fun getActivePlayer(): KombiPlayer = players[activePlayerIndex]
    fun nextPlayer() {
        activePlayerIndex = (activePlayerIndex + 1) % 2
        turnActions = 0
        players[activePlayerIndex].hasPassed = false
    }




















}