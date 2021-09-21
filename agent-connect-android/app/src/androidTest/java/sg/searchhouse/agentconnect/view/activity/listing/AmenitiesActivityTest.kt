package sg.searchhouse.agentconnect.view.activity.listing

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.createIntent
import sg.searchhouse.agentconnect.dsl.readResourcesFile
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.test.*
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher
import sg.searchhouse.agentconnect.test.dsl.expectResultCode
import sg.searchhouse.agentconnect.test.dsl.expectViewPagerPosition
import sg.searchhouse.agentconnect.test.dsl.isGet

class AmenitiesActivityTest : BaseApiActivityTest() {
    private fun launch(amenityOption: LocationEnum.AmenityOption = LocationEnum.AmenityOption.OTHERS): ActivityScenario<AmenitiesActivity> {
        val intent = context.createIntent<AmenitiesActivity> {
            putExtra(AmenitiesActivity.EXTRA_KEY_LISTING_ID, "87688591")
            putExtra(AmenitiesActivity.EXTRA_KEY_LISTING_TYPE, "A")
            putExtra(AmenitiesActivity.EXTRA_KEY_SELECTED_AMENITY_OPTION, amenityOption)
        }
        return user launch intent
    }

    @Test
    fun whenInputMrt_thenShowMrtFragment() {
        launch(amenityOption = LocationEnum.AmenityOption.MRT)
        R.id.view_pager expectViewPagerPosition 1
    }

    @Test
    fun whenInputHospital_thenShowHospitalFragment() {
        launch(amenityOption = LocationEnum.AmenityOption.MEDICAL)
        R.id.view_pager expectViewPagerPosition 6
    }

    @Test
    fun whenClickBackButton_thenFinish() {
        val scenario = launch()
        user click backButton
        scenario expectResultCode Activity.RESULT_CANCELED
    }

    override fun getDispatcher() = dispatcher {
        if (this isGet "/api/v1/listings/search?action=getListing&listingId=87688591&listingType=A") {
            val response =
                this@AmenitiesActivityTest.javaClass.readResourcesFile("response_amenities_listing.json")
            MockResponse().setResponseCode(200).setBody(response)
        } else {
            null
        }
    }
}