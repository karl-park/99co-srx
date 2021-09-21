package sg.searchhouse.agentconnect.viewmodel.fragment.listing.create

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.data.repository.LookupRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ListingManagementPurpose
import sg.searchhouse.agentconnect.model.api.ApiError
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateUpdateListingResponse
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateListingTrackerPO
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import java.util.*
import javax.inject.Inject

class CreateUpdateListingViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var lookUpRepository: LookupRepository

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    //ARGUMENT PARAMS
    val purpose = MutableLiveData<ListingManagementPurpose>()
    val property = MutableLiveData<PropertyPO>()
    val ownershipType = MutableLiveData<ListingEnum.OwnershipType>()

    //VARIABLES
    val listingEditPO = MutableLiveData<ListingEditPO>().apply { value = ListingEditPO() }
    val updateListingEditPO = MutableLiveData<ListingEditPO>()

    val models = MutableLiveData<HashMap<Int, ArrayList<String>>>()

    val showLandedArea = MutableLiveData<Boolean>()
    val hintLandArea = MutableLiveData<String>()
    val hintBuildArea = MutableLiveData<String>()

    val builtSizeType: MutableLiveData<String> = MutableLiveData()

    val actionBtnState = MutableLiveData<ButtonState>()
    val actionBtnLabel: LiveData<String> = Transformations.map(actionBtnState) {
        when (it) {
            ButtonState.SUBMITTING -> {
                return@map if (purpose.value == ListingManagementPurpose.CREATE) {
                    applicationContext.getString(R.string.action_saving)
                } else {
                    applicationContext.getString(R.string.action_updating)
                }
            }
            else -> {
                return@map if (purpose.value == ListingManagementPurpose.CREATE) {
                    applicationContext.getString(R.string.action_next)
                } else {
                    applicationContext.getString(R.string.action_update)
                }
            }
        }
    }

    private var originalOwnershipType: ListingEnum.OwnershipType? = null

    //API Response data
    val createUpdateListingStatus = MutableLiveData<ApiStatus<CreateUpdateListingResponse>>()
    val apiError = MutableLiveData<ApiError>()

    //ERROR GROUP
    val isClassificationValidated = MutableLiveData<Boolean>().apply { value = true }
    val isPropertyTypeValidated = MutableLiveData<Boolean>().apply { value = true }
    val isModelValidated = MutableLiveData<Boolean>().apply { value = true }
    val isLandAreaValidated = MutableLiveData<Boolean>().apply { value = true }
    val isBuiltAreaValidated = MutableLiveData<Boolean>().apply { value = true }

    private val _createListingTracker = MutableLiveData<CreateListingTrackerPO>()
    val createListingTracker: LiveData<CreateListingTrackerPO> get() = _createListingTracker

    init {
        viewModelComponent.inject(this)
        setupInitialData()
    }

    fun setCreateListingTracker(createListingTrackerPO: CreateListingTrackerPO) {
        _createListingTracker.value = createListingTrackerPO
    }

    private fun setupInitialData() {
        //initial states
        actionBtnState.value = ButtonState.NORMAL

        builtSizeType.value = applicationContext.getString(R.string.label_size_sqm)

        showLandedArea.value = false

        hintLandArea.value = applicationContext.resources.getString(R.string.hint_land_size)
        hintBuildArea.value = applicationContext.resources.getString(
            R.string.hint_built_size,
            "(${applicationContext.resources.getString(R.string.label_size_sqm)})"
        )
    }

    fun getModels() {
        ApiUtil.performRequest(
            applicationContext,
            lookUpRepository.lookupModels(),
            onSuccess = { models.postValue(it.models) },
            onFail = { apiError.postValue(it) },
            onError = { Log.e("Models", "Error in getting models") }
        )
    }

    fun getPropertyType() {
        val editPO =
            listingEditPO.value ?: return ErrorUtil.handleError("Undefined listing edit po")
        ApiUtil.performRequest(
            applicationContext,
            locationSearchRepository.getPropertyType(editPO.postalCode, editPO.blk),
            onSuccess = { response ->
                editPO.category = response.propertyType
                editPO.propertyType = response.propertySubType
                listingEditPO.postValue(editPO)
                getPropertyData()
            },
            onFail = { apiError.postValue(it) },
            onError = { Log.e("PropertyType", "Error in getting property type") }
        )
    }

    private fun getPropertyData() {
        val editPO =
            listingEditPO.value ?: return ErrorUtil.handleError("Undefined listing edit po")
        if (editPO.propertyType > 0) {
            ApiUtil.performRequest(
                applicationContext,
                locationSearchRepository.getPropertyData(
                    editPO.postalCode,
                    editPO.blk,
                    editPO.propertyType
                ),
                onSuccess = { response ->
                    //TENURE
                    editPO.tenure = response.tenure.toString()

                    //PROPERTY DATE
                    editPO.propertyCompleteDate =
                        DateTimeUtil.getUnixTimeStamp("${response.builtYear}-01-01")

                    //MODEL
                    val modelList = PropertyTypeUtil.getModelsByPropertyType(
                        editPO.propertyType,
                        models.value ?: hashMapOf()
                    )
                    if (modelList.isNotEmpty()) {
                        editPO.model = modelList[0]
                        isModelValidated.postValue(true)
                    }

                    //SIZE
                    response.size?.run {
                        if (PropertyTypeUtil.isLanded(editPO.propertyType)) {
                            editPO.landArea = this.toDouble()
                        } else {
                            editPO.builtArea = this.toDouble()
                        }
                    }

                    showHideLandArea(editPO.propertyType, response.sizeType)
                    listingEditPO.postValue(editPO)
                },
                onFail = { apiError.postValue(it) },
                onError = { Log.e("PropertyData", "Error in calling property data") }
            )
        } else {
            //Set HDB as default category -> hide land area and show built area for hdb
            if (editPO.category == ListingManagementEnum.PropertyClassification.HDB.value) {
                showLandedArea.postValue(false)
                hintBuildArea.postValue(
                    applicationContext.getString(
                        R.string.hint_built_size,
                        "(${applicationContext.getString(R.string.label_size_sqm)})"
                    )
                )
                builtSizeType.postValue(applicationContext.getString(R.string.label_size_sqm))
            }
        }

    }

    fun getPropertySize() {
        val editPO = listingEditPO.value ?: return
        ApiUtil.performRequest(
            applicationContext,
            locationSearchRepository.getPropertySize(
                editPO.postalCode,
                editPO.blk,
                editPO.propertyType,
                editPO.unitFloor,
                editPO.unitNo
            ),
            onSuccess = { response ->
                val size = response.size ?: -1
                if (size > 0) {
                    if (PropertyTypeUtil.isLanded(editPO.propertyType)) {
                        editPO.landArea = size.toDouble()
                    } else {
                        editPO.builtArea = size.toDouble()
                    }
                }
                listingEditPO.postValue(editPO)
            },
            onFail = { apiError.postValue(it) },
            onError = { Log.e("GetSize", "Errors in get property size") }
        )
    }

    //CREATE UPDATE LISTING
    fun onCreateOrUpdateListing() {
        if (isAllValidated()) {
            actionBtnState.value = ButtonState.SUBMITTING
            when (purpose.value) {
                ListingManagementPurpose.CREATE -> onCreateListing()
                ListingManagementPurpose.UPDATE -> onUpdateListing()
                else -> {
                    //DO nothing
                }
            }
        }
    }

    private fun onCreateListing() {
        val listingEditPO = listingEditPO.value ?: return
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.createListing(listingEditPO),
            onSuccess = { response ->
                actionBtnState.postValue(ButtonState.SUBMITTED)
                createUpdateListingStatus.postValue(ApiStatus.successInstance(response))
            }, onFail = { apiError ->
                actionBtnState.postValue(ButtonState.ERROR)
                createUpdateListingStatus.postValue(ApiStatus.failInstance(apiError))
            }, onError = {
                actionBtnState.postValue(ButtonState.ERROR)
                createUpdateListingStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    private fun isOwnershipTypeUpdated(): Boolean {
        return listingEditPO.value?.getOwnershipType()?.value != originalOwnershipType?.value
    }

    private fun onUpdateListing() {
        val listingEditPO = listingEditPO.value ?: return

        // If ownership type updated from SALE to RENT or vice versa,
        // reset `listedPrice` value to `RESET_LISTED_PRICE` (backend defined reset value)
        if (isOwnershipTypeUpdated()) {
            listingEditPO.listedPrice = RESET_LISTED_PRICE
        }

        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.updateListing(listingEditPO),
            onSuccess = { response ->
                actionBtnState.postValue(ButtonState.SUBMITTED)
                createUpdateListingStatus.postValue(ApiStatus.successInstance(response))
            }, onFail = { apiError ->
                actionBtnState.postValue(ButtonState.ERROR)
                createUpdateListingStatus.postValue(ApiStatus.failInstance(apiError))
            }, onError = {
                actionBtnState.postValue(ButtonState.ERROR)
                createUpdateListingStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    //Validation
    private fun isAllValidated(): Boolean {
        var isAllValidate = true
        val editPO = listingEditPO.value ?: return false

        if (editPO.category > 0) {
            isClassificationValidated.value = true
        } else {
            isAllValidate = false
            isClassificationValidated.value = false
        }

        if (editPO.propertyType > 0) {
            isPropertyTypeValidated.value = true
        } else {
            isAllValidate = false
            isPropertyTypeValidated.value = false
        }

        if (editPO.model.isNotEmpty()) {
            isModelValidated.value = true
        } else {
            isAllValidate = false
            isModelValidated.value = false
        }

        return isAllValidate
    }

    private fun showHideLandArea(propertySubType: Int, sizeType: String? = null) {
        when {
            PropertyTypeUtil.isLanded(propertySubType) -> {
                //LANDED
                showLandedArea.postValue(true)
                hintLandArea.postValue(applicationContext.getString(R.string.hint_land_size))
                hintBuildArea.postValue(
                    applicationContext.getString(
                        R.string.hint_built_size,
                        "(${applicationContext.getString(R.string.label_size_sqft)})"
                    )
                )
                builtSizeType.postValue("")
            }
            PropertyTypeUtil.isHDB(propertySubType) -> {
                //HDB
                showLandedArea.postValue(false)
                var builtArea = applicationContext.getString(
                    R.string.hint_built_size,
                    "(${applicationContext.getString(R.string.label_size_sqm)})"
                )
                if (sizeType != null) {
                    builtArea =
                        applicationContext.getString(R.string.hint_built_size, "($sizeType)")
                }
                hintBuildArea.postValue(builtArea)
                builtSizeType.postValue(applicationContext.getString(R.string.label_size_sqm))
            }
            else -> {
                //CONDO and COMMERCIAL
                showLandedArea.postValue(false)
                var builtArea = applicationContext.getString(
                    R.string.hint_built_size,
                    "(${applicationContext.getString(R.string.label_size_sqft)})"
                )
                if (sizeType != null) {
                    builtArea =
                        applicationContext.getString(R.string.hint_built_size, "($sizeType)")
                }
                hintBuildArea.postValue(builtArea)
                builtSizeType.postValue(applicationContext.getString(R.string.label_size_sqft))
            }
        }
    }

    //Note -> update property classification and change other values
    fun updatePropertyClassification(classificationValue: Int) {
        val editPO = listingEditPO.value ?: return
        editPO.category = classificationValue
        editPO.propertyType = 0
        editPO.model = ""
        editPO.tenure = ""
        editPO.propertyCompleteDate = 0
        editPO.unitFloor = ""
        editPO.unitNo = ""
        editPO.builtArea = 0.0
        editPO.landArea = 0.0
        editPO.developer = ""
        listingEditPO.value = editPO
        isClassificationValidated.value = classificationValue > 0
    }

    fun updatePropertyType(propertySubType: Int) {
        val editPO = listingEditPO.value ?: return
        editPO.propertyType = propertySubType
        editPO.model = ""
        editPO.tenure = ""
        editPO.propertyCompleteDate = 0
        editPO.unitFloor = ""
        editPO.unitNo = ""
        editPO.builtArea = 0.0
        editPO.landArea = 0.0
        editPO.developer = ""
        listingEditPO.value = editPO
        isPropertyTypeValidated.value = propertySubType > 0
        getPropertyData()
    }

    fun setListingEditPO(listingEditPO: ListingEditPO) {
        originalOwnershipType = listingEditPO.getOwnershipType()
        this.listingEditPO.postValue(listingEditPO)
        updateListingEditPO.postValue(listingEditPO)
        showHideLandArea(listingEditPO.propertyType)
    }

    companion object {
        private const val RESET_LISTED_PRICE = 999_999_999
    }
}