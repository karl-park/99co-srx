package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.PropertySubType.*
import java.util.*

@Suppress("unused")
class ListingEnum {
    enum class PropertyPurpose(
        val propertyMainTypes: List<PropertyMainType>?,
        val propertySubTypes: List<PropertySubType>?
    ) {
        RESIDENTIAL(
            listOf(
                PropertyMainType.RESIDENTIAL,
                PropertyMainType.HDB,
                PropertyMainType.CONDO,
                PropertyMainType.LANDED
            ), null
        ),
        COMMERCIAL(
            null,
            listOf(
                ALL_COMMERCIAL,
                RETAIL,
                OFFICE,
                FACTORY,
                WAREHOUSE,
                LAND,
                HDB_SHOP_HOUSE,
                SHOP_HOUSE
            )
        )
    }

    enum class OwnershipType(val value: String, @StringRes val label: Int) {
        SALE("S", R.string.label_sale),
        RENT("R", R.string.label_rent),
        ROOM_RENTAL("ROOM_RENTAL", R.string.label_room_rental)
    }

    enum class RentalType(val value: Boolean?, @StringRes val label: Int) {
        ANY(null, R.string.label_rental_type_any),
        ENTIRE_HOUSE(false, R.string.label_rental_type_entire_house),
        ROOM_RENTAL(true, R.string.label_rental_type_room_rental)
    }

    enum class ListingGroup(val value: String) {
        SRX_STP("srxstp"),
        TABLE_LP("tableLP"),
        MCLP_ALL_MATCH_SEARCH_TERM("mclpAllMatchSearchTerm"),
        MCLP_ALL_NEARBY("mclpAllNearby")
    }

    enum class PropertyMainType(
        @StringRes val label: Int,
        @StringRes val labelPlural: Int,
        @StringRes val labelCount: Int,
        @DrawableRes val iconResId: Int,
        val propertySubTypes: List<PropertySubType>,
        val primarySubType: PropertySubType
    ) {
        RESIDENTIAL(
            R.string.property_type_all,
            R.string.property_type_plural_all,
            R.string.property_type_with_count_residential,
            R.drawable.ic_property_type_all,
            listOf(
                HDB_3_ROOMS,
                HDB_4_ROOMS,
                HDB_5_ROOMS,
                HDB_EXECUTIVE,
                HDB_1_ROOM,
                HDB_2_ROOMS,
                HDB_HUDC,
                HDB_JUMBO,
                CONDOMINIUM,
                APARTMENT,
                TERRACE,
                SEMI_DETACHED,
                DETACHED
            )
            ,
            ALL_PRIVATE_RESIDENTIAL
        ),
        HDB(
            R.string.property_type_hdb, R.string.property_type_plural_hdb,
            R.string.property_type_with_count_hdb,
            R.drawable.ic_property_type_hdb, listOf(
                HDB_3_ROOMS,
                HDB_4_ROOMS,
                HDB_5_ROOMS,
                HDB_EXECUTIVE,
                HDB_1_ROOM,
                HDB_2_ROOMS,
                HDB_HUDC,
                HDB_JUMBO
            )
            , ALL_HDB
        ),
        CONDO(
            R.string.property_type_condo,
            R.string.property_type_plural_condo,
            R.string.property_type_with_count_condo,
            R.drawable.ic_property_type_condo,
            listOf(
                CONDOMINIUM, APARTMENT
            )
            ,
            CONDO_APT
        ),
        LANDED(
            R.string.property_type_landed,
            R.string.property_type_plural_landed,
            R.string.property_type_with_count_landed,
            R.drawable.ic_property_type_landed,
            listOf(
                TERRACE, SEMI_DETACHED, DETACHED
            )
            ,
            ALL_LANDED
        ),
        COMMERCIAL(
            R.string.property_type_commercial,
            R.string.property_type_plural_commercial,
            R.string.property_type_with_count_commercial,
            R.drawable.ic_property_type_factory,
            listOf(
                RETAIL, OFFICE, FACTORY, WAREHOUSE, LAND, HDB_SHOP_HOUSE, SHOP_HOUSE
            )
            ,
            ALL_COMMERCIAL
        )
    }

