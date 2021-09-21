package sg.searchhouse.agentconnect.util

import android.os.Build
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import sg.searchhouse.agentconnect.util.NumberUtil.MAX_INT

class NumberUtilTest {
    @Config(sdk = [Build.VERSION_CODES.P])
    @RunWith(RobolectricTestRunner::class)
    class IsNaturalNumber {
        @Test
        fun `when integer then return true`() {
            val output = NumberUtil.isNaturalNumber("123456")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when integer with zero in front then return true`() {
            val output = NumberUtil.isNaturalNumber("0123")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when integer with multiple zero in front then return true`() {
            val output = NumberUtil.isNaturalNumber("00123")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when zero then return true`() {
            val output = NumberUtil.isNaturalNumber("0")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when overflow integer then return true`() {
            val output = NumberUtil.isNaturalNumber("68403494586904809384094383")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when negative integer then return false`() {
            val output = NumberUtil.isNaturalNumber("-12348")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when mixed then return false`() {
            val output = NumberUtil.isNaturalNumber("a12b")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when string then return false`() {
            val output = NumberUtil.isNaturalNumber("nancy")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when decimal then return false`() {
            val output = NumberUtil.isNaturalNumber("12.4")
            Assert.assertEquals(false, output)
        }
    }

    @Config(sdk = [Build.VERSION_CODES.P])
    @RunWith(RobolectricTestRunner::class)
    class IsInt {
        @Test
        fun `when integer then return true`() {
            val output = NumberUtil.isInt("123456")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when integer with zero in front then return true`() {
            val output = NumberUtil.isInt("0123")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when overflow integer then return true`() {
            val output = NumberUtil.isInt("68403494586904809384094383")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when negative integer then return true`() {
            val output = NumberUtil.isInt("-12348")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when mixed then return false`() {
            val output = NumberUtil.isInt("a12b")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when string then return false`() {
            val output = NumberUtil.isInt("nancy")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when decimal then return false`() {
            val output = NumberUtil.isInt("12.4")
            Assert.assertEquals(false, output)
        }
    }

    @Config(sdk = [Build.VERSION_CODES.P])
    @RunWith(RobolectricTestRunner::class)
    class IsNumber {
        @Test
        fun `when integer then return true`() {
            val output = NumberUtil.isNumber("123456")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when integer with zero in front then return true`() {
            val output = NumberUtil.isNumber("0123")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when overflow integer then return true`() {
            val output = NumberUtil.isNumber("68403494586904809384094383")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when negative integer then return true`() {
            val output = NumberUtil.isNumber("-12348")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when mixed then return false`() {
            val output = NumberUtil.isNumber("a12b")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when string then return false`() {
            val output = NumberUtil.isNumber("nancy")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when decimal then return true`() {
            val output = NumberUtil.isNumber("12.4")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when negative decimal then return true`() {
            val output = NumberUtil.isNumber("-37.5903")
            Assert.assertEquals(true, output)
        }
    }

    @Config(sdk = [Build.VERSION_CODES.P])
    @RunWith(RobolectricTestRunner::class)
    class ToInt {
        @Test
        fun `when integer then return same`() {
            val output = NumberUtil.toInt("123456")
            Assert.assertEquals(123456, output)
        }

        @Test
        fun `when integer with zero in front then return same with zero removed`() {
            val output = NumberUtil.toInt("0123")
            Assert.assertEquals(123, output)
        }

        @Test
        fun `when overflow integer then return max integer`() {
            val output = NumberUtil.toInt("68403494586904809384094383")
            Assert.assertEquals(MAX_INT, output)
        }

        @Test
        fun `when overflow negative integer then return negative max integer`() {
            val output = NumberUtil.toInt("-68403494586904809384094383")
            Assert.assertEquals(-MAX_INT, output)
        }

        @Test
        fun `when negative integer then return same`() {
            val output = NumberUtil.toInt("-12348")
            Assert.assertEquals(-12348, output)
        }

        @Test
        fun `when mixed then return null`() {
            val output = NumberUtil.toInt("a12b")
            Assert.assertEquals(null, output)
        }

        @Test
        fun `when string then return null`() {
            val output = NumberUtil.toInt("nancy")
            Assert.assertEquals(null, output)
        }

        @Test
        fun `when decimal then return null`() {
            val output = NumberUtil.toInt("12.4")
            Assert.assertEquals(null, output)
        }
    }

    @Config(sdk = [Build.VERSION_CODES.P])
    @RunWith(RobolectricTestRunner::class)
    class IsIntOverflow {
        @Test
        fun `when positive normal then return false`() {
            val output = NumberUtil.isIntOverflow("123456")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when negative normal then return false`() {
            val output = NumberUtil.isIntOverflow("-123456")
            Assert.assertEquals(false, output)
        }

        @Test
        fun `when more than 999999999 then return true`() {
            val output = NumberUtil.isIntOverflow("1000000004")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when less than -999999999 then return true`() {
            val output = NumberUtil.isIntOverflow("-1000000004")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when more than 2147483647 then return true`() {
            val output = NumberUtil.isIntOverflow("2147483650")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when less than -2147483647 then return true`() {
            val output = NumberUtil.isIntOverflow("-2147483650")
            Assert.assertEquals(true, output)
        }

        @Test(expected = IllegalArgumentException::class)
        fun `when not integer then throw exception`() {
            NumberUtil.isIntOverflow("abc123")
        }
    }
}