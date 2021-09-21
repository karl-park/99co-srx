package sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.*
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class PortalImportSummaryDialogViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    var successCount: Int = 0
    var failedCount: Int = 0
    val summarySource = MutableLiveData<PortalImportSummarySource>()

    //From main portal listings
    val successListings = MutableLiveData<String>()
    val failedListings = MutableLiveData<String>()
    val isFailedListingsIncluded = MutableLiveData<Boolean>()

    //From preview portal listings
    val previewSuccessCountLabel = MutableLiveData<String>()
    val showFailedCountLabel = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
        //default value
        successListings.value = ""
        failedListings.value = ""
        isFailedListingsIncluded.value = false
    }
}