package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.data.repository.CommunityRepository
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.dsl.performRequestsLenientOneByOne
import sg.searchhouse.agentconnect.enumeration.api.CommunityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.app.CommunityPostEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.model.api.ApiError
import sg.searchhouse.agentconnect.model.api.agent.GetPackageDetailsResponse
import sg.searchhouse.agentconnect.model.api.community.*
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO.ActivateSrxCreditListingPO
import sg.searchhouse.agentconnect.util.CommunityUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModelV2
import javax.inject.Inject

class SponsorListingViewModel constructor(application: Application) :
    ApiBaseViewModelV2<ActivateSrxCreditListingsResponse>(application) {

    @Inject
    lateinit var agentRepository: AgentRepository

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    @Inject
    lateinit var communityRepository: CommunityRepository

    @Inject
    lateinit var applicationContext: Context

    val listingId = MutableLiveData<Int>()

    val hyperTemplateMemberCounts = MutableLiveData<List<Int?>>()

    private val getCommunityHyperTargetResponse =
        MutableLiveData<ApiStatus<GetCommunityHyperTargetsResponse>>()

    val hyperTargetTemplates: LiveData<List<CommunityHyperTargetTemplatePO>> =
        Transformations.map(getCommunityHyperTargetResponse) {
            it?.body?.targets?.results ?: emptyList()
        }

    val isShowMoreHyperTemplatesButton =
        Transformations.map(getCommunityHyperTargetResponse) { it?.body?.targets?.total ?: 0 > 3 }

    val showMoreHyperTemplatesButtonText =
        Transformations.map(getCommunityHyperTargetResponse) {
            val moreCount = NumberUtil.formatThousand((it?.body?.targets?.total ?: 0) - 3)
            applicationContext.getString(R.string.button_load_more_hyper_templates, moreCount)
        }

    private val isHyperTargetTemplatesOccupied: LiveData<Boolean> =
        Transformations.map(hyperTargetTemplates) {
            it?.isNotEmpty() == true
        }

    val isShowHyperTargetTemplates = MediatorLiveData<Boolean>()

    val isShowNoTemplateCreateNewTargetButton = MediatorLiveData<Boolean>()

    // Is summary already loaded for the first time?
    // To check so don't override header and remark text boxes when update target communities
    var isSummaryInitialized: Boolean = false

    val listing: LiveData<ActivateSrxCreditListingPO> = Transformations.map(mainResponseBody) {
        it?.summary?.listings?.firstOrNull()
    }

    val sponsorDuration: LiveData<CommunityEnum.SponsorDuration> =
        Transformations.map(mainResponseBody) {
            it?.summary?.numberOfWeeks?.run {
                CommunityEnum.SponsorDuration.values().find { duration -> duration.value == this }
            } ?: CommunityEnum.SponsorDuration.ONE_WEEK
        }

    val summary: LiveData<ActivateSrxCreditListingSummaryPO> =
        Transformations.map(mainResponseBody) { it?.summary }

    val creditsDeductibleLabel: LiveData<String> =
        Transformations.map(mainResponseBody) {
            it?.summary?.getFormattedCreditsDeductible(applicationContext)
        }

    val header: LiveData<String> = Transformations.map(summary) {
        applicationContext.getString(R.string.header_sponsor_listing, it?.sponsoredLocation)
    }

    val newHyperTargetTemplatePO = MutableLiveData<CommunityHyperTargetTemplatePO>()
    val existingHyperTargetTemplatePO = MutableLiveData<CommunityHyperTargetTemplatePO>()

    val listingHeader = MutableLiveData<String>()

    val listingRemark = MutableLiveData<String>()

    val isShowListingHeaderError = MutableLiveData<Boolean>()
    val isShowListingRemarkError = MutableLiveData<Boolean>()

    val isCheckAgreement = MutableLiveData<Boolean>()

    val isEnableSubmitButton = MediatorLiveData<Boolean>()
    val isEnablePreview: LiveData<Boolean> = Transformations.map(summary) {
        val hasDeductible = it?.creditsDeductible ?: 0 > 0
        val isCreditsEnough = it?.isCreditsEnough() == true
        hasDeductible && isCreditsEnough
    }

    // A proxy to imply that there is existing sponsor for this listing
    val hasExistingTargetCommunities: LiveData<Boolean> = Transformations.map(summary) {
        it?.sponsoredLocation?.isNotEmpty() == true
    }

    val getSrxCreditPackageDetailsStatus = MutableLiveData<ApiStatus<GetPackageDetailsResponse>>()

    val activateSrxCreditListingsStatus =
        MutableLiveData<ApiStatus<ActivateSrxCreditListingsResponse>>()

    val newHyperTargetTotalAvailableAddressCount =
        Transformations.map(summary) {
            it?.totalMembersCount ?: 0
        }

    val newHyperTargetTotalAvailableAddressCountLabel: LiveData<String> =
        Transformations.map(newHyperTargetTotalAvailableAddressCount) {
            NumberUtil.formatThousand(it ?: 0)
        }

    private val newHyperTargetSelectedAddressCount =
        Transformations.map(summary) {
            it?.selectedMembersCount ?: 0
        }

    val newHyperTargetSelectedAddressCountLabel: LiveData<String> =
        Transformations.map(newHyperTargetSelectedAddressCount) {
            applicationContext.resources.getQuantityString(
                R.plurals.label_address_count,
                it,
                NumberUtil.formatThousand(it ?: 0)
            )
        }

    val newHyperTargetAddressesPercentage = MediatorLiveData<Int>()

    val activateListingErrorMessage: LiveData<String> =
        Transformations.map(activateSrxCreditListingsStatus) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    if (it.body?.summary?.succesfullyActivated == true) {
                        null
                    } else {
                        it.body?.getFirstSrxCreditActivationRemark(applicationContext)
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    it.error?.error
                }
                else -> {
                    null
                }
            }
        }

    val promoteType = MutableLiveData<CommunityPostEnum.PromoteType>().apply {
        value = CommunityPostEnum.PromoteType.LISTING
    }
    val target = MutableLiveData<CommunityPostEnum.Target>().apply {
        value = CommunityPostEnum.Target.HYPER // TODO Revert to default COMMUNITY when done
    }

    val totalAvailableAddressCount = MutableLiveData<Int>().apply { value = 0 }

    val totalAvailableAddressCountLabel: LiveData<String> =
        Transformations.map(totalAvailableAddressCount) {
            NumberUtil.formatThousand(it ?: 0)
        }

    private val selectedAddressCount = MutableLiveData<Int>().apply { value = 0 }

    val selectedAddressCountLabel: LiveData<String> = Transformations.map(selectedAddressCount) {
        applicationContext.resources.getQuantityString(
            R.plurals.label_address_count,
            it,
            NumberUtil.formatThousand(it ?: 0)
        )
    }

    private val allCommunities = MutableLiveData<List<CommunityTopDownPO>>()

    val targetCommunityIds = MutableLiveData<List<Int>>()
    val targetCommunities = MediatorLiveData<List<CommunityTopDownPO>>()

    val hasTargetCommunities: LiveData<Boolean> = Transformations.map(targetCommunities) {
        it?.isNotEmpty() == true
    }

    val isSubmitting: LiveData<Boolean> = Transformations.map(activateSrxCreditListingsStatus) {
        it?.key == ApiStatus.StatusKey.LOADING
    }

    val addressesPercentage = MediatorLiveData<Int>()

    val getAddressCountStatus =
        MediatorLiveData<ApiStatus<GetCommunityMemberCountResponse>>()

    init {
        viewModelComponent.inject(this)
        setupIsEnableSubmitButton()
        setupTargetCommunities()
        setupAddressesPercentage()
        setupNewHyperTargetAddressesPercentage()
        performGetCommunities()
        setupIsShowHyperTargetTemplates()
        setupIsShowMainCreateNewTemplateButton()
    }

    private fun setupNewHyperTargetAddressesPercentage() {
        newHyperTargetAddressesPercentage.addSource(newHyperTargetTotalAvailableAddressCount) {
            updateNewHyperTargetAddressesPercentage()
        }
        newHyperTargetAddressesPercentage.addSource(newHyperTargetSelectedAddressCount) {
            updateNewHyperTargetAddressesPercentage()
        }
    }

    private fun setupIsShowMainCreateNewTemplateButton() {
        isShowNoTemplateCreateNewTargetButton.addSource(newHyperTargetTemplatePO) { updateIsShowNoTemplateCreateNewTargetButton() }
        isShowNoTemplateCreateNewTargetButton.addSource(existingHyperTargetTemplatePO) { updateIsShowNoTemplateCreateNewTargetButton() }
        isShowNoTemplateCreateNewTargetButton.addSource(hyperTargetTemplates) { updateIsShowNoTemplateCreateNewTargetButton() }
    }

    private fun updateIsShowNoTemplateCreateNewTargetButton() {
        val hasExistingDraft = existingHyperTargetTemplatePO.value != null
        val hasNewDraft = newHyperTargetTemplatePO.value != null
        val hasExistingTemplates = hyperTargetTemplates.value?.isNotEmpty() == true
        isShowNoTemplateCreateNewTargetButton.postValue(!hasExistingDraft && !hasNewDraft && !hasExistingTemplates)
    }

    private fun setupIsShowHyperTargetTemplates() {
        isShowHyperTargetTemplates.addSource(isHyperTargetTemplatesOccupied) {
            updateIsShowHyperTargetTemplates()
        }
        isShowHyperTargetTemplates.addSource(newHyperTargetTemplatePO) {
            updateIsShowHyperTargetTemplates()
        }
        isShowHyperTargetTemplates.addSource(existingHyperTargetTemplatePO) {
            updateIsShowHyperTargetTemplates()
        }
    }

    private fun updateIsShowHyperTargetTemplates() {
        isShowHyperTargetTemplates.postValue(isHyperTargetTemplatesOccupied.value == true && newHyperTargetTemplatePO.value == null && existingHyperTargetTemplatePO.value == null)
    }

    private fun setupAddressesPercentage() {
        addressesPercentage.addSource(totalAvailableAddressCount) {
            updateAddressesPercentage()
        }
        addressesPercentage.addSource(selectedAddressCount) {
            updateAddressesPercentage()
        }
    }

    private fun updateAddressesPercentage() {
        val total = totalAvailableAddressCount.value ?: return
        val selected = selectedAddressCount.value ?: return
        if (total == 0) {
            addressesPercentage.postValue(0)
        } else {
            val percentage = selected * 100 / total
            addressesPercentage.postValue(percentage)
        }
    }

    private fun updateNewHyperTargetAddressesPercentage() {
        val total = newHyperTargetTotalAvailableAddressCount.value ?: return
        val selected = newHyperTargetSelectedAddressCount.value ?: return
        if (total == 0) {
            newHyperTargetAddressesPercentage.postValue(0)
        } else {
            val percentage = selected * 100 / total
            newHyperTargetAddressesPercentage.postValue(percentage)
        }
    }

    private fun setupTargetCommunities() {
        targetCommunities.addSource(allCommunities) { updateTargetCommunities() }
        targetCommunities.addSource(targetCommunityIds) { updateTargetCommunities() }
    }

    private fun updateTargetCommunities() {
        val allCommunities = allCommunities.value ?: emptyList()
        val targetCommunityIds = targetCommunityIds.value ?: emptyList()
        val communities =
            allCommunities.filter { community -> targetCommunityIds.any { it == community.getCommunityId() } }
        targetCommunities.postValue(communities)
    }

    private fun performGetCommunities() {
        communityRepository.getPlanningAreaSubZoneCommunities()
            .performRequest(applicationContext, onSuccess = {
                populateAllCommunities(it)
            }, onFail = {
                // Do nothing
            }, onError = {
                // Do nothing
            })
    }

    fun performGetCommunityMemberCounts() {
        val communityIds = targetCommunityIds.value ?: emptyList()
        if (communityIds.isEmpty()) {
            selectedAddressCount.postValue(0)
            totalAvailableAddressCount.postValue(0)
            return
        }
        getAddressCountStatus.postValue(ApiStatus.loadingInstance())
        communityRepository.getMemberCount(communityIds)
            .performRequest(applicationContext, onSuccess = {
                selectedAddressCount.postValue(it.selected)
                totalAvailableAddressCount.postValue(it.total)
                getAddressCountStatus.postValue(ApiStatus.successInstance(it))
            }, onFail = {
                selectedAddressCount.postValue(0)
                totalAvailableAddressCount.postValue(0)
                getAddressCountStatus.postValue(ApiStatus.failInstance(it))
            }, onError = {
                selectedAddressCount.postValue(0)
                totalAvailableAddressCount.postValue(0)
                getAddressCountStatus.postValue(ApiStatus.errorInstance())
            })
    }

    fun isDurationSelected(duration: CommunityEnum.SponsorDuration): Boolean {
        return this.sponsorDuration.value?.value == duration.value
    }

    private fun populateAllCommunities(getCommunitiesResponse: GetCommunitiesResponse) {
        allCommunities.postValue(
            CommunityUtil.getFlatPlanningAreasAndSubZones(
                getCommunitiesResponse.communities
            )
        )
    }

    private fun setupIsEnableSubmitButton() {
        isCheckAgreement.addEnableSubmitButtonSource()
        summary.addEnableSubmitButtonSource()
        target.addEnableSubmitButtonSource()
        promoteType.addEnableSubmitButtonSource()
        targetCommunities.addEnableSubmitButtonSource()
        isSubmitting.addEnableSubmitButtonSource()
        mainStatus.addEnableSubmitButtonSource()
    }

    private fun LiveData<*>.addEnableSubmitButtonSource() =
        isEnableSubmitButton.addSource(this) { isEnableSubmitButton.postValue(isEnableSubmitButton()) }

    fun canSubmit(): Boolean {
        val hasTitle = hasHeader()
        val hasRemark = hasRemark()
        return isEnableSubmitButton() && hasTitle && hasRemark
    }

    private fun isEnableSubmitButton(): Boolean {
        val isValidPromoteType = promoteType.value == CommunityPostEnum.PromoteType.LISTING
        val hasDeductible = summary.value?.creditsDeductible ?: 0 > 0
        val isCreditsEnough = summary.value?.isCreditsEnough() == true
        val isAgreementChecked = isCheckAgreement.value == true
        val hasCommunities =
            target.value == CommunityPostEnum.Target.COMMUNITY && targetCommunities.value?.isNotEmpty() == true
        val hyperTargetTemplatePO =
            existingHyperTargetTemplatePO.value ?: newHyperTargetTemplatePO.value
        val hasHyperTargetTemplate =
            target.value == CommunityPostEnum.Target.HYPER && hyperTargetTemplatePO != null
        val hasTarget = hasCommunities || hasHyperTargetTemplate
        val isSubmitting = isSubmitting.value == true
        val isSummaryLoaded = mainStatus.value == ApiStatus.StatusKey.SUCCESS
        return hasDeductible && isCreditsEnough && isAgreementChecked && isValidPromoteType && hasTarget && !isSubmitting && isSummaryLoaded
    }

    private fun hasHeader(): Boolean {
        return listingHeader.value?.isNotEmpty() == true
    }

    private fun hasRemark(): Boolean {
        return listingRemark.value?.isNotEmpty() == true
    }

    fun showErrors() {
        // Assumption: Missing title or description
        ViewUtil.showMessage(R.string.error_missing_required_fields)

        if (!hasHeader()) {
            isShowListingHeaderError.postValue(true)
        }
        if (!hasRemark()) {
            isShowListingRemarkError.postValue(true)
        }
    }

    fun performGetActivateSrxCreditSummary(
        mSponsorDuration: CommunityEnum.SponsorDuration? = null,
        isReload: Boolean = false
    ) {
        val listingId = listingId.value ?: return
        val targetType = target.value ?: throw IllegalArgumentException("Missing `target`.")
        val communityIds = if (targetType == CommunityPostEnum.Target.COMMUNITY) {
            targetCommunityIds.value
        } else {
            null
        }
        val hyperTargetTemplatePO = if (targetType == CommunityPostEnum.Target.HYPER) {
            existingHyperTargetTemplatePO.value ?: newHyperTargetTemplatePO.value
        } else {
            null
        }
        val submitSponsorDuration =
            mSponsorDuration ?: sponsorDuration.value ?: CommunityEnum.SponsorDuration.ONE_WEEK

        if (isReload) {
            performNextRequest(
                listingManagementRepository.getSponsoredActivateSrxCreditSummary(
                    listingId = listingId,
                    sponsorDuration = submitSponsorDuration,
                    targetType = targetType,
                    communityIds = communityIds,
                    hyperTargetTemplatePO = hyperTargetTemplatePO
                )
            )
        } else {
            performRequest(
                listingManagementRepository.getSponsoredActivateSrxCreditSummary(
                    listingId = listingId,
                    sponsorDuration = submitSponsorDuration,
                    targetType = targetType,
                    communityIds = communityIds,
                    hyperTargetTemplatePO = hyperTargetTemplatePO
                )
            )
        }
    }

    @Throws(IllegalArgumentException::class)
    fun performActivateSrxCreditForListings() {
        val listingId = listingId.value ?: throw IllegalArgumentException("Missing `listingId`")
        val title = listingHeader.value ?: throw IllegalArgumentException("Missing `title`")
        val remark = listingRemark.value ?: throw IllegalArgumentException("Missing `remark`")
        val targetType = target.value ?: throw IllegalArgumentException("Missing `target`")
        val communityIds = if (targetType == CommunityPostEnum.Target.COMMUNITY) {
            targetCommunityIds.value ?: throw IllegalArgumentException("Missing `communityIds`")
        } else {
            null
        }
        val hyperTargetTemplatePO = if (targetType == CommunityPostEnum.Target.HYPER) {
            newHyperTargetTemplatePO.value
                ?: throw IllegalArgumentException("Missing `hyperTargetTemplatePO`")
        } else {
            null
        }
        activateSrxCreditListingsStatus.postValue(ApiStatus.loadingInstance())
        listingManagementRepository.activateSrxCreditForSponsoredListings(
            listingId,
            title,
            remark,
            targetType,
            communityIds,
            hyperTargetTemplatePO
        )
            .performRequest(applicationContext, onSuccess = { response ->
                if (response.summary?.succesfullyActivated == true) {
                    activateSrxCreditListingsStatus.postValue(ApiStatus.successInstance(response))
                } else {
                    val error = ApiError(
                        errorCode = "",
                        error = response.getFirstSrxCreditActivationRemark(
                            applicationContext
                        )
                    )
                    activateSrxCreditListingsStatus.postValue(ApiStatus.failInstance(error))
                }
            }, onFail = {
                activateSrxCreditListingsStatus.postValue(ApiStatus.failInstance(it))
            }, onError = {
                activateSrxCreditListingsStatus.postValue(ApiStatus.errorInstance())
            })
    }

    fun performGetSrxCreditPackageDetails() {
        getSrxCreditPackageDetailsStatus.postValue(ApiStatus.loadingInstance())
        agentRepository.getSrxCreditPackageDetails(ListingManagementEnum.SrxCreditMainType.SPONSORED_POST.value)
            .performRequest(
                applicationContext,
                onSuccess = {
                    getSrxCreditPackageDetailsStatus.postValue(ApiStatus.successInstance(it))
                },
                onFail = {
                    getSrxCreditPackageDetailsStatus.postValue(ApiStatus.failInstance(it))
                },
                onError = {
                    getSrxCreditPackageDetailsStatus.postValue(ApiStatus.errorInstance())
                }
            )
    }

    override fun shouldResponseBeOccupied(response: ActivateSrxCreditListingsResponse): Boolean {
        return true
    }

    fun toggleSponsorDuration(duration: CommunityEnum.SponsorDuration) {
        if (duration != sponsorDuration.value) {
            performGetActivateSrxCreditSummary(mSponsorDuration = duration, isReload = true)
        }
    }

    fun performGetCommunityHyperTargets() {
        listingManagementRepository.getCommunityHyperTargets(0, HYPER_TARGET_LIST_SIZE)
            .performRequest(applicationContext, onSuccess = {
                getCommunityHyperTargetResponse.postValue(ApiStatus.successInstance(it))
            }, onFail = {
                getCommunityHyperTargetResponse.postValue(ApiStatus.failInstance(it))
            }, onError = {
                getCommunityHyperTargetResponse.postValue(ApiStatus.errorInstance())
            })
    }

    // Populate count on existing hyper target templates
    fun performGetMemberCountByHyperTargetPOs(hyperTargetTemplatePOs: List<CommunityHyperTargetTemplatePO>) {
        hyperTargetTemplatePOs.map {
            val id = it.id ?: throw IllegalStateException("id must not be null!")
            communityRepository.getMemberCountByHyperTarget(id)
        }.performRequestsLenientOneByOne(applicationContext, onItemSuccess = { responses ->
            hyperTemplateMemberCounts.postValue(responses.map { it?.selected })
        })
    }

    fun hasExistingHyperTargetTemplate(): Boolean {
        return newHyperTargetTemplatePO.value != null || existingHyperTargetTemplatePO.value != null
    }

    fun hasExistingRegularCommunities(): Boolean {
        return hasTargetCommunities.value == true
    }

    companion object {
        const val HYPER_TARGET_LIST_SIZE = 3
        const val LISTING_HEADER_MAX_WORD_COUNT = "70"
        const val LISTING_REMARK_MAX_WORD_COUNT = "140"
    }

    override fun isCheckOverlapRequests(): Boolean {
        return true
    }
}
