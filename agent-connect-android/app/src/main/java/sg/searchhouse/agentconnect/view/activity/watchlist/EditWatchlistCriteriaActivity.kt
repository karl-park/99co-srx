package sg.searchhouse.agentconnect.view.activity.watchlist

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_watchlist_criteria_form.*
import kotlinx.android.synthetic.main.layout_range_number_boxes_new.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.launchActivity
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.app.CriteriaFormType
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import kotlin.math.roundToInt

class EditWatchlistCriteriaActivity :
    WatchlistCriteriaFormActivity() {
    private var defaultSubTypes: List<ListingEnum.PropertySubType>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        observeLiveData()
        setupEditViews()
    }

    private fun setupEditViews() {
        btn_delete.setOnClickListener {
            dialogUtil.showActionDialog(R.string.dialog_watchlist_confirm_delete) {
                viewModel.performDelete()
            }
        }
    }

    private fun setupExtras() {
        val editCriteria =
            intent.extras?.getSerializable(EXTRA_EDIT_CRITERIA) as WatchlistPropertyCriteriaPO?
                ?: throw IllegalStateException("Missing `EXTRA_EDIT_CRITERIA` in `EditWatchlistCriteriaActivity`!")
        viewModel.editCriteria.postValue(editCriteria)
    }

    private fun observeLiveData() {
        viewModel.editCriteria.observeNotNull(this) {
            populateForm(it)
        }
        viewModel.deleteStatus.observe(this) {
            when (it) {
                ApiStatus.StatusKey.SUCCESS -> {
                    ViewUtil.showMessage(
                        getString(
                            R.string.toast_delete_criteria_success,
                            viewModel.name.value ?: ""
                        )
                    )
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                ApiStatus.StatusKey.FAIL -> {
                    // Handled
                }
                ApiStatus.StatusKey.ERROR -> {
                    // Handled
                }
                else -> {
                }
            }
        }
        viewModel.propertyType.observe(this) {
            populateDefaultSubTypes()
        }
        viewModel.mainStatus.observeNotNull(this) {
            when (it) {
                ApiStatus.StatusKey.SUCCESS -> {
                    ViewUtil.showMessage(
                        getString(
                            R.string.toast_update_criteria_success,
                            viewModel.name.value ?: ""
                        )
                    )
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(
                        getString(
                            R.string.toast_update_criteria_fail,
                            viewModel.name.value ?: ""
                        )
                    )
                }
                ApiStatus.StatusKey.ERROR -> {
                    ViewUtil.showMessage(
                        getString(
                            R.string.toast_update_criteria_error,
                            viewModel.name.value ?: ""
                        )
                    )
                }
                else -> {
                }
            }
        }
    }

    private fun populateDefaultSubTypes() {
        defaultSubTypes?.run {
            viewModel.propertySubTypes.postValue(
                if (PropertyTypeUtil.isAllCommercialSubTypes(this)) {
                    listOf(ListingEnum.PropertySubType.ALL_COMMERCIAL)
                } else {
                    this
                }
            )
            defaultSubTypes = null
        }
    }

    override fun getFormType(): CriteriaFormType {
        return CriteriaFormType.EDIT
    }

    private fun populateForm(criteria: WatchlistPropertyCriteriaPO) {
        viewModel.ownershipType.postValue(criteria.getSaleTypeEnum())

        criteria.name.run {
            et_criteria_name.setText(this)
            viewModel.name.postValue(this)
        }

        criteria.location?.run {
            if (this.isNotEmpty()) {
                btn_search.text = this
                viewModel.location.postValue(this)
            }
        }

        setSeekBarRadius(criteria.radius)

        when (criteria.type) {
            WatchlistEnum.WatchlistType.LISTINGS.value -> {
                viewModel.hasListings.postValue(true)
                viewModel.hasTransactions.postValue(false)
            }
            WatchlistEnum.WatchlistType.TRANSACTIONS.value -> {
                viewModel.hasListings.postValue(false)
                viewModel.hasTransactions.postValue(true)
            }
            null -> {
                viewModel.hasListings.postValue(true)
                viewModel.hasTransactions.postValue(true)
            }
        }

        layout_price_range.et_min.setNumber(criteria.priceMin)
        layout_price_range.et_max.setNumber(criteria.priceMax)

        layout_size.et_min.setNumber(criteria.sizeMin)
        layout_size.et_max.setNumber(criteria.sizeMax)

        layout_price_psf.et_min.setNumber(criteria.psfMin?.roundToInt())
        layout_price_psf.et_max.setNumber(criteria.psfMax?.roundToInt())

        viewModel.areaType.postValue(criteria.getAreaTypeEnum())
        viewModel.tenureType.postValue(criteria.getTenureTypeEnum())
        viewModel.rentalType.postValue(criteria.getRentalTypeEnum())
        viewModel.projectType.postValue(criteria.getProjectTypeEnum())

        if (criteria.locationDistricts?.isNotEmpty() == true) {
            viewModel.districts.postValue(criteria.locationDistricts)
        }

        if (criteria.locationHdbTowns?.isNotEmpty() == true) {
            viewModel.hdbTowns.postValue(criteria.locationHdbTowns)
        }

        val subTypes = PropertyTypeUtil.getPropertySubTypes(criteria.cdResearchSubtypes ?: "")
        if (subTypes.isNotEmpty()) {
            defaultSubTypes = subTypes
            val mainType = if (PropertyTypeUtil.isAllResidentialSubTypes(subTypes)) {
                ListingEnum.PropertyMainType.RESIDENTIAL
            } else {
                // Assumption: the main type of all sub types are the same
                val firstSubType = subTypes[0].type
                PropertyTypeUtil.getPropertyMainType(firstSubType)
                    ?: throw IllegalArgumentException("Main type of cdResearchSubType $firstSubType not found!")
            }

            viewModel.propertyPurpose.postValue(
                if (mainType == ListingEnum.PropertyMainType.COMMERCIAL) {
                    ListingEnum.PropertyPurpose.COMMERCIAL
                } else {
                    ListingEnum.PropertyPurpose.RESIDENTIAL
                }
            )
            viewModel.propertyType.postValue(mainType)
        }

        criteria.getBedroomEnums().map { selector_bedroom.selectBedroom(it) }
        criteria.getBathroomEnums().map { selector_bathroom.selectBathroom(it) }

        if (criteria.mrts?.isNotEmpty() == true) {
            viewModel.mrts.postValue(criteria.mrts)
        }

        if (criteria.schools?.isNotEmpty() == true) {
            viewModel.schools.postValue(criteria.schools)
        }
    }

    companion object {
        private const val EXTRA_EDIT_CRITERIA = "EXTRA_CRITERIA"

        fun launch(
            activity: Activity,
            watchlistPropertyCriteriaPO: WatchlistPropertyCriteriaPO
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_EDIT_CRITERIA, watchlistPropertyCriteriaPO)
            activity.launchActivity(EditWatchlistCriteriaActivity::class.java, extras)
        }

        fun launchForResult(
            activity: Activity,
            watchlistPropertyCriteriaPO: WatchlistPropertyCriteriaPO,
            requestCode: Int
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_EDIT_CRITERIA, watchlistPropertyCriteriaPO)
            activity.launchActivityForResult(
                EditWatchlistCriteriaActivity::class.java,
                extras,
                requestCode = requestCode
            )
        }
    }
}
