import java.io.File

fun main() {
    val sampleNfaFileContent = File("optimize-dfa-sample.txt").readLines()
    val dfa = readNfaFromFileContent(sampleNfaFileContent)
    val optimized = optimizeDfa(dfa)
    println(writeToFile(optimized).joinToString("\n"))
}
