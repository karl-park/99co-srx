package sg.searchhouse.agentconnect.enumeration.api

import android.text.TextUtils
import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.PropertySubType
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.PropertySubType.*
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.Tenure
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.CreateListingTenure.*

class ListingManagementEnum {

    enum class PropertyClassification(
        val value: Int, @StringRes val label: Int,
        val propertyTypes: List<PropertySubType>,
        val tenures: List<CreateListingTenure>
    ) {
        HDB(
            1, R.string.property_classification_hdb, listOf(
                HDB_1_ROOM,
                HDB_2_ROOMS,
                HDB_3_ROOMS,
                HDB_4_ROOMS,
                HDB_5_ROOMS,
                HDB_EXECUTIVE,
                HDB_HUDC,
                HDB_JUMBO
            ),
            listOf(
                LEASEHOLD_NINE_NINE_YEARS,
                LEASEHOLD_SIXTY_YEARS,
                LEASEHOLD_THIRTY_YEARS
            )
        ),
        PRIVATE_RESIDENTIAL(
            2, R.string.property_classification_private_residential, listOf(
                CONDOMINIUM,
                APARTMENT,
                TERRACE,
                SEMI_DETACHED,
                DETACHED
            ),
            listOf(
                FREEHOLD,
                LEASEHOLD_NINE_NINE_YEARS,
                NINE_NINE_NINE_YEARS,
                LEASEHOLD_THIRTY_YEARS,
                LEASEHOLD_SIXTY_YEARS,
                LEASEHOLD_ONE_HUNDRED_THREE_YEARS
            )
        ),
        COMMERCIAL_INDUSTRIAL(
            3, R.string.property_classification_commercial_industrial, listOf(
                OFFICE,
                RETAIL,
                SHOP_HOUSE,
                HDB_SHOP_HOUSE,
                WAREHOUSE,
                LAND,
                FACTORY
            ),
            listOf(
                FREEHOLD,
                LEASEHOLD_NINE_NINE_YEARS,
                NINE_NINE_NINE_YEARS,
                LEASEHOLD_THIRTY_YEARS,
                LEASEHOLD_SIXTY_YEARS,
                LEASEHOLD_ONE_HUNDRED_THREE_YEARS
            )
        )
    }

    enum class CreateListingTenure(val value: Int, @StringRes val label: Int) {
        FREEHOLD(Tenure.FREEHOLD.value, R.string.tenure_label_freehold),
        LEASEHOLD_NINE_NINE_YEARS(Tenure.LEASEHOLD.value, R.string.tenure_label_full_leasehold),
        NINE_NINE_NINE_YEARS(
            Tenure.NINE_NINE_NINE_YEARS.value,
            R.string.tenure_label_nine_nine_nine_years
        ),
        LEASEHOLD_THIRTY_YEARS(Tenure.THIRTY_YEARS.value, R.string.tenure_label_full_thirty_years),
        LEASEHOLD_SIXTY_YEARS(Tenure.SIXTY_YEARS.value, R.string.tenure_label_full_sixty_years),
        LEASEHOLD_ONE_HUNDRED_THREE_YEARS(
            Tenure.ONE_HUNDRED_THREE_YEARS.value,
            R.string.tenure_label_full_one_hundred_three_years
        )
    }

    enum class PriceOption(@StringRes val value: Int) {
        NEGOTIABLE(R.string.price_option_negotiable),
        GUIDE_PRICE(R.string.price_option_guide_price),
        OFFER_IN_EXCESS(R.string.price_option_offer_in_excess),
        VIEW_TO_OFFER(R.string.price_option_view_to_offer),
    }

    enum class TenancyStatus(val value: String, @StringRes val label: Int) {
        TENANTED("Yes", R.string.tenancy_status_tenanted),
        NOT_TENANTED("No", R.string.tenancy_status_not_tenanted)
    }

    enum class RoomType(val value: Int, @StringRes val label: Int) {
        MASTER(0, R.string.room_type_master),
        COMMON(1, R.string.room_type_common)
    }

    //FOR COMMERCIAL -> is room rental
    enum class RentalType(val value: String, @StringRes val label: Int) {
        WHOLE_UNIT("false", R.string.rental_type_whole_unit),
        SUBLET("true", R.string.rental_type_sublet)
    }

