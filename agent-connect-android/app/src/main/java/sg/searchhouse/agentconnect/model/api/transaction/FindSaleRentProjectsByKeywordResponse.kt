package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName

data class FindSaleRentProjectsByKeywordResponse(
    @SerializedName("projects")
    val projects: Projects
) {
    data class Projects(
        @SerializedName("projects")
        val subProjects: SubProjects,
        @SerializedName("summary")
        val summary: Summary,
        @SerializedName("transactions")
        val transactions: List<Transaction>
    ) {
        data class SubProjects(
            @SerializedName("Apartment")
            val apartment: List<TransactionSearchResultPO>?,
            @SerializedName("Condominium")
            val condominium: List<TransactionSearchResultPO>?,
            @SerializedName("Detached")
            val detached: List<TransactionSearchResultPO>?,
            @SerializedName("HDB 2 Rooms")
            val hDB2Rooms: List<TransactionSearchResultPO>?,
            @SerializedName("HDB 3 Rooms")
            val hDB3Rooms: List<TransactionSearchResultPO>?,
            @SerializedName("HDB 4 Rooms")
            val hDB4Rooms: List<TransactionSearchResultPO>?,
            @SerializedName("HDB 5 Rooms")
            val hDB5Rooms: List<TransactionSearchResultPO>?,
            @SerializedName("HDB Executive")
            val hDBExecutive: List<TransactionSearchResultPO>?,
            @SerializedName("Semi-Detached")
            val semiDetached: List<TransactionSearchResultPO>?,
            @SerializedName("Terrace")
            val terrace: List<TransactionSearchResultPO>?
        ) {

            fun getSize(): Int {
                return (apartment?.size ?: 0) + (condominium?.size ?: 0) + (detached?.size
                    ?: 0) + (hDB2Rooms?.size ?: 0) + (hDB3Rooms?.size ?: 0) + (hDB4Rooms?.size
                    ?: 0) + (hDB5Rooms?.size ?: 0) + (hDBExecutive?.size ?: 0) + (semiDetached?.size
                    ?: 0) + (terrace?.size ?: 0)
            }
        }

        data class Summary(
            @SerializedName("highestPrice")
            val highestPrice: String,
            @SerializedName("highestPsf")
            val highestPsf: String,
            @SerializedName("lowestPrice")
            val lowestPrice: String,
            @SerializedName("lowestPsf")
            val lowestPsf: String
        )

        data class Transaction(
            @SerializedName("address")
            val address: String,
            @SerializedName("addressColor")
            val addressColor: String,
            @SerializedName("cobrokeInd")
            val cobrokeInd: String,
            @SerializedName("cov")
            val cov: String,
            @SerializedName("dateSold")
            val dateSold: String,
            @SerializedName("flatModel")
            val flatModel: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("latitude")
            val latitude: Int,
            @SerializedName("leaseCommence")
            val leaseCommence: String,
            @SerializedName("longitude")
            val longitude: Int,
            @SerializedName("maxPrice")
            val maxPrice: String,
            @SerializedName("maxPsf")
            val maxPsf: String,
            @SerializedName("minPrice")
            val minPrice: String,
            @SerializedName("minPsf")
            val minPsf: String,
            @SerializedName("noOfOwners")
            val noOfOwners: String,
            @SerializedName("postalCode")
            val postalCode: String,
            @SerializedName("postalDistrict")
            val postalDistrict: Int,
            @SerializedName("price")
            val price: String,
            @SerializedName("projectId")
            val projectId: String,
            @SerializedName("projectName")
            val projectName: String,
            @SerializedName("propertyType")
            val propertyType: String,
            @SerializedName("proprietarySource")
            val proprietarySource: String,
            @SerializedName("proprietarySourceColor")
            val proprietarySourceColor: String,
            @SerializedName("psf")
            val psf: String,
            @SerializedName("remarks")
            val remarks: String,
            @SerializedName("roomRentalInd")
            val roomRentalInd: String,
            @SerializedName("size")
            val size: String,
            @SerializedName("streetName")
            val streetName: String,
            @SerializedName("tenure")
            val tenure: String,
            @SerializedName("town")
            val town: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("typeOfArea")
            val typeOfArea: String,
            @SerializedName("typeOfSale")
            val typeOfSale: String,
            @SerializedName("unRead")
            val unRead: String,
            @SerializedName("unitFloor")
            val unitFloor: String,
            @SerializedName("valuation")
            val valuation: String
        )
    }
}