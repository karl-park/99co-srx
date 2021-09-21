package sg.searchhouse.agentconnect.util

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Patterns
import android.widget.TextView
import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.dsl.widget.setupLink
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.*
import java.util.regex.Pattern

object StringUtil {
    //check empty string or not
    fun isEmpty(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.isEmpty()
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordLengthValid(password: String): Boolean {
        // password length must be 6 and above
        return password.length > 5
    }

    fun isCeaNoValid(ceaNo: String): Boolean {
        val ceaNoExpression = "^[a-zA-Z]{1}[0-9]{6}[a-zA-Z]{1}$"
        val ceaRegistrationNumberPattern =
            Pattern.compile(ceaNoExpression, Pattern.CASE_INSENSITIVE)

        return ceaRegistrationNumberPattern.matcher(ceaNo).matches()
    }

    fun isMobileNoValid(mobile: String): Boolean {
        val mobileExpression = "[689]\\d{7}"
        return Pattern.compile(mobileExpression).matcher(mobile).matches()
    }

    fun isWebUrlValid(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    fun isYoutubeUrlValid(url: String): Boolean {
        val urlExpression = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+"
        return Pattern.compile(urlExpression).matcher(url).matches()
    }

    fun getYoutubeUrlId(url: String): String {
        val matcher =
            Pattern.compile("((?<=(v|V)/)|(?<=be/)|(?<=(\\?|\\&)v=)|(?<=embed/))([\\w-]++)")
                .matcher(url)
        if (matcher.find()) {
            return matcher.group()
        }
        return ""
    }

    // Get styled text e.g. bold to assign to TextView
    fun getSpannedFromHtml(str: String?): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(str)
        }
    }

    fun getInitialUpperCase(string: String): String {
        return string.firstOrNull()?.toString()?.toUpperCase() ?: ""
    }

    fun equals(string1: String?, string2: String?, ignoreCase: Boolean = false): Boolean {
        return if (ignoreCase) {
            TextUtils.equals(
                string1?.toLowerCase(Locale.getDefault()),
                string2?.toLowerCase(Locale.getDefault())
            )
        } else {
            TextUtils.equals(string1, string2)
        }
    }

    // Return null if empty string
    fun getStringIfNotEmpty(text: String?): String? {
        return if (text != "") {
            text
        } else {
            null
        }
    }

    fun getSanitizedMobileNumber(mobile: String): String? {
        val matcher = Pattern.compile("(65).*(\\d{8})|(\\d{8})").matcher(mobile)
        return if (matcher.find()) {
            (1..matcher.groupCount()).mapNotNull {
                matcher.group(it)?.toIntOrNull()
            }.joinToString("")
        } else {
            null
        }
    }

    // Append +65 in front of sanitized mobile number if it is 8-digit
    fun getSanitizedMobileNumberWithSgCountryCode(mobile: String): String? {
        val sanitizedMobileNumber = getSanitizedMobileNumber(mobile)
        return if ((sanitizedMobileNumber?.length ?: 0) == 8) {
            "+65${sanitizedMobileNumber}"
        } else {
            sanitizedMobileNumber
        }
    }

    fun getIntListFromString(inputString: String, delimiter: String = ","): List<Int> {
        return inputString.split(delimiter).mapNotNull {
            NumberUtil.toInt(it)
        }
    }

    @Throws(MalformedURLException::class)
    fun encodeUrl(urlInput: String): String {
        val url = URL(urlInput)
        val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
        return uri.toURL().toString()
    }

    fun maybeEncodeUrl(urlInput: String): String {
        return try {
            encodeUrl(urlInput)
        } catch (e: MalformedURLException) {
            urlInput
        }
    }

    fun toTitleCase(text: String): String {
        return text.split(" ").joinToString(" ") {
            it.mapIndexed { index, c ->
                if (index == 0) {
                    c.toUpperCase()
                } else {
                    c.toLowerCase()
                }
            }.joinToString("")
        }
    }

    // Turn part of TextView text into clickable link
    // linkText is the part of text to become link, taken from the first occurrence of substring in `text`
    // To see the link, set android:textColorLink of the TextView into the link color
    @Deprecated(
        "Use TextViewDsl#setupLink", ReplaceWith(
            "textView.setupLink(textResId, linkTextResId, onClickListener)",
            "sg.searchhouse.agentconnect.dsl.widget.setupLink"
        )
    )
    fun enableTextViewLink(
        textView: TextView,
        @StringRes textResId: Int,
        @StringRes linkTextResId: Int,
        onClickListener: () -> Unit
    ) {
        textView.setupLink(textResId, linkTextResId, onClickListener)
    }

    fun getNumbersFromString(inputText: String): String {
        return removeAll(inputText, "[^0-9]".toRegex())
    }

    fun toLowerCase(inputText: String): String {
        return inputText.toLowerCase(Locale.getDefault())
    }

    fun toUpperCase(inputText: String): String {
        return inputText.toUpperCase(Locale.getDefault())
    }

    // Remove all subjects from input text
    fun removeAll(inputText: String, subject: String): String {
        return inputText.replace(subject, "")
    }

    fun removeAll(inputText: String, subject: Regex): String {
        return inputText.replace(subject, "")
    }
}