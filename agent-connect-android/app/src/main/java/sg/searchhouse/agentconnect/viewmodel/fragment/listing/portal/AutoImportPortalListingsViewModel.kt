package sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalListingPO
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class AutoImportPortalListingsViewModel constructor(application: Application) :
    CoreViewModel(application) {

    val listingTotal = MutableLiveData<Int>()
    val listings = MutableLiveData<List<PortalListingPO>>()
    val portalAccountPO = MutableLiveData<PortalAccountPO>()

    init {
        viewModelComponent.inject(this)
    }

    fun getStartAndEndIndexOfTextLink(text: String, linkText: String): Pair<Int, Int> {
        val split = text.split(linkText)
        val start = split[0].length
        val end = start + linkText.length
        return Pair(start, end)
    }
}