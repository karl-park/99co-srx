package sg.searchhouse.agentconnect.view.fragment.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import sg.searchhouse.agentconnect.R

abstract class FullWidthDialogFragment : BaseDialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            //background transparent
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //full screen with animation
            window.setWindowAnimations(R.style.FullScreenDialogAnimation)
            //full screen
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}