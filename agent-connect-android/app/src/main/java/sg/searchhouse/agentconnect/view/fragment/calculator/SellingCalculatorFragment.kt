package sg.searchhouse.agentconnect.view.fragment.calculator

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.edit_text_new_pill_number.view.*
import kotlinx.android.synthetic.main.fragment_selling_calculator.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentSellingCalculatorBinding
import sg.searchhouse.agentconnect.dsl.widget.requestFocusAndShowKeyboard
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.calculator.AmendSellingInputEvent
import sg.searchhouse.agentconnect.event.calculator.GetSellingCalculatorXValueEvent
import sg.searchhouse.agentconnect.event.calculator.UpdateSellingCalculatorEvent
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.widget.common.PillNumberEditText
import sg.searchhouse.agentconnect.viewmodel.fragment.calculator.SellingCalculatorFragmentViewModel
import java.util.*

class SellingCalculatorFragment :
    ViewModelFragment<SellingCalculatorFragmentViewModel, FragmentSellingCalculatorBinding>() {
    private lateinit var entryType: CalculatorAppEnum.SellingCalculatorEntryType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArguments()
    }

    private fun setupArguments() {
        entryType =
            arguments?.getSerializable(ARGUMENT_ENTRY_TYPE) as CalculatorAppEnum.SellingCalculatorEntryType?
                ?: throw IllegalArgumentException("Missing entry type in selling calculator fragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModelArguments()
        setupView()
        observeLiveData()
        listenRxBuses()
    }

    private fun setupViewModelArguments() {
        viewModel.entryType.postValue(entryType)
    }

    private fun listenRxBuses() {
        listenRxBus(AmendSellingInputEvent::class.java) {
            et_selling_price.et_display.requestFocusAndShowKeyboard()
        }
        listenRxBus(GetSellingCalculatorXValueEvent::class.java) {
            if (isResumed) {
                viewModel.address.postValue(it.address)
                viewModel.xValueEntryParams.postValue(it.xValueEntryParams)
                viewModel.walkupResponseData.postValue(it.walkupResponseData)
                et_selling_price.setNumber(it.xValue)
            }
        }
    }

    private fun observeLiveData() {
        viewModel.estSellingPrice.observePerformRequest()
        viewModel.saleDate.observePerformRequest()
        viewModel.purchaseDate.observePerformRequest()
        viewModel.purchasePrice.observePerformRequest()
        viewModel.buyerStampDuty.observePerformRequest()
        viewModel.mainResponse.observe(viewLifecycleOwner) {
            RxBus.publish(UpdateSellingCalculatorEvent(it))
        }
    }

    override fun onResume() {
        super.onResume()
        RxBus.publish(UpdateSellingCalculatorEvent(viewModel.mainResponse.value))
    }

    private fun LiveData<*>.observePerformRequest() =
        observe(viewLifecycleOwner) { viewModel.performRequest() }

    private fun setupView() {
        setupTextBoxes()
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        btn_sale_date.setOnDateClickListener(
            viewModel.saleDate,
            R.string.dialog_title_calculator_sale_date
        ) {
            viewModel.saleDate.postValue(it)
        }

        btn_purchase_date.setOnDateClickListener(
            viewModel.purchaseDate,
            R.string.dialog_title_calculator_purchase_date
        ) {
            viewModel.purchaseDate.postValue(it)
        }

        btn_property_details.setOnClickListener {
            showPropertyDetailsFormDialog()
        }

        btn_share_to_client.setOnClickListener {
            shareToClient()
        }
    }

    private fun shareToClient() {
        viewModel.mainResponse.value?.result?.shortenUrl?.run {
            val message = getString(R.string.message_share_rental_stamp_duty, this)
            activity?.run {
                IntentUtil.shareText(this, message)
            }
        }
    }

    private fun showPropertyDetailsFormDialog() {
        CalculatorPropertyDetailsDialogFragment.launch(
            childFragmentManager,
            viewModel.xValueEntryParams.value,
            viewModel.walkupResponseData.value
        )
    }

    private fun setupTextBoxes() {
        et_selling_price.setupTextBox { viewModel.estSellingPrice.postValue(it?.toLong()) }
        et_purchase_price.setupTextBox { viewModel.purchasePrice.postValue(it?.toLong()) }
        et_buyer_stamp_duty.setupTextBox { viewModel.buyerStampDuty.postValue(it?.toLong()) }
    }

    private fun PillNumberEditText.setupTextBox(onNumberChangeListener: (number: Int?) -> Unit) =
        run {
            setup(numberTextTransformer = { number, formattedNumberString ->
                number?.run { "\$$formattedNumberString" } ?: ""
            }, onNumberChangeListener = {
                onNumberChangeListener.invoke(it)
            })
            setNumber(0)
        }

    // TODO Refactor
    private fun AppCompatButton.setOnDateClickListener(
        initialDateLiveData: LiveData<Date>,
        @StringRes title: Int,
        onDateChanged: (date: Date) -> Unit
    ) =
        setOnClickListener {
            dialogUtil.showDatePickerDialog2(
                initialDateLiveData.value,
                title
            ) { year, month, dayOfMonth ->
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
        return R.layout.fragment_selling_calculator
    }

    override fun getViewModelClass(): Class<SellingCalculatorFragmentViewModel> {
        return SellingCalculatorFragmentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return entryType.name
    }

    companion object {
        private const val ARGUMENT_ENTRY_TYPE = "ARGUMENT_ENTRY_TYPE"

        fun newInstance(entryType: CalculatorAppEnum.SellingCalculatorEntryType): SellingCalculatorFragment {
            val fragment = SellingCalculatorFragment()
            val arguments = Bundle()
            arguments.putSerializable(ARGUMENT_ENTRY_TYPE, entryType)
            fragment.arguments = arguments
            return fragment
        }
    }
}
