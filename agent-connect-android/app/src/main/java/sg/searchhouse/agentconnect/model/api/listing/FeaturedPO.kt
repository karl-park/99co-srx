package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class FeaturedPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("category")
    val category: Int
) {
    //TODO: NOTE.. will move to listing enum
    enum class Category(val value: Int) {
        FEATURED(1), FEATURED_PLUS(2)
    }
}
