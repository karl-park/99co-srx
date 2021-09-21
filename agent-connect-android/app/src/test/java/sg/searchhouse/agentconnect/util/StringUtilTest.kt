package sg.searchhouse.agentconnect.util

import android.os.Build
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class StringUtilTest {
    class GetSanitizedMobileNumber {
        @Test
        fun `test case one`() {
            val output = StringUtil.getSanitizedMobileNumber("12345678")
            Assert.assertEquals("12345678", output)
        }

        @Test
        fun `test case two`() {
            val output = StringUtil.getSanitizedMobileNumber("(12345678)")
            Assert.assertEquals("12345678", output)
        }

        @Test
        fun `test case three`() {
            val output = StringUtil.getSanitizedMobileNumber("abc12345678efg")
            Assert.assertEquals("12345678", output)
        }

        @Test
        fun `test case four`() {
            val output = StringUtil.getSanitizedMobileNumber("+65-82038566")
            Assert.assertEquals("6582038566", output)
        }

        @Test
        fun `test case five`() {
            val output = StringUtil.getSanitizedMobileNumber("(+65)-82038566")
            Assert.assertEquals("6582038566", output)
        }

        @Test
        fun `test case six`() {
            val output = StringUtil.getSanitizedMobileNumber("")
            Assert.assertEquals(null, output)
        }
    }

    class EncodeUrl {
        @Test
        fun `when path contain space then encode path space to %20`() {
            val output =
                StringUtil.encodeUrl("http://static.streetsine.s3-website-ap-southeast-1.amazonaws.com/Project Photos/10472/M/1352152.jpg")
            Assert.assertEquals(
                "http://static.streetsine.s3-website-ap-southeast-1.amazonaws.com/Project%20Photos/10472/M/1352152.jpg",
                output
            )
        }

        @Test
        fun `when param contain space then encode param space to%20`() {
            val output = StringUtil.encodeUrl("https://example.com/action?username=li sien long")
            Assert.assertEquals("https://example.com/action?username=li%20sien%20long", output)
        }
    }

    class IsEmpty {
        @Test
        fun `when string contain null then return true`() {
            val output = StringUtil.isEmpty(null)
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when string is "" empty then return true`() {
            val output = StringUtil.isEmpty("")
            Assert.assertEquals(true, output)
        }

        @Test
        fun `when string is not empty then return false`() {
            val output = StringUtil.isEmpty("String is not empty")
            Assert.assertEquals(false, output)
        }
    }

    class IsPasswordLengthValid {
        @Test
        fun whenInputMoreThan5PasswordString_thenReturnTrue() {
            val output = StringUtil.isPasswordLengthValid("nwethazin")
            assertTrue(output)
        }

        @Test
        fun whenInputMoreThan5PasswordMixedStringAndNumber_thenReturnTrue() {
            val output = StringUtil.isPasswordLengthValid("04NweThazin")
            assertTrue(output)
        }

        @Test
        fun whenInput5Password_thenReturnFalse() {
            val output = StringUtil.isPasswordLengthValid("12345")
            assertFalse(output)
        }

        @Test
        fun whenInputLessThan5PasswordCount_thenReturnFalse() {
            val output = StringUtil.isPasswordLengthValid("1111")
            assertFalse(output)
        }
    }

    class IsCeaNoValid {
        @Test
        fun whenInputValidCeaNo_thenReturnTrue() {
            val output = StringUtil.isCeaNoValid("R040856I")
            assertTrue(output)
        }

        @Test
        fun whenInputNumberOnly_thenReturnFalse() {
            val output = StringUtil.isCeaNoValid("040856")
            assertFalse(output)
        }

        @Test
        fun whenInputCharactersOnly_thenReturnFalse() {
            val output = StringUtil.isCeaNoValid("AEFRTE")
            assertFalse(output)
        }

        @Test
        fun whenInputInvalidCeaNo_thenReturnFalse() {
            val output = StringUtil.isCeaNoValid("R04085LL")
            assertFalse(output)
        }

        @Test
        fun whenInputEmptyString_thenReturnFalse() {
            val output = StringUtil.isCeaNoValid("")
            assertFalse(output)
        }

        @Test
        fun whenInputSmallerCaseCeaNo_thenReturnTrue() {
            val output = StringUtil.isCeaNoValid("r040856l")
            assertTrue(output)
        }
    }

    class GetNumbersFromString {
        @Test
        fun whenInputStringIncludingNumber_thenReturnNumber() {
            val output = StringUtil.getNumbersFromString("0A9B5C1D7E7F9G6H6I")
            Assert.assertEquals("095177966", output)
        }

        @Test
        fun whenInputStringOnly_thenReturnEmpty() {
            val output = StringUtil.getNumbersFromString("ABCDEFGHIJ")
            Assert.assertEquals("", output)
        }

        @Test
        fun whenInputNumberOnly_thenReturnInputtedNumber() {
            val output = StringUtil.getNumbersFromString("095177966")
            Assert.assertEquals("095177966", output)
        }

        @Test
        fun whenInputEmptyString_thenReturnEmpty() {
            val output = StringUtil.getNumbersFromString("")
            Assert.assertEquals("", output)
        }
    }

    class ToLowerCase {
        @Test
        fun whenInputBothLowerAndUpperCaseString_thenReturnLowerCase() {
            val output = StringUtil.toLowerCase("LowercaseAndUppercase")
            Assert.assertEquals("lowercaseanduppercase", output)
        }

        @Test
        fun whenInputOnlyLowerCaseString_thenReturnLowerCase() {
            val output = StringUtil.toLowerCase("lowercase_inputted_string")
            Assert.assertEquals("lowercase_inputted_string", output)
        }

        @Test
        fun whenInputOnlyUpperCaseString_thenReturnLowerCase() {
            val output = StringUtil.toLowerCase("UPPERCASE_INPUTTED_STRING")
            Assert.assertEquals("uppercase_inputted_string", output)
        }

        @Test
        fun whenInputOnlyEmptyString_thenReturnEmpty() {
            val output = StringUtil.toLowerCase("")
            Assert.assertEquals("", output)
        }

        @Test
        fun whenInputMixedLowerUpperNumberString_thenReturnLowerCase() {
            val output = StringUtil.toLowerCase("LowerCase_12345_UpperCase")
            Assert.assertEquals("lowercase_12345_uppercase", output)
        }
    }

    class ToUpperCase {
        @Test
        fun whenInputBothLowerAndUpperCaseString_thenReturnUpperCase() {
            val output = StringUtil.toUpperCase("LowercaseAndUppercase")
            Assert.assertEquals("LOWERCASEANDUPPERCASE", output)
        }

        @Test
        fun whenInputOnlyLowerCaseString_thenReturnUpperCase() {
            val output = StringUtil.toUpperCase("lowercase_inputted_string")
            Assert.assertEquals("LOWERCASE_INPUTTED_STRING", output)
        }

        @Test
        fun whenInputOnlyUpperCaseString_thenReturnUpperCase() {
            val output = StringUtil.toUpperCase("UPPERCASE_INPUTTED_STRING")
            Assert.assertEquals("UPPERCASE_INPUTTED_STRING", output)
        }

        @Test
        fun whenInputOnlyEmptyString_thenReturnEmpty() {
            val output = StringUtil.toUpperCase("")
            Assert.assertEquals("", output)
        }

        @Test
        fun whenInputMixedLowerUpperNumberString_thenReturnUpperCase() {
            val output = StringUtil.toUpperCase("LowerCase_12345_UpperCase")
            Assert.assertEquals("LOWERCASE_12345_UPPERCASE", output)
        }
    }

    class RemoveAll {

        @Test
        fun whenInputOneString_RemoveACharacter_thenReturnCharacterRemovedString() {
            val output =
                StringUtil.removeAll("an inputted string is Please enter a password..", "a")
            Assert.assertEquals("n inputted string is Plese enter  pssword..", output)
        }

        @Test
        fun whenInputOneString_RemoveOneWord_thenReturnWordRemovedString() {
            val output =
                StringUtil.removeAll(
                    "There are places to eat here to suit everyone - from food stalls in shopping malls to more upmarket restaurants",
                    "to"
                )
            Assert.assertEquals(
                "There are places  eat here  suit everyone - from food stalls in shopping malls  more upmarket restaurants",
                output
            )
        }

        @Test
        fun whenInputEmptyString_thenReturnEmptyString() {
            val output = StringUtil.removeAll("", "")
            Assert.assertEquals("", output)
        }

        @Test
        fun whenInputEmptyString_SubjectIsOneWord_thenReturnEmptyString() {
            val output = StringUtil.removeAll("", "word")
            Assert.assertEquals("", output)
        }

    }

    @Config(sdk = [Build.VERSION_CODES.P])
    @RunWith(RobolectricTestRunner::class)
    class IsEmailValid {
        @Test
        fun whenInputValidEmailFormat_thenReturnTrue() {
            val output = StringUtil.isEmailValid("nwethazin93@gmail.com")
            assertTrue(output)
        }

        @Test
        fun whenInputInvalidEmailFormat_thenReturnFalse() {
            val output = StringUtil.isEmailValid("nwethazin93yahoo.com")
            assertFalse(output)
        }

        @Test
        fun whenInputEmptyEmailString_thenReturnFalse() {
            val output = StringUtil.isEmailValid("")
            assertFalse(output)
        }

        @Test
        fun whenInputInvalidEmailFormatWithoutDomain_thenReturnFalse() {
            val output = StringUtil.isEmailValid("nwethazin@")
            assertFalse(output)
        }
    }


}