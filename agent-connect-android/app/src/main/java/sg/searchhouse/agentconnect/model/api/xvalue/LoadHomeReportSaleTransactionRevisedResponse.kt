package sg.searchhouse.agentconnect.model.api.xvalue

import com.google.gson.annotations.SerializedName

data class LoadHomeReportSaleTransactionRevisedResponse(
    @SerializedName("SaleTransactionData")
    val saleTransactionData: SaleTransactionData
) {
    fun getTransactionList(): List<HomeReportTransaction> {
        return saleTransactionData.recentTransactionPO.transactionList
    }

    data class SaleTransactionData(
        @SerializedName("comparableProjects")
        val comparableProjects: List<Any>,
        @SerializedName("comparableTransactionPO")
        val comparableTransactionPO: ComparableTransactionPO,
        @SerializedName("comparableTransactions")
        val comparableTransactions: List<Any>,
        @SerializedName("lastSold")
        val lastSold: LastSold,
        @SerializedName("recentSaleTransactions")
        val recentSaleTransactions: List<Any>,
        @SerializedName("recentTransactionPO")
        val recentTransactionPO: RecentTransactionPO,
        @SerializedName("unitTransactions")
        val unitTransactions: List<UnitTransaction>
    ) {
        data class ComparableTransactionPO(
            @SerializedName("highestPrice")
            val highestPrice: String,
            @SerializedName("highestPsf")
            val highestPsf: String,
            @SerializedName("lowestPrice")
            val lowestPrice: String,
            @SerializedName("lowestPsf")
            val lowestPsf: String,
            @SerializedName("transactionInfo")
            val transactionInfo: List<TransactionInfo>,
            @SerializedName("transactionList")
            val transactionList: List<HomeReportTransaction>
        ) {
            data class TransactionInfo(
                @SerializedName("key")
                val key: String,
                @SerializedName("value")
                val value: String
            )
        }

        data class LastSold(
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

        data class RecentTransactionPO(
            @SerializedName("highestPrice")
            val highestPrice: String,
            @SerializedName("highestPsf")
            val highestPsf: String,
            @SerializedName("lowestPrice")
            val lowestPrice: String,
            @SerializedName("lowestPsf")
            val lowestPsf: String,
            @SerializedName("transactionInfo")
            val transactionInfo: List<TransactionInfo>,
            @SerializedName("transactionList")
            val transactionList: List<HomeReportTransaction>
        ) {
            data class TransactionInfo(
                @SerializedName("key")
                val key: String,
                @SerializedName("value")
                val value: String
            )
        }

        data class UnitTransaction(
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