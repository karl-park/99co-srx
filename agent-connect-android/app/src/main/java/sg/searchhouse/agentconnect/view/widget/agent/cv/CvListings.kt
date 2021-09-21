package sg.searchhouse.agentconnect.view.widget.agent.cv

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutCvListingsBinding
import sg.searchhouse.agentconnect.model.api.agent.AgentPO

class CvListings(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: LayoutCvListingsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_cv_listings,
        this,
        true
    )

    init {
        binding.switchListing.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isPressed) {
                binding.viewModel?.let { viewModel ->
                    viewModel.agentCvPO.value?.let { cv ->
                        cv.showListings = isChecked
                        viewModel.saveAgentCv()
                    }
                }
            }
        }
    }


    fun populateListingSummary(listingSummary: AgentPO.ListingSummary) {
        binding.saleListings =
            context.getString(R.string.listing_sale, listingSummary.saleTotal.toString())
        binding.rentListings =
            context.getString(R.string.listing_rent, listingSummary.rentTotal.toString())
    }

}