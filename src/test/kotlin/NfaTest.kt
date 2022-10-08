import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NfaTest {

    private fun checkNfa(
        nfa: Nfa,
        shouldAcceptOn: List<String>,
        shouldRejectOn: List<String>
    ) {
        for (shouldBeAccepted in shouldAcceptOn) {
            assertTrue(nfa.acceptsString(shouldBeAccepted))
        }
        for (shouldBeRejected in shouldRejectOn) {
            assertFalse(nfa.acceptsString(shouldBeRejected))
        }
    }

    companion object {
        val trivialDfa: Nfa = run {
            val translationRules = listOf(
                TranslationRule('a', 0, 1),
                TranslationRule('b', 1, 2),
            )
            Nfa(
                setOf(element = 0),
                setOf(element = 2),
                translationRules
            )
        }

        val nfaWithManyInitAccStates = run {
            val translationRules = listOf(
                TranslationRule('a', 0, 1),
                TranslationRule('b', 2, 3),
            )
            Nfa(
                initialStates = setOf(0, 2),
                acceptingStates = setOf(1, 3),
                translationRules = translationRules
            )
        }

        val nfaWithLoop = run {
            val translationRules = listOf(
                TranslationRule('a', 0, 0),
                TranslationRule('b', 0, 1),
            )
            Nfa(
                setOf(element = 0),
                setOf(element = 1),
                translationRules
            )
        }
    }

    @Test
    fun checkTrivialDfa() {
        checkNfa(
            trivialDfa, shouldAcceptOn = listOf("ab"),
            shouldRejectOn = listOf("aab, ba")
        )
    }

    @Test
    fun checkNfaWithALoop() {
        checkNfa(
            nfaWithLoop, shouldAcceptOn = listOf("b", "ab", "aaaab"),
            shouldRejectOn = listOf("ba, bb", "abb")
        )
    }

    @Test
    fun checkNfaWithManyInitialAndAcceptingStates() {
        checkNfa(
            nfaWithManyInitAccStates, shouldAcceptOn = listOf("a", "b"),
            shouldRejectOn = listOf("ba, bb", "abb")
        )
    }
}
