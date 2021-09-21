package sg.searchhouse.agentconnect.enumeration.api

import android.text.TextUtils
import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class TransactionEnum {
    enum class TransactionPropertyMainType(val value: Int, @StringRes val label: Int) {
        HDB(1, R.string.property_type_hdb),
        Condo(2, R.string.property_type_condo),
        Landed(3, R.string.property_type_landed),
        Commercial(4, R.string.property_type_commercial)
    }

    enum class ActivityType {
        BY_GROUP, BY_PROJECT
    }

    enum class SearchMode(val value: Int) {
        RESIDENTIAL(1),
        COMMERCIAL(2)
    }

    enum class AreaType(val value: Int?, @StringRes val label: Int) {
        ANY(null, R.string.label_any),
        STRATA(1, R.string.area_type_strata),
        LAND(2, R.string.area_type_land)
    }

    enum class Radius(val value: Int?, val radiusValue: Double, @StringRes val label: Int) {
        ANY(null, 0.0, R.string.label_any),
        HALF_KM(1, 0.5, R.string.filter_transaction_search_radius_half_km), ONE_KM(
            2,
            1.0,
            R.string.filter_transaction_search_radius_one_km
        ),
        ONE_AND_HALF_KM(3, 1.5, R.string.filter_transaction_search_radius_one_and_half_km), TWO_KM(
            4,
            2.0,
            R.string.filter_transaction_search_radius_two_km
        )
    }

    enum class TenureType(val value: Int?, @StringRes val label: Int) {
        ANY(null, R.string.label_any),
        FREEHOLD(1, R.string.filter_transaction_search_tenure_type_freehold),
        LEASEHOLD(2, R.string.filter_transaction_search_tenure_type_leasehold)
    }

    enum class SortType(val value: Int, @StringRes val label: Int) {
        DEFAULT(1, R.string.label_transaction_sort_type_default),
        CONTRACT_DATE_ASC(3, R.string.label_transaction_sort_type_contract_date_asc),
        CONTRACT_DATE_DESC(2, R.string.label_transaction_sort_type_contract_date_desc),
        BLOCK_ASC(5, R.string.label_transaction_sort_type_block_asc),
        BLOCK_DESC(4, R.string.label_transaction_sort_type_block_desc),
        UNIT_NO_ASC(6, R.string.label_transaction_sort_type_unit_no_asc),
        UNIT_NO_DESC(7, R.string.label_transaction_sort_type_unit_no_desc),
        PRICE_ASC(8, R.string.label_transaction_sort_type_price_asc),
        PRICE_DESC(9, R.string.label_transaction_sort_type_price_desc),
        PSF_ASC(10, R.string.label_transaction_sort_type_psf_asc),
        PSF_DESC(11, R.string.label_transaction_sort_type_psf_desc),
        SIZE_ASC(12, R.string.label_transaction_sort_type_size_asc),
        SIZE_DESC(13, R.string.label_transaction_sort_type_size_desc),
        BUILT_YEAR_ASC(15, R.string.label_transaction_sort_type_built_year_asc),
        BUILT_YEAR_DESC(14, R.string.label_transaction_sort_type_built_year_desc)
    }

    companion object {
        fun getSearchModeFromPropertyPurpose(propertyPurpose: ListingEnum.PropertyPurpose): SearchMode {
            return when (propertyPurpose) {
                ListingEnum.PropertyPurpose.COMMERCIAL -> SearchMode.COMMERCIAL
                ListingEnum.PropertyPurpose.RESIDENTIAL -> SearchMode.RESIDENTIAL
            }
        }

        // TODO: Maybe refactor, similar with ListingManagementEnum#OrderCriteria
        fun isSortTypeNeutral(sortType: SortType): Boolean {
            return getOppositeSortType(sortType) == null
        }

        private fun isSortTypeAsc(sortType: SortType): Boolean {
            return sortType.name.endsWith("ASC")
        }

        private fun isSortTypeDesc(sortType: SortType): Boolean {
            return sortType.name.endsWith("DESC")
        }

        fun getOppositeSortType(sortType: SortType): SortType? {
            return when {
                isSortTypeAsc(sortType) -> {
                    val oppositeValue = sortType.name.replace("ASC", "DESC")
                    SortType.values().find { TextUtils.equals(it.name, oppositeValue) }
                }
                isSortTypeDesc(sortType) -> {
                    val oppositeValue = sortType.name.replace("DESC", "ASC")
                    SortType.values().find { TextUtils.equals(it.name, oppositeValue) }
                }
                else -> null
            }
        }
    }
}