    //cdResearchSubType
    enum class PropertySubType(
        val type: Int, @StringRes val label: Int, @StringRes val labelPlural: Int,
        @StringRes val labelWithCount: Int,
        val iconResId: Int = 0,
        val isBasic: Boolean = true
    ) {
        HDB_3_ROOMS(
            1,
            R.string.listing_sub_type_hdb_3_rooms,
            R.string.listing_sub_type_plural_hdb_3_rooms,
            R.string.listing_sub_type_with_count_hdb_3_rooms
        ),
        HDB_4_ROOMS(
            2,
            R.string.listing_sub_type_hdb_4_rooms,
            R.string.listing_sub_type_plural_hdb_4_rooms,
            R.string.listing_sub_type_with_count_hdb_4_rooms
        ),
        HDB_5_ROOMS(
            3,
            R.string.listing_sub_type_hdb_5_rooms,
            R.string.listing_sub_type_plural_hdb_5_rooms,
            R.string.listing_sub_type_with_count_hdb_5_rooms
        ),
        HDB_EXECUTIVE(
            4,
            R.string.listing_sub_type_hdb_executive,
            R.string.listing_sub_type_plural_hdb_executive,
            R.string.listing_sub_type_with_count_hdb_executive
        ),
        TERRACE(
            5,
            R.string.listing_sub_type_terrace,
            R.string.listing_sub_type_plural_terrace,
            R.string.listing_sub_type_with_count_terrace
        ),
        CONDOMINIUM(
            6,
            R.string.listing_sub_type_condominium,
            R.string.listing_sub_type_plural_condominium,
            R.string.listing_sub_type_with_count_condominium
        ),
        APARTMENT(
            7,
            R.string.listing_sub_type_apartment,
            R.string.listing_sub_type_plural_apartment,
            R.string.listing_sub_type_with_count_apartment
        ),
        SEMI_DETACHED(
            8,
            R.string.listing_sub_type_semi_detached,
            R.string.listing_sub_type_plural_semi_detached,
            R.string.listing_sub_type_with_count_semi_detached
        ),
        DETACHED(
            10,
            R.string.listing_sub_type_detached,
            R.string.listing_sub_type_plural_detached,
            R.string.listing_sub_type_with_count_detached
        ),

        HDB_1_ROOM(
            16,
            R.string.listing_sub_type_hdb_1_room,
            R.string.listing_sub_type_plural_hdb_1_room,
            R.string.listing_sub_type_with_count_hdb_1_room
        ),
        HDB_2_ROOMS(
            17,
            R.string.listing_sub_type_hdb_2_rooms,
            R.string.listing_sub_type_plural_hdb_2_rooms,
            R.string.listing_sub_type_with_count_hdb_2_rooms
        ),
        HDB_HUDC(
            18,
            R.string.listing_sub_type_hdb_hudc,
            R.string.listing_sub_type_plural_hdb_hudc,
            R.string.listing_sub_type_with_count_hdb_hudc
        ),
        HDB_JUMBO(
            19,
            R.string.listing_sub_type_hdb_jumbo,
            R.string.listing_sub_type_plural_hdb_jumbo,
            R.string.listing_sub_type_with_count_hdb_jumbo
        ),

        OFFICE(
            11,
            R.string.listing_sub_type_office,
            R.string.listing_sub_type_plural_office,
            R.string.listing_sub_type_with_count_office,
            R.drawable.ic_property_type_office
        ),
        WAREHOUSE(
            14,
            R.string.listing_sub_type_warehouse,
            R.string.listing_sub_type_plural_warehouse,
            R.string.listing_sub_type_with_count_warehouse,
            R.drawable.ic_property_type_warehouse
        ),
        SHOP_HOUSE(
            13,
            R.string.listing_sub_type_shop_house,
            R.string.listing_sub_type_plural_shop_house,
            R.string.listing_sub_type_with_count_shop_house,
            R.drawable.ic_property_type_shophouse
        ),
        RETAIL(
            12,
            R.string.listing_sub_type_retail,
            R.string.listing_sub_type_plural_retail,
            R.string.listing_sub_type_with_count_retail,
            R.drawable.ic_property_type_retail
        ),
        LAND(
            21,
            R.string.listing_sub_type_land,
            R.string.listing_sub_type_plural_land,
            R.string.listing_sub_type_with_count_land,
            R.drawable.ic_property_type_land
        ),
        FACTORY(
            15,
            R.string.listing_sub_type_factory,
            R.string.listing_sub_type_plural_factory,
            R.string.listing_sub_type_with_count_factory,
            R.drawable.ic_property_type_factory
        ),
        HDB_SHOP_HOUSE(
            20,
            R.string.listing_sub_type_hdb_shop_house,
            R.string.listing_sub_type_plural_hdb_shop_house,
            R.string.listing_sub_type_with_count_hdb_shop_house,
            R.drawable.ic_property_type_hdb_shophouse
        ),


        ALL_PRIVATE_RESIDENTIAL(
            80,
            R.string.listing_sub_type_all_private_residential,
            R.string.listing_sub_type_plural_all_private_residential,
            R.string.listing_sub_type_with_count_all_private_residential,
            isBasic = false
        ),
        ALL_HDB(
            81,
            R.string.listing_sub_type_all_hdb,
            R.string.listing_sub_type_plural_all_hdb,
            R.string.listing_sub_type_with_count_all_hdb,
            isBasic = false
        ),
        ALL_COMMERCIAL(
            82,
            R.string.listing_sub_type_all_commercial,
            R.string.listing_sub_type_plural_all_commercial,
            R.string.listing_sub_type_with_count_all_commercial,
            R.drawable.ic_property_type_all_2,
            isBasic = false
        ),
        ALL_LANDED(
            85,
            R.string.listing_sub_type_all_landed,
            R.string.listing_sub_type_plural_all_landed,
            R.string.listing_sub_type_with_count_all_landed,
            isBasic = false
        ),
        CONDO_APT(
            86,
            R.string.listing_sub_type_condo_apt,
            R.string.listing_sub_type_plural_condo_apt,
            R.string.listing_sub_type_with_count_condo_apt,
            isBasic = false
        ),
        URA_INDUSTRIAL(
            87,
            R.string.listing_sub_type_ura_industrial,
            R.string.listing_sub_type_plural_ura_industrial,
            R.string.listing_sub_type_with_count_ura_industrial,
            isBasic = false
        ),
    }

