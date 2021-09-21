package sg.searchhouse.agentconnect.viewmodel.fragment.listing.certifiedlisting

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.model.api.listing.GetListingResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingOwnerCertificationPO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.RequestOwnerCertificationResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class CertifiedListingViewModel constructor(application: Application) :
    ApiBaseViewModel<GetListingResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    val listingIdType = MutableLiveData<String>()
    val listingId = MutableLiveData<Int>()
    val listingPO: LiveData<ListingPO> =
        Transformations.map(mainResponse) { it?.fullListing?.listingDetailPO?.listingPO }
    val mobileNumber = MutableLiveData<String>()

    val btnState = MutableLiveData<ButtonState>()
    val isBtnEnabled: LiveData<Boolean> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING, ButtonState.ERROR -> false
            else -> true
        }
    }
    val btnLabel: LiveData<String> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_sending_invite)
            else -> applicationContext.getString(R.string.action_send_invite)
        }
    }

    val requestHomeOwnerStatus = MutableLiveData<ApiStatus<RequestOwnerCertificationResponse>>()
    val isHomeOwnerNotified = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
        setupInitialData()
    }

    private fun setupInitialData() {
        btnState.value = ButtonState.NORMAL
        isHomeOwnerNotified.value = false
    }

    fun getListingById(listingIdType: String) {
        val listingIdTypeList = listingIdType.split(",")
        val id = listingIdTypeList.first()
        val type = listingIdTypeList.last()
        listingId.value = id.toIntOrNull()
        performRequest(
            listingSearchRepository.getListing(
                listingId = id,
                listingType = type
            )
        )
    }

    fun requestHomeOwnerCertification() {
        if (isValidate()) {
            val id = listingId.value
                ?: return ErrorUtil.handleError("Missing listing id")
            val mobile = mobileNumber.value ?: return ErrorUtil.handleError("Missing mobile number")

            btnState.value = ButtonState.SUBMITTING

            if (NumberUtil.isInt(mobile)) {
                ApiUtil.performRequest(
                    applicationContext,
                    listingManagementRepository.requestOwnerCertification(
                        ListingOwnerCertificationPO(
                            id = null,
                            listingId = id,
                            mobileLocalNum = mobile.toInt(),
                            certifiedInd = null,
                            dateCertified = null,
                            dateCreate = null
                        )
                    ),
                    onSuccess = { response ->
                        isHomeOwnerNotified.postValue(true)
                        requestHomeOwnerStatus.postValue(ApiStatus.successInstance(response))
                        btnState.postValue(ButtonState.SUBMITTED)
                    }, onFail = { apiError ->
                        requestHomeOwnerStatus.postValue(ApiStatus.failInstance(apiError))
                        btnState.postValue(ButtonState.NORMAL)
                    }, onError = {
                        requestHomeOwnerStatus.postValue(ApiStatus.errorInstance())
                        btnState.postValue(ButtonState.NORMAL)
                    }
                )
            }
        } else {
            btnState.value = ButtonState.ERROR
        }
    }

    private fun isValidate(): Boolean {
        val mobile = mobileNumber.value ?: return false
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