package entity

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for the [Action] enum class.
 * Verifies the defined actions, their names, and enum order.
 */
class ActionTest {

    /**
     * Tests that all expected action constants exist in the enum.
     */
    @Test
    fun testAllEnumValuesExist() {
        val expected = setOf(
            Action.DRAW_CARD,
            Action.EXCHANGE_CARD,
            Action.PLAY_SEQUENCE,
            Action.PLAY_TRIPLE,
            Action.PLAY_QUADRUPLE,
            Action.PASS
        )

        val actual = Action.values().toSet()

        assertEquals(expected.size, actual.size, "Enum should contain exactly 6 values.")
        assertTrue(actual.containsAll(expected), "Enum must contain all defined action types.")
    }

    /**
     * Tests that the name of each enum constant matches its declaration.
     */
    @Test
    fun testActionEnumNames() {
        assertEquals("DRAW_CARD", Action.DRAW_CARD.name)
        assertEquals("EXCHANGE_CARD", Action.EXCHANGE_CARD.name)
        assertEquals("PLAY_SEQUENCE", Action.PLAY_SEQUENCE.name)
        assertEquals("PLAY_TRIPLE", Action.PLAY_TRIPLE.name)
        assertEquals("PLAY_QUADRUPLE", Action.PLAY_QUADRUPLE.name)
        assertEquals("PASS", Action.PASS.name)
    }

    /**
     * Tests the ordinal order of the enum constants.
     */
    @Test
    fun testOrdinalOrder() {
        assertEquals(0, Action.DRAW_CARD.ordinal)
        assertEquals(1, Action.EXCHANGE_CARD.ordinal)
        assertEquals(2, Action.PLAY_SEQUENCE.ordinal)
        assertEquals(3, Action.PLAY_TRIPLE.ordinal)
        assertEquals(4, Action.PLAY_QUADRUPLE.ordinal)
        assertEquals(5, Action.PASS.ordinal)
    }
}
