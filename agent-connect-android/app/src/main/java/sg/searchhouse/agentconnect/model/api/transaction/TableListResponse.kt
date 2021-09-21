package sg.searchhouse.agentconnect.model.api.transaction

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.getEmptyIfDash
import sg.searchhouse.agentconnect.dsl.remove
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil
import kotlin.math.abs

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/723320839/Agent+Transactions+V1+API
 */
data class TableListResponse(
    @SerializedName("params")
    val params: Params,
    @SerializedName("transactions")
    val transactions: Transactions
) {
    data class Params(
        @SerializedName("ageFrom")
        val ageFrom: Int,
        @SerializedName("ageTo")
        val ageTo: Int,
        @SerializedName("dateFrom")
        val dateFrom: String,
        @SerializedName("dateTo")
        val dateTo: String,
        @SerializedName("districts")
        val districts: List<Any>,
        @SerializedName("hdbTowns")
        val hdbTowns: List<Any>,
        @SerializedName("isCompleted")
        val isCompleted: Boolean,
        @SerializedName("isFreeholdTenure")
        val isFreeholdTenure: Boolean,
        @SerializedName("limit")
        val limit: Int,
        @SerializedName("orderBy")
        val orderBy: String,
        @SerializedName("orderByParam")
        val orderByParam: Int,
        @SerializedName("page")
        val page: Int,
        @SerializedName("projectId")
        val projectId: Int,
        @SerializedName("propertyTypes")
        val propertyTypes: List<Any>,
        @SerializedName("radius")
        val radius: Int,
        @SerializedName("sizeFrom")
        val sizeFrom: Int,
        @SerializedName("sizeTo")
        val sizeTo: Int,
        @SerializedName("text")
        val text: String,
        @SerializedName("userVO")
        val userVO: UserVO?
    )

    data class Transactions(
        @SerializedName("results")
        val results: List<Result>,
        @SerializedName("total")
        val total: Int
    ) {
        data class Result(
            @SerializedName("address")
            val address: String,
            @SerializedName("block")
            val block: String,
            @SerializedName("builtYear")
            val builtYear: String?,
            @SerializedName("contractDate")
            val contractDate: String,
            @SerializedName("floor")
            val floor: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("postalCode")
            val postalCode: Int,
            @SerializedName("price")
            val price: Int,
            @SerializedName("projectEstate")
            val projectEstate: String,
            @SerializedName("propertyType")
            val propertyType: Int,
            @SerializedName("psf")
            val psf: Double,
            @SerializedName("rawContractDate")
            val rawContractDate: String?,
            @SerializedName("source")
            val source: String,
            @SerializedName("sqft")
            val sqft: Double,
            @SerializedName("sqm")
            val sqm: Double,
            @SerializedName("street")
            val street: String,
            @SerializedName("unit")
            val unit: String,
            @SerializedName("unitNumber")
            val unitNumber: String,
            @SerializedName("valuation")
            val valuation: Int,
            @SerializedName("tenure")
            val tenure: String?,
            @SerializedName("typeOfSale")
            val typeOfSale: String,
            @SerializedName("typeOfArea")
            val typeOfArea: String
        ) {
            fun getFormattedContractDate(): String {
                return DateTimeUtil.getConvertedFormatDate(
                    contractDate,
                    DateTimeUtil.FORMAT_DATE_4,
                    DateTimeUtil.FORMAT_DATE_5
                )
            }

            fun getFormattedUnit(): String {
                return when {
                    !TextUtils.isEmpty(unitNumber) -> {
                        unitNumber
                            .replace("Level ", "lvl")
                            .replace(" to ", "-")
                    }
                    !TextUtils.isEmpty(floor) && !TextUtils.isEmpty(unit) -> {
                        "#$floor-$unit"
                    }
                    !TextUtils.isEmpty(floor) -> {
                        "#$floor-XX"
                    }
                    !TextUtils.isEmpty(unit) -> {
                        unit
                    }
                    !TextUtils.isEmpty(block) -> {
                        block
                    }
                    else -> ""
                }
            }

            fun getFormattedPrice(): String {
                return "\$${NumberUtil.formatThousand(price)}"
            }

            fun getFormattedSalePrice(): String {
                return when {
                    price > 1_000_000 -> {
                        val millions = NumberUtil.roundDouble(price / 1_000_000.0, 3)
                        "\$${NumberUtil.formatThousand(millions, 3)}M"
                    }
                    price > 1000 -> {
                        val thousands = (price / 1000.0).toInt()
                        "\$${NumberUtil.formatThousand(thousands)}K"
                    }
                    else -> {
                        "\$${NumberUtil.formatThousand(price)}"
                    }
                }
            }

            fun getFormattedCov(): String {
                return getCov()?.run {
                    val inThousand = abs(this).toDouble() / 1000
                    val figure = NumberUtil.roundDoubleString(inThousand, 1)
                    when {
                        this < 0 -> "-\$${figure}K"
                        inThousand.toInt() == 0 -> "\$0"
                        else -> "\$${figure}K"
                    }
                } ?: ""
            }

            fun getFormattedSqft(): String {
                return when (sqft) {
                    0.0 -> "-"
                    else -> NumberUtil.formatThousand(sqft)
                }
            }

            fun getFormattedSqm(): String {
                return when (sqm) {
                    0.0 -> "-"
                    else -> NumberUtil.formatThousand(sqm)
                }
            }

            fun getFormattedLandedAddress(): String {
                return listOf(block, street).filter { it.isNotEmpty() }.joinToString(" ")
            }

            fun getFormattedArea(): String {
                return if (sqm != 0.0 && sqft != 0.0) {
                    "${getFormattedSqm()} sqm /\n${getFormattedSqft()} sqft"
                } else {
                    ""
                }
            }

            fun getFormattedPsfSale(): String {
                return when (psf) {
                    0.0 -> "-"
                    else -> {
                        "\$${NumberUtil.formatThousand(NumberUtil.roundDouble(psf, 0))}"
                    }
                }
            }

            fun getFormattedPsfRental(): String {
                return when (psf) {
                    0.0 -> "-"
                    else -> "\$${NumberUtil.formatThousand(NumberUtil.roundDouble(psf, 2), 2)}"
                }
            }

            fun getFormattedProjectEstate(): String {
                return if (!TextUtils.equals(projectEstate, street)) {
                    projectEstate
                } else {
                    ""
                }
            }

            fun getFormattedMainAddress(): String {
                return when {
                    !TextUtils.isEmpty(block) -> "$block $street"
                    else -> street
                }
            }

            fun getFormattedMainAddressAndPropertyType(context: Context): String {
                return "${getFormattedMainAddress()} ${getPropertyTypeName(context)}"
            }

            fun getFormattedMainAddressAndProjectEstate(): String {
                return "${getFormattedMainAddress()} ${getFormattedProjectEstate()}"
            }

            fun getUnitStreet(): String {
                return when {
                    !TextUtils.isEmpty(getFormattedUnit()) -> {
                        listOf(getFormattedUnit(), street).joinToString(" ")
                    }
                    else -> street
                }
            }

            fun getDisplayProjectAddress(): String {
                return if (!TextUtils.isEmpty(projectEstate) && !TextUtils.equals(
                        street,
                        projectEstate
                    )
                ) {
                    "$street\n$projectEstate"
                } else {
                    street
                }
            }

            fun getDisplayProjectAddress(context: Context): String {
                val block = "Blk $block"
                val address = if (!TextUtils.isEmpty(projectEstate) && !TextUtils.equals(
                        street,
                        projectEstate
                    )
                ) {
                    "$street\n$projectEstate"
                } else {
                    street
                }
                val propertyType = getPropertyTypeName(context)
                return "$block\n$address\n$propertyType"
            }

            private fun getCov(): Int? {
                val rawCov = price - valuation
                return when {
                    price > 0 && valuation > 0 -> rawCov
                    else -> null
                }
            }

            fun getPropertyTypeName(context: Context): String {
                return ListingEnum.PropertySubType.values()
                    .find { it.type == propertyType }?.label?.let {
                        context.getString(it)
                    } ?: ""
            }

            fun getFormattedBuiltYear(): String {
                return if (NumberUtil.isNaturalNumber(builtYear ?: "")) {
                    builtYear ?: ""
                } else {
                    ""
                }
            }

            fun getFormattedTypeOfSale(): String {
                return StringUtil.toTitleCase(typeOfSale)
            }

            private fun getProjectEstateUnitStreetPropertyType(context: Context): String {
                return "${getFormattedProjectEstate()} ${getUnitStreet()} ${
                    getPropertyTypeName(
                        context
                    )
                }"
            }

            fun getGroupHdbCsvContent(
                context: Context,
                ownershipType: ListingEnum.OwnershipType
            ): String {
                val contractDate = getFormattedContractDate().sanitizeCell()
                val address = getFormattedMainAddressAndPropertyType(context).sanitizeCell()
                val unit = getFormattedUnit().sanitizeCell()
                val price = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedSalePrice()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPrice()
                }.sanitizeCell()
                val sizeSqm = getFormattedSqm().sanitizeCell()
                val psf = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedPsfSale()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPsfRental()
                }.sanitizeCell()
                val builtYear = getFormattedBuiltYear().sanitizeCell()
                val cov = getFormattedCov()
                return "$contractDate,$address,$unit,$price,$sizeSqm,$psf,$builtYear,$cov"
            }

            fun getGroupNlpCsvContent(ownershipType: ListingEnum.OwnershipType): String {
                val contractDate = getFormattedContractDate().sanitizeCell()
                val address = getFormattedMainAddressAndProjectEstate().sanitizeCell()
                val unit = getFormattedUnit().sanitizeCell()
                val price = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedSalePrice()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPrice()
                }.sanitizeCell()
                val sizeSqft = getFormattedSqft().sanitizeCell()
                val psf = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedPsfSale()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPsfRental()
                }.sanitizeCell()
                val builtYear = getFormattedBuiltYear().sanitizeCell()
                val typeOfSale = getFormattedTypeOfSale().sanitizeCell()

                return "$contractDate,$address,$unit,$price,$sizeSqft,$psf,$builtYear,$typeOfSale"
            }

            fun getGroupLandedCsvContent(
                context: Context,
                ownershipType: ListingEnum.OwnershipType
            ): String {
                val contractDate = getFormattedContractDate().sanitizeCell()
                val address = getProjectEstateUnitStreetPropertyType(context).sanitizeCell()
                val price = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedSalePrice()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPrice()
                }.sanitizeCell()
                val sizeSqft = getFormattedSqft().sanitizeCell()
                val psf = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedPsfSale()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPsfRental()
                }.sanitizeCell()
                val builtYear = getFormattedBuiltYear().sanitizeCell()
                val typeOfSale = getFormattedTypeOfSale().sanitizeCell()

                return "$contractDate,$address,$price,$sizeSqft,$psf,$builtYear,$typeOfSale"
            }

            fun getGroupCommercialCsvContent(
                context: Context,
                ownershipType: ListingEnum.OwnershipType
            ): String {
                val contractDate = getFormattedContractDate().sanitizeCell()
                val address = getProjectEstateUnitStreetPropertyType(context).sanitizeCell()
                val unit = getFormattedUnit().sanitizeCell()
                val price = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedSalePrice()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPrice()
                }.sanitizeCell()
                val sqft = getFormattedSqft().sanitizeCell()
                val psf = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedPsfSale()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPsfRental()
                }.sanitizeCell()
                val builtYear = getFormattedBuiltYear().sanitizeCell()
                val typeOfSale = getFormattedTypeOfSale().sanitizeCell()

                return "$contractDate,$address,$unit,$price,$sqft,$psf,$builtYear,$typeOfSale"
            }

            fun getProjectRealTimeHdbCsvContent(ownershipType: ListingEnum.OwnershipType): String {
                val formattedContractDate = getFormattedContractDate().sanitizeCell()
                val block = block.sanitizeCell()
                val formattedUnit = getFormattedUnit().sanitizeCell()
                val price = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedSalePrice()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPrice()
                }.sanitizeCell()
                val formattedSqm = getFormattedSqm().sanitizeCell()
                val psf = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedPsfSale()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPsfRental()
                }.sanitizeCell()
                val formattedBuiltYear = getFormattedBuiltYear().sanitizeCell()

                return "$formattedContractDate,$block,$formattedUnit,$price,$formattedSqm,$psf,$formattedBuiltYear"
            }

            fun getProjectRealTimeNlpCsvContent(ownershipType: ListingEnum.OwnershipType): String {
                val formattedContractDate = getFormattedContractDate().sanitizeCell()
                val block = block.sanitizeCell()
                val formattedUnit = getFormattedUnit().sanitizeCell()
                val price = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedSalePrice()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPrice()
                }.sanitizeCell()
                val formattedSqft = getFormattedSqft().sanitizeCell()
                val psf = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedPsfSale()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPsfRental()
                }.sanitizeCell()
                val formattedBuiltYear = getFormattedBuiltYear().sanitizeCell()
                val formattedTypeOfSale = getFormattedTypeOfSale().sanitizeCell()

                return "$formattedContractDate,$block,$formattedUnit,$price,$formattedSqft,$psf,$formattedBuiltYear,$formattedTypeOfSale"
            }

            fun getProjectRealTimeLandedCsvContent(ownershipType: ListingEnum.OwnershipType): String {
                val formattedContractDate = getFormattedContractDate().sanitizeCell()
                val price = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedSalePrice()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPrice()
                }.sanitizeCell()
                val formattedAddress = getFormattedLandedAddress().sanitizeCell()
                val formattedSqft = getFormattedSqft().sanitizeCell()
                val psf = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedPsfSale()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPsfRental()
                }.sanitizeCell()
                val formattedLandType = typeOfArea.sanitizeCell()
                val formattedBuiltYear = getFormattedBuiltYear().sanitizeCell()
                val formattedTypeOfSale = getFormattedTypeOfSale().sanitizeCell()

                return "$formattedContractDate,$formattedAddress,$price,$formattedSqft,$psf,$formattedLandType,$formattedBuiltYear,$formattedTypeOfSale"
            }

            fun getProjectRealTimeCommercialCsvContent(ownershipType: ListingEnum.OwnershipType): String {
                val formattedContractDate = getFormattedContractDate().sanitizeCell()
                val block = block.sanitizeCell()
                val formattedUnit = getFormattedUnit().sanitizeCell()
                val price = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedSalePrice()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPrice()
                }.sanitizeCell()
                val formattedSqft = getFormattedSqft().sanitizeCell()
                val psf = when (ownershipType) {
                    ListingEnum.OwnershipType.SALE -> getFormattedPsfSale()
                    ListingEnum.OwnershipType.RENT, ListingEnum.OwnershipType.ROOM_RENTAL -> getFormattedPsfRental()
                }.sanitizeCell()
                val formattedBuiltYear = getFormattedBuiltYear().sanitizeCell()
                val formattedTypeOfSale = getFormattedTypeOfSale().sanitizeCell()

                return "$formattedContractDate,$block,$formattedUnit,$price,$formattedSqft,$psf,$formattedBuiltYear,$formattedTypeOfSale"
            }
        }

        companion object {
            private fun String.sanitizeCell(): String = run {
                remove(",").getEmptyIfDash()
            }

            fun getGroupHdbCsvTitle(context: Context): String {
                val dateSold = context.getString(R.string.label_column_transaction_date_sold)
                val address = context.getString(R.string.label_column_transaction_address)
                val unit = context.getString(R.string.label_column_transaction_unit)
                val price = context.getString(R.string.label_column_transaction_price)
                val sizeSqm = context.getString(R.string.label_column_transaction_size_sqm)
                val psf = context.getString(R.string.label_column_transaction_psf)
                val builtYear = context.getString(R.string.label_column_transaction_built_year)
                val cov = context.getString(R.string.label_column_transaction_cov)

                return "$dateSold,$address,$unit,$price,$sizeSqm,$psf,$builtYear,$cov"
            }

            fun getGroupNlpCsvTitle(context: Context): String {
                val dateSold = context.getString(R.string.label_column_transaction_date_sold)
                val address = context.getString(R.string.label_column_transaction_address)
                val unit = context.getString(R.string.label_column_transaction_unit)
                val price = context.getString(R.string.label_column_transaction_price)
                val sizeSqft = context.getString(R.string.label_column_transaction_size_sqft)
                val psf = context.getString(R.string.label_column_transaction_psf)
                val builtYear = context.getString(R.string.label_column_transaction_built_year)
                val typeOfSale = context.getString(R.string.label_column_transaction_transaction)

                return "$dateSold,$address,$unit,$price,$sizeSqft,$psf,$builtYear,$typeOfSale"
            }

            fun getGroupLandedCsvTitle(context: Context): String {
                val dateSold = context.getString(R.string.label_column_transaction_date_sold)
                val address = context.getString(R.string.label_column_transaction_address)
                val price = context.getString(R.string.label_column_transaction_price)
                val sizeSqft = context.getString(R.string.label_column_transaction_size_sqft)
                val psf = context.getString(R.string.label_column_transaction_psf)
                val builtYear = context.getString(R.string.label_column_transaction_built_year)
                val typeOfSale = context.getString(R.string.label_column_transaction_transaction)

                return "$dateSold,$address,$price,$sizeSqft,$psf,$builtYear,$typeOfSale"
            }

            fun getGroupCommercialCsvTitle(context: Context): String {
                val dateSold = context.getString(R.string.label_column_transaction_date_sold)
                val address = context.getString(R.string.label_column_transaction_address)
                val unit = context.getString(R.string.label_column_transaction_unit)
                val price = context.getString(R.string.label_column_transaction_price)
                val sizeSqft = context.getString(R.string.label_column_transaction_size_sqft)
                val psf = context.getString(R.string.label_column_transaction_psf)
                val builtYear = context.getString(R.string.label_column_transaction_built_year)
                val transaction = context.getString(R.string.label_column_transaction_transaction)

                return "$dateSold,$address,$unit,$price,$sizeSqft,$psf,$builtYear,$transaction"
            }

            fun getProjectRealTimeHdbCsvTitle(context: Context): String {
                val dateSold = context.getString(R.string.label_column_transaction_date_sold)
                val block = context.getString(R.string.label_column_transaction_block)
                val unit = context.getString(R.string.label_column_transaction_unit)
                val price = context.getString(R.string.label_column_transaction_price)
                val sizeSqm = context.getString(R.string.label_column_transaction_size_sqm)
                val psf = context.getString(R.string.label_column_transaction_psf)
                val builtYear = context.getString(R.string.label_column_transaction_built_year)

                return "$dateSold,$block,$unit,$price,$sizeSqm,$psf,$builtYear"
            }

            fun getProjectRealTimeNlpCsvTitle(context: Context): String {
                val dateSold = context.getString(R.string.label_column_transaction_date_sold)
                val block = context.getString(R.string.label_column_transaction_block)
                val unit = context.getString(R.string.label_column_transaction_unit)
                val price = context.getString(R.string.label_column_transaction_price)
                val sizeSqft = context.getString(R.string.label_column_transaction_size_sqft)
                val psf = context.getString(R.string.label_column_transaction_psf)
                val builtYear = context.getString(R.string.label_column_transaction_built_year)
                val transaction = context.getString(R.string.label_column_transaction_transaction)

                return "$dateSold,$block,$unit,$price,$sizeSqft,$psf,$builtYear,$transaction,"
            }

            fun getProjectRealTimeLandedCsvTitle(context: Context): String {
                val dateSold = context.getString(R.string.label_column_transaction_date_sold)
                val address = context.getString(R.string.label_column_transaction_address)
                val price = context.getString(R.string.label_column_transaction_price)
                val sizeSqft = context.getString(R.string.label_column_transaction_size_sqft)
                val psf = context.getString(R.string.label_column_transaction_psf)
                val builtYear = context.getString(R.string.label_column_transaction_built_year)
                val transaction = context.getString(R.string.label_column_transaction_transaction)
                val landType = context.getString(R.string.label_column_transaction_area_type)

                return "$dateSold,$address,$price,$sizeSqft,$psf,$landType,$builtYear,$transaction"
            }

            fun getProjectRealTimeCommercialCsvTitle(context: Context): String {
                val dateSold = context.getString(R.string.label_column_transaction_date_sold)
                val block = context.getString(R.string.label_column_transaction_block)
                val unit = context.getString(R.string.label_column_transaction_unit)
                val price = context.getString(R.string.label_column_transaction_price)
                val sizeSqft = context.getString(R.string.label_column_transaction_size_sqft)
                val psf = context.getString(R.string.label_column_transaction_psf)
                val builtYear = context.getString(R.string.label_column_transaction_built_year)
                val transaction = context.getString(R.string.label_column_transaction_transaction)

                return "$dateSold,$block,$unit,$price,$sizeSqft,$psf,$builtYear,$transaction"
            }
        }
    }
}