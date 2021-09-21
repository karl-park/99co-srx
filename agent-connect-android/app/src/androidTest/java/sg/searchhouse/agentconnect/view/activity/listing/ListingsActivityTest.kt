package sg.searchhouse.agentconnect.view.activity.listing

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.createIntent
import sg.searchhouse.agentconnect.dsl.readResourcesFile
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.app.SearchEntryType
import sg.searchhouse.agentconnect.helper.Json
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.*
import sg.searchhouse.agentconnect.view.helper.search.SearchResultActivityEntry

class ListingsActivityTest : BaseApiActivityTest() {
    private fun Intent.setupExtras() {
        putExtra(SearchResultActivityEntry.EXTRA_KEY_QUERY, "")
        putExtra(SearchResultActivityEntry.EXTRA_KEY_TITLE, "")
        putExtra(
            SearchResultActivityEntry.EXTRA_KEY_OWNERSHIP_TYPE,
            ListingEnum.OwnershipType.SALE
        )
        putExtra(
            SearchResultActivityEntry.EXTRA_KEY_PROPERTY_PURPOSE,
            ListingEnum.PropertyPurpose.RESIDENTIAL
        )
        putExtra(
            SearchResultActivityEntry.EXTRA_KEY_ENTRY_TYPE,
            SearchEntryType.QUERY
        )
    }

    private fun launchActivity(): ActivityScenario<ListingsActivity> {
        return user.launch(context.createIntent<ListingsActivity> {
            setupExtras()
        })
    }

    @Test
    fun givenValidInput_whenLaunchActivity_thenShowListItems() {
        launchActivity().run {
            Thread.sleep(1000)
            this expectSeeView R.id.list
        }
    }

    @Test
    fun whenClickBackButton_thenFinish() {
        launchActivity().run {
            user click R.id.btn_back
            expectFinishActivity()
        }
    }

    @Test
    fun whenClickFilterResultButton_thenGoToFilterListingActivity() {
        launchActivity().run {
            user click R.id.btn_filter_result
            this expectLaunch FilterListingActivity::class.java
        }
    }

    @Test
    fun whenClickSearchHeader_thenGoToSearchActivity() {
        launchActivity().run {
            user click R.id.btn_header
            this expectLaunch SearchActivity::class.java
        }
    }

    @Test
    fun whenClickSortButton_thenShowSortDialog() {
        launchActivity().run {
            user click R.id.btn_sort
            withText(R.string.dialog_title_listing_sort_order) expect isDisplayed()
        }
    }

    @Test
    fun whenClickExportListingsButton_thenActivateExportListingsMode() {
        launchActivity().run {
            user click R.id.btn_export_listings
            R.id.btn_submit_export expect isDisplayed()
        }
    }

    override fun getDispatcher() = dispatcher {
        when {
            this isGet ENDPOINT_GET_LISTINGS -> {
                // Find listings
                val response =
                    this@ListingsActivityTest.javaClass.readResourcesFile("response_listings.json")
                MockResponse().setResponseCode(200).setBody(response)
            }
            this isGet ENDPOINT_GET_HDB_COUNT -> {
                // HDB count
                MockResponse().setResponseCode(200).setBody(listingsCountResponse.toString())
            }
            this isGet ENDPOINT_GET_CONDO_COUNT -> {
                // Condo count
                MockResponse().setResponseCode(200).setBody(listingsCountResponse.toString())
            }
            this isGet ENDPOINT_GET_LANDED_COUNT -> {
                // Landed count
                MockResponse().setResponseCode(200).setBody(listingsCountResponse.toString())
            }
            this isGet ENDPOINT_GET_ALL_COUNT -> {
                // All listings count
                MockResponse().setResponseCode(200).setBody(listingsCountResponse.toString())
            }
            else -> {
                null
            }
        }
    }

    companion object {
        private const val ENDPOINT_GET_LISTINGS = "/api/v1/listings/search?action=findListings&startResultIndex=0&maxResults=50&wantedListingGroups=srxstp,tableLP&type=S&cdResearchSubTypes=1,2,3,4,16,17,18,19,6,7,5,8,10&orderCriteria=Default&isTransacted=false"
        private const val ENDPOINT_GET_HDB_COUNT = "/api/v1/listings/search?action=findListingsCount&wantedListingGroups=srxstp,tableLP&type=S&cdResearchSubTypes=1,2,3,4,16,17,18,19&isTransacted=false"
        private const val ENDPOINT_GET_CONDO_COUNT = "/api/v1/listings/search?action=findListingsCount&wantedListingGroups=srxstp,tableLP&type=S&cdResearchSubTypes=6,7&isTransacted=false"
        private const val ENDPOINT_GET_LANDED_COUNT = "/api/v1/listings/search?action=findListingsCount&wantedListingGroups=srxstp,tableLP&type=S&cdResearchSubTypes=5,8,10&isTransacted=false"
        private const val ENDPOINT_GET_ALL_COUNT = "/api/v1/listings/search?action=findListingsCount&wantedListingGroups=srxstp,tableLP&type=S&cdResearchSubTypes=1,2,3,4,16,17,18,19,6,7,5,8,10&isTransacted=false"

        val listingsCountResponse = Json {
            "result" to "success"
            "total" to 1234
        }
    }
}