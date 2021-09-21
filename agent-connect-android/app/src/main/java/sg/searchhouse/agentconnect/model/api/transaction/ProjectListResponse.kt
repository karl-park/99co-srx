package sg.searchhouse.agentconnect.model.api.transaction

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil

data class ProjectListResponse(
    @SerializedName("projects")
    val projects: List<Project>
) {
    data class Project(
        @SerializedName("completionYear")
        val completionYear: String,
        @SerializedName("coverPhoto")
        val coverPhoto: String,
        @SerializedName("maxPrice")
        val maxPrice: Int,
        @SerializedName("minPrice")
        val minPrice: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("projectId")
        val projectId: Int,
        @SerializedName("propertyType")
        val propertyType: String,
        @SerializedName("propertyTypeName")
        val propertyTypeName: String
    ) {
        fun getFormattedCompletionYear(context: Context): String {
            return if (NumberUtil.isNaturalNumber(completionYear)) {
                context.getString(R.string.project_item_built_year, completionYear)
            } else {
                ""
            }
        }

        fun getPriceRange(context: Context): String {
            return context.getString(
                R.string.project_item_price,
                NumberUtil.formatThousand(minPrice),
                NumberUtil.formatThousand(maxPrice)
            )
        }

        fun getEncodedCoverPhoto(): String {
            return StringUtil.maybeEncodeUrl(coverPhoto)
        }
    }
}