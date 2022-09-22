class SetsEnumerator {
    private val sets = mutableListOf<Set<Int>>()
    private var currentNum = 0
    fun registerOrGetSetsNumber(set: Set<Int>): Int {
        for (i in 0 until currentNum) {
            if (sets[i] == set)
                return i
        }
        sets.add(set)
        val setNumber = currentNum
        ++currentNum
        return setNumber
    }
}

class SetsMarker {
    val markedSets = mutableSetOf<Set<Int>>()
    fun isMarked(set: Set<Int>): Boolean = markedSets.contains(set)

    fun mark(set: Set<Int>) {
        markedSets.add(set)
    }
}

fun Nfa.getAlphabet(): String = translationRules.map(TranslationRule::char).joinToString("")

fun nfaToDfa(nfa: Nfa): Nfa {
    val enumerator = SetsEnumerator()
    val setsWithProcessedOutputs = SetsMarker()
    val alphabet = nfa.getAlphabet()
    val stackOfSetsToMark = mutableListOf(nfa.initialStates)
    setsWithProcessedOutputs.mark(nfa.initialStates)
    val newTranslationRules = mutableListOf<TranslationRule>()
    while (stackOfSetsToMark.isNotEmpty()) {
        val currentStates = stackOfSetsToMark.removeLast()
        val currentStatesIndex = enumerator.registerOrGetSetsNumber(currentStates)
        for (symbol in alphabet) {
            val newStates = currentStates.flatMap { possibleState ->
                nfa.translationRules.possibleStatesOnInput(possibleState, symbol)
            }.toSet()
            val newStatesIndex = enumerator.registerOrGetSetsNumber(newStates)
            newTranslationRules.add(TranslationRule(symbol, currentStatesIndex, newStatesIndex))
            if (!setsWithProcessedOutputs.isMarked(newStates)) {
                setsWithProcessedOutputs.mark(newStates)
                stackOfSetsToMark.add(newStates)
            }
        }
    }
    return Nfa(
        setOf(enumerator.registerOrGetSetsNumber(nfa.initialStates)),
        setsWithProcessedOutputs.markedSets.filter { it.intersect(nfa.acceptingStates).isNotEmpty() }
            .map(enumerator::registerOrGetSetsNumber).toSet(),
        newTranslationRules
    )
}
