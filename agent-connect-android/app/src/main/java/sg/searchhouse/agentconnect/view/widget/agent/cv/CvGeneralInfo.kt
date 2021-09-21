package sg.searchhouse.agentconnect.view.widget.agent.cv

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutCvGeneralInfoBinding

class CvGeneralInfo(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: LayoutCvGeneralInfoBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_cv_general_info,
        this,
        true
    )

    init {
        binding.isExpand = false

        binding.btnExpandDescription.setOnClickListener {
            binding.isExpand = true
        }
        binding.btnCollapseDescription.setOnClickListener {
            binding.isExpand = false
        }
        binding.switchGeneralInfo.setOnCheckedChangeListener { compoundButton, b ->
            binding.viewModel?.let { viewModel ->
                viewModel.agentCvPO.value?.let { cv ->
                    if (compoundButton.isPressed) {
                        cv.showAboutMe = b
                        viewModel.saveAgentCv()
                    }
                }
            }
        } //end of switch
    }
}