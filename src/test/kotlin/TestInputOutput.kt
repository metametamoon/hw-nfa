import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestInputOutput {
    companion object {
        fun nfas() = listOf(
            NfaTest.nfaWithLoop,
            NfaTest.trivialDfa,
            NfaTest.nfaWithManyInitAccStates
        )
    }

    private fun assertEquals(leftNfa: Nfa, rightNfa: Nfa) {
        assertEquals(leftNfa.initialStates, rightNfa.initialStates)
        assertEquals(leftNfa.acceptingStates, rightNfa.acceptingStates)
        assertEquals(leftNfa.translationRules.toSet(), rightNfa.translationRules.toSet())
    }

    @Test
    fun `check input-output consistency`() {
        for (nfa in nfas()) {
            assertEquals(nfa, readNfaFromFileContent(writeToFile(nfa)))
        }
    }

    @Test
    fun `throws if too few lines`() {
        assertThrows<IllegalArgumentException> {
            readNfaFromFileContent(listOf("2", "4", "2 3"))
        }
    }

    @Test
    fun `throws if not a number`() {
        assertThrows<Exception> {
            readNfaFromFileContent(listOf("a", "4", "2 3", "3 1", "1 a 2"))
        }
    }
}