    enum class Model(val model: String) {
        //value to send backend model is all capital letter
        EXECUTIVE_CONDOMINIUM("EXECUTIVE CONDOMINIUM"),
        WALK_UP_APT("WALK-UP APT"),
        CLUSTER_HOUSE("CLUSTER HOUSE")
    }

    enum class OrderCriteria(val value: String, @StringRes val label: Int) {
        DEFAULT("Default", R.string.listing_order_criteria_default),
        DATE_POSTED_ASC("DatePostedAsc", R.string.listing_order_criteria_date_posted_asc),
        DATE_POSTED_DESC("DatePostedDesc", R.string.listing_order_criteria_date_posted_desc),

        DISTANCE_ASC("DistanceAsc", R.string.listing_order_criteria_distance_asc),
        DISTANCE_DESC("DistanceDesc", R.string.listing_order_criteria_distance_desc),

        PRICE_ASC("PriceAsc", R.string.listing_order_criteria_price_asc),
        PRICE_DESC("PriceDesc", R.string.listing_order_criteria_price_desc),
        SIZE_ASC("SizeAsc", R.string.listing_order_criteria_size_asc),
        SIZE_DESC("SizeDesc", R.string.listing_order_criteria_size_desc),

        PSF_ASC("PsfAsc", R.string.listing_order_criteria_psf_asc),
        PSF_DESC("PsfDesc", R.string.listing_order_criteria_psf_desc),
        NUM_BEDROOMS_ASC("NumBedroomsAsc", R.string.listing_order_criteria_num_bedrooms_asc),
        NUM_BEDROOMS_DESC("NumBedroomsDesc", R.string.listing_order_criteria_num_bedrooms_desc),
        LISTING_QUALITY_ASC(
            "ListingQualityAsc",
            R.string.listing_order_criteria_listing_quality_asc
        ),
        LISTING_QUALITY_DESC(
            "ListingQualityDesc",
            R.string.listing_order_criteria_listing_quality_desc
        )
    }

    enum class Tenure(
        val value: Int,
        @StringRes val label: Int,
        @StringRes val labelWithCount: Int,
        @StringRes val labelFull: Int
    ) {
        NOT_SPECIFIED(
            0,
            R.string.tenure_label_not_specified,
            R.string.tenure_label_with_count_not_specified,
            R.string.tenure_label_full_not_specified
        ),
        FREEHOLD(
            1,
            R.string.tenure_label_freehold,
            R.string.tenure_label_with_count_freehold,
            R.string.tenure_label_full_freehold
        ),
        LEASEHOLD(
            2,
            R.string.tenure_label_leasehold,
            R.string.tenure_label_with_count_leasehold,
            R.string.tenure_label_full_leasehold
        ),
        NINE_NINE_NINE_YEARS(
            3,
            R.string.tenure_label_nine_nine_nine_years,
            R.string.tenure_label_with_count_nine_nine_nine_years,
            R.string.tenure_label_full_nine_nine_nine_years
        ), // rare
        THIRTY_YEARS(
            4,
            R.string.tenure_label_thirty_years,
            R.string.tenure_label_with_count_thirty_years,
            R.string.tenure_label_full_thirty_years
        ),
        SIXTY_YEARS(
            5,
            R.string.tenure_label_sixty_years,
            R.string.tenure_label_with_count_sixty_years,
            R.string.tenure_label_full_sixty_years
        ),
        ONE_HUNDRED_THREE_YEARS(
            6,
            R.string.tenure_label_one_hundred_three_years,
            R.string.tenure_label_with_count_one_hundred_three_years,
            R.string.tenure_label_full_one_hundred_three_years
        ) // rare
    }

    enum class Furnish(val value: String, @StringRes val label: Int) {
        FULL("FULL", R.string.label_furnish_full), HALF(
            "HALF",
            R.string.label_furnish_half
        ),
        NOT("NOT", R.string.label_furnish_not),
    }

