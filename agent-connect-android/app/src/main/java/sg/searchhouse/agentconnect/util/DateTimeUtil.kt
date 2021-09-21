package sg.searchhouse.agentconnect.util

import android.content.Context
import android.os.Build
import sg.searchhouse.agentconnect.R
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

object DateTimeUtil {

    const val FORMAT_DATE_TIME_FULL = "yyyy-MM-dd HH:mm:ss"
    private const val FORMAT_DATE_TIME_FULL_FILE_NAME = "yyyyMMddHHmmss"

    const val FORMAT_DATE = "dd/MM/yy"
    const val FORMAT_DATE_YYYY_MM_DD_HYPHEN = "yyyy-MM-dd"
    const val FORMAT_DATE_1 = "MMM dd, yyyy"
    const val FORMAT_DATE_2 = "dd-MM-yyyy"
    const val FORMAT_DATE_3 = "dd-MMM-yyyy"
    const val FORMAT_DATE_4 = "dd MMM yyyy"
    const val FORMAT_DATE_5 = "dd MMM yy"
    const val FORMAT_DATE_6 = "yyyy/MM/dd"
    const val FORMAT_DATE_7 = "ddMMMyy"
    const val FORMAT_DATE_8 = "MMM yyyy"
    const val FORMAT_DATE_9 = "MMMyy"
    const val FORMAT_DATE_10 = "dd/MM/yyyy"
    const val FORMAT_DATE_11 = "EEE dd MMM"
    const val FORMAT_DATE_12 = "yyyy-MM-dd"
    const val FORMAT_DATE_13 = "dd MMM, yyyy"
    const val FORMAT_DATE_14 = "MMM dd"
    const val FORMAT_DATE_15 = "ddMMyyyy"
    const val FORMAT_DATE_16 = "d MMM yyyy"

    const val FORMAT_YEAR_MONTH = "yyyy MMM"
    const val FORMAT_YEAR = "yyyy"
    const val FORMAT_YEAR_SHORT = "yy"
    const val FORMAT_MONTH = "MMM"

    private const val FORMAT_DAY_OF_WEEK = "EEEE"

    const val FORMAT_TIME = "h:mm a"
    const val FORMAT_TIME_1 = "HH:mm:ss"
    const val FORMAT_TIME_2 = "hh:mm a"

    private fun removeTimeFromDate(calendar: Calendar): Calendar {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    fun getTodayDate(format: String = FORMAT_DATE_TIME_FULL): String {
        return convertCalendarToString(Calendar.getInstance(), format)
    }

    fun getCurrentTime(format: String = FORMAT_TIME_1): String {
        return convertCalendarToString(Calendar.getInstance(), format)
    }

    fun getMonthShortName(month: Int): String {
        return DateFormatSymbols().shortMonths[month]
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH)
    }

