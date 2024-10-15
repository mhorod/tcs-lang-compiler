package cacophony.automata

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PartitionRefinementTest {
    @Test
    fun baseTest() {
        val pr = PartitionRefinement(listOf(1, 2))
        pr.refine(listOf(1))
    }
}