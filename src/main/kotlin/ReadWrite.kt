fun String.splitBySpace() = split(' ').filter(String::isNotEmpty)

fun readNfaFromFileContent(content: List<String>): Nfa {
    require(content.size >= 5) { "Expected at least 5 lines of input" }
    @Suppress("UNUSED_VARIABLE") val n = content[0].toInt()
    @Suppress("UNUSED_VARIABLE") val m = content[1].toInt()
    val initStates = content[2].splitBySpace().map(String::toInt).toSet()
    val accStates = content[3].splitBySpace().map(String::toInt).toSet()
    val translationRules = content.subList(4, content.size).map { line ->
        val tokens = line.splitBySpace()
        require(tokens.size == 3) { "Expected exactly 3 tokens, but found ${tokens.size} in line \"$line\"" }
        val ch = tokens[1][0]
        val from = tokens[0].toInt()
        val to = tokens[2].toInt()
        TranslationRule(ch, from, to)
    }
    return Nfa(initStates, accStates, translationRules)
}

fun writeToFile(nfa: Nfa): List<String> {
    val n = nfa.translationRules.flatMap { listOf(it.newState, it.oldState) }.toSet().size
    val m = nfa.translationRules.map(TranslationRule::char).size
    val initStatesLine = nfa.initialStates.joinToString(" ")
    val accStatesLine = nfa.acceptingStates.joinToString(" ")
    val translationRulesLines = nfa.translationRules.map { "${it.oldState} ${it.char} ${it.newState}" }
    return listOf(
        "$n",
        "$m",
        initStatesLine,
        accStatesLine
    ) + translationRulesLines
}