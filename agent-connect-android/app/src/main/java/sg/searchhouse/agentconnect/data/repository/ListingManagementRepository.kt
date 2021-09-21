package sg.searchhouse.agentconnect.data.repository

import android.text.TextUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant.BATCH_SIZE_LISTINGS
import sg.searchhouse.agentconnect.constant.AppConstant.DEFAULT_BATCH_SIZE
import sg.searchhouse.agentconnect.data.datasource.GoogleApiDataSource
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.CommunityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.*
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.app.CommunityPostEnum
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.model.api.community.GetCommunityHyperTargetsResponse
import sg.searchhouse.agentconnect.model.api.googleapi.GetYoutubeVideoInfoResponse
import sg.searchhouse.agentconnect.model.api.listing.*
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.*
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.SrxCreditListingActivationPO.ListingActivationPO.ListingActivationSponsored
import sg.searchhouse.agentconnect.model.api.listing.search.ExportListingsRequest
import sg.searchhouse.agentconnect.model.api.listing.search.ExportListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.search.FindListingsCountResponse
import sg.searchhouse.agentconnect.model.api.listing.user.FindMyListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.user.ListingsGroupSummaryResponse
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Listing management:  https://streetsine.atlassian.net/wiki/spaces/SIN/pages/685375547/Listing+Management+V1+API
 * Data structure:      https://streetsine.atlassian.net/wiki/spaces/SIN/pages/690290736/Listing+V1+Data+Structures
 */