    enum class Floor(@StringRes val label: Int, val value: String) {
        GROUND(R.string.label_floor_ground, "ground"),
        LOW(R.string.label_floor_low, "low"),
        MID(R.string.label_floor_mid, "mid"),
        HIGH(R.string.label_floor_high, "high"),
        PENTHOUSE(R.string.label_floor_penthouse, "penthouse")
    }

    enum class LeaseTermOption(val value: Int) {
        ONE_TO_TWELVE_MONTHS(1),
        TWELVE_TO_TWENTY_FOUR_MONTHS(2),
        MORE_THAN_TWENTY_FOUR_MONTHS(3),
        FLEXIBLE(4)
    }

    enum class ProjectLaunchStatus(val value: Int) {
        NEW_LAUNCH(1), RESALE(2), EXCLUDE_NEW_LAUNCHES(3)
    }

    enum class BedroomCount(@StringRes val label: Int, val value: Int) {
        ANY(R.string.room_count_any, -1),
        ONE(R.string.room_count_one, 1),
        TWO(R.string.room_count_two, 2),
        THREE(R.string.room_count_three, 3),
        FOUR(R.string.room_count_four, 4),
        FIVE(R.string.room_count_five, 5),
        SIX_AND_ABOVE(R.string.room_count_six_and_above, 6)
    }

    enum class BathroomCount(@StringRes val label: Int, val value: Int) {
        ANY(R.string.room_count_any, -1),
        ONE(R.string.room_count_one, 1),
        TWO(R.string.room_count_two, 2),
        THREE(R.string.room_count_three, 3),
        FOUR(R.string.room_count_four, 4),
        FIVE(R.string.room_count_five, 5),
        SIX_AND_ABOVE(R.string.room_count_six_and_above, 6)
    }

    enum class PropertyAge(
        @StringRes val label: Int, @StringRes val labelWithCount: Int, val from: Int,
        val to: Int
    ) {
        ZERO_TO_FIVE_YEARS_OLD(
            R.string.property_age_zero_to_five_years_old,
            R.string.property_age_with_count_zero_to_five_years_old,
            0,
            5
        ),
        FIVE_TO_TEN_YEARS_OLD(
            R.string.property_age_five_to_ten_years_old,
            R.string.property_age_with_count_five_to_ten_years_old,
            5,
            10
        ),
        TEN_TO_TWENTY_YEARS_OLD(
            R.string.property_age_ten_to_twenty_years_old,
            R.string.property_age_with_count_ten_to_twenty_years_old,
            10,
            20
        ),
        TWENTY_TO_THIRTY_YEARS_OLD(
            R.string.property_age_twenty_to_thirty_years_old,
            R.string.property_age_with_count_twenty_to_thirty_years_old,
            20,
            30
        ),
        THIRTY_TO_FORTY_YEARS_OLD(
            R.string.property_age_more_than_thirty_years_old,
            R.string.property_age_with_count_more_than_thirty_years_old,
            31,
            500
        )
    }

    enum class VideoViewingIndicator(val value: Boolean, @StringRes val label: Int) {
        AVAILABLE(true, R.string.listing_video_viewing_available),
        NOT_AVAILABLE(false, R.string.listing_video_viewing_unavailable)
    }

    // unitTime is from Calendar, e.g. Calendar.MONTH
    enum class MinDateFirstPosted(val value: Int, val unitTime: Int, @StringRes val label: Int) {
        ANY(-1, -1, R.string.label_date_first_posted_any),
        WITHIN_THREE_DAYS(
            3,
            Calendar.DAY_OF_YEAR,
            R.string.label_date_first_posted_within_three_days
        ),
        WITHIN_ONE_WEEK(1, Calendar.WEEK_OF_YEAR, R.string.label_date_first_posted_within_one_week),
        WITHIN_TWO_WEEKS(
            2,
            Calendar.WEEK_OF_YEAR,
            R.string.label_date_first_posted_within_two_weeks
        ),
        WITHIN_ONE_MONTH(1, Calendar.MONTH, R.string.label_date_first_posted_within_one_month),
    }

    enum class ListingType(val value: String, @StringRes val label: Int) {
        SRX_LISTING("A", R.string.label_listing_type_srx_listing),
        PUBLIC_LISTING("P", R.string.label_listing_type_public_listing)
    }

    enum class XValueDisplayInd(val value: String) {
        HIDE("0"),
        SHOW("1")
    }

    enum class ExportOption(@StringRes val label: Int) {
        PDF(R.string.tab_export_listings_pdf), EXCEL(R.string.tab_export_listings_excel)
    }

    enum class ListingStatus(val value: String?) {
        VERIFIED("V"), TRANSACTED("S"), DELETED("D"), NOT_APPLICABLE(null)
    }
}