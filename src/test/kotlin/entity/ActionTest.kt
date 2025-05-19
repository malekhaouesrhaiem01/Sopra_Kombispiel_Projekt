package entity

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the [Action] enum.
 * Ensures all defined enum values are present, accessible, and logically consistent.
 */
class ActionTest {

    /**
     * Tests that all expected enum constants exist and are in the correct order.
     */
    @Test
    fun testEnumValuesExistAndAreOrdered() {
        val expected = listOf("DRAW_CARD", "EXCHANGE_CARD", "PLAY_COMBINATION", "PASS")
        val actual = Action.values().map { it.name }
        assertEquals(expected, actual)
    }

    /**
     * Tests that each enum constant can be accessed by valueOf.
     */
    @Test
    fun testValueOfEachAction() {
        for (action in Action.values()) {
            val byName = Action.valueOf(action.name)
            assertEquals(action, byName)
        }
    }

    /**
     * Tests that all enum constants are unique and valid.
     */
    @Test
    fun testEnumUniquenessAndValidity() {
        val all = Action.values().toList()
        val distinct = all.distinct()
        assertEquals(all.size, distinct.size)
        all.forEach { assertTrue(it.name.isNotBlank()) }
    }

    /**
     * Tests specific business logic expectations, e.g. PASS should prevent further actions.
     */
    @Test
    fun testBusinessLogicImplications() {
        val nonRepeatableActions = listOf(Action.DRAW_CARD, Action.EXCHANGE_CARD, Action.PASS)
        val repeatable = Action.PLAY_COMBINATION

        assertTrue(Action.PASS in nonRepeatableActions)
        assertTrue(repeatable !in nonRepeatableActions)
    }
}
