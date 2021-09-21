package sg.searchhouse.agentconnect.view.fragment.base

abstract class FullScreenDialogFragment : BaseDialogFragment() {

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }
}