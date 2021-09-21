package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class LocationEnum {
    enum class HdbTown(val id: Int, @StringRes val label: Int) {
        ANGMOKIO( 1, R.string.hdb_town_name_angmokio),
        BEDOK(2, R.string.hdb_town_name_bedok),
        BISHAN(3, R.string.hdb_town_name_bishan),
        BUKIT_BATOK(4, R.string.hdb_town_name_bukit_batok),
        BUKIT_MERAH(5, R.string.hdb_town_name_bukit_merah),
        BUKIT_PANJANG(6, R.string.hdb_town_name_bukit_panjang),
        BUKIT_TIMAH(7, R.string.hdb_town_name_bukit_timah),
        CENTRAL_AREA(8, R.string.hdb_town_name_central_area),
        CHOA_CHU_KANG(9, R.string.hdb_town_name_choa_chu_kang),
        CLEMENTI(10, R.string.hdb_town_name_clementi),
        GEYLANG(11, R.string.hdb_town_name_geylang),
        HOUGANG(12, R.string.hdb_town_name_hougang),
        JURONG_EAST(13, R.string.hdb_town_name_jurong_east),
        JURONG_WEST(14, R.string.hdb_town_name_jurong_west),
        KALLANG(15, R.string.hdb_town_name_kallang),
        LIM_CHU_KANG(16, R.string.hdb_town_name_lim_chu_kang),
        MARINE_PARADE(17, R.string.hdb_town_name_marine_parade),
        PASIR_RIS(18, R.string.hdb_town_name_pasir_ris),
        PUNGGOL(19, R.string.hdb_town_name_punggol),
        QUEENSTOWN(20, R.string.hdb_town_name_queenstown),
        SEMBAWANG(21, R.string.hdb_town_name_sembawang),
        SENGKANG(22, R.string.hdb_town_name_sengkang),
        SERANGOON(23, R.string.hdb_town_name_serangoon),
        TAMPINES(24, R.string.hdb_town_name_tampines),
        TOAPAYOH(25, R.string.hdb_town_name_toapayoh),
        WOODLANDS(26, R.string.hdb_town_name_woodlands),
        YISHUN(27, R.string.hdb_town_name_yishun),
        TENGAH(28, R.string.hdb_town_name_tengah)
    }

    enum class District(val id: Int, @StringRes val label: Int) {
        D1(1, R.string.district_name_d1),
        D2(2, R.string.district_name_d2),
        D3(3, R.string.district_name_d3),
        D4(4, R.string.district_name_d4),
        D5(5, R.string.district_name_d5),
        D6(6, R.string.district_name_d6),
        D7(7, R.string.district_name_d7),
        D8(8, R.string.district_name_d8),
        D9(9, R.string.district_name_d9),
        D10(10, R.string.district_name_d10),
        D11(11, R.string.district_name_d11),
        D12(12, R.string.district_name_d12),
        D13(13, R.string.district_name_d13),
        D14(14, R.string.district_name_d14),
        D15(15, R.string.district_name_d15),
        D16(16, R.string.district_name_d16),
        D17(17, R.string.district_name_d17),
        D18(18, R.string.district_name_d18),
        D19(19, R.string.district_name_d19),
        D20(20, R.string.district_name_d20),
        D21(21, R.string.district_name_d21),
        D22(22, R.string.district_name_d22),
        D23(23, R.string.district_name_d23),
        D24(24, R.string.district_name_d24),
        D25(25, R.string.district_name_d25),
        D26(26, R.string.district_name_d26),
        D27(27, R.string.district_name_d27),
        D28(28, R.string.district_name_d28)
    }

    enum class AmenityOption(val value: String, @StringRes val label: Int) {
        OTHERS("Others", R.string.label_amenity_option_others),
        MRT("MRT", R.string.label_amenity_option_mrt),
        BUS("Bus", R.string.label_amenity_option_bus),
        SCHOOLS("Schools", R.string.label_amenity_option_schools),
        RETAIL("Retail", R.string.label_amenity_option_retail),
        RESTAURANT("Restaurant", R.string.label_amenity_option_restaurant),
        MEDICAL("Medical", R.string.label_amenity_option_medical),
        WORSHIP("Worship", R.string.label_amenity_option_worship)
    }

    // https://developers.google.com/maps/documentation/directions/intro#TravelModes
    enum class TravelMode(val value: String, @StringRes val label: Int, @DrawableRes val icon: Int) {
        DRIVING("driving", R.string.amenity_transport_drive, R.drawable.ic_car),
        WALKING("walking", R.string.amenity_transport_walk, R.drawable.ic_walk),
        TRANSIT("transit", R.string.amenity_transport_bus_mrt, R.drawable.ic_mrt)
    }

    // https://developers.google.com/places/web-service/supported_types
    enum class PlaceType(val value: String) {
        RESTAURANT("restaurant"),
        DOCTOR("doctor"),
        CHURCH("church"),
        MOSQUE("mosque")
    }
}