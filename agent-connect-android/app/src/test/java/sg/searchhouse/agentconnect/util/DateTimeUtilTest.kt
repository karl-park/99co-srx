package sg.searchhouse.agentconnect.util

import org.junit.Assert
import org.junit.Test
import sg.searchhouse.agentconnect.util.DateTimeUtil.convertDateToString
import sg.searchhouse.agentconnect.util.DateTimeUtil.convertStringToDate
import sg.searchhouse.agentconnect.util.DateTimeUtil.getConvertedFormatDate
import sg.searchhouse.agentconnect.util.DateTimeUtil.getDaysBetweenDates
import java.text.ParseException
import java.util.*

class DateTimeUtilTest {
    class ConvertStringToDate {
        @Test
        fun `when input and format are valid then return corresponding date`() {
            val date: Date =
                convertStringToDate("2019-07-19 17:46:20", DateTimeUtil.FORMAT_DATE_TIME_FULL)
            val calendar = Calendar.getInstance()
            calendar.time = date

            Assert.assertEquals(calendar.get(Calendar.YEAR), 2019)
            Assert.assertEquals(calendar.get(Calendar.MONTH), 6)
            Assert.assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 19)
            Assert.assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 17)
            Assert.assertEquals(calendar.get(Calendar.MINUTE), 46)
            Assert.assertEquals(calendar.get(Calendar.SECOND), 20)
        }

        @Test
        fun `when input is not valid date then throw ParseException`() {
            try {
                convertStringToDate("this input is not a date", DateTimeUtil.FORMAT_DATE_TIME_FULL)
                Assert.fail("Should throw ParseException")
            } catch (e: ParseException) {
                // Success
            }
        }

        @Test
        fun `when format is not valid date then throw IllegalArgumentException`() {
            try {
                convertStringToDate("2019-07-19 17:46:20", "anyhow")
                Assert.fail("Should throw IllegalArgumentException")
            } catch (e: IllegalArgumentException) {
                // Success
            }
        }
    }

    class ConvertDateToString {
        @Test
        fun `when date and format are valid then return corresponding date`() {
            val date = Calendar.getInstance()
            date.set(2019, 1, 23, 1, 17, 52) // 23 Feb 2019
            val dateString: String =
                convertDateToString(date.time, DateTimeUtil.FORMAT_DATE_TIME_FULL)
            Assert.assertEquals(dateString, "2019-02-23 01:17:52")
        }

        // Chun Hoe: Fix this by check and throw illegal argument exception from the method? (low priority)
        @Test
        fun `when date is not valid date then return a date anyway`() {
            val date = Calendar.getInstance()
            date.set(-1, 19, -78) // Invalid date
            val dateString: String =
                convertDateToString(date.time, DateTimeUtil.FORMAT_DATE_TIME_FULL)
            Assert.assertNotNull(dateString)
        }

        @Test
        fun `when format is not valid date then throw IllegalArgumentException`() {
            try {
                val date = Calendar.getInstance()
                date.set(2019, 1, 23, 1, 17, 52) // 23 Feb 2019
                convertDateToString(date.time, "anyhow")
                Assert.fail("Should throw IllegalArgumentException")
            } catch (e: IllegalArgumentException) {
                // Success
            }
        }
    }

    class GetDaysBetweenDates {
        @Test
        fun `when dates within one day then return 0`() {
            val startDate = Calendar.getInstance()
            startDate.set(2019, 3, 4, 11, 30, 10)
            val endDate = Calendar.getInstance()
            endDate.set(2019, 3, 4, 18, 20, 8)
            val result = getDaysBetweenDates(startDate.time, endDate.time)
            Assert.assertEquals(0, result)
        }

        @Test
        fun `when dates were one day apart then return 1`() {
            val startDate = Calendar.getInstance()
            startDate.set(2019, 3, 4, 11, 30, 10)
            val endDate = Calendar.getInstance()
            endDate.set(2019, 3, 5, 15, 30, 10)
            val result = getDaysBetweenDates(startDate.time, endDate.time)
            Assert.assertEquals(1, result)
        }

        @Test
        fun `when dates were less than one week apart then return less than 7`() {
            val startDate = Calendar.getInstance()
            startDate.set(2019, 3, 4, 11, 30, 10)
            val endDate = Calendar.getInstance()
            endDate.set(2019, 3, 10, 15, 30, 10)
            val result = getDaysBetweenDates(startDate.time, endDate.time)
            Assert.assertTrue(result < 7)
        }
    }

    class GetConvertedFormatDate {
        @Test
        fun `when input 2019-12-31 then return Dec 2019`() {
            val output = getConvertedFormatDate(
                "2019-12-31",
                DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN,
                DateTimeUtil.FORMAT_DATE_8
            )
            Assert.assertEquals("Dec 2019", output)
        }

        @Test
        fun `when input invalid inputDate then return empty string`() {
            val output = getConvertedFormatDate(
                "abcefg",
                DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN,
                DateTimeUtil.FORMAT_DATE_8
            )
            Assert.assertEquals("", output)
        }

        @Test
        fun `when input invalid inputDateFormat then throw IllegalArgumentException`() {
            try {
                getConvertedFormatDate(
                    "2019-12-31",
                    "luanlai",
                    DateTimeUtil.FORMAT_DATE_8
                )
                Assert.fail("Should throw ParseException")
            } catch (e: IllegalArgumentException) {
                // Success
            }
        }

        @Test
        fun `when input invalid outputDateFormat then throw IllegalArgumentException`() {
            try {
                getConvertedFormatDate(
                    "2019-12-31",
                    DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN,
                    "luanlai"
                )
                Assert.fail("Should throw ParseException")
            } catch (e: IllegalArgumentException) {
                // Success
            }
        }
    }

    class IsSameDay {
        @Test
        fun `when both days are in same day then return true`() {
            val date1 = Calendar.getInstance()
            date1.set(2020, 7, 31, 3, 38)
            val date2 = Calendar.getInstance()
            date2.set(2020, 7, 31, 17, 20)
            Assert.assertEquals(
                true,
                DateTimeUtil.isSameDay(date1.timeInMillis, date2.timeInMillis)
            )
        }

        @Test
        fun `when both days are in same day also then return true`() {
            val date1 = Calendar.getInstance()
            date1.set(2020, 7, 31, 0, 1)
            val date2 = Calendar.getInstance()
            date2.set(2020, 7, 31, 23, 59)
            Assert.assertEquals(
                true,
                DateTimeUtil.isSameDay(date1.timeInMillis, date2.timeInMillis)
            )
        }

        @Test
        fun `when both days are not in same day then return false`() {
            val date1 = Calendar.getInstance()
            date1.set(2020, 5, 20, 3, 38)
            val date2 = Calendar.getInstance()
            date2.set(2020, 9, 7, 17, 20)
            Assert.assertEquals(
                false,
                DateTimeUtil.isSameDay(date1.timeInMillis, date2.timeInMillis)
            )
        }

        @Test
        fun `when both days are not in same day but close in time then return false`() {
            val date1 = Calendar.getInstance()
            date1.set(2020, 5, 20, 0, 1)
            val date2 = Calendar.getInstance()
            date2.set(2020, 5, 19, 23, 59)
            Assert.assertEquals(
                false,
                DateTimeUtil.isSameDay(date1.timeInMillis, date2.timeInMillis)
            )
        }
    }
}