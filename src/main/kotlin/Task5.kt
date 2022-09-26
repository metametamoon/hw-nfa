import java.io.File

fun main() {
    val sampleNfaFileContent = File("task4-sample-nfa.txt").readLines()
    val nfa = readNfaFromFileContent(sampleNfaFileContent)
    val dfa = nfaToDfa(nfa)
    println(writeToFile(dfa).joinToString("\n"))
}
