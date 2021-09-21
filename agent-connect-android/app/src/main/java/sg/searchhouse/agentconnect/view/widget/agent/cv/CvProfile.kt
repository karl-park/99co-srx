package sg.searchhouse.agentconnect.view.widget.agent.cv

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutCvProfileBinding
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.view.activity.agent.cv.CvCreateUrlActivity

class CvProfile(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: LayoutCvProfileBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_cv_profile,
        this,
        true
    )

    init {
        binding.tvAgentEmail.setOnClickListener {
            binding.agentPO?.let {
                IntentUtil.sendEmail(context, arrayOf(it.email))
            }
        }

        binding.tvCvUrl.setOnClickListener {
            binding.cvUrl?.let {
                IntentUtil.visitUrl(context, it)
            }
        }

        binding.tvEditCvUrl.setOnClickListener {
            context.startActivity(Intent(context, CvCreateUrlActivity::class.java))
        }
    }
}