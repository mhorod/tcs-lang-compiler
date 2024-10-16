package cacophony.automata.minimalization

import cacophony.automata.DFA

fun <E> PartitionRefinement<E>.smallerSet(a: PartitionId, b: PartitionId): PartitionId {
    return if (getElements(a).size < getElements(b).size) a else b
}

// this is class and not dataclass to make equals() and hashcode() test for object identity,
// which is sufficient in our case and makes sure there are no checks for the list equality
class ContractedDFAState<DFAState>(states: List<DFAState>) {
    val originalStates: List<DFAState> = states
}

fun <DFAState> DFA<DFAState>.minimalize(): DFA<ContractedDFAState<DFAState>> {
    val baseSet = mutableSetOf<DFAState?>(null);
    baseSet.addAll(getAllStates())

    val symbols = getSymbols()

    listOf(1, 2, 3, 4)

    val acceptingStates = baseSet.filter(this::isAcceptingNullSafe)

    val preimageCalculator = DFAPreimageCalculator(this)

    val refineStructure = PartitionRefinement(baseSet)
    refineStructure.refine(acceptingStates)

    val queue = mutableSetOf(refineStructure.getPartitionId(null))

    while (queue.isNotEmpty()) {
        val partitionId = queue.first().also { queue.remove(it) }
        val partitionElements = refineStructure.getElements(partitionId).toList()
        for (symbol in symbols) {
            val preimage = preimageCalculator.getPreimage(partitionElements, symbol)
            for ((oldId, newId) in refineStructure.refine(preimage)) {
                if (queue.contains(oldId)) {
                    queue.add(newId)
                }
                else {
                    queue.add(refineStructure.smallerSet(oldId, newId))
                }
            }
        }
    }

    val toNewState: MutableMap<DFAState, ContractedDFAState<DFAState>> = HashMap()
    val allStates = refineStructure.getAllPartitions().map {
        val withoutNull: List<DFAState> = it.filterNotNull()
        if (withoutNull.size != it.size) return@map null
        val newState = ContractedDFAState(withoutNull)
        for (e in withoutNull) toNewState[e] = newState
        return@map newState
    }.filterNotNull()

    val newAcceptingStates = acceptingStates.map { toNewState[it]!! }.toSet()
    val newStartingState = toNewState[getStartingState()] ?: throw IllegalArgumentException("DFA interface does not allow automata for empty languages")
    val newProductions = getProductions().map { (kv, result) ->
        val (from, symbol) = kv
        val newFrom = toNewState[from]
        val newResult = toNewState[result]
        if (newFrom != null && newResult != null) return@map Pair(Pair(newFrom, symbol), newResult)
        return@map null
    }.filterNotNull().toMap()

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

        override fun getProduction(state: ContractedDFAState<DFAState>, symbol: Char): ContractedDFAState<DFAState>? {
            return newProductions[Pair(state, symbol)]
        }

        override fun isAccepting(state: ContractedDFAState<DFAState>): Boolean {
            return state in newAcceptingStates
        }

    }
}