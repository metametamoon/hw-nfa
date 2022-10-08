/**
 * Eliminates dead translations
 */
fun Nfa.optimizedTranslationRules(): Nfa {
    val states = translationRules.flatMap { listOf(it.oldState, it.newState) }
    val adjacencyList = translationRules.groupBy({ it.oldState }) { it.newState }
    val visited = MutableList(states.max() + 1) { false }
    fun dfs(vertex: Int) {
        visited[vertex] = true
        for (neighbor in adjacencyList[vertex].orEmpty()) {
            if (!visited[neighbor])
                dfs(neighbor)
        }
    }
    for (initState in initialStates) {
        dfs(initState)
    }
    val optimizedTranslationRules = translationRules.filter { visited[it.oldState] && visited[it.newState] }
    return Nfa(initialStates, acceptingStates, optimizedTranslationRules).enumerateStatesFromZero()
}

fun Nfa.enumerateStatesFromZero(): Nfa {
    val currentStates = (initialStates +
            acceptingStates +
            translationRules.flatMap { listOf(it.newState, it.oldState) }.toSet()
            ).toList()
    val maxState = currentStates.max() + 1
    val mapping = MutableList(maxState) { -1 }
    for ((index, state) in currentStates.withIndex()) {
        mapping[index] = state
    }
    val newInitStates = initialStates.map { mapping[it] }.toSet()
    val newAcceptingStates = acceptingStates.map { mapping[it] }.toSet()
    val newTranslationRules =
        translationRules.map { TranslationRule(it.char, mapping[it.oldState], mapping[it.newState]) }
    return Nfa(newInitStates, newAcceptingStates, newTranslationRules)
}

fun optimizeDfa(dfa: Nfa): Nfa {
    val normalizedDfa = dfa.optimizedTranslationRules()
    val alphabet = normalizedDfa.getAlphabet()

    val statesToClass = MutableList(normalizedDfa.maxStateNumber + 1) { 0 }
    val quickRules = getQuickRules(normalizedDfa)

    for (accState in normalizedDfa.acceptingStates) {
        statesToClass[accState] = 1
    }
    val classesStates = splitStatesIntoClasses(normalizedDfa, quickRules, statesToClass, alphabet)
    val acceptingStates = classesStates.filter { it.intersect(normalizedDfa.acceptingStates).isNotEmpty() }
        .map { classStates -> statesToClass[classStates.first()] }
    val initialState = classesStates.single { it.intersect(normalizedDfa.initialStates).isNotEmpty() }
        .let { setOf(statesToClass[it.first()]) }

    val newRules = mutableListOf<TranslationRule>()
    for (classStates in classesStates) {
        val representative = classStates.first()
        for (symbol in alphabet) {
            val classOnTransitionFromState = { state: Int, symbol: Char ->
                val newState = quickRules[state].first { it.char == symbol }.newState
                statesToClass[newState]
            }
            val currentClass = statesToClass[representative]
            newRules.add(
                TranslationRule(
                    symbol,
                    currentClass,
                    classOnTransitionFromState(currentClass, symbol)
                )
            )
        }
    }
    return Nfa(initialState, acceptingStates.toSet(), newRules)
}

private fun splitStatesIntoClasses(
    normalizedDfa: Nfa,
    quickRules: List<MutableList<TranslationRule>>,
    statesToClass: MutableList<Int>,
    alphabet: String
): MutableList<List<Int>> {
    var classesStates = mutableListOf<List<Int>>()
    classesStates.add(normalizedDfa.acceptingStates.toList())
    classesStates.add(((0..normalizedDfa.maxStateNumber).toSet() - normalizedDfa.acceptingStates).toList())
    var maxFreeIndex = 2
    var changed = true
    while (changed) {
        changed = false
        val newClassesStates = mutableListOf<List<Int>>()
        for (classStates in classesStates) {
            val classOnTransitionFromState = { state: Int, symbol: Char ->
                val newState = quickRules[state].first { it.char == symbol }.newState
                statesToClass[newState]
            }

            var newSubclasses = listOf(classStates)
            for (symbol in alphabet) {
                val split = classStates.groupBy { state -> classOnTransitionFromState(state, symbol) }
                if (split.size >= 2) {
                    changed = true
                    val splitList = split.toList()
                    for ((_, newClass) in splitList.drop(1)) {
                        for (state in newClass) {
                            statesToClass[state] = maxFreeIndex
                        }
                        maxFreeIndex += 1
                    }
                    newSubclasses = split.values.toList()
                    break
                }
            }
            newClassesStates += newSubclasses
        }
        classesStates = newClassesStates
    }
    return classesStates
}

private fun getQuickRules(normalizedDfa: Nfa): List<MutableList<TranslationRule>> {
    val rulesArray = List(normalizedDfa.maxStateNumber + 1) { mutableListOf<TranslationRule>() }
    normalizedDfa.translationRules.forEach { rule ->
        rulesArray[rule.oldState].add(rule)
    }
    return rulesArray
}
