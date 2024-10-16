package cacophony.automata.minimalization

import cacophony.automata.DFA

class DFAPreimageCalculator<DFAState>(dfa: DFA<DFAState>) {
    private val cache: MutableMap<Pair<DFAState?, Char>, MutableList<DFAState?>> = HashMap()

    init {
        val symbols = dfa.getSymbols()
        val states = dfa.getAllStatesNullSafe()

        for (state in states) {
            for (symbol in symbols) {
                val nextState = dfa.getProductionNullSafe(state, symbol)
                cache.getOrPut(Pair(nextState, symbol)) { mutableListOf() }.add(state)
            }
        }
    }

    fun getPreimage(states: List<DFAState?>, symbol: Char): List<DFAState?> {
        return states.flatMap { cache[Pair(it, symbol)] ?: listOf() }
    }
}

fun <DFAState> DFA<DFAState>.getSymbols(): List<Char> {
    return getProductions().keys.map { it.second }.toSet().toList()
}

fun <DFAState> DFA<DFAState>.isAcceptingNullSafe(state: DFAState?): Boolean {
    return if (state == null) false else isAccepting(state)
}

fun <DFAState> DFA<DFAState>.getProductionNullSafe(state: DFAState?, symbol: Char): DFAState? {
    return if (state == null) null else getProduction(state, symbol)
}

fun <DFAState> DFA<DFAState>.getAllStatesNullSafe(): List<DFAState?> {
    return getAllStates().plus(null)
}