package sg.searchhouse.agentconnect.view.activity.calculator

import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.activity_calculator_rental_stamp_duty.*
import kotlinx.android.synthetic.main.edit_text_new_pill_number.view.*
import kotlinx.android.synthetic.main.layout_header_calculator_stamp_duty_rental.*
import kotlinx.android.synthetic.main.layout_stamp_duty_rental_property_details.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCalculatorRentalStampDutyBinding
import sg.searchhouse.agentconnect.dsl.widget.requestFocusAndShowKeyboard
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.widget.common.PillNumberEditText
import sg.searchhouse.agentconnect.viewmodel.activity.calculator.RentalStampDutyCalculatorViewModel
import java.util.*

class RentalStampDutyCalculatorActivity :
    ViewModelActivity<RentalStampDutyCalculatorViewModel, ActivityCalculatorRentalStampDutyBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.monthlyRent.observe(this) { viewModel.performRequest() }

        viewModel.otherMonthlyCharges.observe(this) { viewModel.performRequest() }

        viewModel.startDate.observe(this) { viewModel.performRequest() }

        viewModel.endDate.observe(this) { viewModel.performRequest() }
    }

    private fun setupView() {
        setupTextBoxes()
        setOnClickListeners()
    }

    private fun setupTextBoxes() {
        et_monthly_rent.setup { viewModel.monthlyRent.postValue(it) }
        et_other_monthly_charges.setup { viewModel.otherMonthlyCharges.postValue(it) }
    }

    private fun PillNumberEditText.setup(onNumberChangeListener: (number: Int?) -> Unit) = run {
        setup(numberTextTransformer = { number, formattedNumberString ->
            number?.run { "\$$formattedNumberString" } ?: ""
        }, onNumberChangeListener = {
            onNumberChangeListener.invoke(it)
        })
        setNumber(0)
    }

    private fun setOnClickListeners() {
        btn_amend.setOnClickListener { et_monthly_rent.et_display.requestFocusAndShowKeyboard() }
        btn_start_date.setOnDateClickListener(viewModel.startDate) {
            viewModel.startDate.postValue(it)
        }
        btn_end_date.setOnDateClickListener(viewModel.endDate) {
            viewModel.endDate.postValue(it)
        }
        btn_share_to_client.setOnClickListener {
            viewModel.mainResponse.value?.result?.shortenUrl?.run {
                val message = getString(R.string.message_share_rental_stamp_duty, this)
                IntentUtil.shareText(this@RentalStampDutyCalculatorActivity, message)
            }
        }
    }

    private fun AppCompatButton.setOnDateClickListener(
        initialDateLiveData: LiveData<Date>,
        onDateChanged: (date: Date) -> Unit
    ) =
        setOnClickListener {
            dialogUtil.showDatePickerDialog2(initialDateLiveData.value) { year, month, dayOfMonth ->
                val date = getDate(year, month, dayOfMonth)
                onDateChanged.invoke(date)
            }
        }

    private fun getDate(year: Int, month: Int, dayOfMonth: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return calendar.time
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_calculator_rental_stamp_duty
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun getViewModelClass(): Class<RentalStampDutyCalculatorViewModel> {
        return RentalStampDutyCalculatorViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }
}