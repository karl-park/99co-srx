package sg.searchhouse.agentconnect.view.widget.main.findagent

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutFindAgentSearchBinding
import timber.log.Timber
import java.util.concurrent.TimeUnit

class FindAgentSearchLayout constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    private var etSearchAgentEventDisposable: Disposable? = null

    val binding: LayoutFindAgentSearchBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_find_agent_search,
        this,
        true
    )

    //Variables
    private var searchText: String = ""
    private var selectedDistrictIds: String = ""
    private var selectedHdbTownIds: String = ""
    private var selectedAreaSpecializations: String = ""
    private var selectedPropertyMainType: PropertyMainType? = null

    //Event listeners
    var searchAgentByFilter: ((String, String, String, String) -> Unit)? = null
    var onPressHDB: ((String) -> Unit)? = null
    var onPressDistrict: ((String) -> Unit)? = null

    init {
        handleListeners()
        handlePropertyType()
        handleClearSearchCriteria()
        handleAreaOrDistrict()
    }

    private fun handleListeners() {
        //handle agent text changed
        etSearchAgentEventDisposable = Observable.create<String> {
            binding.etSearchAgent.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val chars = charSequence ?: return
                    if (chars.trim().length > 2 || chars.trim().isEmpty()) {
                        it.onNext(chars.toString())
                    }
                }
            })
        }
            .debounce(500L, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .subscribe({
                searchText = it
                findAgentByFilter()
            }, {
                Timber.e(it)
            })
    }

    private fun handlePropertyType() {
        binding.btnHdb.setOnClickListener {
            showAreaOrDistrictSelection(PropertyMainType.HDB)
            selectedAreaSpecializations = PropertyMainType.HDB.value
            findAgentByFilter()
        }

        binding.btnCondo.setOnClickListener {
            showAreaOrDistrictSelection(PropertyMainType.CONDO)
            selectedAreaSpecializations = PropertyMainType.CONDO.value
            findAgentByFilter()
        }

        binding.btnLanded.setOnClickListener {
            showAreaOrDistrictSelection(PropertyMainType.LANDED)
            selectedAreaSpecializations = PropertyMainType.LANDED.value
            findAgentByFilter()
        }

        binding.btnCommercial.setOnClickListener {
            showAreaOrDistrictSelection(PropertyMainType.COMMERCIAL)
            selectedAreaSpecializations = PropertyMainType.COMMERCIAL.value
            findAgentByFilter()
        }
    }

    private fun handleClearSearchCriteria() {
        binding.tvClearSelectedPropertyType.setOnClickListener {
            //clear all text and call api again
            selectedAreaSpecializations = ""
            selectedDistrictIds = ""
            selectedHdbTownIds = ""
            findAgentByFilter()
            //show hide again
            binding.llPropertyType.visibility = View.VISIBLE
            binding.llAreasOrDistrict.visibility = View.GONE
        }
    }

    private fun showAreaOrDistrictSelection(propertyType: PropertyMainType) {
        selectedPropertyMainType = propertyType
        //handle view
        binding.llPropertyType.visibility = View.GONE
        binding.llAreasOrDistrict.visibility = View.VISIBLE
        //change button text and icon
        binding.btnSelectedPropertyType.text =
            context.resources.getString(propertyType.propertyTypeLabel)
        binding.btnSelectedPropertyType.setCompoundDrawablesWithIntrinsicBounds(
            propertyType.propertyTypeIcon, 0, 0, 0
        )
        binding.btnSelectedAreaOrDistrict.text =
            context.resources.getString(propertyType.areaOrDistrictLabel)
    }

    private fun findAgentByFilter() {
        searchAgentByFilter?.invoke(
            searchText,
            selectedDistrictIds,
            selectedHdbTownIds,
            selectedAreaSpecializations
        )
    }

    private fun handleAreaOrDistrict() {
        binding.btnSelectedAreaOrDistrict.setOnClickListener {
            when (selectedPropertyMainType) {
                PropertyMainType.HDB -> onPressHDB?.invoke(selectedHdbTownIds)
                PropertyMainType.CONDO, PropertyMainType.LANDED, PropertyMainType.COMMERCIAL -> onPressDistrict?.invoke(
                    selectedDistrictIds
                )
                else -> println("Do nothing for wrong type")
            }
        }
    }

    fun getSelectedHdbTowns(selectedHdbTowns: String, selectedHdbTownNames: String) {
        selectedHdbTownIds = selectedHdbTowns
        binding.btnSelectedAreaOrDistrict.text = if (!TextUtils.isEmpty(selectedHdbTownNames)) {
            context.getString(R.string.msg_selected_areas, selectedHdbTownNames)
        } else {
            val areaLabel = selectedPropertyMainType?.areaOrDistrictLabel ?: return
            context.getString(areaLabel)
        }
        findAgentByFilter()
    }

    fun getSelectedDistricts(selectedDistricts: String) {
        selectedDistrictIds = selectedDistricts
        binding.btnSelectedAreaOrDistrict.text = if (!TextUtils.isEmpty(selectedDistricts)) {
            context.getString(R.string.msg_selected_districts, selectedDistricts)
        } else {
            val districtLabel = selectedPropertyMainType?.areaOrDistrictLabel ?: return
            context.getString(districtLabel)
        }
        findAgentByFilter()
    }

    enum class PropertyMainType(
        val value: String,
        @StringRes val propertyTypeLabel: Int,
        @DrawableRes val propertyTypeIcon: Int,
        @StringRes val areaOrDistrictLabel: Int
    ) {
        HDB(
            "HS,HR",
            R.string.property_type_hdb,
            R.drawable.ic_property_type_hdb,
            R.string.listing_option_areas
        ),
        CONDO(
            "CS,CR",
            R.string.property_type_condo,
            R.drawable.ic_property_type_condo,
            R.string.listing_option_districts
        ),
        LANDED(
            "LS,LR",
            R.string.property_type_landed,
            R.drawable.ic_property_type_landed,
            R.string.listing_option_districts
        ),
        COMMERCIAL(
            "CMS,CMR",
            R.string.property_type_commercial,
            R.drawable.ic_property_type_factory,
            R.string.listing_option_districts
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        etSearchAgentEventDisposable?.dispose()
    }
}
