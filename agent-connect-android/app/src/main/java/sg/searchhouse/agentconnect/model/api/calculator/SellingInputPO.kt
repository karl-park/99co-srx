package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SellingInputPO(
    @SerializedName("sellingPrice")
    val sellingPrice: Long,
    @SerializedName("purchasePrice")
    val purchasePrice: Long,
    @SerializedName("bsdAbsd")
    val bsdAbsd: Long,
    @SerializedName("saleDate")
    val saleDate: String,
    @SerializedName("purchaseDate")
    val purchaseDate: String
) : Serializable