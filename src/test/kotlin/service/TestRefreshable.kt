package service

/**
 * Dummy implementation of [Refreshable] to verify UI callback methods.
 */
class TestRefreshable : Refreshable {
    var refreshAfterStartCalled = false

    override fun refreshAfterStart() {
        refreshAfterStartCalled = true
    }
}
