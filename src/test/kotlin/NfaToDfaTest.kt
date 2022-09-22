import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource


fun List<String>.concat(rightLanguage: List<String>): List<String> {
    val result = mutableListOf<String>()
    for (left in this) {
        rightLanguage.mapTo(result) { left + it }
    }
    return result
}

fun List<String>.power(n: Int): List<String> {
    var ans = listOf("")
    repeat(n) {
        ans = ans.concat(this)
    }
    return ans
}

fun List<String>.powerNoMoreThan(n: Int): List<String> =
    (0..n).fold(listOf()) { acc, deg -> acc + this.power(deg) }


internal class NfaToDfaTest {
    @ParameterizedTest
    @MethodSource("dataForEq")
    fun testEquivalence(nfaLeft: Nfa) {
        val nfaRight = nfaToDfa(nfaLeft)
        listOf("a", "b").powerNoMoreThan(5)
            .asSequence()
            .filter { nfaLeft.acceptsString(it) != nfaRight.acceptsString(it) }
            .forEach { assertEquals(nfaLeft.acceptsString(it), nfaRight.acceptsString(it)) }
    }

    companion object {
        @JvmStatic
        fun dataForEq() = listOf(
            Arguments.of(NfaTest.nfaWithLoop),
            Arguments.of(NfaTest.trivialNfa),
            Arguments.of(NfaTest.nfaWithManyInitAccStates)
        )
    }
}
