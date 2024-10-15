package cacophony.automata

data class PartitionId(val id: Int)

class PartitionRefinement<E>(baseSet: Collection<E>) {
    private val elementToPartitionId: MutableMap<E, PartitionId> = HashMap();
    private val partitionToElements: MutableMap<PartitionId, MutableSet<E>> = HashMap();

    init {
        baseSet.associateWithTo(elementToPartitionId) { PartitionId(0) }
        partitionToElements[PartitionId(0)] = baseSet.toMutableSet()
    }

    fun refine(refineBy: Collection<E>): List<Pair<PartitionId, PartitionId>> {
        refineBy.groupBy { e -> elementToPartitionId[e]!! }.forEach { (id, elements) -> {
            if (partitionToElements[id]!!.size == elements.size)
                return@forEach
            var x = 1;
        }}
        return listOf()
    }

    fun getElements(id: PartitionId): Set<E> {
        return partitionToElements[id]!!
    }

    fun getPartitions(): Collection<Set<E>> {
        return partitionToElements.values
    }
}