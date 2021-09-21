package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data structure:
 * https://streetsine.atlassian.net/wiki/spaces/SIN/pages/691175438/Agent+V1+Data+Structures
 *
 * Subscription:
 * https://streetsine.atlassian.net/wiki/spaces/SIN/pages/736231462/Agent+Subscription+V1+API
 * https://streetsine.atlassian.net/wiki/spaces/SIN/pages/695238693/Agent+Details+V1+API
 */
@Singleton
class AgentRepository @Inject constructor(private val srxDataSource: SrxDataSource) {

    fun getAgentDetails(): Call<GetAgentDetailsResponse> {
        return srxDataSource.getAgentDetails()
    }

    fun getAgentCv(agentId: Int): Call<GetAgentCvResponse> {
        return srxDataSource.getAgentCV(agentId)
    }

    fun saveOrUpdateUserAgentCV(agentCv: AgentCvPO): Call<SaveOrUpdateAgentCvResponse> {
        return srxDataSource.saveOrUpdateUserAgentCV(agentCv)
    }

    fun getAgentTransactions(sortOrder: String): Call<GetAgentTransactions> {
        return srxDataSource.getAgentTransactions(sortOrder)
    }

    fun concealTransaction(transactionId: Int): Call<DefaultResultResponse> {
        return srxDataSource.concealTransaction(transactionId)
    }

    fun revealTransaction(transactionId: Int): Call<DefaultResultResponse> {
        return srxDataSource.revealTransaction(transactionId)
    }

    fun checkIsPublicUrlAvailable(publicUrl: String): Call<DefaultResultResponse> {
        return srxDataSource.checkIsPublicUrlAvailable(publicUrl)
    }

    fun getAgentFullProfile(): Call<GetAgentFullProfileResponse> {
        return srxDataSource.getAgentFullProfile()
    }

    //call subscription api to get package details
    fun getSubscriptionPackageDetails(): Call<GetPackageDetailsResponse> {
        return srxDataSource.getSubscriptionPackageDetails()
    }

    //get srx credits package details
    fun getSrxCreditPackageDetails(creditMainType: Int): Call<GetPackageDetailsResponse> {
        return srxDataSource.getSrxCreditPackageDetails(creditMainType)
    }

    //payment
    fun generatePaymentLink(
        paymentType: Int,
        packageId: String,
        isInstallment: Boolean,
        bankId: Int? = null,
        installmentMonth: Int? = null
    ): Call<GeneratePaymentLinkResponse> {
        return srxDataSource.generatePaymentLink(
            paymentType,
            packageId,
            isInstallment,
            bankId,
            installmentMonth
        )
    }

    fun createTrialAccount(): Call<DefaultResultResponse> {
        return srxDataSource.createTrialAccount()
    }

    //other agent transactions
    fun findOtherAgentTransactions(
        agentUserId: Int,
        type: String,
        cdResearchSubtypes: String
    ): Call<FindOtherAgentTransactionsResponse> {
        return srxDataSource.findOtherAgentTransactions(agentUserId, type, cdResearchSubtypes)
    }
}