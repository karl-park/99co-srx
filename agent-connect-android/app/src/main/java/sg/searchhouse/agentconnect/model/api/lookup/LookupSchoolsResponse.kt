package sg.searchhouse.agentconnect.model.api.lookup

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R

data class LookupSchoolsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("schools")
    val schools: Schools
) {
    data class Schools(
        @SerializedName("International Schools")
        val internationalSchools: List<School>,
        @SerializedName("Polytechnic")
        val polytechnic: List<School>,
        @SerializedName("Primary Schools")
        val primarySchools: List<School>,
        @SerializedName("Private Schools")
        val privateSchools: List<School>,
        @SerializedName("Secondary Schools")
        val secondarySchools: List<School>,
        @SerializedName("Universities")
        val universities: List<School>
    ) {
        fun getAll(): List<School> {
            return internationalSchools + polytechnic + primarySchools + privateSchools + secondarySchools + universities
        }

        data class School(
            @SerializedName("description")
            val description: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("latitude")
            val latitude: Double,
            @SerializedName("longtitude")
            val longtitude: Double,
            @SerializedName("name")
            val name: String
        )

        // TODO: Probably have better way of looping properties
        enum class SchoolType(val value: String, @StringRes val label: Int) {
            INTERNATIONAL_SCHOOLS(
                "internationalSchools",
                R.string.school_type_international_schools
            ),
            POLYTECHNIC("polytechnic", R.string.school_type_polytechnic),
            PRIMARY_SCHOOLS("primarySchools", R.string.school_type_primary_schools),
            PRIVATE_SCHOOLS("privateSchools", R.string.school_type_private_schools),
            SECONDARY_SCHOOLS("secondarySchools", R.string.school_type_secondary_schools),
            UNIVERSITIES("universities", R.string.school_type_universities)
        }
    }
}