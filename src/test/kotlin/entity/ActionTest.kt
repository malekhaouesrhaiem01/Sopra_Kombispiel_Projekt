package entity

import kotlin.test.*

/**
 * Test class for the Action enum.
 * Verifies point calculation logic and default behavior.
 */
class ActionTest {

    /**
     * Tests that TRIPLE and QUADRUPLE return fixed flat points.
     */
    @Test
    fun testFixedPointActions() {
        assertEquals(10, Action.TRIPLE.calculatePoints(), "TRIPLE should award 10 points")
        assertEquals(15, Action.QUADRUPLE.calculatePoints(), "QUADRUPLE should award 15 points")
    }

    /**
     * Tests that COMBINATION returns the correct score per card.
     */
    @Test
    fun testCombinationPoints() {
        assertEquals(6, Action.COMBINATION.calculatePoints(3), "3-card COMBINATION should return 6 points")
        assertEquals(8, Action.COMBINATION.calculatePoints(4), "4-card COMBINATION should return 8 points")
        assertEquals(10, Action.COMBINATION.calculatePoints(5), "5-card COMBINATION should return 10 points")
    }

    /**
     * Tests that NOACTION always returns 0 points regardless of input.
     */
    @Test
    fun testNoActionPoints() {
        assertEquals(0, Action.NOACTION.calculatePoints(), "NOACTION should return 0 points")
        assertEquals(0, Action.NOACTION.calculatePoints(100), "NOACTION should ignore card count and return 0")
    }
}
