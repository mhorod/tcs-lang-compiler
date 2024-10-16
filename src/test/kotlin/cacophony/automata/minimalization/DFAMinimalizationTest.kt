package cacophony.automata.minimalization

import cacophony.automata.DFA
import cacophony.automata.createHelper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.random.Random

class DFAMinimalizationTest {
    fun <E> checkThatAutomatasAreEquivalent(dfa: DFA<E>) {
        val minDfa = dfa.minimalize()
        val helper = createHelper(dfa, minDfa)

        for (state in minDfa.getAllStates()) {
            for (oldState in state.originalStates) {
                assert(helper.areEquivalent(oldState, state))
            }
        }

        assert(helper.areEquivalent(dfa.getStartingState(), minDfa.getStartingState()))
    }

    fun <E> checkMinimality(dfa: DFA<E>) {
        val helper = createHelper(dfa, dfa)

        val states = dfa.getAllStates()
        val equivClassesMap: MutableMap<E, MutableSet<E>> = mutableMapOf()

        states.forEach {
            for (s in states) {
                if (helper.areEquivalent(s, it)) {
                    equivClassesMap.getOrPut(s) { mutableSetOf() }.add(it)
                    return@forEach
                }
            }
            throw Exception()
        }

        val equivClassesBruted = equivClassesMap.values.toSet()

        val minDfa = dfa.minimalize()
        val equivalenceClasses = minDfa.getAllStates().map { it.originalStates.toSet() }.toMutableSet()

        val returnedByMinimalization = equivalenceClasses.flatten()
        assertEquals(returnedByMinimalization.toSet().size, returnedByMinimalization.size)

        val missingEquivalenceClass = dfa.getAllStates().minus(returnedByMinimalization.toSet())
        if (missingEquivalenceClass.isNotEmpty())
            equivalenceClasses.add(missingEquivalenceClass.toSet())

        assertEquals(equivClassesBruted, equivalenceClasses)

        println("${dfa.getAllStates().size} ${equivalenceClasses.size}")
    }

    fun <E> check(dfa: DFA<E>) {
        if (dfa.isLanguageEmpty()) {
            try {
                dfa.minimalize()
            }
            catch (e: IllegalArgumentException) {
                return
            }
            assert(false)
        }
        checkThatAutomatasAreEquivalent(dfa)
        checkMinimality(dfa)
    }

    private fun generateDFA(n: Int, seed: Int): DFA<Int> {
        val random = Random(seed)
        val symbols = "abc";
        val states = (1..n).toList()
        val accepting = states.map { random.nextDouble() < 0.2 }
        val start = states.random(random)
        val productions: MutableMap<Pair<Int, Char>, Int> = mutableMapOf()
        for (s in states) {
            for (c in symbols) {
                if (random.nextDouble() < 0.2)
                    productions[Pair(s, c)] = states.random(random)
            }
        }
        return object : DFA<Int> {
            override fun getStartingState(): Int {
                return start
            }

            override fun getAllStates(): List<Int> {
                return states
            }

            override fun getProductions(): Map<Pair<Int, Char>, Int> {
                return productions
            }

            override fun getProduction(state: Int, symbol: Char): Int? {
                return productions[Pair(state, symbol)]
            }

            override fun isAccepting(state: Int): Boolean {
                return accepting[state - 1]
            }

        }
    }
    @Test
    fun testRandom() {
        (0..2000).forEach { check(generateDFA(1 + it % 50, it)) }
    }
}