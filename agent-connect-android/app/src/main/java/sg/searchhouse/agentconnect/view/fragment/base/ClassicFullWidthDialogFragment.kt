package sg.searchhouse.agentconnect.view.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// Convenient super class for fragment
abstract class ClassicFullWidthDialogFragment : FullWidthDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(getLayoutResId(), container, false)
    }

    // e.g. R.layout.fragment_your
    abstract fun getLayoutResId(): Int
}