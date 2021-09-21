package sg.searchhouse.agentconnect.model.api.transaction


import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.Serializable


data class TowerViewForLastSoldTransactionResponse(
    @SerializedName("results")
    val results: Results
) : Serializable {
    data class UnitsItem(
        @SerializedName("lastSoldPrice")
        val lastSoldPrice: Int = 0,
        @SerializedName("lastSoldFloorLandAreaSqft")
        val lastSoldFloorLandAreaSqft: Int = 0,
        @SerializedName("unitNo")
        val unitNo: String = "",
        @SerializedName("crunchResearchStreetId")
        val crunchResearchStreetId: Int = 0,
        @SerializedName("hasFloorplan")
        val hasFloorplan: String = "",
        @SerializedName("previousUnitTransactions")
        val previousUnitTransactions: List<PreviousUnitTransactionsItem>?,
        @SerializedName("lastSoldContractDate")
        val lastSoldContractDate: String = "",
        @SerializedName("block")
        val block: String = "",
        @SerializedName("lastSoldPsf")
        val lastSoldPsf: Int = 0,
        @SerializedName("unitFloor")
        val unitFloor: String = "",
        @SerializedName("bedrooms")
        val bedrooms: String = "",
        @SerializedName("address")
        val address: String = ""
    ) : Serializable {
        fun getFormattedLastSoldPrice(): String {
            return if (lastSoldPrice > 0) {
                "\$${NumberUtil.formatThousand(lastSoldPrice)}"
            } else {
                ""
            }
        }

        fun getSqftPsf(): String {
            return if (lastSoldFloorLandAreaSqft > 0 || lastSoldPsf > 0) {
                val formattedSqft = NumberUtil.formatThousand(lastSoldFloorLandAreaSqft)
                val formattedPsf = NumberUtil.formatThousand(lastSoldPsf)
                "$formattedSqft sqft |  $formattedPsf psf"
            } else {
                ""
            }
        }

        fun getCaveatCount(): Int {
            return previousUnitTransactions?.size ?: 0
        }

        fun getCaveatsLodgedTitle(context: Context): String {
            val caveatCount = getCaveatCount()
            return context.resources.getQuantityString(
                R.plurals.dialog_header_caveats,
                caveatCount,
                caveatCount.toString()
            )
        }

        fun getCaveatsLodged(context: Context): String {
            val caveatCount = getCaveatCount()
            return context.resources.getQuantityString(
                R.plurals.label_unit_caveat_count,
                caveatCount,
                caveatCount.toString()
            )
        }
    }

    data class Results(
        @SerializedName("towerAnalysisResult")
        val towerAnalysisResult: TowerAnalysisResult
    ) : Serializable


    data class PreviousUnitTransactionsItem(
        @SerializedName("lastSoldPrice")
        val lastSoldPrice: Int = 0,
        @SerializedName("lastSoldFloorLandAreaSqft")
        val lastSoldFloorLandAreaSqft: Int = 0,
        @SerializedName("unitNo")
        val unitNo: String = "",
        @SerializedName("crunchResearchStreetId")
        val crunchResearchStreetId: Int = 0,
        @SerializedName("hasFloorplan")
        val hasFloorplan: String = "",
        @SerializedName("lastSoldContractDate")
        val lastSoldContractDate: String = "",
        @SerializedName("block")
        val block: String = "",
        @SerializedName("lastSoldPsf")
        val lastSoldPsf: Int = 0,
        @SerializedName("unitFloor")
        val unitFloor: String = "",
        @SerializedName("bedrooms")
        val bedrooms: String = ""
    ) : Serializable {
        fun getFormattedLastSoldPrice(): String {
            return "\$${NumberUtil.formatThousand(lastSoldPrice)}"
        }
    }

    data class TowerAnalysisResult(
        @SerializedName("blocks")
        val blocks: List<BlocksItem>?,
        @SerializedName("name")
        val name: String = ""
    ) : Serializable

    data class BlocksItem(
        @SerializedName("block")
        val block: String = "",
        @SerializedName("units")
        val units: List<UnitsItem>?
    ) : Serializable
}