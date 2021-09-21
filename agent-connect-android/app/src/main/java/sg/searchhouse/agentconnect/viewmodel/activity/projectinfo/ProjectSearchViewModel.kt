package sg.searchhouse.agentconnect.viewmodel.activity.projectinfo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel.*

class ProjectSearchViewModel constructor(application: Application) : CoreViewModel(application) {

    val searchResultType = MutableLiveData<SearchResultType>()
    var propertyPurpose: ListingEnum.PropertyPurpose? = null

    init {
        viewModelComponent.inject(this)
    }
}