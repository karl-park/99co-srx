package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum

data class ProjectTransactionRequest(
    @SerializedName("saleOrRent")
    val saleOrRent: String,
    @SerializedName("dateFrom")
    val dateFrom: String? = null,
    @SerializedName("dateTo")
    val dateTo: String? = null,
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("orderBy")
    val orderBy: String? = null,
    @SerializedName("orderByParam")
    var orderByParam: Int = TransactionEnum.SortType.DEFAULT.value,
    @SerializedName("page")
    var page: Int? = null,
    @SerializedName("projectId")
    val projectId: Int,

    @SerializedName("ageFrom")
    val ageFrom: Int?,
    @SerializedName("ageTo")
    val ageTo: Int?,
    @SerializedName("isFreeholdTenure")
    val isFreeholdTenure: Boolean?,
    @SerializedName("propertyTypes")
    val propertyTypes: List<Int>?,
    @SerializedName("radius")
    val radius: Double?,
    @SerializedName("typeOfArea")
    val typeOfArea: Int?,
    @SerializedName("sizeFrom")
    val sizeFrom: Int?,
    @SerializedName("sizeTo")
    val sizeTo: Int?
)