    enum class OwnerType(val value: Int, @StringRes val label: Int) {
        NOT_AVAILABLE(0, R.string.owner_type_not_available),
        PRIVATE(1, R.string.owner_type_private),
        GOVERNMENT(2, R.string.owner_type_government)
    }

    enum class LeaseTerm(val value: Int, @StringRes val label: Int) {
        FLEXIBLE(0, R.string.post_listing_lease_term_flexible),
        SIX_MONTHS(6, R.string.post_listing_lease_term_six_months),
        ONE_YEAR(12, R.string.post_listing_lease_term_one_year),
        TWO_YEARS(24, R.string.post_listing_lease_term_two_years),
        TWO_YEARS_ABOVE(1000000, R.string.post_listing_lease_term_two_years_and_above)
    }

    enum class AssignmentType(val value: String, @StringRes val label: Int) {
        TAKEOVER("true", R.string.assignment_type_takeover),
        NO_TAKEOVER("false", R.string.assignment_type_no_takeover)
    }

    enum class FurnishLevel(val value: String, @StringRes val label: Int) {
        BARE("1", R.string.furnish_level_bare),
        PARTIALLY_FITTED("2", R.string.furnish_level_partially_fitted),
        FULLY_FITTED("3", R.string.furnish_level_fully_fitted)
    }

    enum class ShowHideXValueIndicator(val value: String, val booleanValue: Boolean) {
        HIDE("0", false),
        SHOW("1", true)
    }

    //Listing creation from portal listings
    enum class AppPortalType(val value: Int, @StringRes val portalListingId: Int) {
        PROPERTY_GURU(1, R.string.label_portal_type_pg_id),
        PORTAL(2, R.string.label_portal_type_portal_id),
        AGENCY(3, R.string.label_portal_type_agency_id)
    }

    enum class PortalType(val value: Int) {
        PROPERTY_GURU(1),
        NINETY_NINE_CO(2)
    }

    enum class PortalMode(val value: String) {
        SERVER("server"),
        CLIENT("client")
    }

    enum class PortalListingsType(val value: Int, @StringRes val label: Int) {
        ALL_LISTINGS(1, R.string.label_portal_listings_show_all),
        IMPORTED_LISTING(2, R.string.label_portal_listings_imported)
    }

    enum class PortalSyncOption(val value: Int) {
        AUTO(1),
        MANUAL(2)
    }

    enum class PortalSyncIndicator(val value: Int) {
        SYNC_NONE(0),
        SYNC_ENABLED(1),
        SYNC_DISABLED(2)
    }

    enum class PortalImportSummarySource(val value: Int) {
        PORTAL_PREVIEW_SCREEN(1),
        PORTAL_MAIN_SCREEN(2)
    }

    enum class AddressSearchSource {
        CREATE_LISTING,
        MAIN_SCREEN
    }

    enum class ListingManagementPurpose {
        CREATE,
        UPDATE
    }

    enum class ListingPhotoType {
        LISTING_IMAGE,
        FLOOR_PLAN,
        PROJECT_PHOTOS
    }

    // MY Listings
    enum class OrderCriteria(val value: String, @StringRes val label: Int) {
        DEFAULT("Default", R.string.label_my_listing_order_criteria_default),
        LISTING_QUALITY_ASC(
            "ListingQualityAsc",
            R.string.label_my_listing_order_criteria_listing_quality_asc
        ),
        LISTING_QUALITY_DESC(
            "ListingQualityDesc",
            R.string.label_my_listing_order_criteria_listing_quality_desc
        ),
        DATE_POSTED_ASC("DatePostedAsc", R.string.label_my_listing_order_criteria_date_posted_asc),
        DATE_POSTED_DESC(
            "DatePostedDesc",
            R.string.label_my_listing_order_criteria_date_posted_desc
        ),
        V_T360_ASC("VT360Asc", R.string.label_my_listing_order_criteria_v_t360_asc),
        SPV_ASC("SpvAsc", R.string.label_my_listing_order_criteria_spv_asc),
        DRONE_VIEW_ASC("DroneViewAsc", R.string.label_my_listing_order_criteria_drone_view_asc),
        CREATE_DATE_ASC("CreateDateAsc", R.string.label_my_listing_order_criteria_create_date_asc),
        CREATE_DATE_DESC(
            "CreateDateDesc",
            R.string.label_my_listing_order_criteria_create_date_desc
        )
    }

