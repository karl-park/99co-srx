package sg.searchhouse.agentconnect.view.widget.agent.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.google.android.flexbox.FlexboxLayout
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.Endpoint
import sg.searchhouse.agentconnect.databinding.LayoutSaleAgreementBinding
import sg.searchhouse.agentconnect.util.IntentUtil

class SaleAgreement(context: Context, attributeSet: AttributeSet? = null) :
    FlexboxLayout(context, attributeSet) {

    val binding: LayoutSaleAgreementBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.layout_sale_agreement, this, true
    )

    init {
        binding.tvTermsOfUse.setOnClickListener {
            IntentUtil.visitSrxUrl(context, Endpoint.TERMS_OF_USE)
        }
        binding.tvTermsOfSale.setOnClickListener {
            IntentUtil.visitSrxUrl(context, Endpoint.TERMS_OF_SALE)
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            IntentUtil.visitSrxUrl(context, Endpoint.PRIVACY_POLICY)
        }
    }

}