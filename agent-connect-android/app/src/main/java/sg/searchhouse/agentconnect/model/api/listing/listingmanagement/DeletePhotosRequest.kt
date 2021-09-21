package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName

data class DeletePhotosRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("photosSubmitted")
    val photosSubmitted: List<PostListingPhotoPO>?,
    @SerializedName("floorPlanPhotosSubmitted")
    val floorPlanPhotosSubmitted: List<PostListingPhotoPO>?,
    @SerializedName("projectPhotos")
    val projectPhotos: List<PostListingPhotoPO>?
)