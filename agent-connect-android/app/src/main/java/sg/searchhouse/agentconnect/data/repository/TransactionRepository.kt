package sg.searchhouse.agentconnect.data.repository

import com.google.gson.Gson
import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.model.api.transaction.*
import sg.searchhouse.agentconnect.util.StringUtil
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/703987813/Transaction+Listing+V1+API
 */
@Singleton
class TransactionRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun getLatestRentalTransactions(id: Int): Call<LatestRentalTransactionsResponse> {
        val latestTransactionsRequest = LatestTransactionsRequest(id)
        return srxDataSource.getLatestRentalTransactions(latestTransactionsRequest)
    }

    fun loadTowerViewForLastSoldTransaction(id: Int): Call<TowerViewForLastSoldTransactionResponse> {
        return srxDataSource.loadTowerViewForLastSoldTransaction(id)
    }

    private fun getIsFreeholdTenure(tenureType: TransactionEnum.TenureType?): Boolean? {
        return when (tenureType) {
            TransactionEnum.TenureType.FREEHOLD -> true
            TransactionEnum.TenureType.LEASEHOLD -> false
            else -> null
        }
    }

    fun findProjectsByAmenity(
        amenityIds: String,
        ownershipType: ListingEnum.OwnershipType,
        cdResearchSubtypes: String? = null,
        radius: TransactionEnum.Radius? = null,
        typeOfArea: TransactionEnum.AreaType? = null,
        built: Int? = null,
        builtMax: Int? = null,
        tenureType: TransactionEnum.TenureType? = null,
        age: Int? = null,
        ageMax: Int? = null
    ): Call<TransactionSummaryResponse> {
        val transactionSearchCriteriaV2VO = TransactionSearchCriteriaV2VO(
            saleOrRent = ownershipType.value,
            propertyTypes = cdResearchSubtypes?.let { StringUtil.getIntListFromString(it) },
            radius = radius?.radiusValue,
            typeOfArea = typeOfArea?.value,
            sizeFrom = built,
            sizeTo = builtMax,
            isFreeholdTenure = getIsFreeholdTenure(tenureType),
            ageFrom = age,
            ageTo = ageMax,
            districts = null,
            hdbTowns = null,
            amenities = StringUtil.getIntListFromString(amenityIds),
            text = null
        )
        return summary(transactionSearchCriteriaV2VO)
    }

    fun findProjectsByDistricts(
        districts: String,
        ownershipType: ListingEnum.OwnershipType,
        cdResearchSubtypes: String? = null,
        radius: TransactionEnum.Radius? = null,
        typeOfArea: TransactionEnum.AreaType? = null,
        built: Int? = null,
        builtMax: Int? = null,
        tenureType: TransactionEnum.TenureType? = null,
        age: Int? = null,
        ageMax: Int? = null
    ): Call<TransactionSummaryResponse> {
        val transactionSearchCriteriaV2VO = TransactionSearchCriteriaV2VO(
            saleOrRent = ownershipType.value,
            propertyTypes = cdResearchSubtypes?.let { StringUtil.getIntListFromString(it) },
            radius = radius?.radiusValue,
            typeOfArea = typeOfArea?.value,
            sizeFrom = built,
            sizeTo = builtMax,
            isFreeholdTenure = getIsFreeholdTenure(tenureType),
            ageFrom = age,
            ageTo = ageMax,
            districts = StringUtil.getIntListFromString(districts),
            amenities = null,
            hdbTowns = null,
            text = null
        )
        return summary(transactionSearchCriteriaV2VO)
    }

    fun findProjectsByHdbTownships(
        hdbTownIds: String,
        ownershipType: ListingEnum.OwnershipType,
        cdResearchSubtypes: String? = null,
        radius: TransactionEnum.Radius? = null,
        typeOfArea: TransactionEnum.AreaType? = null,
        built: Int? = null,
        builtMax: Int? = null,
        tenureType: TransactionEnum.TenureType? = null,
        age: Int? = null,
        ageMax: Int? = null
    ): Call<TransactionSummaryResponse> {
        val transactionSearchCriteriaV2VO = TransactionSearchCriteriaV2VO(
            saleOrRent = ownershipType.value,
            propertyTypes = cdResearchSubtypes?.let { StringUtil.getIntListFromString(it) },
            radius = radius?.radiusValue,
            typeOfArea = typeOfArea?.value,
            sizeFrom = built,
            sizeTo = builtMax,
            isFreeholdTenure = getIsFreeholdTenure(tenureType),
            ageFrom = age,
            ageTo = ageMax,
            districts = null,
            hdbTowns = StringUtil.getIntListFromString(hdbTownIds),
            amenities = null,
            text = null
        )
        return summary(transactionSearchCriteriaV2VO)
    }

    fun findProjectsBySearchText(
        searchText: String,
        ownershipType: ListingEnum.OwnershipType,
        cdResearchSubtypes: String? = null,
        radius: TransactionEnum.Radius? = null,
        typeOfArea: TransactionEnum.AreaType? = null,
        built: Int? = null,
        builtMax: Int? = null,
        tenureType: TransactionEnum.TenureType? = null,
        age: Int? = null,
        ageMax: Int? = null
    ): Call<TransactionSummaryResponse> {
        val transactionSearchCriteriaV2VO = TransactionSearchCriteriaV2VO(
            saleOrRent = ownershipType.value,
            propertyTypes = cdResearchSubtypes?.let { StringUtil.getIntListFromString(it) },
            radius = radius?.radiusValue,
            typeOfArea = typeOfArea?.value,
            sizeFrom = built,
            sizeTo = builtMax,
            isFreeholdTenure = getIsFreeholdTenure(tenureType),
            ageFrom = age,
            ageTo = ageMax,
            districts = null,
            hdbTowns = null,
            amenities = null,
            text = searchText
        )
        return summary(transactionSearchCriteriaV2VO)
    }

    fun findProjectsByPropertySubType(
        propertySubType: ListingEnum.PropertySubType,
        ownershipType: ListingEnum.OwnershipType,
        radius: TransactionEnum.Radius? = null,
        typeOfArea: TransactionEnum.AreaType? = null,
        built: Int? = null,
        builtMax: Int? = null,
        tenureType: TransactionEnum.TenureType? = null,
        age: Int? = null,
        ageMax: Int? = null
    ): Call<TransactionSummaryResponse> {
        val transactionSearchCriteriaV2VO = TransactionSearchCriteriaV2VO(
            saleOrRent = ownershipType.value,
            propertyTypes = listOf(propertySubType.type),
            radius = radius?.radiusValue,
            typeOfArea = typeOfArea?.value,
            sizeFrom = built,
            sizeTo = builtMax,
            isFreeholdTenure = getIsFreeholdTenure(tenureType),
            ageFrom = age,
            ageTo = ageMax,
            districts = null,
            hdbTowns = null,
            amenities = null,
            text = null
        )
        return summary(transactionSearchCriteriaV2VO)
    }

    fun findProjectsBySearchTextPropertySubType(
        searchText: String,
        propertySubType: ListingEnum.PropertySubType,
        ownershipType: ListingEnum.OwnershipType,
        radius: TransactionEnum.Radius? = null,
        typeOfArea: TransactionEnum.AreaType? = null,
        built: Int? = null,
        builtMax: Int? = null,
        tenureType: TransactionEnum.TenureType? = null,
        age: Int? = null,
        ageMax: Int? = null
    ): Call<TransactionSummaryResponse> {
        val transactionSearchCriteriaV2VO = TransactionSearchCriteriaV2VO(
            saleOrRent = ownershipType.value,
            propertyTypes = listOf(propertySubType.type),
            radius = radius?.radiusValue,
            typeOfArea = typeOfArea?.value,
            sizeFrom = built,
            sizeTo = builtMax,
            isFreeholdTenure = getIsFreeholdTenure(tenureType),
            ageFrom = age,
            ageTo = ageMax,
            districts = null,
            hdbTowns = null,
            amenities = null,
            text = searchText
        )
        return summary(transactionSearchCriteriaV2VO)
    }

    fun tableList(transactionSearchCriteriaV2VO: TransactionSearchCriteriaV2VO): Call<TableListResponse> {
        val bodyString = Gson().toJson(transactionSearchCriteriaV2VO)
        return srxDataSource.tableList(bodyString)
    }

    fun projectTableList(projectTransactionRequest: ProjectTransactionRequest): Call<TableListResponse> {
        val bodyString = Gson().toJson(projectTransactionRequest)
        return srxDataSource.tableList(bodyString)
    }

    fun paginatedProjectList(transactionSearchCriteriaV2VO: TransactionSearchCriteriaV2VO): Call<PaginatedProjectListResponse> {
        val bodyString = Gson().toJson(transactionSearchCriteriaV2VO)
        return srxDataSource.paginatedProjectList(bodyString)
    }

    fun summary(transactionSearchCriteriaV2VO: TransactionSearchCriteriaV2VO): Call<TransactionSummaryResponse> {
        val bodyString = Gson().toJson(transactionSearchCriteriaV2VO)
        return srxDataSource.transactionSummary(bodyString)
    }

    fun projectSummary(projectTransactionRequest: ProjectTransactionRequest): Call<TransactionSummaryResponse> {
        val bodyString = Gson().toJson(projectTransactionRequest)
        return srxDataSource.transactionSummary(bodyString)
    }

    fun findProjects(
        searchMode: ProjectEnum.SearchMode,
        cdResearchSubtypes: String? = null,
        searchText: String? = null,
        districts: String? = null,
        hdbTowns: String? = null,
        radius: Int? = null,
        tenureType: Int? = null,
        typeOfArea: Int? = null,
        age: Int? = null,
        ageMax: Int? = null,
        property: String? = null,
        order: String? = null
    ): Call<FindProjectsByKeywordResponse> {
        return srxDataSource.findProjectsByKeyword(
            searchMode = searchMode.value,
            cdResearchSubtypes = cdResearchSubtypes,
            searchText = searchText,
            districts = districts,
            hdbTowns = hdbTowns,
            radius = radius,
            tenureType = tenureType,
            typeOfArea = typeOfArea,
            age = age,
            ageMax = ageMax,
            property = property,
            order = order
        )
    }
}