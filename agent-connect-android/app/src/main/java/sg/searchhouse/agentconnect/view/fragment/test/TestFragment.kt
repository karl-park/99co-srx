package sg.searchhouse.agentconnect.view.fragment.test

import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentTestBinding
import sg.searchhouse.agentconnect.view.fragment.base.ClassicFragment

// Purposefully blank fragment for testing purpose
class TestFragment : ClassicFragment<FragmentTestBinding>() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_test
    }
}