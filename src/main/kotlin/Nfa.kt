data class TranslationRule(
    val char: Char,
    val oldState: Int,
    val newState: Int
)

fun List<TranslationRule>.possibleStatesOnInput(currentState: Int, input: Char) =
    filter { it.char == input && it.oldState == currentState }
        .map(TranslationRule::newState)

class Nfa(
    val initialStates: Set<Int>,
    val acceptingStates: Set<Int>,
    val translationRules: List<TranslationRule>
) {
    private var currentStates: Set<Int> = initialStates
    private fun reset() {
        currentStates = initialStates
    }

    val maxStateNumber by lazy {
        translationRules.flatMap { listOf(it.oldState, it.newState) }.toSet().max()
    }

    private fun update(char: Char) {
        currentStates = currentStates.flatMap { currentState ->
            translationRules.possibleStatesOnInput(currentState, char)
        }.toSet()
    }

    fun acceptsString(str: String): Boolean {
        reset()
        for (ch in str) {
            update(ch)
        }
        val hasAccepted = hasAccepted()
        return hasAccepted
    }

    private fun hasAccepted(): Boolean = currentStates.intersect(acceptingStates).isNotEmpty()

}