    enum class ListingGroup(val value: Int, @StringRes val label: Int) {
        ACTIVE(1, R.string.label_listing_group_active),
        DRAFTS(2, R.string.label_listing_group_drafts),
        EXPIRED(3, R.string.label_listing_group_expired),
        TAKEN_OFF(4, R.string.label_listing_group_taken_off),
        TRANSACTION(5, R.string.label_listing_group_transaction),
        CEA_FORM(128, R.string.label_listing_group_cea_form)
    }

    enum class SrxCreditMainType(
        val value: Int,
        @StringRes val label: Int,
        @StringRes val typeLabel: Int,
        @StringRes val description: Int,
        @StringRes val availableCreditLabel: Int,
        @StringRes val addOnLabel: Int
    ) {
        //TODO: will add another type options in next phase
        VALUATION(
            1,
            R.string.title_add_valuation,
            R.string.label_type_valuation,
            R.string.msg_add_on_drone,
            R.string.label_available_valuation_credit,
            0
        ),
        DRONE(
            2,
            R.string.title_add_drone,
            R.string.label_type_drone,
            R.string.msg_add_on_v360,
            R.string.label_available_drone_credit,
            0
        ),
        V360(
            3,
            R.string.title_add_v_360,
            R.string.label_type_v_360,
            R.string.msg_add_on_valuation,
            R.string.label_available_v360_credit,
            0
        ),
        FEATURED_LISTING(
            6,
            R.string.title_add_feature_listing,
            R.string.label_type_feature,
            R.string.msg_add_on_feature_listing,
            R.string.label_available_feature_listing_credit,
            R.string.label_add_on_feature_listing
        ),
        SPONSORED_POST(
            7,
            R.string.title_add_sponsor,
            R.string.label_type_sponsor,
            R.string.msg_add_on_sponsor,
            R.string.label_available_sponsored_listing_credit,
            R.string.label_add_on_sponsor_listing
        )
    }

    enum class ListingDraftMode(val value: Int, @StringRes val label: Int) {
        LISTING(1, R.string.label_my_listing_draft_listing),
        CEA_FORMS(2, R.string.label_my_listing_draft_cea_forms)
    }

    enum class QualityListingPhotoStatus(val value: Int) {
        PASS(1),
        FAIL(0)
    }

    enum class QualityListingAppealStatus(val value: Int, @StringRes val label: Int) {
        SUBMITTED(0, R.string.label_quality_listing_appeal_status_submitted),
        APPROVED(1, R.string.label_quality_listing_appeal_status_approve),
        REJECTED(2, R.string.label_quality_listing_appeal_status_rejected),
        KIV(3, R.string.label_quality_listing_appeal_status_kiv) //by API documentation
    }

    companion object {
        private fun isOrderCriteriaAsc(orderCriteria: OrderCriteria): Boolean {
            return orderCriteria.value.endsWith("Asc")
        }

        private fun isOrderCriteriaDesc(orderCriteria: OrderCriteria): Boolean {
            return orderCriteria.value.endsWith("Desc")
        }

        fun isOrderCriteriaNeutral(orderCriteria: OrderCriteria): Boolean {
            return getOppositeOrderCriteria(orderCriteria) == null
        }

        fun getOppositeOrderCriteria(orderCriteria: OrderCriteria): OrderCriteria? {
            return when {
                isOrderCriteriaAsc(orderCriteria) -> {
                    val oppositeValue = orderCriteria.value.replace("Asc", "Desc")
                    OrderCriteria.values().find { TextUtils.equals(it.value, oppositeValue) }
                }
                isOrderCriteriaDesc(orderCriteria) -> {
                    val oppositeValue = orderCriteria.value.replace("Desc", "Asc")
                    OrderCriteria.values().find { TextUtils.equals(it.value, oppositeValue) }
                }
                else -> null
            }
        }
    }

}