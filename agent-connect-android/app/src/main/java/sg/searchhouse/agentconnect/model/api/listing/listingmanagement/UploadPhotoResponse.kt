package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName

data class UploadPhotoResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("photo")
    val photo: PostListingPhotoPO
)