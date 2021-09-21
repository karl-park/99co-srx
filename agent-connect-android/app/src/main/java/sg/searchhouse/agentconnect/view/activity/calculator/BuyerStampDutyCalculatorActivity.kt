package sg.searchhouse.agentconnect.view.activity.calculator

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.activity_calculator_buyer_stamp_duty.*
import kotlinx.android.synthetic.main.edit_text_new_pill_number.view.*
import kotlinx.android.synthetic.main.layout_header_calculator_stamp_duty_buyer.*
import kotlinx.android.synthetic.main.layout_stamp_duty_buyer_details.*
import kotlinx.android.synthetic.main.layout_stamp_duty_buyer_details.layout_container
import kotlinx.android.synthetic.main.layout_stamp_duty_buyer_person.view.*
import kotlinx.android.synthetic.main.layout_stamp_duty_buyer_property_details.*
import kotlinx.android.synthetic.main.layout_stamp_duty_property_number.view.*
import kotlinx.android.synthetic.main.tab_layout_stamp_duty_application_type.*
import kotlinx.android.synthetic.main.tab_layout_stamp_duty_property_type.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCalculatorBuyerStampDutyBinding
import sg.searchhouse.agentconnect.dsl.widget.requestFocusAndShowKeyboard
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.CalculatorEnum
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.calculator.BuyerStampDutyCalculatorViewModel

class BuyerStampDutyCalculatorActivity :
    ViewModelActivity<BuyerStampDutyCalculatorViewModel, ActivityCalculatorBuyerStampDutyBinding>() {
    private val buyerProfiles = CalculatorEnum.BuyerProfile.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.selectedPropertyType.observePerformRequest()
        viewModel.purchasePrice.observePerformRequest()
        viewModel.selectedApplicationType.observePerformRequest()
        viewModel.selectedBuyerProfile.observePerformRequest()
        viewModel.ownedPropertyNumber.observePerformRequest()
        viewModel.selectedBuyerProfile2.observePerformRequest()
        viewModel.ownedPropertyNumber2.observePerformRequest()
    }

    private fun LiveData<*>.observePerformRequest() =
        observe(this@BuyerStampDutyCalculatorActivity) { viewModel.performRequest() }

    private fun setupView() {
        layout_container.setupLayoutAnimation()
        setupTextBoxes()
        setOnClickListeners()
    }

    private fun setupTextBoxes() {
        et_purchase_price.setup(numberTextTransformer = { number, formattedNumberString ->
            if (number != null) {
                "\$${formattedNumberString}"
            } else {
                ""
            }
        }, onNumberChangeListener = {
            viewModel.purchasePrice.postValue(it)
        })
        et_purchase_price.setNumber(0)
    }

    private fun setOnClickListeners() {
        btn_qualify.setOnClickListener {
            // TODO
        }
        btn_tab_residential.setOnClickListener {
            viewModel.selectedPropertyType.postValue(CalculatorEnum.PropertyType.RESIDENTIAL)
        }
        btn_tab_commercial.setOnClickListener {
            viewModel.selectedPropertyType.postValue(CalculatorEnum.PropertyType.COMMERCIAL)
        }
        btn_tab_single.setOnClickListener {
            viewModel.selectedApplicationType.postValue(CalculatorEnum.ApplicationType.SINGLE)
        }
        btn_tab_joint_applicant.setOnClickListener {
            viewModel.selectedApplicationType.postValue(CalculatorEnum.ApplicationType.JOINT_APPLICANT)
        }

        // Buyer 1
        layout_buyer_one.btn_buyer_profile.setOnClickListener {
            dialogUtil.showListDialog(buyerProfiles.map { it.label }, { _, position ->
                viewModel.selectedBuyerProfile.postValue(buyerProfiles[position])
            })
        }
        layout_buyer_one.btn_minus_property_number.setOnClickListener {
            viewModel.updateOwnedPropertyNumber(isPlus = false)
        }
        layout_buyer_one.btn_plus_property_number.setOnClickListener {
            viewModel.updateOwnedPropertyNumber(isPlus = true)
        }

        // Buyer 2
        layout_buyer_two.btn_buyer_profile.setOnClickListener {
            dialogUtil.showListDialog(buyerProfiles.map { it.label }, { _, position ->
                viewModel.selectedBuyerProfile2.postValue(buyerProfiles[position])
            })
        }
        layout_buyer_two.btn_minus_property_number.setOnClickListener {
            viewModel.updateOwnedPropertyNumber2(isPlus = false)
        }
        layout_buyer_two.btn_plus_property_number.setOnClickListener {
            viewModel.updateOwnedPropertyNumber2(isPlus = true)
        }

        btn_amend.setOnClickListener { et_purchase_price.et_display.requestFocusAndShowKeyboard() }
        btn_share_to_client.setOnClickListener {
            viewModel.mainResponse.value?.result?.shortenUrl?.run {
                val message = getString(R.string.message_share_buyer_stamp_duty, this)
                IntentUtil.shareText(this@BuyerStampDutyCalculatorActivity, message)
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_calculator_buyer_stamp_duty
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun getViewModelClass(): Class<BuyerStampDutyCalculatorViewModel> {
        return BuyerStampDutyCalculatorViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }
}