package sg.searchhouse.agentconnect.dsl

import java.io.FileNotFoundException

/**
 * Read file content in /resources/ folder
 */
@Throws(FileNotFoundException::class)
fun <T> Class<T>.readResourcesFile(fileName: String): String = run {
    return classLoader?.getResourceAsStream(
        fileName
    )?.bufferedReader()?.readText()
        ?: throw FileNotFoundException("Error when reading `${fileName}`!")
}