package sg.searchhouse.agentconnect.model.api.transaction

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil

data class TransactionSummaryResponse(
    @SerializedName("summary")
    val summary: Summary?
) {
    data class Summary(
        @SerializedName("highestPrice")
        val highestPrice: Int,
        @SerializedName("lastMonthChange")
        val lastMonthChange: Double,
        @SerializedName("lowestPrice")
        val lowestPrice: Int,
        @SerializedName("medianPsf")
        val medianPsf: Double,
        @SerializedName("srxUrl")
        val srxUrl: String?,
        @SerializedName("header")
        val header: String?,
        @SerializedName("tenure")
        val tenure: String?,
        @SerializedName("builtYear")
        val builtYear: String?,
        @SerializedName("isSingleProject")
        val isSingleProject: Boolean,
        @SerializedName("projectId")
        val projectId: Int,
        @SerializedName("cdResearchSubtype")
        val cdResearchSubtype: Int,
        @SerializedName("district")
        val district: String,
        @SerializedName("hdbTown")
        val hdbTown: String,
        @SerializedName("isLastMonthChangeNA")
        val isLastMonthChangeNA: Boolean
    ) {
        fun getFormattedHighestPrice(): String {
            return if (highestPrice != 0) {
                "\$${NumberUtil.formatThousand(highestPrice)}"
            } else {
                ""
            }
        }

        fun getFormattedLastMonthChange(): String {
            return if (lastMonthChange != 0.0) {
                "${NumberUtil.formatThousand(lastMonthChange, 1)}%"
            } else {
                ""
            }
        }

        fun getFormattedLowestPrice(): String {
            return if (lowestPrice != 0) {
                "\$${NumberUtil.formatThousand(lowestPrice)}"
            } else {
                ""
            }
        }

        fun getFormattedSaleMedianPsf(): String {
            return if (medianPsf != 0.0) {
                "\$${NumberUtil.formatThousand(medianPsf, 0)}"
            } else {
                ""
            }
        }

        fun getFormattedRentalMedianPsf(): String {
            return if (medianPsf != 0.0) {
                val rounded = NumberUtil.roundDouble(medianPsf, 2)
                "\$${NumberUtil.formatThousand(rounded, 2)}"
            } else {
                ""
            }
        }

        fun getDescription(context: Context): String {
            return when {
                PropertyTypeUtil.isHDB(cdResearchSubtype) -> {
                    val propertySubType = context.getString(getPropertySubType().label)
                    return listOf(hdbTown, propertySubType).filter {
                        it.isNotEmpty()
                    }.joinToString(" | ")
                }
                PropertyTypeUtil.isCondo(cdResearchSubtype) -> {
                    val completedYear =
                        context.getString(
                            R.string.description_transactions_project_completed,
                            builtYear
                        )
                    return listOf(district, tenure, completedYear).filter {
                        !TextUtils.isEmpty(
                            when {
                                TextUtils.equals(it, completedYear) -> builtYear
                                else -> it
                            }
                        )
                    }.joinToString(" | ")
                }
                PropertyTypeUtil.isLanded(cdResearchSubtype) -> {
                    val propertySubType = context.getString(getPropertySubType().label)
                    return listOf(district, propertySubType).filter {
                        it.isNotEmpty()
                    }.joinToString(" | ")
                }
                PropertyTypeUtil.isCommercial(cdResearchSubtype) -> {
                    val propertySubType = context.getString(getPropertySubType().label)
                    return listOf(district, propertySubType).filter {
                        it.isNotEmpty()
                    }.joinToString(" | ")
                }
                else -> ""
            }
        }

        private fun getSubTypesWithoutTower(): List<ListingEnum.PropertySubType> {
            return listOf(
                ListingEnum.PropertySubType.ALL_LANDED,
                ListingEnum.PropertySubType.TERRACE,
                ListingEnum.PropertySubType.DETACHED,
                ListingEnum.PropertySubType.SEMI_DETACHED,
                ListingEnum.PropertySubType.LAND
            )
        }

        fun getPropertySubType(): ListingEnum.PropertySubType {
            return PropertyTypeUtil.getPropertySubType(cdResearchSubtype)
                ?: throw IllegalArgumentException("Missing/invalid property sub type $cdResearchSubtype in this project summary!")
        }

        // Based on its property sub type, decide whether should show tower view on project transaction page
        fun isShowTower(): Boolean {
            return !getSubTypesWithoutTower().contains(getPropertySubType())
        }
    }
}