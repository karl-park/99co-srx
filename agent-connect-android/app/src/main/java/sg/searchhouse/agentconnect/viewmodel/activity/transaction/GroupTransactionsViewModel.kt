package sg.searchhouse.agentconnect.viewmodel.activity.transaction

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.TransactionRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.SearchEntryType
import sg.searchhouse.agentconnect.enumeration.app.TransactionsDisplayMode
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSummaryResponse
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.FileUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.io.IOException
import javax.inject.Inject

class GroupTransactionsViewModel constructor(application: Application) :
    ApiBaseViewModel<TransactionSummaryResponse>(application) {
    val entryType = MutableLiveData<SearchEntryType>()
    val districtIds = MutableLiveData<String>()
    val districtNames = MutableLiveData<String>()
    val hdbTownIds = MutableLiveData<String>()
    val hdbTownNames = MutableLiveData<String>()
    val amenityIds = MutableLiveData<String>()
    val amenityNames = MutableLiveData<String>()
    val query = MutableLiveData<String>()
    val ownershipType = MutableLiveData<ListingEnum.OwnershipType>()
    val propertyPurpose = MutableLiveData<ListingEnum.PropertyPurpose>()
    val propertySubTypes = MutableLiveData<List<ListingEnum.PropertySubType>>()
    val displayMode = MutableLiveData<TransactionsDisplayMode>()

    var propertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()

    var externalRadius: TransactionEnum.Radius? = null
    var externalAreaType: TransactionEnum.AreaType? = null
    var externalTenureType: TransactionEnum.TenureType? = null

    var externalMinAge: Int? = null
    var externalMaxAge: Int? = null

    var externalMinSize: Int? = null
    var externalMaxSize: Int? = null

    val isLockScrollView = MutableLiveData<Boolean>()

    val isShowProjects = MutableLiveData<Boolean>()

    // Query label shown at the top "Search" button
    val queryLabel: MediatorLiveData<String> = MediatorLiveData()

    val summary: LiveData<TransactionSummaryResponse.Summary> =
        Transformations.map(mainResponse) {
            it?.summary
        }

    val lastMonthChangeIcon: LiveData<Drawable> =
        Transformations.map(mainResponse) {
            val lastMonthChange = it?.summary?.lastMonthChange ?: 0.0
            when {
                lastMonthChange > 0 -> {
                    applicationContext.getDrawable(R.drawable.ic_triangle_up)
                }
                lastMonthChange < 0 -> {
                    applicationContext.getDrawable(R.drawable.ic_triangle_down)
                }
                else -> null
            }
        }

    val summaryLabel = MediatorLiveData<String>()

    val reportExcelLocalFileName = MutableLiveData<String>()

    val tabPosition = MutableLiveData<Int>().apply { value = 0 }
    val isShowExportButton = MediatorLiveData<Boolean>()

    @Inject
    lateinit var transactionRepository: TransactionRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        displayMode.value =
            TransactionsDisplayMode.LIST
        setupQueryLabel()
        setupSummaryLabel()
        setupIsShowExportButton()
    }

    private fun setupIsShowExportButton() {
        isShowExportButton.addSource(isShowProjects) {
            isShowExportButton.postValue(getIsShowExportButton())
        }
        isShowExportButton.addSource(tabPosition) {
            isShowExportButton.postValue(getIsShowExportButton())
        }
    }

    private fun getIsShowExportButton(): Boolean {
        return isShowProjects.value != true || (tabPosition.value ?: 0) > 0
    }

    private fun setupSummaryLabel() {
        summaryLabel.addSource(queryLabel) { queryLabel ->
            entryType.value?.run { summaryLabel.postValue(getSummaryLabel(queryLabel, this)) }
        }
        summaryLabel.addSource(entryType) { entryType ->
            entryType?.run {
                summaryLabel.postValue(
                    getSummaryLabel(
                        queryLabel.value ?: "",
                        entryType
                    )
                )
            }
        }
        summaryLabel.addSource(propertySubTypes) {
            entryType.value?.run {
                summaryLabel.postValue(
                    getSummaryLabel(
                        queryLabel.value ?: "",
                        this
                    )
                )
            }
        }
        summaryLabel.addSource(propertyMainType) {
            entryType.value?.run {
                summaryLabel.postValue(
                    getSummaryLabel(
                        queryLabel.value ?: "",
                        this
                    )
                )
            }
        }
    }

    private fun getSummaryLabel(
        queryLabel: String,
        entryType: SearchEntryType
    ): String {
        return when (entryType) {
            SearchEntryType.PROPERTY_SUB_TYPE -> {
                // Entry from commercial property button in `SearchActivity`
                val propertySubType = propertySubTypes.value?.getOrNull(0) ?: return ""
                val propertySubTypeLabel = applicationContext.getString(propertySubType.labelPlural)
                applicationContext.getString(
                    R.string.label_group_transaction_summary_title_for,
                    propertySubTypeLabel
                )
            }
            SearchEntryType.AMENITY -> {
                propertyMainType.value?.let { propertyMainType ->
                    val propertyMainTypeLabel =
                        applicationContext.getString(propertyMainType.labelPlural)
                    val suffix = applicationContext.getString(
                        R.string.title_group_transaction_suffix_property_type_amenity,
                        propertyMainTypeLabel,
                        queryLabel
                    )
                    applicationContext.getString(
                        R.string.label_group_transaction_summary_title_for,
                        suffix
                    )
                } ?: run {
                    applicationContext.getString(
                        R.string.label_group_transaction_summary_title_near,
                        queryLabel
                    )
                }
            }
            else -> {
                propertyMainType.value?.let { propertyMainType ->
                    val propertyMainTypeLabel =
                        applicationContext.getString(propertyMainType.labelPlural)
                    val suffix = applicationContext.getString(
                        R.string.title_group_transaction_suffix_property_type_address,
                        propertyMainTypeLabel,
                        queryLabel
                    )
                    applicationContext.getString(
                        R.string.label_group_transaction_summary_title_for,
                        suffix
                    )
                } ?: run {
                    applicationContext.getString(
                        R.string.label_group_transaction_summary_title_near,
                        queryLabel
                    )
                }
            }
        }
    }

    override fun shouldResponseBeOccupied(response: TransactionSummaryResponse): Boolean {
        return response.summary != null
    }

    fun getPropertySubTypes(): String {
        return when {
            propertySubTypes.value != null -> {
                propertySubTypes.value!!.map { it.type }.joinToString(",")
            }
            else -> {
                getDefaultPropertySubTypes().joinToString(",")
            }
        }
    }

    private fun getDefaultPropertySubTypes(): List<Int> {
        return when (propertyPurpose.value) {
            ListingEnum.PropertyPurpose.COMMERCIAL -> {
                ListingEnum.PropertyMainType.COMMERCIAL.propertySubTypes.map { it.type }
            }
            ListingEnum.PropertyPurpose.RESIDENTIAL -> {
                ListingEnum.PropertyMainType.RESIDENTIAL.propertySubTypes.map { it.type }
            }
            else -> throw IllegalArgumentException("Missing property purpose from view model")
        }
    }

    fun performRequest() {
        val ownershipType = ownershipType.value ?: return
        val entryType = entryType.value ?: return
        when (entryType) {
            SearchEntryType.QUERY -> {
                val query = query.value ?: return
                performRequest(
                    transactionRepository.findProjectsBySearchText(
                        query,
                        ownershipType,
                        cdResearchSubtypes = getPropertySubTypes(),
                        radius = externalRadius,
                        typeOfArea = externalAreaType,
                        tenureType = externalTenureType,
                        age = externalMinAge,
                        ageMax = externalMaxAge,
                        built = externalMinSize,
                        builtMax = externalMaxSize
                    )
                )
            }
            SearchEntryType.PROPERTY_MAIN_TYPE -> {
                // NOTE: For now, share the same logic as `SearchEntryType.QUERY`
                val query = query.value ?: return
                performRequest(
                    transactionRepository.findProjectsBySearchText(
                        query,
                        ownershipType,
                        cdResearchSubtypes = getPropertySubTypes(),
                        radius = externalRadius,
                        typeOfArea = externalAreaType,
                        tenureType = externalTenureType,
                        age = externalMinAge,
                        ageMax = externalMaxAge,
                        built = externalMinSize,
                        builtMax = externalMaxSize
                    )
                )
            }
            SearchEntryType.PROPERTY_SUB_TYPE -> {
                val propertySubType = propertySubTypes.value?.getOrNull(0) ?: return
                performRequest(
                    transactionRepository.findProjectsByPropertySubType(
                        propertySubType,
                        ownershipType,
                        radius = externalRadius,
                        typeOfArea = externalAreaType,
                        tenureType = externalTenureType,
                        age = externalMinAge,
                        ageMax = externalMaxAge,
                        built = externalMinSize,
                        builtMax = externalMaxSize
                    )
                )
            }
            SearchEntryType.QUERY_PROPERTY_SUB_TYPE -> {
                val query = query.value ?: return
                val propertySubType = propertySubTypes.value?.getOrNull(0) ?: return
                performRequest(
                    transactionRepository.findProjectsBySearchTextPropertySubType(
                        query,
                        propertySubType,
                        ownershipType,
                        radius = externalRadius,
                        typeOfArea = externalAreaType,
                        tenureType = externalTenureType,
                        age = externalMinAge,
                        ageMax = externalMaxAge,
                        built = externalMinSize,
                        builtMax = externalMaxSize
                    )
                )
            }
            SearchEntryType.HDB_TOWN -> {
                val hdbTownIds = hdbTownIds.value ?: return
                performRequest(
                    transactionRepository.findProjectsByHdbTownships(
                        hdbTownIds,
                        ownershipType,
                        cdResearchSubtypes = getPropertySubTypes(),
                        radius = externalRadius,
                        typeOfArea = externalAreaType,
                        tenureType = externalTenureType,
                        age = externalMinAge,
                        ageMax = externalMaxAge,
                        built = externalMinSize,
                        builtMax = externalMaxSize
                    )
                )
            }
            SearchEntryType.DISTRICT -> {
                val districtIds = districtIds.value ?: return
                performRequest(
                    transactionRepository.findProjectsByDistricts(
                        districtIds,
                        ownershipType,
                        cdResearchSubtypes = getPropertySubTypes(),
                        radius = externalRadius,
                        typeOfArea = externalAreaType,
                        tenureType = externalTenureType,
                        age = externalMinAge,
                        ageMax = externalMaxAge,
                        built = externalMinSize,
                        builtMax = externalMaxSize
                    )
                )
            }
            SearchEntryType.AMENITY -> {
                val amenityIds = amenityIds.value ?: return
                performRequest(
                    transactionRepository.findProjectsByAmenity(
                        amenityIds,
                        ownershipType,
                        cdResearchSubtypes = getPropertySubTypes(),
                        radius = externalRadius,
                        typeOfArea = externalAreaType,
                        tenureType = externalTenureType,
                        age = externalMinAge,
                        ageMax = externalMaxAge,
                        built = externalMinSize,
                        builtMax = externalMaxSize
                    )
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid searchEntryType `${entryType.name}`")
            }
        }
    }

    fun getPropertySubTypesString(): String {
        return propertySubTypes.value?.map { it.type }?.joinToString(",") ?: ""
    }

    fun isHdb(): Boolean {
        val subTypesNotAllHdb = propertySubTypes.value?.any {
            !ListingEnum.PropertyMainType.HDB.propertySubTypes.contains(it)
        } != false

        return !subTypesNotAllHdb || propertyMainType.value == ListingEnum.PropertyMainType.HDB
    }

    private fun setupQueryLabel() {
        queryLabel.addSource(query) { query ->
            queryLabel.postValue(
                when {
                    query != null && query.isNotEmpty() -> query
                    else -> applicationContext.getString(R.string.label_listings_everywhere)
                }
            )
        }

        queryLabel.addSource(entryType) {
            maybeSetPropertySubTypeQueryLabel()
        }

        queryLabel.addSource(propertySubTypes) {
            maybeSetPropertySubTypeQueryLabel()
        }

        queryLabel.addSource(amenityNames) { amenitiesNames ->
            queryLabel.postValue(
                when {
                    amenitiesNames != null && amenitiesNames.isNotEmpty() -> amenitiesNames
                    else -> applicationContext.getString(R.string.label_listings_everywhere)
                }
            )
        }

        queryLabel.addSource(districtNames) { districtNames ->
            queryLabel.postValue(
                when {
                    districtNames != null && districtNames.isNotEmpty() -> districtNames
                    else -> applicationContext.getString(R.string.label_listings_everywhere)
                }
            )
        }

        queryLabel.addSource(hdbTownNames) { hdbTownNames ->
            queryLabel.postValue(
                when {
                    hdbTownNames != null && hdbTownNames.isNotEmpty() -> hdbTownNames
                    else -> applicationContext.getString(R.string.label_listings_everywhere)
                }
            )
        }
    }

    private fun maybeSetPropertySubTypeQueryLabel() {
        if (entryType.value == SearchEntryType.PROPERTY_SUB_TYPE) {
            queryLabel.postValue(applicationContext.getString(R.string.label_listings_everywhere))
        }
    }

    fun exportTransactions(transactionsCsvContent: String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val userName = SessionUtil.getCurrentUser()?.name?.replace(" ", "_") ?: ""
                val fileName = "${userName}_Transaction_Report.csv"
                try {
                    FileUtil.writeTextToFile(
                        applicationContext,
                        transactionsCsvContent,
                        fileName,
                        AppConstant.DIR_TRANSACTION_REPORT
                    )
                } catch (e: IOException) {
                    return@withContext ErrorUtil.handleError(applicationContext.getString(R.string.toast_generate_report_failed))
                }
                reportExcelLocalFileName.postValue(fileName)
            }
        }
    }
}