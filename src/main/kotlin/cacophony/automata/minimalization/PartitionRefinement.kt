package cacophony.automata.minimalization

data class PartitionId(val id: Int)

class PartitionRefinement<E>(baseSet: Collection<E>) {
    private val elementToPartitionId: MutableMap<E, PartitionId> = HashMap()
    private val partitionToElements: MutableMap<PartitionId, MutableSet<E>> = HashMap()

    init {
        baseSet.associateWithTo(elementToPartitionId) { PartitionId(0) }
        partitionToElements[PartitionId(0)] = baseSet.toMutableSet()
    }

    fun refine(refineBy: Collection<E>): List<Pair<PartitionId, PartitionId>> {
        return refineBy
            .groupBy(this::getPartitionId)
            .map { (oldId, elements) ->
                val oldPartition = partitionToElements[oldId]!!
                val newPartition = elements.toMutableSet()
                if (oldPartition.size == newPartition.size) return@map null

                val newId = PartitionId(partitionToElements.size)

                partitionToElements[newId] = newPartition
                oldPartition.removeAll(newPartition)
                for (e in newPartition) elementToPartitionId[e] = newId

                return@map Pair(oldId, newId)
            }
            .filterNotNull()
    }

    fun getPartitionId(e: E): PartitionId {
        return elementToPartitionId[e]!!
    }

    fun getElements(id: PartitionId): Set<E> {
        return partitionToElements[id]!!
    }

    fun getAllPartitions(): Collection<Set<E>> {
        return partitionToElements.values
    }
}