    fun getCurrentDayOfMonth(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    fun getCurrentHourOfDay(): Int {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }

    fun getCurrentMinute(): Int {
        return Calendar.getInstance().get(Calendar.MINUTE)
    }

    fun getCurrentTimeInMillis(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun getNDaysFromNowDateString(numberOfDays: Int, format: String): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, numberOfDays)
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun convertMillisecondToMinutesSecondsFormat(milliseconds: Long): String {
        val minutes = String.format("%02d", (milliseconds / 1000) / 60)
        val seconds = String.format("%02d", (milliseconds / 1000) % 60)
        return "$minutes : $seconds"
    }

    @Throws(ParseException::class, IllegalArgumentException::class)
    fun convertStringToDate(input: String, format: String = FORMAT_DATE_TIME_FULL): Date {
        // It is not possible for null result to be returned, hence !!
        // rf. DateTimeUtilTest
        return SimpleDateFormat(format, Locale.US).parse(input)!!
    }

    @Throws(ParseException::class, IllegalArgumentException::class)
    fun convertStringToCalendar(input: String, format: String = FORMAT_DATE_TIME_FULL): Calendar {
        val date = convertStringToDate(input, format)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    @Throws(ParseException::class, java.lang.IllegalArgumentException::class)
    fun convertCalendarToString(
        calendar: Calendar,
        format: String = FORMAT_DATE_TIME_FULL
    ): String {
        return SimpleDateFormat(
            format,
            Locale.getDefault()
        ).format(calendar.time)
    }

    // Convert date string of format A to format B
    // Return empty string if invalid date input
    // Throws `IllegalArgumentException` if date format invalid
    @Throws(IllegalArgumentException::class)
    fun getConvertedFormatDate(
        inputDate: String,
        inputDateFormat: String,
        outputDateFormat: String
    ): String {
        val date = try {
            convertStringToDate(inputDate, inputDateFormat)
        } catch (e: ParseException) {
            return ""
        }
        return convertDateToString(date, outputDateFormat)
    }

    @Throws(IllegalArgumentException::class)
    fun convertDateToString(date: Date, format: String): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(date)
    }

    fun getDaysBetweenDates(startDate: Date, endDate: Date): Int {
        return ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    fun getCalendar(timeInMillis: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return calendar
    }

    fun getMonthsBetweenDates(startDate: Date, endDate: Date): Int {
        val startCalendar = getCalendar(startDate)
        val endCalendar = getCalendar(endDate)

        val daysBetweenDates = getDaysBetweenDates(startDate, endDate)
        val startMonth = startCalendar.get(Calendar.MONTH) + 1
        val startYear = startCalendar.get(Calendar.YEAR)
        val endMonth = endCalendar.get(Calendar.MONTH) + 1
        val endYear = endCalendar.get(Calendar.YEAR)
        return when {
            daysBetweenDates < 30 -> 0
            startYear == endYear -> endMonth - startMonth
            else -> {
                val years = endYear - startYear - 1
                (12 - startMonth) + endMonth + (years * 12)
            }
        }
    }

    fun getYearsBetweenDates(startDate: Date, endDate: Date): Int {
        val startCalendar = getCalendar(startDate)
        val endCalendar = getCalendar(endDate)

        val daysBetweenDates = getDaysBetweenDates(startDate, endDate)
        val startYear = startCalendar.get(Calendar.YEAR)
        val endYear = endCalendar.get(Calendar.YEAR)

        return when {
            daysBetweenDates < 365 -> 0
            else -> endYear - startYear
        }
    }

    fun getMinutesBetweenTimes(startTimeInMillis: Long, endTimeInMillis: Long): Long {
        return ((endTimeInMillis - startTimeInMillis) / (1000 * 60))
    }

    // Check if 2 times fall within same day
    // TODO Might have more efficient implementation
    fun isSameDay(startTimeInMillis: Long, endTimeInMillis: Long): Boolean {
        val startTime = getCalendar(startTimeInMillis)
        val endTime = getCalendar(endTimeInMillis)

        val isSameYear = startTime.get(Calendar.YEAR) == endTime.get(Calendar.YEAR)
        val isSameMonth = startTime.get(Calendar.MONTH) == endTime.get(Calendar.MONTH)
        val isSameDay = startTime.get(Calendar.DAY_OF_MONTH) == endTime.get(Calendar.DAY_OF_MONTH)

        return isSameYear && isSameMonth && isSameDay
    }

    @Throws(IllegalArgumentException::class)
    fun getFormattedDateTime(
        context: Context,
        date: Date,
        dateFormat: String,
        dateOnly: Boolean = false,
        simplifyThisWeek: Boolean = true
    ): String {
        val cal = Calendar.getInstance()
        cal.time = date
        //Remove time to get actual days difference
        val dayDifferences = getDaysBetweenDates(
            removeTimeFromDate(cal).time,
            removeTimeFromDate(Calendar.getInstance()).time
        )

        return when {
            dayDifferences == 0 -> {
                if (dateOnly) {
                    context.resources.getString(R.string.label_today)
                } else {
                    convertDateToString(date, FORMAT_TIME)
                }
            }
            simplifyThisWeek && dayDifferences == 1 -> context.resources.getString(R.string.label_yesterday)
            simplifyThisWeek && dayDifferences <= 7 -> convertDateToString(date, FORMAT_DAY_OF_WEEK)
            else -> convertDateToString(date, dateFormat)
        }
    }

    // Convert server formatted date time to date time on display
    @Throws(ParseException::class, IllegalArgumentException::class)
    fun getFormattedDateTime(context: Context, rawDateFromServer: String): String {
        //default date format dd/MM/yy including showing time for today
        val date = convertStringToDate(rawDateFromServer, FORMAT_DATE_TIME_FULL)
        return getFormattedDateTime(context, date, FORMAT_DATE, false)
    }

    @Throws(ParseException::class, java.lang.IllegalArgumentException::class)
    fun getFormattedDate(context: Context, rawDateFromServer: String, dateFormat: String): String {
        //get customized date format with date only
        val date = convertStringToDate(rawDateFromServer, FORMAT_DATE_TIME_FULL)
        return getFormattedDateTime(context, date, dateFormat, true)
    }

    // Get the date of n-th days, weeks, months, before now
    // Time unit is defined in Calendar, e.g. Calendar.MONTH
    // Units is positive integer if you want the past
    fun getDateBeforeNow(units: Int, timeUnit: Int): Calendar {
        val date = Calendar.getInstance()
        date.time = Date()
        date.add(timeUnit, -units)
        return date
    }

    // Get the date of n-th days, weeks, months, after now
    // Time unit is defined in Calendar, e.g. Calendar.MONTH
    fun getDateAfterNow(units: Int, timeUnit: Int): Calendar {
        val date = Calendar.getInstance()
        date.time = Date()
        date.add(timeUnit, units)
        return date
    }

    fun getUnixTimeStamp(
        dateTimeFull: String,
        format: String = FORMAT_DATE_YYYY_MM_DD_HYPHEN
    ): Long {
        val date = convertStringToDate(dateTimeFull, format)
        return date.time / 1000
    }

    // Current time stamp for purpose of append to file name
    fun getCurrentFileNameTimeStamp(): String {
        return convertDateToString(Calendar.getInstance().time, FORMAT_DATE_TIME_FULL_FILE_NAME)
    }

    // NOTE: Try multiply the input time stamp by 1000 if does not displayed correctly
    fun convertTimeStampToString(timeStamp: Long, dateFormat: String): String {
        val date = Date(timeStamp)
        return convertDateToString(date, dateFormat)
    }

    fun getNow(): Date {
        return Calendar.getInstance().time
    }

    fun getDaysOfMonth(calendar: Calendar): Int {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            YearMonth.of(year, month + 1).lengthOfMonth()
        } else {
            GregorianCalendar(year, month, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH)
        }
    }
}