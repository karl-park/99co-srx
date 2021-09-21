package sg.searchhouse.agentconnect.dsl.widget

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

fun TextView.setupLink(
    @StringRes textResId: Int,
    @StringRes linkTextResId: Int,
    onClickListener: () -> Unit
) = run {
    val text = context.getString(textResId)
    val linkText = context.getString(linkTextResId)

    this.setupLink(text, linkText, onClickListener)
}

fun TextView.setupLink(
    text: String,
    linkText: String,
    onClickListener: () -> Unit
) = run {
    if (!text.contains(linkText)) return

    val split = text.split(linkText)

    val start = split[0].length
    val end = start + linkText.length

    val spannableString = SpannableString(text)
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onClickListener.invoke()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    this.text = spannableString
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

/**
 * TODO Cater case of > 1 links with identical text
 */
fun TextView.setupLinks(
    @StringRes textResId: Int,
    @StringRes linkTextResIds: List<Int>,
    onClickListeners: List<() -> Unit>
) = run {
    val text = context.getString(textResId)
    val linkTexts = linkTextResIds.map { context.getString(it) }

    val spannableString = SpannableString(text)
    linkTexts.mapIndexed { index, linkText ->
        // TODO Maybe refactor
        if (!text.contains(linkText)) return
        val split = text.split(linkText)
        val start = split[0].length
        val end = start + linkText.length
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                onClickListeners[index].invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    this.text = spannableString
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

fun TextView.setupSubTextColor(
    @ColorRes colorRes: Int,
    text: String,
    subText: String
) = run {
    val split = text.split(subText)

    val start = split[0].length
    val end = start + subText.length

    val spannableString = SpannableString(text)
    spannableString.setSpan(
        ForegroundColorSpan(this.context.getColor(colorRes)),
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    this.text = spannableString
}
