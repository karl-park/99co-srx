package sg.searchhouse.agentconnect.dsl

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import java.util.*

fun String.isNaturalNumber() = NumberUtil.isNaturalNumber(this)

@Throws(JsonSyntaxException::class)
fun <T> String.toJsonObject(classy: Class<T>): T = Gson().fromJson(this, classy)

fun <T> String.toJsonObjectOrNull(classy: Class<T>): T? = try {
    toJsonObject(classy)
} catch (e: JsonSyntaxException) {
    null
}

infix fun <T> String.canParseToJson(classy: Class<T>): Boolean {
    return toJsonObjectOrNull(classy) != null
}

fun String.toUpperCaseDefault() = toUpperCase(Locale.getDefault())

fun String.toLowerCaseDefault() = toLowerCase(Locale.getDefault())

fun String.toQueryText() = replace(" ", "").toLowerCaseDefault()

// Return null if empty string, else normal
fun String.getOrNull() = if (isEmpty()) {
    null
} else {
    this
}

// Return integer list delimited by comma
// e.g. "1,2,4,7"
@Throws(NumberFormatException::class)
fun String.getIntList(): List<Int> = run {
    if (isEmpty()) return emptyList()
    split(",").map { it.toInt() }
}

// Return size of integer list delimited by comma
// e.g. "1,2,4,7"
@Throws(NumberFormatException::class)
fun String.getIntListCount(): Int = getIntList().size

fun String.remove(toBeRemoved: String): String = replace(toBeRemoved, "")

fun String.getEmptyIfDash(): String = when {
    this == "-" -> ""
    else -> this
}

fun String.showToast() {
    ViewUtil.showMessage(this)
}