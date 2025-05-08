package service

import entity.KombiGame
/**
 * The root service class is responsible for managing services and the entity layer reference.
 * This class acts as a central hub for every other service within the application.
 *
 */

class RootService {

    /**
     * The currently active Kombi-Duel game. Can be `null` if not started yet.
     */
    var currentGame: KombiGame? = null

    /**
     * Game logic service (starting/ending game, turns).
     */
    val gameService = GameService(this)

    /**
     * Player action logic service (draw, exchange, play, pass).
     */
    val playerActionService = PlayerActionService(this)

    /**
     * Adds a single [Refreshable] UI callback to all sub-services.
     */
    fun addRefreshable(newRefreshable: Refreshable) {
        gameService.addRefreshable(newRefreshable)
        playerActionService.addRefreshable(newRefreshable)
    }

    /**
     * Adds multiple [Refreshable] callbacks at once to all sub-services.
     */
    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }
}
