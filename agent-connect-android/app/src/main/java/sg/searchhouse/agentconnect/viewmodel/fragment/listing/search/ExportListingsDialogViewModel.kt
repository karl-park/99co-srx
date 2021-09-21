package sg.searchhouse.agentconnect.viewmodel.fragment.listing.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class ExportListingsDialogViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    val listingCount by lazy { MutableLiveData<Int>().apply { value = 0 } }

    val isExportListingDetailsChecked by lazy { MutableLiveData<Boolean>().apply { value = false } }

    val isEnableSubmitButton = MediatorLiveData<Boolean>()

    val maxLimit = MediatorLiveData<Int>()

    val exportOption by lazy {
        MutableLiveData<ListingEnum.ExportOption>().apply {
            value = ListingEnum.ExportOption.PDF
        }
    }

    fun toggleExportListingDetails() {
        isExportListingDetailsChecked.postValue(isExportListingDetailsChecked.value != true)
    }

    init {
        viewModelComponent.inject(this)
        setupIsEnableSubmitButton()
        setupMaxLimit()
    }

    private fun setupIsEnableSubmitButton() {
        isEnableSubmitButton.addSource(listingCount) {
            updateEnableSubmitButton()
        }
        isEnableSubmitButton.addSource(isExportListingDetailsChecked) {
            updateEnableSubmitButton()
        }
        isEnableSubmitButton.addSource(exportOption) {
            updateEnableSubmitButton()
        }
    }

    private fun updateEnableSubmitButton() {
        isEnableSubmitButton.postValue(!shouldDisableSubmitButton())
    }

    private fun setupMaxLimit() {
        maxLimit.addSource(isExportListingDetailsChecked) {
            updateMaxLimit()
        }
        maxLimit.addSource(exportOption) {
            updateMaxLimit()
        }
    }

    private fun updateMaxLimit() {
        maxLimit.postValue(
            when (shouldApplyListingDetailsLimit()) {
                true -> AppConstant.MAX_LIMIT_EXPORT_LISTING_DETAILS_PDF
                false -> AppConstant.MAX_LIMIT_EXPORT_LISTINGS
            }
        )
    }

    private fun shouldDisableSubmitButton(): Boolean {
        return exportOption.value == ListingEnum.ExportOption.PDF
                && listingCount.value ?: 0 > AppConstant.MAX_LIMIT_EXPORT_LISTING_DETAILS_PDF
                && isExportListingDetailsChecked.value == true
    }

    private fun shouldApplyListingDetailsLimit(): Boolean {
        return exportOption.value == ListingEnum.ExportOption.PDF && isExportListingDetailsChecked.value == true
    }
}