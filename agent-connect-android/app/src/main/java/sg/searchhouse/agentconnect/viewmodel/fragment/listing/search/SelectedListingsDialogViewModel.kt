package sg.searchhouse.agentconnect.viewmodel.fragment.listing.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class SelectedListingsDialogViewModel constructor(application: Application) :
    CoreViewModel(application) {
    val selectedListings = MutableLiveData<List<ListingPO>>()
    val hasListings: LiveData<Boolean> = Transformations.map(selectedListings) {
        it?.isNotEmpty() == true
    }
}