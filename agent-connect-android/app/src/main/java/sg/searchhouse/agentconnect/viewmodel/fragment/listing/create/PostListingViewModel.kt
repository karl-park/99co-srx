package sg.searchhouse.agentconnect.viewmodel.fragment.listing.create

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.data.repository.LookupRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ListingPhotoType
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.QualityListingPhotoStatus
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.googleapi.GetYoutubeVideoInfoResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO
import sg.searchhouse.agentconnect.model.api.listing.ListingVideoPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.*
import sg.searchhouse.agentconnect.model.api.lookup.LookupBedroomsResponse
import sg.searchhouse.agentconnect.model.api.lookup.LookupListingFeaturesFixturesAreasResponse
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.io.File
import javax.inject.Inject

class PostListingViewModel constructor(application: Application) :
    ApiBaseViewModel<GetListingForListingManagementResponse>(application) {

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    @Inject
    lateinit var lookupRepository: LookupRepository

    @Inject
    lateinit var applicationContext: Context

    var listingPhotoType: ListingPhotoType? = null

    val listingId = MutableLiveData<String>()
    val isNewListing = MutableLiveData<Boolean>()
    val initialListingEditPO: LiveData<ListingEditPO> =
        Transformations.map(mainResponse) { it?.listingEditPO }
    val listingEditPO = MutableLiveData<ListingEditPO>()
    val listingGroup = MutableLiveData<ListingManagementEnum.ListingGroup>()
    val bedrooms = MutableLiveData<List<LookupBedroomsResponse.Bedroom>>()
    val postListingCredits = MutableLiveData<GetPostingCreditsResponse>()

    val appealPhoto = MutableLiveData<PostListingPhotoPO>()

    val isUpdateSpecificInfoCard = MutableLiveData<Boolean>()
    val isUpdateAdditionalInfoCard = MutableLiveData<Boolean>()

    val removedPhoto = MutableLiveData<PostListingPhotoPO>()

    //images part
    val imageFiles = MutableLiveData<List<File>>()

    //button state variables
    val btnState = MutableLiveData<ButtonState>()
    val actionBtnLabel = MediatorLiveData<String>()
    val isBtnEnabled: LiveData<Boolean> =
        Transformations.map(btnState) { response ->
            return@map when (response) {
                ButtonState.SUBMITTING -> false
                else -> true
            }
        }

    //API Response Data
    val bedroomsResponse = MutableLiveData<ApiStatus<LookupBedroomsResponse>>()
    val featuresFixturesResponse =
        MutableLiveData<ApiStatus<LookupListingFeaturesFixturesAreasResponse>>()
    val getPostingCreditsResponse = MutableLiveData<ApiStatus<GetPostingCreditsResponse>>()
    val updateListingResponse = MutableLiveData<ApiStatus<CreateUpdateListingResponse>>()
    val updateRepostListingResponse = MutableLiveData<ApiStatus<CreateUpdateListingResponse>>()
    val checkYouTubeUrlResponse = MutableLiveData<GetYoutubeVideoInfoResponse>()
    val uploadNewPhotoResponse = MutableLiveData<ApiStatus<UploadPhotoResponse>>()
    val vetPhotoResponse = MutableLiveData<ApiStatus<VetPhotoResponsePO>>()
    val appealPhotoResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val updateListingAfterUploadedPhoto =
        MutableLiveData<ApiStatus<CreateUpdateListingResponse>>()
    val removePhotoResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val setCoverPhotoResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val movePhotoResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val reloadListingPhotosResponse =
        MutableLiveData<Pair<ApiStatus<GetListingForListingManagementResponse>, Boolean>>()
    val postListingResponse = MutableLiveData<ApiStatus<PostListingResponse>>()
    val apiCallResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()

    init {
        viewModelComponent.inject(this)
        setupBtnStateData()
    }

    private fun getRefreshListingLabel(): String {
        return applicationContext.getString(
            when (SessionUtil.getSubscriptionConfig().srx99CombinedSub) {
                true -> R.string.action_refresh_listing
                false -> R.string.action_repost_listing
            }
        )
    }

    private fun getRefreshingListingLabel(): String {
        return applicationContext.getString(
            when (SessionUtil.getSubscriptionConfig().srx99CombinedSub) {
                true -> R.string.action_refreshing_listing
                false -> R.string.action_reposting_listing
            }
        )
    }

    private fun setupBtnStateData() {
        btnState.value = ButtonState.NORMAL
        actionBtnLabel.addSource(btnState) { buttonState ->
            when (buttonState) {
                ButtonState.SUBMITTING -> {
                    if (listingGroup.value == ListingManagementEnum.ListingGroup.ACTIVE) {
                        actionBtnLabel.postValue(getRefreshingListingLabel())
                    } else {
                        actionBtnLabel.postValue(applicationContext.getString(R.string.action_posting_listing))
                    }
                }
                else -> {
                    if (listingGroup.value == ListingManagementEnum.ListingGroup.ACTIVE) {
                        actionBtnLabel.postValue(getRefreshListingLabel())
                    } else {
                        actionBtnLabel.postValue(applicationContext.getString(R.string.action_post_listing))
                    }
                }
            }
        }
        actionBtnLabel.addSource(listingGroup) { listingGroup ->
            when (listingGroup) {
                ListingManagementEnum.ListingGroup.ACTIVE -> {
                    if (btnState.value == ButtonState.SUBMITTING) {
                        actionBtnLabel.postValue(getRefreshingListingLabel())
                    } else {
                        actionBtnLabel.postValue(getRefreshListingLabel())
                    }
                }
                else -> {
                    if (btnState.value == ButtonState.SUBMITTING) {
                        actionBtnLabel.postValue(applicationContext.getString(R.string.action_posting_listing))
                    } else {
                        actionBtnLabel.postValue(applicationContext.getString(R.string.action_post_listing))
                    }
                }
            }
        }
    }

    //get listing id
    fun getListing(listingId: String) {
        performRequest(listingManagementRepository.getListingForListingManagement(listingId = listingId))
    }

    //get posting credits
    fun getPostingCredits(listingId: String) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.getPostingCredits(listingId = listingId),
            onSuccess = { response ->
                getPostingCreditsResponse.postValue(ApiStatus.successInstance(response))
            }, onFail = { apiError ->
                getPostingCreditsResponse.postValue(ApiStatus.failInstance(apiError))
            }, onError = {
                getPostingCreditsResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    //Bedrooms
    fun getBedrooms() {
        ApiUtil.performRequest(
            applicationContext,
            lookupRepository.lookupBedrooms(),
            onSuccess = { response ->
                bedroomsResponse.postValue(ApiStatus.successInstance(response))
            }, onFail = { apiError ->
                bedroomsResponse.postValue(ApiStatus.failInstance(apiError))
            }, onError = {
                bedroomsResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    //Get fixtures and features
    fun getFixturesAndFeatures(category: Int) {
        ApiUtil.performRequest(
            applicationContext,
            lookupRepository.lookupListingFeaturesFixturesAreas(category),
            onSuccess = { response ->
                featuresFixturesResponse.postValue(ApiStatus.successInstance(response))
            }, onFail = { apiError ->
                featuresFixturesResponse.postValue(ApiStatus.failInstance(apiError))
            }, onError = {
                featuresFixturesResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    //Update Listing Edit Po
    fun updateListingEditPO(isRepostListing: Boolean = false) {
        val editPO = listingEditPO.value ?: return ErrorUtil.handleError("Missing listing edit")
        updateListingResponse.postValue(ApiStatus.loadingInstance(existingApiStatus = updateListingResponse.value))
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.updateListing(editPO),
            onSuccess = { response ->
                if (updateListingResponse.value?.isRequestRepeated() == true) {
                    return@performRequest
                }
                if (isRepostListing) {
                    //after updating, then call repost listing
                    val listingId = listingEditPO.value?.id ?: return@performRequest
                    repostListing(listingId.toString())
                } else {
                    updateListingResponse.postValue(ApiStatus.successInstance(response))
                }
            }, onFail = { apiError ->
                if (updateListingResponse.value?.isRequestRepeated() == true) {
                    return@performRequest
                }
                if (isRepostListing) {
                    btnState.postValue(ButtonState.NORMAL)
                    updateRepostListingResponse.postValue(ApiStatus.failInstance(apiError))
                } else {
                    updateListingResponse.postValue(ApiStatus.failInstance(apiError))
                }
            }, onError = {
                if (updateListingResponse.value?.isRequestRepeated() == true) {
                    return@performRequest
                }
                if (isRepostListing) {
                    btnState.postValue(ButtonState.NORMAL)
                    updateRepostListingResponse.postValue(ApiStatus.errorInstance())
                } else {
                    updateListingResponse.postValue(ApiStatus.errorInstance())
                }
            }
        )
    }

    fun takeOffListing(listingId: String) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.takeOffListings(listOf(listingId)),
            onSuccess = {
                apiCallResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                apiCallResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                apiCallResponse.postValue(ApiStatus.errorInstance())
            })
    }

    fun deleteListing(listingId: String) {
        ApiUtil.performRequest(applicationContext,
            listingManagementRepository.deleteListings(listOf(listingId)),
            onSuccess = {
                apiCallResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                apiCallResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                apiCallResponse.postValue(ApiStatus.errorInstance())
            })
    }

    fun postListing() {
        val editPO = listingEditPO.value ?: return ErrorUtil.handleError("Undefine listing edit po")
        btnState.postValue(ButtonState.SUBMITTING)
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.postListing(editPO),
            onSuccess = { response ->
                btnState.postValue(ButtonState.SUBMITTED)
                postListingResponse.postValue(ApiStatus.successInstance(response))
            }, onFail = { apiError ->
                btnState.postValue(ButtonState.NORMAL)
                postListingResponse.postValue(ApiStatus.failInstance(apiError))
            }, onError = {
                btnState.postValue(ButtonState.NORMAL)
                postListingResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun checkYoutubeVideoLink(videoUrl: String) {
        val videoId = StringUtil.getYoutubeUrlId(videoUrl)
        if (!TextUtils.isEmpty(videoId)) {
            ApiUtil.performExternalRequest(
                applicationContext,
                listingManagementRepository.getYoutubeStatusById(videoId),
                onSuccess = {
                    if (it.items.isNotEmpty() && it.error == null) {
                        val editPO = listingEditPO.value ?: return@performExternalRequest
                        editPO.videos.clear()
                        editPO.videos.add(ListingVideoPO(url = videoUrl))
                        updateListingEditPO()
                    }
                    checkYouTubeUrlResponse.postValue(it)
                }, onFail = { apiError ->
                    checkYouTubeUrlResponse.postValue(apiError)
                }, onError = {
                    ErrorUtil.handleError(applicationContext.getString(R.string.msg_invalid_youtube_link))
                }, errorClass = GetYoutubeVideoInfoResponse::class.java
            )
        } else {
            ErrorUtil.handleError(applicationContext.getString(R.string.msg_invalid_youtube_link))
        }
    }

    private fun getImagePositionByType(photoType: ListingPhotoType): Int {
        return when (photoType) {
            ListingPhotoType.LISTING_IMAGE -> {
                val listingPhotoSize = listingEditPO.value?.photosSubmitted?.size ?: 0
                listingPhotoSize + 1
            }
            ListingPhotoType.FLOOR_PLAN -> {
                val floorPlanSize = listingEditPO.value?.floorPlanPhotosSubmitted?.size ?: 0
                floorPlanSize + 1
            }
            else -> 0
        }
    }

    fun uploadNewPhoto(file: File, photoType: ListingPhotoType) {
        val listingId =
            listingEditPO.value?.id ?: return ErrorUtil.handleError("Undefined listing id")
        val position = getImagePositionByType(photoType)
        //Loading
        uploadNewPhotoResponse.postValue(ApiStatus.loadingInstance())

        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.uploadNewPhoto(
                listingId = listingId,
                position = position,
                isFloorPlan = photoType == ListingPhotoType.FLOOR_PLAN,
                isCover = position == 1,
                file = file
            ),
            onSuccess = {
                val editPO = listingEditPO.value ?: return@performRequest
                if (photoType == ListingPhotoType.LISTING_IMAGE) {
                    editPO.photosSubmitted.add(it.photo)
                    //Note: call update listing edit po to update the quality bar
                    if (editPO.photosSubmitted.size < 5) {
                        updateUploadedPhoto(editPO)
                    }
                    vetPhoto(uploadPhotoResponse = it, photoType = photoType)
                } else if (photoType == ListingPhotoType.FLOOR_PLAN) {
                    editPO.floorPlanPhotosSubmitted.add(it.photo)
                    uploadNewPhotoResponse.postValue(ApiStatus.successInstance(it))
                }
            },
            onFail = {
                uploadNewPhotoResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                uploadNewPhotoResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun arrangeNonQualityPhotos(uploadedNewPhoto: PostListingPhotoPO) {
        val editPO = listingEditPO.value ?: return
        if (uploadedNewPhoto.getQualityListingPhotoStatus() == QualityListingPhotoStatus.PASS && listingPhotoType == ListingPhotoType.LISTING_IMAGE) {
            val nonQualityListingPhotos =
                editPO.photosSubmitted.filter { it.getQualityListingPhotoStatus() == QualityListingPhotoStatus.FAIL }
            val qualifiedListingPhotosCount =
                editPO.photosSubmitted.filter { it.getQualityListingPhotoStatus() == QualityListingPhotoStatus.PASS }.size

            if (nonQualityListingPhotos.isNotEmpty()) {
                uploadedNewPhoto.position = qualifiedListingPhotosCount //no need to increase
                movePhoto(uploadedNewPhoto, qualifiedListingPhotosCount)
            }

        } //end
    }

    private fun vetPhoto(uploadPhotoResponse: UploadPhotoResponse, photoType: ListingPhotoType) {
        val photoUrl = uploadPhotoResponse.photo.url
        val photoId = uploadPhotoResponse.photo.id.toIntOrNull() ?: return
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.vetPhoto(photoUrl, photoId),
            onSuccess = { vetPhotoPO ->
                //update in listing edit po
                if (photoType == ListingPhotoType.LISTING_IMAGE) {
                    uploadPhotoResponse.photo.vetPhoto = vetPhotoPO
                    uploadNewPhotoResponse.postValue(
                        ApiStatus.successInstance(
                            uploadPhotoResponse
                        )
                    )
                } else {
                    vetPhotoResponse.postValue(ApiStatus.successInstance(vetPhotoPO))
                }
            },
            onFail = { vetPhotoResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { vetPhotoResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun appealPhoto(postListingPhotoPO: PostListingPhotoPO) {
        val photoId = postListingPhotoPO.id.toIntOrNull() ?: return
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.appealPhoto(photoId),
            onSuccess = {
                postListingPhotoPO.appealStatus =
                    ListingManagementEnum.QualityListingAppealStatus.SUBMITTED.value
                appealPhoto.postValue(postListingPhotoPO)
            },
            onFail = { appealPhotoResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { appealPhotoResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    private fun updateUploadedPhoto(editPO: ListingEditPO) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.updateListing(editPO),
            onSuccess = {
                listingEditPO.postValue(it.listingEditPO)
            },
            onFail = { updateListingAfterUploadedPhoto.postValue(ApiStatus.failInstance(it)) },
            onError = { updateListingAfterUploadedPhoto.postValue(ApiStatus.errorInstance()) }
        )
    }

    // Objective: Update quality bar when toggle facility on/off check box
    // TODO Maybe refactor
    fun performUpdateListing(editPO: ListingEditPO) {
        updateListingResponse.postValue(ApiStatus.loadingInstance())
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.updateListing(editPO),
            onSuccess = {
                updateListingResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                updateListingResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                updateListingResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun deletePhotos(
        photo: PostListingPhotoPO,
        type: ListingPhotoType
    ) {
        listingPhotoType = type
        when (type) {
            ListingPhotoType.LISTING_IMAGE -> {
                removeSubmittedListingPhotos(photo)
            }
            ListingPhotoType.FLOOR_PLAN -> {
                deleteFloorPlanPhotos(photo)
            }
            else -> {
                //do nothing
            }
        }
    }

    private fun removeSubmittedListingPhotos(photo: PostListingPhotoPO) {
        val listingId =
            listingEditPO.value?.id ?: return ErrorUtil.handleError("Missing listing id")
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.deleteSubmittedPhotos(listingId, listOf(photo)),
            onSuccess = { response ->
                removedPhoto.postValue(photo)
                val editPO = listingEditPO.value ?: return@performRequest
                if (photo.coverPhoto && editPO.photosSubmitted.size > 1) {
                    val nextPhotoToSetCover = editPO.photosSubmitted[1]
                    setCoverPhoto(nextPhotoToSetCover)
                } else {
                    removePhotoResponse.postValue(ApiStatus.successInstance(response))
                    reLoadListingPhotos(isUpdateMediaCard = true)
                }
            }, onFail = { apiError ->
                removePhotoResponse.postValue(ApiStatus.failInstance(apiError))
            }, onError = {
                removePhotoResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    private fun deleteFloorPlanPhotos(photo: PostListingPhotoPO) {
        val listingId =
            listingEditPO.value?.id ?: return ErrorUtil.handleError("Missing listing id")
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.deleteFloorPlanPhotos(listingId, listOf(photo)),
            onSuccess = { response ->
                removedPhoto.postValue(photo)
                removePhotoResponse.postValue(ApiStatus.successInstance(response))
            }, onFail = { apiError ->
                removePhotoResponse.postValue(ApiStatus.failInstance(apiError))
            }, onError = {
                removePhotoResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun movePhoto(photo: PostListingPhotoPO, imagePosition: Int) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.movePhoto(photo),
            onSuccess = {
                //only listing images are allowed to move
                listingPhotoType = ListingPhotoType.LISTING_IMAGE
                if (imagePosition == 1) {
                    setCoverPhoto(photo)
                } else {
                    reLoadListingPhotos(isUpdateMediaCard = true)
                }
            }, onFail = {
                movePhotoResponse.postValue(ApiStatus.failInstance(it))
            }, onError = {
                movePhotoResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun setCoverPhoto(photo: PostListingPhotoPO) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.setCoverPhoto(photo),
            onSuccess = {
                //cover photo is only for listing image
                listingPhotoType = ListingPhotoType.LISTING_IMAGE
                reLoadListingPhotos(isUpdateMediaCard = true)
            }, onFail = {
                setCoverPhotoResponse.postValue(ApiStatus.failInstance(it))
            }, onError = {
                setCoverPhotoResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    private fun reLoadListingPhotos(isUpdateMediaCard: Boolean) {
        val listingId =
            listingEditPO.value?.id ?: return ErrorUtil.handleError("Missing listing id")
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.getListingForListingManagement(listingId.toString()),
            onSuccess = {
                reloadListingPhotosResponse.postValue(
                    Pair(
                        ApiStatus.successInstance(it),
                        isUpdateMediaCard
                    )
                )
            },
            onFail = {
                reloadListingPhotosResponse.postValue(Pair(ApiStatus.failInstance(it), false))
            },
            onError = {
                reloadListingPhotosResponse.postValue(Pair(ApiStatus.errorInstance(), false))
            }
        )
    }

    private fun repostListing(listingId: String) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.repostListings(listOf(listingId)),
            onSuccess = {
                btnState.postValue(ButtonState.SUBMITTED)
                apiCallResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                btnState.postValue(ButtonState.NORMAL)
                apiCallResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                btnState.postValue(ButtonState.NORMAL)
                apiCallResponse.postValue(ApiStatus.errorInstance())
            })
    }

    override fun shouldResponseBeOccupied(response: GetListingForListingManagementResponse): Boolean {
        return true
    }

    // Additional Info Card
    fun isFurnishLevelSelected(furnishLevel: ListingManagementEnum.FurnishLevel): Boolean {
        listingEditPO.value?.let { return furnishLevel.value == it.furnishLevel }
        return false
    }

    // Additional Info Card
    fun isOwnerTypeSelected(ownerType: ListingManagementEnum.OwnerType): Boolean {
        listingEditPO.value?.let { return it.ownerType == ownerType.value }
        return false
    }

    // Additional Info Card
    fun isFloorSelected(floor: ListingEnum.Floor): Boolean {
        listingEditPO.value?.let {
            return StringUtil.equals(it.floor?.trim(), floor.value.trim(), ignoreCase = true)
        }
        return false
    }

    // Additional Info Card
    fun isFurnishSelected(furnish: ListingEnum.Furnish): Boolean {
        listingEditPO.value?.let { return furnish.value == it.furnish }
        return false
    }

    //Specific Info Card
    fun isPriceOptionSelected(listedPriceModel: String): Boolean {
        listingEditPO.value?.let {
            return it.listedPriceModel.equals(listedPriceModel, ignoreCase = true)
        }
        return false
    }

    //Specific Info Card
    fun isRoomTypeSelected(roomType: ListingManagementEnum.RoomType): Boolean {
        listingEditPO.value?.let { return roomType.value == it.roomType }
        return false
    }

    //Specific Info Card
    fun isLeaseTermSelected(leaseTerm: ListingManagementEnum.LeaseTerm): Boolean {
        listingEditPO.value?.let { return it.leaseTerm == leaseTerm.value }
        return false
    }

    //Specific Info Card
    fun isRentalTypeSelected(rentalType: ListingManagementEnum.RentalType): Boolean {
        listingEditPO.value?.let { editPO -> return editPO.roomRental == rentalType.value }
        return false
    }

    //Specific Info Card
    fun isTenancySelected(tenancyStatus: String): Boolean {
        listingEditPO.value?.let { return it.tenanted == tenancyStatus }
        return false
    }

    //Specific Info Card
    fun isAssignmentTypeSelected(assignmentType: ListingManagementEnum.AssignmentType): Boolean {
        listingEditPO.value?.let { return it.takeover == assignmentType.value }
        return false
    }

    fun onCheckedChanged(btnView: CompoundButton, isChecked: Boolean) {
        if (btnView.isPressed) {
            val editPO = listingEditPO.value ?: return
            editPO.isAcknowledged = isChecked
            listingEditPO.value = editPO
        }
    }
}