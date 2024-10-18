package cacophony.automata.minimalization

import cacophony.automata.DFA

fun <E> PartitionRefinement<E>.smallerSet(
    a: PartitionId,
    b: PartitionId,
): PartitionId {
    return if (getElements(a).size < getElements(b).size) a else b
}

// this is class and not dataclass to make equals() and hashcode() test for object identity,
// which is sufficient in our case and makes sure there are no checks for the list equality
class ContractedDFAState<DFAState>(states: List<DFAState>) {
    val originalStates: List<DFAState> = states
}

fun <DFAState> DFA<DFAState>.minimalize(): DFA<ContractedDFAState<DFAState>> {
    return minimalizeImpl(withStates(getAliveStates() intersect getReachableStates()))
}

// assumes dfa contains only alive and reachable states
private fun <DFAState> minimalizeImpl(dfa: DFA<DFAState>): DFA<ContractedDFAState<DFAState>> {
    val baseElements = dfa.getAllStates()

    val acceptingStates = baseElements.filter(dfa::isAccepting)

    val preimagesCalculator = DFAPreimagesCalculator(dfa)

    val refineStructure = PartitionRefinement(baseElements)
    refineStructure.refine(acceptingStates)

    val queue = mutableSetOf(refineStructure.getPartitionId(acceptingStates[0]))

    while (queue.isNotEmpty()) {
        val partitionId = queue.first().also { queue.remove(it) }
        val preimages: Map<Char, Set<DFAState>> = preimagesCalculator.getPreimages(refineStructure.getElements(partitionId))

        for (preimageClass in preimages.values) {
            for ((oldId, newId) in refineStructure.refine(preimageClass)) {
                if (queue.contains(oldId)) {
                    queue.add(newId)
                } else {
                    queue.add(refineStructure.smallerSet(oldId, newId))
                }
            }
        }
    }

    val toNewState: MutableMap<DFAState, ContractedDFAState<DFAState>> = HashMap()
    val allStates =
        refineStructure.getAllPartitions().map {
            val newState = ContractedDFAState(it.toList())
            for (e in newState.originalStates) toNewState[e] = newState
            return@map newState
        }

    val newAcceptingStates = acceptingStates.map { toNewState[it]!! }.toSet()
    val newStartingState = toNewState[dfa.getStartingState()]!!
    val newProductions =
        dfa.getProductions().map { (kv, result) ->
            val (from, symbol) = kv
            val newFrom = toNewState[from]!!
            val newResult = toNewState[result]!!
            return@map Pair(Pair(newFrom, symbol), newResult)
        }.toMap()

    return object : DFA<ContractedDFAState<DFAState>> {
        override fun getStartingState(): ContractedDFAState<DFAState> {
            return newStartingState
        }

        override fun getAllStates(): List<ContractedDFAState<DFAState>> {
            return allStates
        }

        override fun getProductions(): Map<Pair<ContractedDFAState<DFAState>, Char>, ContractedDFAState<DFAState>> {
            return newProductions
        }

        override fun getProduction(
            state: ContractedDFAState<DFAState>,
            symbol: Char,
        ): ContractedDFAState<DFAState>? {
            return newProductions[Pair(state, symbol)]
        }

        override fun isAccepting(state: ContractedDFAState<DFAState>): Boolean {
            return state in newAcceptingStates
        }
    }
}
