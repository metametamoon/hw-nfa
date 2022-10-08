import org.junit.jupiter.api.Test


internal class DfaOptimizationTest {

    private val trivialDfa: Nfa = run {
        val dead = 3
        val translationRules = listOf(
            TranslationRule('a', 0, 1),
            TranslationRule('b', 0, dead),
            TranslationRule('b', 1, 2),
            TranslationRule('a', 1, dead),
            TranslationRule('a', 2, dead),
            TranslationRule('b', 2, dead),
            TranslationRule('a', dead, dead),
            TranslationRule('b', dead, dead),
        )
        Nfa(
            setOf(element = 0),
            setOf(element = 2),
            translationRules
        )
    }

    @Test
    fun works() {
        val optimizedDfa = optimizeDfa(trivialDfa)
        assertEquivalent(trivialDfa, optimizedDfa)
    }
}
