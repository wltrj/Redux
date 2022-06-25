package ceneax.app.lib.redux.compiler

import java.io.File
import java.util.*

const val DEFAULT_PACKAGE = "ceneax.app.lib.redux.default.module"

internal fun getModuleName(generateDir: String): String {
    return try {
        val kaptGenDir = "${File.separatorChar}build${File.separatorChar}generated${File.separatorChar}source${File.separatorChar}kaptKotlin"
        val pathIndex = generateDir.lastIndexOf(kaptGenDir)
        val subStr = generateDir.substring(0, pathIndex)
        val lastIndex = subStr.lastIndexOf(File.separatorChar)
        val result = subStr.substring(lastIndex + 1)
        "Redux${result.camelCase()}Module"
    } catch (e: Exception) {
        "ReduxDefaultModule"
    }
}

internal fun String.camelCase(): String {
    val words: List<String> = split("[\\W_]+".toRegex())
    val builder = StringBuilder()
    words.forEach {
        val word = if (it.isEmpty()) it else it[0].uppercase() + it.substring(1).lowercase()
        builder.append(word)
    }

    return builder.toString()
}

internal fun String.cap(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}