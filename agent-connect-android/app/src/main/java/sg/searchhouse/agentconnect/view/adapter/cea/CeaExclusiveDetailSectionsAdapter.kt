package sg.searchhouse.agentconnect.view.adapter.cea

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.edit_text_new_pill_number.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.*
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.cea.CeaFormRowPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormSectionPO
import sg.searchhouse.agentconnect.event.cea.CeaGetInfoByApiCall
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class CeaExclusiveDetailSectionsAdapter(
    val context: Context,
    val dialogUtil: DialogUtil,
    private val onUpdateValue: (row: CeaFormRowPO, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<Any>()
    private var requiredFields = listOf<String>()
    private var commissionRates = linkedMapOf<String, CeaFormRowPO>()
    private var renewalCommissionRates = linkedMapOf<String, CeaFormRowPO>()

    companion object {
        private const val VIEW_TYPE_SECTION = 100
        private const val VIEW_TYPE_ROW_TYPE_UNCHANGED = 0
        private const val VIEW_TYPE_ROW_TYPE_DECIMAL_PAD = 1
        private const val VIEW_TYPE_ROW_TYPE_GENERIC_PICKER = 2
        private const val VIEW_TYPE_ROW_TYPE_ASCII = 3
        private const val VIEW_TYPE_ROW_TYPE_NUMBER_PAD = 4
        private const val VIEW_TYPE_ROW_TYPE_NOT_EDITABLE = 5
        private const val VIEW_TYPE_ROW_TYPE_DATE_PICKER = 6
        private const val VIEW_TYPE_ROW_TYPE_TOGGLE = 7
        private const val VIEW_TYPE_ROW_TYPE_DISCLOSURE = 8
        private const val VIEW_TYPE_ROW_TYPE_INFORMATION = 9
        private const val VIEW_TYPE_ROW_TYPE_INFORMATION_ALERT = 10
        private const val VIEW_TYPE_ROW_TYPE_CUSTOM_COMMISSION_NUMBER_PAD = 11

        private const val VIEW_TYPE_LOADING = 101
    }

    private val priceTransformer = { number: Int?, numberString: String? ->
        if (number != null) {
            "\$$numberString"
        } else {
            ""
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_SECTION -> {
                return ListItemCeaFormSectionViewHolder(
                    ListItemCeaFormSectionBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_GENERIC_PICKER -> {
                return ListItemGenericPickerViewHolder(
                    ListItemCeaRowTypeGenericPickerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_ASCII -> {
                return ListItemTextInputViewHolder(
                    ListItemCeaRowTypeTextInputBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_NUMBER_PAD -> {
                return ListItemNumberPadViewHolder(
                    ListItemCeaRowTypeNumberPadBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_NOT_EDITABLE -> {
                return ListItemTextInputViewHolder(
                    ListItemCeaRowTypeTextInputBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_DATE_PICKER -> {
                return ListItemDatePickerViewHolder(
                    ListItemCeaRowTypeDatePickerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_TOGGLE -> {
                return ListItemToggleViewHolder(
                    ListItemCeaRowTypeToggleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_DISCLOSURE -> {
                return ListItemDisclosureViewHolder(
                    ListItemCeaRowTypeDisclosureBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_INFORMATION -> {
                return ListItemInformationViewHolder(
                    ListItemCeaRowTypeInformationBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_INFORMATION_ALERT -> {
                return ListItemInformationAlertViewHolder(
                    ListItemCeaRowTypeInformationBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_CUSTOM_COMMISSION_NUMBER_PAD -> {
                return ListItemCustomCommissionNumberPadViewHolder(
                    ListItemCeaCustomCommissionNumberPadBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_LOADING -> {
                return LoadingViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.loading_indicator,
                        parent,
                        false
                    )
                )
            }
            else -> throw Throwable("Wrong View Type in cea exclusive detail section adapter")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemCeaFormSectionViewHolder -> {
                val section = items[position] as CeaFormSectionPO
                holder.binding.section = section
            }
            is ListItemGenericPickerViewHolder -> {
                val row = items[position] as CeaFormRowPO
                showHideItemView(row.visible, holder)

                if (row.visible) {
                    holder.binding.rowPO = row
                    holder.binding.selectedValue = row.rowValue
                    holder.binding.isRequiredField = requiredFields.contains(row.keyValue)
                    //LISTENERS
                    holder.binding.tvGenericPicker.setOnClickListener {
                        if (row.mapper != null) {
                            if (row.mapper.isNotEmpty()) {
                                //get keys and values from mapper
                                val keys = row.mapper.keys.toList()
                                val values = row.mapper.values.toList()
                                val selectedValue = keys.indexOf(row.rowValue)
                                //show generic picker
                                showGenericPicker(
                                    keys,
                                    values,
                                    row,
                                    holder,
                                    row.title,
                                    selectedValue,
                                    position
                                )
                            }
                        } else if (row.pickerSelection.isNotEmpty()) {
                            showGenericPicker(
                                row.pickerSelection,
                                row,
                                holder,
                                row.pickerSelection.indexOf(row.rowValue),
                                position
                            )
                        }
                    }
                    holder.binding.ibInfo.setOnClickListener { showInformationDialog(row.info) }
                }
            }
            is ListItemTextInputViewHolder -> {
                //NOT EDITABLE AND ASCII share list item text input view holder
                val row = items[position] as CeaFormRowPO
                showHideItemView(row.visible, holder)

                if (row.visible) {
                    holder.binding.isEnabled =
                        row.getCeaFormRowType() != CeaFormRowType.NOT_EDITABLE
                    holder.binding.isRequiredField = requiredFields.contains(row.keyValue)
                    holder.binding.rowPO = row
                    holder.binding.ibInfo.setOnClickListener { showInformationDialog(row.info) }
                }
            }
            is ListItemNumberPadViewHolder -> {
                val row = items[position] as CeaFormRowPO
                showHideItemView(row.visible, holder)

                if (row.visible) {
                    holder.binding.rowPO = row
                    holder.binding.isRequiredField = requiredFields.contains(row.keyValue)
                    if (row.currencyFormat) {
                        handleCurrencyNumberPad(holder, row, position)
                    } else {
                        holder.binding.etNumberPad.setOnFocusChangeListener { _, isFocus ->
                            if (!isFocus) {
                                if (row.keyValue == CeaFormRowTypeKeyValue.PARTY_POSTAL_CODE.value) {
                                    RxBus.publish(
                                        CeaGetInfoByApiCall(
                                            CeaFormRowTypeKeyValue.PARTY_POSTAL_CODE,
                                            row
                                        )
                                    )
                                } else if (row.keyValue == CeaFormRowTypeKeyValue.REPRESNETER_POSTAL_CODE.value) {
                                    RxBus.publish(
                                        CeaGetInfoByApiCall(
                                            CeaFormRowTypeKeyValue.REPRESNETER_POSTAL_CODE,
                                            row
                                        )
                                    )
                                }
                            }
                        }
                    }
                    holder.binding.ibInfo.setOnClickListener { showInformationDialog(row.info) }
                }
            }
            is ListItemDatePickerViewHolder -> {
                val row = items[position] as CeaFormRowPO
                showHideItemView(row.visible, holder)

                if (row.visible) {
                    holder.binding.rowPO = row
                    holder.binding.isRequiredField = requiredFields.contains(row.keyValue)
                    //LISTENERS
                    holder.binding.tvDatePicker.setOnClickListener {
                        showDatePicker(holder, row, position)
                    }
                    holder.binding.ibInfo.setOnClickListener { showInformationDialog(row.info) }
                }
            }
            is ListItemToggleViewHolder -> {
                val row = items[position] as CeaFormRowPO
                showHideItemView(row.visible, holder)

                //Bind only when row is visible
                if (row.visible) {
                    holder.binding.rowType = row
                    holder.binding.showDivider =
                        row.keyValue != CeaFormRowTypeKeyValue.UNDERSTOOD_TERMS.value
                    holder.binding.switchToggle.isChecked = row.getRowValueBooleanForToggleType()
                    holder.binding.ibInfo.setOnClickListener { showInformationDialog(row.info) }
                    holder.binding.switchToggle.setOnCheckedChangeListener { compoundButton, isChecked ->
                        if (compoundButton.isPressed) {
                            row.rowValue = isChecked.toString()
                            onUpdateValue.invoke(row, position)
                        }
                    }
                }
            }
            is ListItemDisclosureViewHolder -> {
                val row = items[position] as CeaFormRowPO
                showHideItemView(row.visible, holder)

                if (row.visible) {
                    holder.binding.rowPO = row
                    holder.binding.etDisclosure.setOnClickListener {
                        onUpdateValue.invoke(row, position)
                    }
                }
            }
            is ListItemInformationViewHolder -> {
                val row = items[position] as CeaFormRowPO
                holder.binding.rowPO = row
                holder.binding.showInformation = true
            }
            is ListItemInformationAlertViewHolder -> {
                val row = items[position] as CeaFormRowPO
                holder.binding.rowPO = row
                holder.binding.showInformation = false
            }
            is ListItemCustomCommissionNumberPadViewHolder -> {
                var row = items[position] as CeaFormRowPO
                showHideItemView(row.visible, holder)

                //Bind only when row is visible
                if (row.visible) {
                    holder.binding.etPriceCommission.et_display.background =
                        ContextCompat.getDrawable(context, R.drawable.card_group_rounded_end)
                    holder.binding.isPriceCommission =
                        row.keyValue == CeaFormRowTypeKeyValue.PRICE_COMMISSION.value ||
                                row.keyValue == CeaFormRowTypeKeyValue.PRICE_RENEWAL_COMMISSION.value
                    holder.binding.isRateCommission =
                        row.keyValue == CeaFormRowTypeKeyValue.RATE_COMMISSION.value ||
                                row.keyValue == CeaFormRowTypeKeyValue.RATE_RENEWAL_COMMISSION.value
                    holder.binding.isRequiredField = requiredFields.contains(row.keyValue)
                    holder.binding.rowPO = row
                    if (row.currencyFormat) {
                        handleCurrencyNumberPad(holder, row)
                    }
                    //LISTENERS
                    holder.binding.ibInfo.setOnClickListener { showInformationDialog(row.info) }

                    holder.binding.btnPriceCommission.setOnClickListener {
                        if (row.keyValue == CeaFormRowTypeKeyValue.RATE_COMMISSION.value) {
                            row =
                                commissionRates.getOrElse(CeaFormRowTypeKeyValue.PRICE_COMMISSION.value) { row }
                            row.rowTitle = context.getString(R.string.label_cea_commission)

                        } else if (row.keyValue == CeaFormRowTypeKeyValue.RATE_RENEWAL_COMMISSION.value) {
                            row =
                                renewalCommissionRates.getOrElse(CeaFormRowTypeKeyValue.PRICE_RENEWAL_COMMISSION.value) { row }
                            row.rowTitle = context.getString(R.string.label_cea_renewal_commission)
                        }
                        row.rowType =
                            CeaFormRowType.CUSTOM_COMMISSION_NUMBER_PAD.value
                        holder.binding.isPriceCommission = true
                        holder.binding.isRateCommission = false
                        holder.binding.rowPO = row
                        handleCurrencyNumberPad(holder, row)
                        onUpdateValue.invoke(row, position)
                    }

                    holder.binding.btnRateCommission.setOnClickListener {

                        if (row.keyValue == CeaFormRowTypeKeyValue.PRICE_COMMISSION.value) {
                            row =
                                commissionRates.getOrElse(CeaFormRowTypeKeyValue.RATE_COMMISSION.value) { row }
                            row.rowTitle = context.getString(R.string.label_cea_commission)

                        } else if (row.keyValue == CeaFormRowTypeKeyValue.PRICE_RENEWAL_COMMISSION.value) {
                            row =
                                renewalCommissionRates.getOrElse(CeaFormRowTypeKeyValue.RATE_RENEWAL_COMMISSION.value) { row }
                            row.rowTitle = context.getString(R.string.label_cea_renewal_commission)
                        }
                        row.rowType = CeaFormRowType.CUSTOM_COMMISSION_NUMBER_PAD.value
                        holder.binding.isPriceCommission = false
                        holder.binding.isRateCommission = true
                        holder.binding.rowPO = row
                        onUpdateValue.invoke(row, position)
                    }
                }
            }
            is LoadingViewHolder -> {
                println("showing loading now..")
            }
            else -> throw Throwable("Wrong view type in on bind view holder")
        }
    }

    private fun showInformationDialog(message: String) {
        dialogUtil.showInformationDialog(message)
    }

    private fun showHideItemView(visible: Boolean, holder: RecyclerView.ViewHolder) {
        if (visible) {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } else {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    private fun showDatePicker(
        holder: ListItemDatePickerViewHolder,
        row: CeaFormRowPO,
        position: Int
    ) {
        val formattedDate =
            DateTimeUtil.convertStringToDate(row.rowValue, DateTimeUtil.FORMAT_DATE_13)
        dialogUtil.showDatePickerDialog(
            formattedDate,
            onDateChangedListener = { year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val outputtedDate = DateTimeUtil.convertDateToString(
                    DateTimeUtil.convertStringToDate(
                        "${String.format("%02d", dayOfMonth)}-" +
                                "${String.format("%02d", monthOfYear + 1)}-" +
                                "$year", DateTimeUtil.FORMAT_DATE_2
                    ),
                    DateTimeUtil.FORMAT_DATE_13
                )
                row.rowValue = outputtedDate
                holder.binding.rowPO = row
                onUpdateValue.invoke(row, position)
            })
    }

    private fun showGenericPicker(
        keys: List<String>,
        values: List<String>,
        row: CeaFormRowPO,
        holder: ListItemGenericPickerViewHolder,
        title: String,
        selectedValue: Int,
        rowPosition: Int
    ) {
        dialogUtil.showWheelPickerDialog(
            keys,
            { _, position ->
                val itemKey = keys[position]
                val itemValue = values[position]
                println(itemValue)
                //Update Maps
                row.rowValue = itemKey
                onUpdateValue.invoke(row, rowPosition)
                //holder.binding.isRequiredField = false
                holder.binding.selectedValue = itemKey
            },
            title,
            selectedValue
        )
    }

    private fun showGenericPicker(
        values: List<String>,
        row: CeaFormRowPO,
        holder: ListItemGenericPickerViewHolder,
        selectedValue: Int,
        rowPosition: Int
    ) {
        dialogUtil.showWheelPickerDialog(
            values, { _, position ->
                val item = values[position]
                row.rowValue = item
                onUpdateValue.invoke(row, rowPosition)
                holder.binding.selectedValue = item
            },
            row.rowTitle,
            selectedValue
        )
    }

    @SuppressLint("ResourceType")
    private fun handleCurrencyNumberPad(
        holder: RecyclerView.ViewHolder,
        row: CeaFormRowPO,
        position: Int? = null
    ) {
        val numberChangeListener = { number: Int? -> row.rowValue = number?.toString() ?: "" }
        if (holder is ListItemNumberPadViewHolder) {
            holder.binding.etCurrencyNumberPad.setup(
                NumberUtil.MAX_INT,
                priceTransformer,
                numberChangeListener
            )
            holder.binding.etCurrencyNumberPad.setHint(row.placeHolder)
            holder.binding.etCurrencyNumberPad.setHintColor(R.color.light_gray_invertible)
            holder.binding.etCurrencyNumberPad.background =
                if (requiredFields.contains(row.keyValue)) {
                    ContextCompat.getDrawable(context, R.drawable.et_error_background)
                } else {
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.et_rounded_gray_background_with_border
                    )
                }
            holder.binding.etCurrencyNumberPad.setNumber(row.rowValue.toIntOrNull())
            holder.binding.etCurrencyNumberPad.et_display.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    //auto populate listed price when expected price is entered
                    if (row.keyValue == CeaFormRowTypeKeyValue.PRICE.value && !TextUtils.isEmpty(row.rowValue)) {
                        items.filterIsInstance<CeaFormRowPO>()
                            .find { it.keyValue == CeaFormRowTypeKeyValue.LIST_PRICE.value }?.run {
                                if (TextUtils.isEmpty(this.rowValue)) {
                                    this.rowValue = row.rowValue
                                    notifyItemChanged((position ?: 0) + 1)
                                }
                            }
                    } //end price
                }
            }
        } else if (holder is ListItemCustomCommissionNumberPadViewHolder) {
            holder.binding.etPriceCommission.setup(
                NumberUtil.MAX_INT,
                priceTransformer,
                numberChangeListener
            )
            holder.binding.etPriceCommission.setHint(row.placeHolder)
            holder.binding.etPriceCommission.setHintColor(R.color.light_gray_invertible)
            //Note : need checking positive int or not- > Sometimes, rowValue is double value -> backend legacy
            if (NumberUtil.isNumber(row.rowValue)) {
                holder.binding.etPriceCommission.setNumber(row.rowValue.toDoubleOrNull()?.toInt())
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CeaFormSectionPO -> VIEW_TYPE_SECTION
            is CeaFormRowPO -> {
                when ((items[position] as CeaFormRowPO).getCeaFormRowType()) {
                    CeaFormRowType.GENERIC_PICKER -> VIEW_TYPE_ROW_TYPE_GENERIC_PICKER
                    CeaFormRowType.ASCII -> VIEW_TYPE_ROW_TYPE_ASCII
                    CeaFormRowType.NUMBER_PAD -> VIEW_TYPE_ROW_TYPE_NUMBER_PAD
                    CeaFormRowType.NOT_EDITABLE -> VIEW_TYPE_ROW_TYPE_NOT_EDITABLE
                    CeaFormRowType.DATE_PICKER -> VIEW_TYPE_ROW_TYPE_DATE_PICKER
                    CeaFormRowType.TOGGLE -> VIEW_TYPE_ROW_TYPE_TOGGLE
                    CeaFormRowType.DISCLOSURE -> VIEW_TYPE_ROW_TYPE_DISCLOSURE
                    CeaFormRowType.INFORMATION -> VIEW_TYPE_ROW_TYPE_INFORMATION
                    CeaFormRowType.INFORMATION_ALERT -> VIEW_TYPE_ROW_TYPE_INFORMATION_ALERT
                    CeaFormRowType.CUSTOM_COMMISSION_NUMBER_PAD -> VIEW_TYPE_ROW_TYPE_CUSTOM_COMMISSION_NUMBER_PAD
                    else -> VIEW_TYPE_LOADING
                }
            }
            else -> throw Throwable("Wrong View Type")
        }
    }

    fun updateItems(list: List<Any>) {
        this.items = list
        notifyDataSetChanged()
    }

    fun updateCommissionItems(items: LinkedHashMap<String, CeaFormRowPO>) {
        this.commissionRates = items
    }

    fun updateRenewalCommissionItems(items: LinkedHashMap<String, CeaFormRowPO>) {
        this.renewalCommissionRates = items
    }

    fun updateRequiredFields(list: List<String>) {
        this.requiredFields = list
        notifyDataSetChanged()
    }

    //Title or header
    class ListItemCeaFormSectionViewHolder(val binding: ListItemCeaFormSectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    //2 - ROW_TYPE_GENERIC_PICKER
    class ListItemGenericPickerViewHolder(val binding: ListItemCeaRowTypeGenericPickerBinding) :
        RecyclerView.ViewHolder(binding.root)

    //3 - ROW_TYPE_ASCII and 5 -> NOT_EDITABLE
    class ListItemTextInputViewHolder(val binding: ListItemCeaRowTypeTextInputBinding) :
        RecyclerView.ViewHolder(binding.root)

    //4 - ROW_TYPE_NUMBER_PAD
    class ListItemNumberPadViewHolder(val binding: ListItemCeaRowTypeNumberPadBinding) :
        RecyclerView.ViewHolder(binding.root)

    // 6 -> ROW_TYPE_DATE_PICKER
    class ListItemDatePickerViewHolder(val binding: ListItemCeaRowTypeDatePickerBinding) :
        RecyclerView.ViewHolder(binding.root)

    // 7 - ROW_TYPE_TOGGLE
    class ListItemToggleViewHolder(val binding: ListItemCeaRowTypeToggleBinding) :
        RecyclerView.ViewHolder(binding.root)

    // 8 - DISCLOSURE
    class ListItemDisclosureViewHolder(val binding: ListItemCeaRowTypeDisclosureBinding) :
        RecyclerView.ViewHolder(binding.root)

    //9
    class ListItemInformationViewHolder(val binding: ListItemCeaRowTypeInformationBinding) :
        RecyclerView.ViewHolder(binding.root)

    //10
    class ListItemInformationAlertViewHolder(val binding: ListItemCeaRowTypeInformationBinding) :
        RecyclerView.ViewHolder(binding.root)

    //11 custom commission
    class ListItemCustomCommissionNumberPadViewHolder(val binding: ListItemCeaCustomCommissionNumberPadBinding) :
        RecyclerView.ViewHolder(binding.root)
}