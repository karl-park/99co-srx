package sg.searchhouse.agentconnect.util

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import sg.searchhouse.agentconnect.R
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs

object NumberUtil {

    const val MAX_INT = 999_999_999
    private const val SQFT_PER_SQM = 10.7639104

    // Check if string is zero or positive integer, include zero padding
    // Valid case: 0, 4, 77, 0056
    fun isNaturalNumber(string: String): Boolean {
        return when {
            TextUtils.isEmpty(string) -> false
            else -> TextUtils.isDigitsOnly(string)
        }
    }

    // Is negative integer
    private fun isNegativeInt(string: String): Boolean {
        return isInt(string) && TextUtils.equals(string.firstOrNull()?.toString(), "-")
    }

    // Is integer
    fun isInt(string: String): Boolean {
        return string.matches("-?\\d+".toRegex())
    }

    fun isNumber(string: String): Boolean {
        return string.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    // Convert valid string to integer with max limit 999,999,999
    // Null if string is not integer
    fun toInt(string: String): Int? {
        return when {
            !isInt(string) -> null
            isIntOverflow(string) -> when {
                isNegativeInt(string) -> -MAX_INT
                else -> MAX_INT
            }
            else -> string.toInt()
        }
    }

    @Throws(IllegalArgumentException::class)
    fun isIntOverflow(string: String): Boolean {
        require(isInt(string)) { "String can't be parsed to integer" }
        return try {
            val thisInteger = string.toInt()
            thisInteger > MAX_INT || thisInteger < -MAX_INT
        } catch (e: NumberFormatException) {
            true
        }
    }

    // Rationale: Remove decimal from the string if rounded to 0 decimal place
    @Throws(IllegalArgumentException::class)
    fun roundDoubleString(double: Double, decimalPlace: Int): String {
        return String.format("%.${decimalPlace}f", roundDouble(double, decimalPlace))
    }

    @Throws(IllegalArgumentException::class)
    fun roundDouble(double: Double, decimalPlace: Int): Double {
        return double.toBigDecimal().setScale(decimalPlace, RoundingMode.HALF_EVEN).toDouble()
    }

    @Throws(IllegalArgumentException::class)
    fun roundFloat(float: Float, decimalPlace: Int): Float {
        return float.toBigDecimal().setScale(decimalPlace, RoundingMode.HALF_EVEN).toFloat()
    }

    fun toString(number: Int?): String {
        return number?.run { this.toString() } ?: run { "" }
    }

    fun getSqftFromSqm(sqm: Int): Int {
        return roundDouble((SQFT_PER_SQM * sqm), 0).toInt()
    }

    fun getSqmFromSqft(sqft: Double): Int {
        return roundDouble((sqft / SQFT_PER_SQM), 0).toInt()
    }

    @JvmStatic
    fun formatThousand(number: Int): String {
        return NumberFormat.getInstance(Locale.US).format(number)
    }

    @JvmStatic
    fun formatThousand(number: Double, decimalPlace: Int = 0): String {
        val roundedString = roundDouble(number, decimalPlace).toString()

        // TODO: Use better implementation of round up
        val primary = roundedString.substringBefore(".")
        var decimal = roundedString.substringAfter(".")

        val primaryFormatted = formatThousand(primary.toInt())

        return if (decimalPlace > 0) {
            while (decimal.length < decimalPlace) {
                decimal += "0"
            }
            "$primaryFormatted.$decimal"
        } else {
            primaryFormatted
        }
    }

    @JvmStatic
    fun formatThousand(number: Float, decimalPlace: Int = 0): String {
        val roundedString = roundFloat(number, decimalPlace).toString()

        // TODO: Use better implementation of round up
        val primary = roundedString.substringBefore(".")
        var decimal = roundedString.substringAfter(".")

        val primaryFormatted = formatThousand(primary.toInt())

        return if (decimalPlace > 0) {
            while (decimal.length < decimalPlace) {
                decimal += "0"
            }
            "$primaryFormatted.$decimal"
        } else {
            primaryFormatted
        }
    }

    fun getFormattedCurrency(number: Int): String {
        val thousandFormatted = formatThousand(abs(number))
        return if (number >= 0) {
            "$$thousandFormatted"
        } else {
            "-$$thousandFormatted"
        }
    }

    fun getFormattedCurrency(number: Double, decimalPlace: Int = 0): String {
        val thousandFormatted = formatThousand(abs(number), decimalPlace)
        if (thousandFormatted.isNotEmpty()) {
            return if (number >= 0) {
                "$$thousandFormatted"
            } else {
                "-$$thousandFormatted"
            }
        }
        return thousandFormatted
    }

    fun getTextBoxCurrency(context: Context, formattedNumberString: String?): String {
        return if (formattedNumberString?.isNotEmpty() == true) {
            context.getString(R.string.label_price, formattedNumberString)
        } else {
            ""
        }
    }

    fun isZeroOrNull(number: Int?): Boolean {
        return number == null || number == 0
    }

    // The default value of getIntExtra is -1
    // If no value passed for this extra key, which return -1, this method will return null
    fun getIntOrNullFromExtra(data: Intent?, extraKey: String): Int? {
        return when (val value = data?.getIntExtra(extraKey, -1)) {
            -1 -> null
            else -> value
        }
    }

    fun getThousandMillionShortForm(number: Int): String {
        val abs = abs(number)
        return when {
            abs >= 1_000_000 -> {
                val value = abs / 1000_000.0
                "${formatThousand(value, 2)}M"
            }
            else -> getThousandShortForm(abs)
        }
    }

    fun getThousandShortForm(number: Int): String {
        val abs = abs(number)
        return when {
            abs >= 1000 -> {
                val value = abs / 1000.0
                "${formatThousand(value)}K"
            }
            else -> {
                formatThousand(number)
            }
        }
    }

    fun getThousandMillionShortForm(number: Float): String {
        val abs = abs(number)
        return when {
            abs >= 1_000_000 -> {
                val value = abs / 1_000_000.0
                "${formatThousand(value, 2)}M"
            }
            abs >= 1000 -> {
                val value = abs / 1000.0
                "${formatThousand(value)}K"
            }
            else -> {
                formatThousand(number)
            }
        }
    }

    fun getThousandMillionShortForm(number: Double, isFormatThousand: Boolean = true): String {
        val abs = abs(number)
        return when {
            abs >= 1_000_000 -> {
                val value = abs / 1_000_000.0
                if (isFormatThousand) {
                    "${formatThousand(value, 2)}M"
                } else {
                    "${roundDoubleString(value, 2)}M"
                }
            }
            abs >= 1000 -> {
                val value = abs / 1000.0
                if (isFormatThousand) {
                    "${formatThousand(value)}K"
                } else {
                    "${roundDoubleString(value, 0)}K"
                }
            }
            else -> {
                if (isFormatThousand) {
                    formatThousand(number)
                } else {
                    roundDoubleString(number, 0)
                }
            }
        }
    }

    // Return integer string with pre-filled zero e.g. 1 -> 01, 2 -> 02
    fun getTwoDigitNumberString(number: Int): String {
        return String.format("%02d", number)
    }

    // Get negative sign if negative
    fun maybeGetNegativeSign(number: Int): String {
        return if (number < 0) {
            "-"
        } else {
            ""
        }
    }
}