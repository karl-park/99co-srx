package sg.searchhouse.agentconnect.viewmodel.activity.listing.user

import android.app.Application
import android.content.Context
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO.ActivateSrxCreditListingPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.SrxCreditListingActivationPO
import sg.searchhouse.agentconnect.service.GenerateGeneralReportService
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class FeaturesCreditApplicationViewModel constructor(application: Application) :
    ApiBaseViewModel<ActivateSrxCreditListingsResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    var isAllListingInactivate: Boolean = false
    var selectedListingIds = listOf<String>()

    var srxCreditListings = arrayListOf<ActivateSrxCreditListingPO>()
    var activationListingsTemp =
        mapOf<String, SrxCreditListingActivationPO.ListingActivationPO>()

    val creditSummaryPO = MutableLiveData<ActivateSrxCreditListingSummaryPO>()
    val creditType = MutableLiveData<ListingManagementEnum.SrxCreditMainType>()
    val deductibleCredits = MutableLiveData<String>()
    val availableCredits = MutableLiveData<String>()
    val isCheckAgreement = MutableLiveData<Boolean>()

    val btnState = MutableLiveData<ButtonState>()
    val isConfirmButtonEnabled = MutableLiveData<Boolean>()
    val actionBtnLabel: LiveData<String> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_sending_request)
            else -> applicationContext.getString(R.string.action_confirm)
        }
    }

    val activateSrxCreditStatus = MutableLiveData<ApiStatus<ActivateSrxCreditListingsResponse>>()
    val activationListingsUpdatedList =
        MutableLiveData<Map<String, SrxCreditListingActivationPO.ListingActivationPO>>()

    init {
        viewModelComponent.inject(this)
        setupInitialValues()
    }

    private fun setupInitialValues() {
        isCheckAgreement.value = false
        btnState.value = ButtonState.NORMAL
        isConfirmButtonEnabled.value = false
    }

    //Get SRX Credit Summary
    fun getActivateSrxCreditSummary() {
        val creditTypeValue =
            creditType.value?.value ?: return ErrorUtil.handleError("Missing credit type value")
        val items = arrayListOf<SrxCreditListingActivationPO.ListingActivationPO>()

        selectedListingIds.map {
            val idsTypeLists = it.split(",")
            val type = idsTypeLists.last()
            val id = idsTypeLists.first()

            if (type == ListingEnum.ListingType.SRX_LISTING.value && NumberUtil.isNaturalNumber(id)) {
                items.add(
                    SrxCreditListingActivationPO.ListingActivationPO(
                        id = id.toInt(),
                        value = false,
                        virtualTour = null,
                        valuation = null
                    )
                )
            }
        } //end of selected listings ids map

        activationListingsTemp = items.associateBy { it.id.toString() }

        performRequest(
            listingManagementRepository.getActivateSrxCreditSummary(
                creditTypeValue,
                items
            )
        )
    }

    fun updateActivateSrxCreditSummary(updateItem: SrxCreditListingActivationPO.ListingActivationPO) {
        val creditTypeValue = creditType.value?.value ?: 0
        val items = srxCreditListings.mapNotNull { it.activationListing }.toMutableList()

        val item = items.find { it.id == updateItem.id } ?: return
        items[items.indexOf(item)] = updateItem
        activationListingsTemp = items.associateBy { it.id.toString() }

        when (creditType.value) {
            ListingManagementEnum.SrxCreditMainType.FEATURED_LISTING -> {
                performNextRequest(
                    listingManagementRepository.getActivateSrxCreditSummary(
                        creditTypeValue,
                        items
                    )
                )
            }
            else -> {
                activationListingsUpdatedList.value = activationListingsTemp
            }
        }
    }

    fun activateSrxCreditForListings() {
        //Start changing state of button
        btnState.value = ButtonState.SUBMITTING
        isConfirmButtonEnabled.value = false

        //Note: removed srxCreditActivationAvailability false listings from srxCreditListings
        //Only activate available listings for features
        val creditTypeValue: Int = creditType.value?.value ?: 0
        val items = srxCreditListings.filter { it.srxCreditActivationAvailability }
            .mapNotNull {
                return@mapNotNull when (creditType.value) {
                    ListingManagementEnum.SrxCreditMainType.V360 -> {
                        it.activationListing?.virtualTour?.remarks = it.optionalRemark
                        it.activationListing
                    }
                    ListingManagementEnum.SrxCreditMainType.VALUATION -> {
                        it.activationListing?.valuation?.remarks = it.optionalRemark
                        it.activationListing
                    }
                    else -> it.activationListing
                }
            }

        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.activateSrxCreditForListings(creditTypeValue, items),
            onSuccess = {
                val listings = arrayListOf<SrxCreditListingActivationPO.ListingActivationPO>()
                it.summary?.listings?.map { listing ->
                    listings.add(
                        SrxCreditListingActivationPO.ListingActivationPO(
                            id = listing.id.toInt(),
                            value = false,
                            virtualTour = null,
                            valuation = null
                        )
                    )
                }
                activationListingsTemp = listings.associateBy { item -> item.id.toString() }

                btnState.postValue(ButtonState.SUBMITTED)
                isConfirmButtonEnabled.postValue(true)
                isCheckAgreement.postValue(false)
                activateSrxCreditStatus.postValue(ApiStatus.successInstance(it))
                mainResponse.postValue(it)
            },
            onFail = {
                btnState.postValue(ButtonState.NORMAL)
                isConfirmButtonEnabled.postValue(true)
                activateSrxCreditStatus.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                btnState.postValue(ButtonState.NORMAL)
                isConfirmButtonEnabled.postValue(true)
                activateSrxCreditStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    //Download sample report of valuation
    fun downloadValuationSampleReport(fileName: String) {
        val reportUrl =
            creditSummaryPO.value?.sampleValuationReportUrl ?: return ErrorUtil.handleError(
                applicationContext.getString(R.string.fail_generic_contact_srx)
            )
        GenerateGeneralReportService.launch(
            applicationContext,
            reportUrl,
            ReportEnum.ReportServiceType.VALUATION_SAMPLE_REPORT,
            fileName
        )
    }

    fun onCheckedChanged(btnView: CompoundButton, isChecked: Boolean) {
        if (btnView.isPressed) {
            isCheckAgreement.value = isChecked
        }
    }

    override fun shouldResponseBeOccupied(response: ActivateSrxCreditListingsResponse): Boolean {
        return response.summary?.listings?.isNotEmpty() ?: false
    }
}