package sg.searchhouse.agentconnect.model.api.xvalue

import android.text.TextUtils
import com.google.gson.annotations.SerializedName

data class HomeReportTransaction(
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
) {
    fun getSizeAndPsf(): String {
        return if (!TextUtils.isEmpty(size) && !TextUtils.isEmpty(psf)) {
            "$size | $psf"
        } else {
            ""
        }
    }

    fun isSourceAvailable(): Boolean {
        return !TextUtils.isEmpty(proprietarySource)
    }
}