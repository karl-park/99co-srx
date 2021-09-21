package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum

//radius = externalRadius,
//typeOfArea = externalAreaType,
//tenureType = externalTenureType,
//age = externalMinAge,
//ageMax = externalMaxAge,
//built = externalMinSize,
//builtMax = externalMaxSize

data class TransactionSearchCriteriaV2VO(
    @SerializedName("saleOrRent")
    val saleOrRent: String,
    @SerializedName("ageFrom")
    val ageFrom: Int?,
    @SerializedName("ageTo")
    val ageTo: Int?,
    @SerializedName("dateFrom")
    val dateFrom: String? = null,
    @SerializedName("dateTo")
    val dateTo: String? = null,
    @SerializedName("districts")
    val districts: List<Int>?,
    @SerializedName("hdbTowns")
    val hdbTowns: List<Int>?,
    @SerializedName("amenities")
    val amenities: List<Int>?,
    @SerializedName("isCompleted")
    val isCompleted: Boolean? = null,
    @SerializedName("isFreeholdTenure")
    val isFreeholdTenure: Boolean?,
    @SerializedName("limit")
    var limit: Int? = null,
    @SerializedName("orderBy")
    val orderBy: String? = null,
    @SerializedName("orderByParam")
    var orderByParam: Int = TransactionEnum.SortType.DEFAULT.value,
    @SerializedName("page")
    var page: Int? = null, // Start from 1
    @SerializedName("propertyTypes")
    val propertyTypes: List<Int>?,
    @SerializedName("radius")
    val radius: Double?,
    @SerializedName("typeOfArea")
    val typeOfArea: Int?,
    @SerializedName("sizeFrom")
    val sizeFrom: Int?,
    @SerializedName("sizeTo")
    val sizeTo: Int?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("userVO")
    val userVO: UserVO? = null
)
