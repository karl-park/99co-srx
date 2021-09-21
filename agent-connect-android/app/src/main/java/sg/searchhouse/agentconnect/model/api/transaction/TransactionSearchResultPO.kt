package sg.searchhouse.agentconnect.model.api.transaction

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.ApiConstant
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil

data class TransactionSearchResultPO(
    @SerializedName("agencyName")
    val agencyName: String,
    @SerializedName("agencyProprietaryName")
    val agencyProprietaryName: String,
    @SerializedName("blkNo")
    val blkNo: String,
    @SerializedName("blue")
    val blue: String,
    @SerializedName("cdResearchSubtype")
    val cdResearchSubtype: String,
    @SerializedName("completed")
    val completed: String,
    @SerializedName("developer")
    val developer: String,
    @SerializedName("district")
    val district: String,
    @SerializedName("estateDistance")
    val estateDistance: String,
    @SerializedName("green")
    val green: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("noOfUnits")
    val noOfUnits: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("projectType")
    val projectType: String,
    @SerializedName("proprietary")
    val proprietary: String,
    @SerializedName("red")
    val red: String,
    @SerializedName("streetKey")
    val streetKey: String,
    @SerializedName("streetName")
    val streetName: String,
    @SerializedName("tenure")
    val tenure: String,
    @SerializedName("transactionKeyVOs")
    val transactionKeyVOs: List<Any>,
    @SerializedName("unread")
    val unread: String,
    @SerializedName("virtual")
    val virtual: Boolean
) {
    // Description shown at project transactions activity
    fun getDescription(context: Context): String {
        return context.getString(
            R.string.description_transactions_project,
            district,
            tenure,
            completed
        )
    }

    fun getCompletionStatus(context: Context): String {
        return when {
            NumberUtil.isNaturalNumber(completed) -> context.getString(
                R.string.label_list_item_transaction_project_completed,
                completed
            )
            TextUtils.equals(
                completed,
                ApiConstant.PROJECT_COMPLETION_STATUS_UNKNOWN
            ) -> context.getString(
                R.string.label_list_item_transaction_project_completed,
                completed
            )
            else -> completed
        }
    }

    fun getDistrictFullDescription(context: Context): String {
        return "${context.getString(R.string.label_listing_details_district)} $district"
    }

    fun getCompletedFullDescription(context: Context): String {
        return if (StringUtil.equals(
                completed,
                ApiConstant.PROJECT_COMPLETION_STATUS_UNKNOWN,
                ignoreCase = true
            )
        ) {
            completed
        } else {
            "${context.getString(R.string.project_completion_completed)} $completed"
        }
    }
}