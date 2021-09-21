package sg.searchhouse.agentconnect.viewmodel.activity.agent.profile

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPurpose
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.SubscriptionCreditSource
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.GeneratePaymentLinkResponse
import sg.searchhouse.agentconnect.model.api.agent.GetPackageDetailsResponse
import sg.searchhouse.agentconnect.model.api.agent.PackageDetailPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class SubscriptionPackageDetailsViewModel constructor(application: Application) :
    ApiBaseViewModel<GetPackageDetailsResponse>(application) {

    @Inject
    lateinit var agentRepository: AgentRepository

    @Inject
    lateinit var applicationContext: Context

    var source: SubscriptionCreditSource? = null
    var packages = arrayListOf<PackageDetailPO>()

    val paymentType = MutableLiveData<Int>()
    val paymentPurpose = MutableLiveData<PaymentPurpose>()
    val paymentLinkResult = MutableLiveData<ApiStatus<GeneratePaymentLinkResponse>>()
    val createTrialAccountResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()

    init {
        viewModelComponent.inject(this)
    }

    fun getSubscriptionPackageDetails() {
        performRequest(agentRepository.getSubscriptionPackageDetails())
    }

    fun getSrxCreditPackageDetails(creditType: Int) {
        performRequest(agentRepository.getSrxCreditPackageDetails(creditType))
    }

    fun generatePaymentLink(
        packageId: String,
        isInstallment: Boolean,
        bankId: Int?,
        installmentMonth: Int?
    ) {
        val paymentType = paymentType.value ?: return
        ApiUtil.performRequest(
            applicationContext,
            agentRepository.generatePaymentLink(
                paymentType,
                packageId,
                isInstallment,
                bankId,
                installmentMonth
            ),
            onSuccess = { paymentLinkResult.postValue(ApiStatus.successInstance(it)) },
            onFail = { paymentLinkResult.postValue(ApiStatus.failInstance(it)) },
            onError = { paymentLinkResult.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun performCreateTrialAccount() {
        createTrialAccountResponse.postValue(ApiStatus.loadingInstance())
        agentRepository.createTrialAccount().performRequest(applicationContext,
            onSuccess = { createTrialAccountResponse.postValue(ApiStatus.successInstance(it)) },
            onFail = { createTrialAccountResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { createTrialAccountResponse.postValue(ApiStatus.errorInstance()) })
    }

    override fun shouldResponseBeOccupied(response: GetPackageDetailsResponse): Boolean {
        return true
    }
}