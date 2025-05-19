import gui.SopraApplication

/**
 * Main entry point that starts the [SopraApplication]
 *
 * Once the application is closed, it prints a message indicating the end of the application.
 */
fun main() {
    println("🚀 Launching app...") // MUST print this
    SopraApplication().show()
    println("👋 Application ended")
}