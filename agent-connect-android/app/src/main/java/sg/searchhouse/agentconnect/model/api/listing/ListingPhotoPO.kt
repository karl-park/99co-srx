package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ListingPhotoPO(
    @SerializedName("listingImageLinks")
    val listingImageLinks: List<PhotoPO>,
    @SerializedName("floorplanPhotos")
    val floorplanPhotos: List<PhotoPO>
): Serializable