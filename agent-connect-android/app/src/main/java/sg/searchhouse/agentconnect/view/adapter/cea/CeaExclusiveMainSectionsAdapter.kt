package sg.searchhouse.agentconnect.view.adapter.cea

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.*
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.cea.CeaFormRowPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormSectionPO
import sg.searchhouse.agentconnect.event.cea.CeaGetInfoByApiCall
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder
import java.util.ArrayList

class CeaExclusiveMainSectionsAdapter(
    private val context: Context,
    private val dialogUtil: DialogUtil,
    private val onUpdateValueRowType: (row: CeaFormRowPO) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var models: HashMap<Int, ArrayList<String>> = hashMapOf()
    private var selectedCdResearchSubType: Int? = null

    private var items: List<Any> = listOf()
    private var requiredItems: List<String> = listOf()

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

    companion object {
        private const val VIEW_TYPE_SECTION = 0
        private const val VIEW_TYPE_ROW_TYPE_GENERIC_PICKER = 2
        private const val VIEW_TYPE_ROW_TYPE_TEXT_INPUT = 3 //(3+5)
        private const val VIEW_TYPE_ROW_TYPE_NUMBER_PAD = 4
        private const val VIEW_TYPE_ROW_TYPE_TOGGLE = 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_SECTION -> {
                return ListItemCeaFormSectionViewHolder(
                    ListItemCeaFormSectionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_GENERIC_PICKER -> {
                return ListItemGenericPickerViewHolder(
                    ListItemCeaRowTypeGenericPicker1Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_NUMBER_PAD -> {
                return ListItemNumberPadViewHolder(
                    ListItemCeaRowTypeNumberPad1Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ROW_TYPE_TEXT_INPUT -> {
                return ListItemTextInputViewHolder(
                    ListItemCeaRowTypeTextInput1Binding.inflate(
                        LayoutInflater.from(parent.context), parent, false
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
            else -> return LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.loading_indicator,
                    parent,
                    false
                )
            )
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
                holder.binding.section = items[position] as CeaFormSectionPO
            }
            is ListItemGenericPickerViewHolder -> {
                val row = items[position] as CeaFormRowPO

                holder.binding.rowPO = row
                holder.binding.selectedValue = row.rowValue
                holder.binding.isRequiredField = requiredItems.contains(row.keyValue)
                if (row.visible) {
                    holder.itemView.visibility = View.VISIBLE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                } else {
                    holder.itemView.visibility = View.GONE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }

                //ITEM LISTENER
                holder.binding.tvGenericPicker.setOnClickListener {
                    if (row.keyValue == CeaFormRowTypeKeyValue.MODEL.value) {
                        val propertyType = selectedCdResearchSubType ?: return@setOnClickListener
                        showWheelPickerForModels(
                            PropertyTypeUtil.getModelsByPropertyType(
                                propertyType,
                                models
                            ), row, holder
                        )
                    } else {
                        row.mapper?.let {
                            if (it.isNotEmpty()) {
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
                                    row.rowTitle,
                                    selectedValue
                                )
                            }
                        }
                    }
                }

                holder.binding.ibInfo.setOnClickListener {
                    showInformationDialog(row.info)
                }
            }
            is ListItemNumberPadViewHolder -> {
                val row = items[position] as CeaFormRowPO

                holder.binding.rowPO = row
                holder.binding.isRequiredField = requiredItems.contains(row.keyValue)
                if (row.visible) {
                    holder.itemView.visibility = View.VISIBLE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                } else {
                    holder.itemView.visibility = View.GONE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }

                //ITEM LISTENER
                holder.binding.ibInfo.setOnClickListener {
                    showInformationDialog(row.info)
                }

                if (row.currencyFormat) {
                    handleCurrencyNumberPad(holder, row)
                } else {
                    holder.binding.etNumberPad.setOnFocusChangeListener { _, isFocus ->
                        if (!isFocus) {
                            if (row.keyValue == CeaFormRowTypeKeyValue.POSTAL_CODE.value) {
                                RxBus.publish(
                                    CeaGetInfoByApiCall(
                                        CeaFormRowTypeKeyValue.POSTAL_CODE,
                                        row
                                    )
                                )
                            }
                        } else {
                            if (row.keyValue == CeaFormRowTypeKeyValue.BUILT_MIN.value) {
                                RxBus.publish(
                                    CeaGetInfoByApiCall(
                                        CeaFormRowTypeKeyValue.BUILT_MIN,
                                        row
                                    )
                                )
                            }
                        }
                    }
                }
            }
            is ListItemTextInputViewHolder -> {
                //Note -> can be ASCII and NOT Editable text input (3 and 5)
                val row = items[position] as CeaFormRowPO

                holder.binding.rowPO = row
                holder.binding.isRequiredField = requiredItems.contains(row.keyValue)
                holder.binding.isEnabled =
                    row.getCeaFormRowType() != CeaFormRowType.NOT_EDITABLE
                if (row.visible) {
                    holder.itemView.visibility = View.VISIBLE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                } else {
                    holder.itemView.visibility = View.GONE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }

                holder.binding.ibInfo.setOnClickListener {
                    showInformationDialog(row.info)
                }

                holder.binding.etTextInput.setOnFocusChangeListener { _, isFocus ->
                    if (!isFocus) {
                        onUpdateValueRowType.invoke(row)
                    } else {
                        println("not on focus")
                    }
                }
            }
            is ListItemToggleViewHolder -> {
                val row = items[position] as CeaFormRowPO
                holder.binding.rowType = row
                holder.binding.switchToggle.isChecked = row.getRowValueBooleanForToggleType()

                holder.binding.switchToggle.setOnCheckedChangeListener { compoundButton, isChecked ->
                    if (compoundButton.isPressed) {
                        row.rowValue = isChecked.toString()
                        onUpdateValueRowType.invoke(row)
                    }
                }
                holder.binding.ibInfo.setOnClickListener {
                    showInformationDialog(row.info)
                }
            }
        }
    }

    private fun handleCurrencyNumberPad(holder: ListItemNumberPadViewHolder, row: CeaFormRowPO) {
        val numberChangeListener = { number: Int? ->
            val inputtedNumber = number ?: ""
            row.rowValue = inputtedNumber.toString()
        }
        holder.binding.etCurrencyNumberPad.setup(
            NumberUtil.MAX_INT,
            priceTransformer,
            numberChangeListener
        )
        holder.binding.etCurrencyNumberPad.setNumber(row.rowValue.toIntOrNull())
        holder.binding.etCurrencyNumberPad.setHint(row.placeHolder)
        holder.binding.etCurrencyNumberPad.setHintColor(R.color.light_gray_invertible)
    }

    private fun showInformationDialog(message: String) {
        dialogUtil.showInformationDialog(message)
    }

    private fun showGenericPicker(
        keys: List<String>,
        values: List<String>,
        row: CeaFormRowPO,
        holder: ListItemGenericPickerViewHolder,
        title: String,
        selectedValue: Int
    ) {
        dialogUtil.showWheelPickerDialog(
            keys,
            { _, position ->
                val itemKey = keys[position]
                val itemValue = values[position]
                //Update Maps
                row.rowValue = itemKey
                onUpdateValueRowType.invoke(row)
                //store property type value
                if (row.keyValue == CeaFormRowTypeKeyValue.CD_RESEARCH_SUB_TYPE.value) {
                    if (NumberUtil.isNaturalNumber(itemValue)) {
                        selectedCdResearchSubType = itemValue.toIntOrNull()
                        handlePropertySubType()
                    }
                }
                //Is required
                holder.binding.isRequiredField = false
                holder.binding.selectedValue = itemKey
            },
            title,
            selectedValue
        )
    }

    private fun handlePropertySubType() {
        val cdResearchSubType = selectedCdResearchSubType ?: 0
        when {
            PropertyTypeUtil.isHDB(cdResearchSubType) -> {
                items.forEach {
                    if (it is CeaFormRowPO) {
                        when (it.keyValue) {
                            CeaFormRowTypeKeyValue.FLOOR.value -> it.visible = true
                            CeaFormRowTypeKeyValue.UNIT.value -> it.visible = true
                            CeaFormRowTypeKeyValue.BUILT_MIN.value -> it.rowTitle =
                                context.getString(R.string.label_cea_built_sqm)
                            CeaFormRowTypeKeyValue.LAND_MIN.value -> it.visible = false
                            CeaFormRowTypeKeyValue.TENURE.value -> it.visible = false
                            CeaFormRowTypeKeyValue.NUM_OF_STOREY.value -> it.visible = false
                            CeaFormRowTypeKeyValue.TENANTED_AMOUNT.value -> println("do nothing")
                            else -> it.visible = true
                        }
                    }
                }
                notifyDataSetChanged()
            }
            PropertyTypeUtil.isLanded(cdResearchSubType) -> {
                items.forEach {
                    if (it is CeaFormRowPO) {
                        when (it.keyValue) {
                            CeaFormRowTypeKeyValue.FLOOR.value -> it.visible = false
                            CeaFormRowTypeKeyValue.UNIT.value -> it.visible = false
                            CeaFormRowTypeKeyValue.BUILT_MIN.value -> it.rowTitle =
                                context.getString(R.string.label_cea_built_sqft)
                            CeaFormRowTypeKeyValue.TENANTED_AMOUNT.value -> println(" do nothing")
                            else -> it.visible = true
                        }
                    }
                }
                notifyDataSetChanged()
            }
            PropertyTypeUtil.isCondo(cdResearchSubType) -> {
                items.forEach {
                    if (it is CeaFormRowPO) {
                        when (it.keyValue) {
                            CeaFormRowTypeKeyValue.FLOOR.value -> it.visible = true
                            CeaFormRowTypeKeyValue.UNIT.value -> it.visible = true
                            CeaFormRowTypeKeyValue.BUILT_MIN.value -> it.rowTitle =
                                context.getString(R.string.label_cea_built_sqft)
                            CeaFormRowTypeKeyValue.LAND_MIN.value -> it.visible = false
                            CeaFormRowTypeKeyValue.NUM_OF_STOREY.value -> it.visible = false
                            CeaFormRowTypeKeyValue.TENANTED_AMOUNT.value -> println("do nothing")
                            else -> it.visible = true
                        }
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    //Note: specific one cannot combine with general one
    private fun showWheelPickerForModels(
        modelsByPropertyType: List<String>,
        row: CeaFormRowPO,
        holder: ListItemGenericPickerViewHolder
    ) {
        dialogUtil.showWheelPickerDialog(
            modelsByPropertyType,
            { _, position ->
                val model = modelsByPropertyType[position]
                //update model value
                row.rowValue = model
                onUpdateValueRowType.invoke(row)
                //update value in xml
                holder.binding.isRequiredField = false
                holder.binding.selectedValue = model
            },
            row.rowTitle,
            modelsByPropertyType.indexOf(row.rowValue)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CeaFormSectionPO -> VIEW_TYPE_SECTION
            is CeaFormRowPO -> {
                when ((items[position] as CeaFormRowPO).getCeaFormRowType()) {
                    CeaFormRowType.GENERIC_PICKER -> VIEW_TYPE_ROW_TYPE_GENERIC_PICKER
                    CeaFormRowType.ASCII -> VIEW_TYPE_ROW_TYPE_TEXT_INPUT
                    CeaFormRowType.NUMBER_PAD -> VIEW_TYPE_ROW_TYPE_NUMBER_PAD
                    CeaFormRowType.NOT_EDITABLE -> VIEW_TYPE_ROW_TYPE_TEXT_INPUT
                    CeaFormRowType.TOGGLE -> VIEW_TYPE_ROW_TYPE_TOGGLE
                    else -> 0
                }
            }
            else -> throw Throwable("Wrong view type")
        }
    }

    fun updateModels(items: HashMap<Int, ArrayList<String>>) {
        models = items
    }

    fun updateItems(list: List<Any>) {
        this.items = list
        notifyDataSetChanged()
    }

    fun updateRequiredItems(list: List<String>) {
        this.requiredItems = list
        notifyDataSetChanged()
    }

    //0 - SECTION
    class ListItemCeaFormSectionViewHolder(val binding: ListItemCeaFormSectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    //2 - ROW_TYPE_GENERIC_PICKER
    class ListItemGenericPickerViewHolder(val binding: ListItemCeaRowTypeGenericPicker1Binding) :
        RecyclerView.ViewHolder(binding.root)

    //4 - ROW_TYPE_NUMBER_PAD
    class ListItemNumberPadViewHolder(val binding: ListItemCeaRowTypeNumberPad1Binding) :
        RecyclerView.ViewHolder(binding.root)

    //3 - ROW_TYPE_ASCII and 5 - ROW TYPE NON EDITABLE
    class ListItemTextInputViewHolder(val binding: ListItemCeaRowTypeTextInput1Binding) :
        RecyclerView.ViewHolder(binding.root)

    // 7 - ROW_TYPE_TOGGLE
    class ListItemToggleViewHolder(val binding: ListItemCeaRowTypeToggleBinding) :
        RecyclerView.ViewHolder(binding.root)
}