@Singleton
class ListingManagementRepository @Inject constructor(
    private val srxDataSource: SrxDataSource,
    private val googleApiDataSource: GoogleApiDataSource
) {
    fun getListingsSummary(): Call<ListingsSummaryResponse> {
        return srxDataSource.getListingsSummary()
    }

    fun getListingsGroupSummary(): Call<ListingsGroupSummaryResponse> {
        return srxDataSource.getListingsGroupSummary()
    }

    fun createListing(listingEditPO: ListingEditPO): Call<CreateUpdateListingResponse> {
        return srxDataSource.createListing(listingEditPO)
    }

    fun updateListing(listingEditPO: ListingEditPO): Call<CreateUpdateListingResponse> {
        return srxDataSource.updateListing(listingEditPO)
    }

    fun postListing(listingEditPO: ListingEditPO): Call<PostListingResponse> {
        return srxDataSource.postListing(listingEditPO)
    }

    fun getListingForListingManagement(listingId: String): Call<GetListingForListingManagementResponse> {
        return srxDataSource.getListingForListingManagement(listingId)
    }

    fun movePhoto(photo: PostListingPhotoPO): Call<DefaultResultResponse> {
        return srxDataSource.movePhoto(photo)
    }

    fun setCoverPhoto(photo: PostListingPhotoPO): Call<DefaultResultResponse> {
        return srxDataSource.setCoverPhoto(photo)
    }

    fun uploadNewPhoto(
        listingId: Int,
        position: Int,
        isFloorPlan: Boolean,
        isCover: Boolean,
        file: File?
    ): Call<UploadPhotoResponse> {

        var fileParam: MultipartBody.Part? = null
        if (file != null) {
            fileParam =
                MultipartBody.Part.createFormData(
                    "File", file.name, file
                        .asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
        }

        return srxDataSource.uploadPhoto(
            listingId = listingId,
            position = position,
            isFloorPlan = isFloorPlan,
            isCover = isCover,
            file = fileParam
        )
    }

    fun updateExistingPhoto(
        listingId: Int,
        position: Int,
        isFloorPlan: Boolean,
        isCover: Boolean,
        file: File?,
        photoId: Int
    ): Call<UploadPhotoResponse> {

        var fileParam: MultipartBody.Part? = null
        if (file != null) {
            fileParam =
                MultipartBody.Part.createFormData(
                    "File", file.name, file
                        .asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
        }

        return srxDataSource.uploadPhoto(
            listingId,
            position,
            isFloorPlan,
            isCover,
            fileParam,
            photoId
        )
    }

    fun deleteSubmittedPhotos(
        listingId: Int,
        photosSubmitted: List<PostListingPhotoPO>? = null
    ): Call<DefaultResultResponse> {
        return srxDataSource.deletePhotos(
            DeletePhotosRequest(
                listingId,
                photosSubmitted,
                null,
                null
            )
        )
    }

    fun deleteFloorPlanPhotos(
        listingId: Int,
        floorPlanPhotos: List<PostListingPhotoPO>? = null
    ): Call<DefaultResultResponse> {
        return srxDataSource.deletePhotos(
            DeletePhotosRequest(
                listingId,
                null,
                floorPlanPhotos,
                null
            )
        )
    }

    fun hideProjectPhotos(
        listingId: Int,
        projectPhotos: List<PostListingPhotoPO>? = null
    ): Call<DefaultResultResponse> {
        return srxDataSource.deletePhotos(
            DeletePhotosRequest(
                listingId,
                null,
                null,
                projectPhotos
            )
        )
    }

    fun getPostingCredits(listingId: String): Call<GetPostingCreditsResponse> {
        return srxDataSource.getPostingCredits(listingId)
    }

    fun getPostingsCredits(listingIds: List<String>): Call<GetPostingCreditsResponse> {
        val listingIdsString = listingIds.joinToString(",")
        return srxDataSource.getPostingsCredits(listingIdsString)
    }

    fun requestOwnerCertification(
        listingOwnerCertificationPO: ListingOwnerCertificationPO
    ): Call<RequestOwnerCertificationResponse> {
        return srxDataSource.requestOwnerCertification(listingOwnerCertificationPO)
    }

    //You tube
    fun getYoutubeStatusById(id: String): Call<GetYoutubeVideoInfoResponse> {
        return googleApiDataSource.getYoutubeVideos(
            id = id,
            part = "status"
        )
    }

    //under listing management
    fun findMyListings(
        startResultIndex: Int,
        listingGroups: List<ListingManagementEnum.ListingGroup>,
        orderCriteria: ListingManagementEnum.OrderCriteria,
        ownershipType: OwnershipType,
        propertySubTypes: List<PropertySubType>? = null,
        propertyAge: PropertyAge? = null,
        bedrooms: Int? = null,
        tenure: String? = null,
        address: String? = null
    ): Call<FindMyListingsResponse> {
        return findMyListingsMain(
            startResultIndex = startResultIndex,
            isCountOnly = false,
            listingGroups = listingGroups,
            orderCriteria = orderCriteria,
            ownershipType = ownershipType,
            propertySubTypes = propertySubTypes,
            propertyAge = propertyAge,
            bedrooms = bedrooms,
            tenure = tenure,
            address = address
        )
    }

    //under listing management
    fun findMyListingsCount(
        listingGroups: List<ListingManagementEnum.ListingGroup>,
        orderCriteria: ListingManagementEnum.OrderCriteria,
        ownershipType: OwnershipType,
        propertySubTypes: List<PropertySubType>,
        propertyAge: PropertyAge? = null,
        bedrooms: Int? = null,
        tenure: String? = null,
        address: String? = null
    ): Call<FindMyListingsResponse> {
        return findMyListingsMain(
            isCountOnly = true,
            listingGroups = listingGroups,
            orderCriteria = orderCriteria,
            ownershipType = ownershipType,
            propertySubTypes = propertySubTypes,
            propertyAge = propertyAge,
            bedrooms = bedrooms,
            tenure = tenure,
            address = address
        )
    }

    //under listing management
    @Throws(IllegalArgumentException::class)
    private fun findMyListingsMain(
        startResultIndex: Int? = null,
        isCountOnly: Boolean,
        listingGroups: List<ListingManagementEnum.ListingGroup>,
        orderCriteria: ListingManagementEnum.OrderCriteria,
        ownershipType: OwnershipType,
        propertySubTypes: List<PropertySubType>? = null,
        propertyAge: PropertyAge? = null,
        bedrooms: Int? = null,
        tenure: String? = null,
        address: String? = null
    ): Call<FindMyListingsResponse> {
        require(ownershipType != OwnershipType.ROOM_RENTAL) { "Invalid ownership type ${ownershipType.value}" }
        val listingGroupIds = listingGroups.map { it.value }.joinToString(",")
        val cdResearchSubtypes = propertySubTypes?.map { it.type }?.joinToString(",")

        var builtYearMin: Int? = null
        var builtYearMax: Int? = null
        propertyAge?.let {
            builtYearMin = DateTimeUtil.getCurrentYear() - it.to
            builtYearMax = DateTimeUtil.getCurrentYear() - it.from
        }

        return if (isCountOnly) {
            //Find Listings Count only
            srxDataSource.findMyListingsCount(
                groupId = listingGroupIds,
                orderCriteria = orderCriteria.value,
                type = ownershipType.value,
                cdResearchSubtypes = cdResearchSubtypes,
                builtYearMin = builtYearMin,
                builtYearMax = builtYearMax,
                bedrooms = bedrooms,
                cdTenure = tenure,
                address = address
            )
        } else {
            //Find Listings
            srxDataSource.findMyListings(
                startResultIndex = startResultIndex ?: 0,
                maxResults = DEFAULT_BATCH_SIZE,
                groupId = listingGroupIds,
                orderCriteria = orderCriteria.value,
                type = ownershipType.value,
                cdResearchSubtypes = cdResearchSubtypes,
                builtYearMin = builtYearMin,
                builtYearMax = builtYearMax,
                bedrooms = bedrooms,
                cdTenure = tenure,
                address = address
            )
        }
    }

    fun getActivateSrxCreditSummary(
        srxCreditMainType: Int,
        listings: List<SrxCreditListingActivationPO.ListingActivationPO>
    ): Call<ActivateSrxCreditListingsResponse> {
        return srxDataSource.getActivateSrxCreditSummary(
            SrxCreditListingActivationPO(
                srxCreditMainType,
                listings
            )
        )
    }

    @Throws(IllegalArgumentException::class)
    fun getSponsoredActivateSrxCreditSummary(
        listingId: Int,
        sponsorDuration: CommunityEnum.SponsorDuration,
        targetType: CommunityPostEnum.Target,
        communityIds: List<Int>? = null,
        hyperTargetTemplatePO: CommunityHyperTargetTemplatePO? = null
    ): Call<ActivateSrxCreditListingsResponse> {
        val listings =
            listOf(
                SrxCreditListingActivationPO.ListingActivationPO(
                    id = listingId,
                    value = null,
                    sponsored = ListingActivationSponsored(
                        numberOfWeeks = sponsorDuration.value,
                        targetType = targetType.value,
                        communityIds = communityIds?.joinToString(","),
                        hyperTargetTemplate = hyperTargetTemplatePO
                    )
                )
            )
        return getActivateSrxCreditSummary(
            ListingManagementEnum.SrxCreditMainType.SPONSORED_POST.value,
            listings
        )
    }

    fun activateSrxCreditForListings(
        srxCreditMainType: Int,
        listings: List<SrxCreditListingActivationPO.ListingActivationPO>
    ): Call<ActivateSrxCreditListingsResponse> {
        return srxDataSource.activateSrxCreditForListings(
            SrxCreditListingActivationPO(
                srxCreditMainType,
                listings
            )
        )
    }

    fun activateSrxCreditForSponsoredListings(
        listingId: Int,
        title: String,
        remarks: String,
        targetType: CommunityPostEnum.Target,
        communityIds: List<Int>? = null,
        hyperTargetTemplatePO: CommunityHyperTargetTemplatePO? = null
    ): Call<ActivateSrxCreditListingsResponse> {
        when (targetType) {
            CommunityPostEnum.Target.COMMUNITY -> {
                if (communityIds?.isNotEmpty() != true) {
                    throw IllegalArgumentException("communityIds should not be empty when target type is `COMMUNITY`")
                }
            }
            CommunityPostEnum.Target.HYPER -> {
                if (hyperTargetTemplatePO == null) {
                    throw IllegalArgumentException("hyperTargetTemplatePO should not be null when target type is `HYPER`")
                }
            }
        }

        val listings =
            listOf(
                SrxCreditListingActivationPO.ListingActivationPO(
                    id = listingId,
                    value = null,
                    sponsored = ListingActivationSponsored(
                        title = title,
                        remarks = remarks,
                        targetType = targetType.value,
                        communityIds = communityIds?.joinToString(","),
                        hyperTargetTemplate = hyperTargetTemplatePO
                    )
                )
            )
        return srxDataSource.activateSrxCreditForListings(
            SrxCreditListingActivationPO(
                ListingManagementEnum.SrxCreditMainType.SPONSORED_POST.value,
                listings
            )
        )
    }

    fun repostListings(listingIds: List<String>): Call<DefaultResultResponse> {
        return srxDataSource.repostListings(listingIds)
    }

    fun takeOffListings(listingIds: List<String>): Call<DefaultResultResponse> {
        return srxDataSource.takeOffListings(listingIds)
    }

    fun deleteListings(listingIds: List<String>): Call<DefaultResultResponse> {
        return srxDataSource.deleteListings(listingIds)
    }

    fun getAddonPrompts(limit: Int? = null): Call<GetAddonPromptResponse> {
        return srxDataSource.getAddonPrompts(limit)
    }

    fun copyListing(listingEditPO: ListingEditPO): Call<CreateUpdateListingResponse> {
        return srxDataSource.copyListing(listingEditPO)
    }

    fun vetPhoto(photoUrl: String, photoId: Int): Call<VetPhotoResponsePO> {
        return srxDataSource.vetPhoto(VetPhotoRequest(url = photoUrl, mclppid = photoId))
    }

    fun appealPhoto(photoId: Int): Call<DefaultResultResponse> {
        return srxDataSource.appealPhoto(appealPhotoRequest = AppealPhotoRequest(mclppid = photoId))
    }

    fun getOwnerCertificationNotificationTemplate(): Call<GetOwnerCertificationNotificationTemplateResponse> {
        return srxDataSource.getOwnerCertificationNotificationTemplate()
    }

    fun sendOwnerCertificationNotifications(mobiles: List<String>): Call<DefaultResultResponse> {
        return srxDataSource.sendOwnerCertificationNotifications(mobiles)
    }

    fun getCommunityHyperTargets(
        startIndex: Int,
        maxResult: Int
    ): Call<GetCommunityHyperTargetsResponse> {
        return srxDataSource.getCommunityHyperTargets(startIndex, maxResult)
    }
}