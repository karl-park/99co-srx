package sg.searchhouse.agentconnect.model.api.xvalue

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant.SOURCE_SRX
import sg.searchhouse.agentconnect.model.api.common.SrxDate
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import kotlin.math.abs

data class GetValuationDetailResponse(
    @SerializedName("data")
    val `data`: Data
) {
    data class Data(
        @SerializedName("averageComparablePsf")
        val averageComparablePsf: Double,
        @SerializedName("comparables")
        val comparables: List<Comparable>,
        @SerializedName("includePES")
        val includePES: String,
        @SerializedName("overridePsf")
        val overridePsf: Boolean,
        @SerializedName("renovationCost")
        val renovationCost: Int,
        @SerializedName("renovationYear")
        val renovationYear: Int,
        @SerializedName("selectedProjects")
        val selectedProjects: List<SelectedProject>
    ) {
        data class Comparable(
            @SerializedName("address")
            val address: String,
            @SerializedName("adjustedPsf")
            val adjustedPsf: Int,
            @SerializedName("ageAdj")
            val ageAdj: Double,
            @SerializedName("bcaInfo")
            val bcaInfo: Any?,
            @SerializedName("bedrooms")
            val bedrooms: Int,
            @SerializedName("builtYear")
            val builtYear: String,
            @SerializedName("cdResearchSubtype")
            val cdResearchSubtype: Int,
            @SerializedName("contract")
            val contract: Any?,
            @SerializedName("cornerTerrace")
            val cornerTerrace: String,
            @SerializedName("crunchReseachStreetId")
            val crunchReseachStreetId: Int,
            @SerializedName("customAdjustments")
            val customAdjustments: CustomAdjustments,
            @SerializedName("date")
            val date: SrxDate,
            @SerializedName("dateAdj")
            val dateAdj: Double,
            @SerializedName("distance")
            val distance: Double,
            @SerializedName("district")
            val district: Int,
            @SerializedName("dteUpd")
            val dteUpd: String,
            @SerializedName("floorAdj")
            val floorAdj: Double,
            @SerializedName("gfa")
            val gfa: Int,
            @SerializedName("hdbLease")
            val hdbLease: Int,
            @SerializedName("hdbModel")
            val hdbModel: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("indexValue")
            val indexValue: Int,
            @SerializedName("latitude")
            val latitude: Double,
            @SerializedName("leaseAdj")
            val leaseAdj: Double,
            @SerializedName("leaseCommence")
            val leaseCommence: Int,
            @SerializedName("leaseRemaining")
            val leaseRemaining: Int,
            @SerializedName("longitude")
            val longitude: Double,
            @SerializedName("mobile")
            val mobile: Boolean,
            @SerializedName("mrtAdj")
            val mrtAdj: Double,
            @SerializedName("overridePsf")
            val overridePsf: Boolean,
            @SerializedName("postalCode")
            val postalCode: Int,
            @SerializedName("price")
            val price: Int,
            @SerializedName("projectName")
            val projectName: String,
            @SerializedName("proprietary")
            val proprietary: Boolean,
            @SerializedName("psf")
            val psf: Double,
            @SerializedName("reason")
            val reason: String,
            @SerializedName("schoolAdj")
            val schoolAdj: Double,
            @SerializedName("simdexAdj")
            val simdexAdj: Double,
            @SerializedName("similarityIndex")
            val similarityIndex: Int,
            @SerializedName("size")
            val size: Double,
            @SerializedName("sizeAdj")
            val sizeAdj: Double,
            @SerializedName("source")
            val source: String,
            @SerializedName("stackAdj")
            val stackAdj: Double,
            @SerializedName("stdAdjustments")
            val stdAdjustments: StdAdjustments,
            @SerializedName("storey")
            val storey: Int,
            @SerializedName("streetName")
            val streetName: String,
            @SerializedName("streetViewUrl")
            val streetViewUrl: String,
            @SerializedName("tenure")
            val tenure: String,
            @SerializedName("toAdjust")
            val toAdjust: Boolean,
            @SerializedName("transactedPrice")
            val transactedPrice: Int,
            @SerializedName("transactionData")
            val transactionData: Any?,
            @SerializedName("transactionId")
            val transactionId: Int,
            @SerializedName("transactionType")
            val transactionType: String,
            @SerializedName("typeOfArea")
            val typeOfArea: String,
            @SerializedName("typeOfSale")
            val typeOfSale: String,
            @SerializedName("unitBlock")
            val unitBlock: String,
            @SerializedName("unitFloor")
            val unitFloor: String,
            @SerializedName("unitNo")
            val unitNo: String,
            @SerializedName("unitSize")
            val unitSize: Double,
            @SerializedName("valuerPsf")
            val valuerPsf: Double,
            @SerializedName("viewAdj")
            val viewAdj: Double,
            @SerializedName("weight")
            val weight: Int
        ) {
            fun getFormattedTransactedPrice(context: Context): String {
                // Use price because transactedPrice always zero
                return context.getString(R.string.label_price, NumberUtil.formatThousand(price))
            }

            fun getFormattedPsf(context: Context): String {
                return context.getString(R.string.label_price, NumberUtil.formatThousand(psf, 2))
            }

            fun getFormattedValuerPsf(context: Context): String {
                return context.getString(
                    R.string.label_price,
                    NumberUtil.formatThousand(valuerPsf, 2)
                )
            }

            fun getFormattedDate(): String {
                return DateTimeUtil.convertTimeStampToString(date.time, DateTimeUtil.FORMAT_DATE_6)
            }

            fun getFormattedSize(context: Context): String {
                val sqft = NumberUtil.formatThousand(size)
                val sqm = NumberUtil.formatThousand(NumberUtil.getSqmFromSqft(size))
                return context.getString(R.string.label_area_sqft_sqm, sqft, sqm)
            }

            fun isSourceSrx(): Boolean {
                return TextUtils.equals(source, SOURCE_SRX)
            }

            fun isHdb(): Boolean {
                return PropertyTypeUtil.isHDB(cdResearchSubtype)
            }

            class CustomAdjustments

            data class StdAdjustments(
                @SerializedName("Date")
                val date: Double,
                @SerializedName("Floor")
                val floor: Double,
                @SerializedName("Lease")
                val lease: Double,
                @SerializedName("Size")
                val size: Double
            ) {
                fun getFormattedLease(context: Context): String {
                    return if (abs(lease) > 0) {
                        context.getString(
                            R.string.label_percent,
                            NumberUtil.formatThousand(lease, 2)
                        )
                    } else {
                        ""
                    }
                }

                fun getFormattedDate(context: Context): String {
                    return if (abs(date) > 0) {
                        context.getString(
                            R.string.label_percent,
                            NumberUtil.formatThousand(date, 2)
                        )
                    } else {
                        ""
                    }
                }

                fun getFormattedFloor(context: Context): String {
                    return if (abs(floor) > 0) {
                        context.getString(
                            R.string.label_percent,
                            NumberUtil.formatThousand(floor, 2)
                        )
                    } else {
                        ""
                    }
                }

                fun getFormattedSize(context: Context): String {
                    return if (abs(size) > 0) {
                        context.getString(
                            R.string.label_percent,
                            NumberUtil.formatThousand(size, 2)
                        )
                    } else {
                        ""
                    }
                }

                // TODO: Time rush, refactor later
                fun getLeaseColor(context: Context): Int {
                    return if (lease < 0) {
                        context.getColor(R.color.red)
                    } else {
                        context.getColor(R.color.black)
                    }
                }

                fun getDateColor(context: Context): Int {
                    return if (date < 0) {
                        context.getColor(R.color.red)
                    } else {
                        context.getColor(R.color.black)
                    }
                }

                fun getFloorColor(context: Context): Int {
                    return if (floor < 0) {
                        context.getColor(R.color.red)
                    } else {
                        context.getColor(R.color.black)
                    }
                }

                fun getSizeColor(context: Context): Int {
                    return if (size < 0) {
                        context.getColor(R.color.red)
                    } else {
                        context.getColor(R.color.black)
                    }
                }
            }
        }

        data class SelectedProject(
            @SerializedName("crunchResearchStreetId")
            val crunchResearchStreetId: Int,
            @SerializedName("latitude")
            val latitude: Double,
            @SerializedName("longitude")
            val longitude: Double,
            @SerializedName("name")
            val name: String,
            @SerializedName("postalCode")
            val postalCode: Int,
            @SerializedName("subtype")
            val subtype: String
        )
    }
}