import java.io.File

fun main() {
    val sampleNfaFileContent = File("task4-sample-nfa.txt").readLines()
    val sampleStrings = File("task4-sample-input.txt").readLines()
    val nfa = readNfaFromFileContent(sampleNfaFileContent)
    sampleStrings.forEach { string ->
        println(nfa.acceptsString(string))
    }
}
