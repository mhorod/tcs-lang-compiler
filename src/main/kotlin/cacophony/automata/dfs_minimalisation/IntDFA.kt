package cacophony.automata.dfs_minimalisation

import cacophony.automata.DFA

////class IntDFA(stateCount: Int, startingState: Int, ) : DFA<Int> {
////    val accepting: BooleanArray;
////
////    init {
////        accepting = BooleanArray(n)
////    }
////
////    fun getStateCount(): Int {
////        TODO()
////    }
////
////    override fun isAccepting(state: Int): Boolean {
////        TODO("Not yet implemented")
////    }
////
////    override fun getProduction(state: Int, symbol: Char): Int? {
////        TODO("Not yet implemented")
////    }
////
////    override fun getProductions(): Map<Pair<Int, Char>, Int> {
////        TODO("Not yet implemented")
////    }
////}
//
//
//fun tester(): DFA<Long> {
//    return null!!
//}
//
//private fun <DFAState> DFA<DFAState>.toIntDFA(): DFA<Int> {
//    val oldStates = getAllStates()
//    val newStates = (0..oldStates.size).toList()
//    val stateMap = oldStates.zip(newStates).toMap()
//    val newProductions = getProductions().map { (kv, res) -> Pair(Pair(stateMap[kv.first]!!, kv.second), stateMap[res]) }.toMap()
//    val newStartingState = stateMap[getStartingState()]!!
//    val acceptingLambda = { x: Int -> isAccepting(oldStates[x]!!)}
//
//    return object : DFA<Int> {
//        override fun getStartingState(): Int {
//            return newStartingState
//        }
//
//        override fun getAllStates(): List<Int> {
//            return newStates
//        }
//
//        override fun getProductions(): Map<Pair<Int, Char>, Int> {
//            return newProductions
//        }
//
//        override fun getProduction(state: Int, symbol: Char): Int? {
//            return newProductions[Pair(state, symbol)]
//        }
//
//        override fun isAccepting(state: Int): Boolean {
//            return acceptingLambda(state)
//        }
//    }
//}