package sg.searchhouse.agentconnect.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.DisplayMetrics
import android.view.Menu
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.webkit.WebView
import android.widget.EditText
import android.widget.ScrollView
import androidx.annotation.*
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant.PROFILE_COLORS
import sg.searchhouse.agentconnect.enumeration.app.ProfileColorInitialEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.app.ShowFeedbackMessageEvent
import sg.searchhouse.agentconnect.event.app.ShowMessageEvent
import sg.searchhouse.agentconnect.event.app.ShowMessageResEvent
import kotlin.random.Random


object ViewUtil {

    private const val ANIMATION_DURATION: Long = 100

    fun showMessage(message: String?) {
        RxBus.publish(ShowMessageEvent(message))
    }

    fun showMessage(@StringRes messageResId: Int) {
        RxBus.publish(ShowMessageResEvent(messageResId))
    }

    fun showFeedbackMessage(message: String) {
        RxBus.publish(ShowFeedbackMessageEvent(message = message))
    }

    fun showFeedbackMessage(@StringRes messageResId: Int) {
        RxBus.publish(ShowFeedbackMessageEvent(messageResId = messageResId))
    }

    @ColorInt
    fun generateRandomColor(context: Context): Int {
        val random = Random.nextInt(PROFILE_COLORS.size)
        return context.getColor(PROFILE_COLORS[random])
    }

    @SuppressLint("ResourceAsColor")
    @ColorInt
    fun getColorByAlphabetCharacter(context: Context, char: Char): Int {
        return context.getColor(
            ProfileColorInitialEnum.values().find { it.char == char.toUpperCase() }?.color
                ?: kotlin.run { R.color.profile_color_0 }
        )
    }

    fun showKeyboard(editText: EditText) {
        val imm =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(editText, SHOW_IMPLICIT)
    }

    fun hideKeyboard(editText: EditText, callback: (() -> Unit)? = null) {
        val imm =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.windowToken, 0, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                super.onReceiveResult(resultCode, resultData)
                callback?.invoke()
            }
        })
    }

    fun setToolbarHomeIconColor(toolbar: Toolbar, @ColorRes colorResId: Int) {
        val colorFilter =
            PorterDuffColorFilter(toolbar.context.getColor(colorResId), PorterDuff.Mode.SRC_ATOP)
        toolbar.navigationIcon?.colorFilter = colorFilter
    }

    @Throws(NullPointerException::class)
    fun setMenuItemIconColor(
        context: Context,
        menu: Menu, @IdRes menuItemResId: Int, @ColorRes colorResId: Int
    ) {
        val menuItem =
            menu.findItem(menuItemResId)
                ?: throw NullPointerException("Menu item not available, please check if call super.onPrepareOptionMenu first, and item exists in menu.")
        val colorFilter =
            PorterDuffColorFilter(context.getColor(colorResId), PorterDuff.Mode.SRC_ATOP)
        menuItem.icon?.colorFilter = colorFilter
    }

    fun getScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun listenVerticalScrollEnd(
        scrollView: ScrollView,
        reachTop: (() -> Unit)? = null,
        reachBottom: (() -> Unit)? = null
    ) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.getChildAt(0).bottom <= scrollView.height + scrollView.scrollY) {
                reachBottom?.invoke()
            } else if (scrollView.getChildAt(0).top == 0) {
                reachTop?.invoke()
            }
        }
    }

    fun listenVerticalScrollEnd(
        scrollView: NestedScrollView,
        reachTop: (() -> Unit)? = null,
        reachBottom: (() -> Unit)? = null
    ) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.getChildAt(0).bottom <= scrollView.height + scrollView.scrollY) {
                reachBottom?.invoke()
            } else if (scrollView.getChildAt(0).top == 0) {
                reachTop?.invoke()
            }
        }
    }

    // TODO Pending bug: When list is too short, scroll to top end has the same effect as scroll to bottom end
    fun listenVerticalScrollEnd(
        recyclerView: RecyclerView,
        reachTop: (() -> Unit)? = null,
        reachBottom: (() -> Unit)? = null
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                    reachBottom?.invoke()
                } else if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_UP)) {
                    reachTop?.invoke()
                }
            }
        })
    }

    //TODO: temp method to solve urgent scrolling top issue. will merge with listenVerticalScrollEnd method
    fun listenVerticalScrollTopEnd(
        recyclerView: RecyclerView,
        reachTop: (() -> Unit)
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(-1)) {
                    reachTop.invoke()
                }
            }
        })
    }

    //Animation views
    fun fadeInView(view: View) {
        val animation = AlphaAnimation(0f, 1f)
        animation.duration = ANIMATION_DURATION
        animation.fillAfter = true
        view.startAnimation(animation)
        view.visibility = View.VISIBLE
    }

    fun fadeOutView(view: View) {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = ANIMATION_DURATION
        animation.fillAfter = false
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.visibility = View.GONE
            }

            override fun onAnimationStart(p0: Animation?) {
            }

        })
        view.startAnimation(animation)
    }

    fun getBitmapFromDrawable(resources: Resources, @DrawableRes drawableRes: Int): Bitmap {
        val drawable = resources.getDrawable(drawableRes, null)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)

        return bitmap
    }

    fun getScaledBitmapFromDrawable(
        resources: Resources, @DrawableRes drawableRes: Int,
        maxSize: Int
    ): Bitmap {
        val bitmap = getBitmapFromDrawable(resources, drawableRes)

        var width = bitmap.width
        var height = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()

        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    fun scrollToBottom(scrollView: ScrollView) {
        scrollView.scrollTo(0, scrollView.getChildAt(0).height)
    }

    fun scrollToBottom(nestedScrollView: NestedScrollView) {
        nestedScrollView.scrollTo(0, nestedScrollView.getChildAt(0).height)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadWebView(webView: WebView, url: String?) {
        if (url == null || !StringUtil.isWebUrlValid(url)) {
            return showMessage(R.string.toast_error_invalid_url)
        }
        webView.loadUrl(url)
        webView.settings.javaScriptEnabled = true
    }

    fun copyToClipBoard(context: Context, text: String, label: String = "copied text") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}