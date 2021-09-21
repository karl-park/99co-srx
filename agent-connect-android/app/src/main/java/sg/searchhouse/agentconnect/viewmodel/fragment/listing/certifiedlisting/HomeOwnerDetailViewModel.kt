package sg.searchhouse.agentconnect.viewmodel.fragment.listing.certifiedlisting

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.listing.GetListingResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingOwnerCertificationPO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.RequestOwnerCertificationResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class HomeOwnerDetailViewModel constructor(application: Application) :
    ApiBaseViewModel<GetListingResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    val listingId = MutableLiveData<String>()
    val listingPO = MutableLiveData<ListingPO>()
    val mobileNum = MutableLiveData<String>().apply { value = "" }

    val requestOwnerStatus = MutableLiveData<ApiStatus<RequestOwnerCertificationResponse>>()
    val sendInviteButtonState = MutableLiveData<ButtonState>()
    val isSendInviteButtonEnabled: LiveData<Boolean> =
        Transformations.map(sendInviteButtonState) { response ->
            response?.let {
                return@map when (it) {
                    ButtonState.NORMAL, ButtonState.SUBMITTED -> true
                    else -> false
                }
            }
        }
    val notifiedHomeOwner = MutableLiveData<Boolean>().apply { value = false }

    val showCertifiedListingSection = MediatorLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
        setupShowCertifiedListingSection()
    }

    private fun setupShowCertifiedListingSection() {
        showCertifiedListingSection.addSource(notifiedHomeOwner) { notifyOwner ->
            if (listingPO.value?.isCommercialListing() == true) {
                showCertifiedListingSection.postValue(false)
            } else {
                showCertifiedListingSection.postValue(notifyOwner == false)
            }
        }

        showCertifiedListingSection.addSource(listingPO) { listing ->
            if (listing?.isCommercialListing() == true) {
                showCertifiedListingSection.postValue(false)
            } else {
                showCertifiedListingSection.postValue(notifiedHomeOwner.value == false)
            }

        }
    }

    fun getListingById(listingId: String) {
        if (NumberUtil.isNaturalNumber(listingId)) {
            performRequest(
                listingSearchRepository.getListing(
                    listingId,
                    ListingEnum.ListingType.SRX_LISTING.value
                )
            )
        }
    }

    fun requestOwnerCertification() {
        if (isValidate()) {
            sendInviteButtonState.postValue(ButtonState.SUBMITTING)
            val mobile = mobileNum.value ?: ""
            val id = listingId.value ?: ""
            if (NumberUtil.isNaturalNumber(id) && NumberUtil.isNaturalNumber(id)) {
                val ownerCertification = ListingOwnerCertificationPO(
                    id = null,
                    listingId = id.toInt(),
                    mobileLocalNum = mobile.toInt(),
                    certifiedInd = null,
                    dateCertified = null,
                    dateCreate = null
                )
                ApiUtil.performRequest(
                    applicationContext,
                    listingManagementRepository.requestOwnerCertification(ownerCertification),
                    onSuccess = { response ->
                        sendInviteButtonState.postValue(ButtonState.SUBMITTED)
                        requestOwnerStatus.postValue(ApiStatus.successInstance(response))
                    }, onFail = { apiError ->
                        sendInviteButtonState.postValue(ButtonState.NORMAL)
                        requestOwnerStatus.postValue(ApiStatus.failInstance(apiError))
                    }, onError = {
                        sendInviteButtonState.postValue(ButtonState.NORMAL)
                        requestOwnerStatus.postValue(ApiStatus.errorInstance())
                    }
                )
            }
        } else {
            sendInviteButtonState.value = ButtonState.ERROR
        }
    }

    fun afterTextChangedMobileNum(editable: Editable?) {
        val mobileNum = editable?.toString() ?: ""
        if (!TextUtils.isEmpty(mobileNum.trim())) {
            sendInviteButtonState.value = ButtonState.NORMAL
        }
    }

    private fun isValidate(): Boolean {
        val mobile = mobileNum.value ?: return false
        if (TextUtils.isEmpty(mobile)) {
            return false
        } else if (!StringUtil.isMobileNoValid(mobile)) {
            return false
        }
        return true
    }

    override fun shouldResponseBeOccupied(response: GetListingResponse): Boolean {
        return true
    }
}