package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.OwnershipType
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.PropertyPurpose
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

// View model for SearchActivity
class SearchViewModel constructor(application: Application) : CoreViewModel(application) {
    // Show hide `Is Commercial` toggle
    val canTogglePropertyPurpose: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = true }

    val isShowAppBar: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val isCloseSearchPanel = MutableLiveData<Boolean>().apply { value = true }
    val ownershipType: MutableLiveData<OwnershipType> = MutableLiveData<OwnershipType>().apply {
        postValue(
            OwnershipType.SALE
        )
    }

    private val _propertyPurpose = MutableLiveData<PropertyPurpose>().apply { value = PropertyPurpose.RESIDENTIAL }
    val propertyPurpose: LiveData<PropertyPurpose> = _propertyPurpose

    fun setPropertyPurpose(isCommercial: Boolean) {
        setPropertyPurpose(
            when (isCommercial) {
                true -> PropertyPurpose.COMMERCIAL
                false -> PropertyPurpose.RESIDENTIAL
            }
        )
    }

    fun setPropertyPurpose(propertyPurpose: PropertyPurpose) {
        _propertyPurpose.postValue(propertyPurpose)
    }

    // From transaction fragment, read only
    val fragmentPropertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()
}