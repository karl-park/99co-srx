package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.ListingsSummaryResponse
import sg.searchhouse.agentconnect.event.dashboard.ViewMyListingsEvent
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class DashboardUserListingsViewModel constructor(application: Application) :
    ApiBaseViewModel<ListingsSummaryResponse>(application) {

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository
    @Inject
    lateinit var applicationContext: Context

    val activeSaleListingsTitle: LiveData<String> =
        Transformations.map(mainResponse) { mainResponse ->
            val totalSaleActiveListings =
                mainResponse?.saleListingsBreakdown?.sumBy { it.total } ?: return@map ""
            applicationContext.resources.getQuantityString(
                R.plurals.title_sale_listings_summary,
                totalSaleActiveListings,
                totalSaleActiveListings
            )
        }

    val activeRentListingsTitle: LiveData<String> =
        Transformations.map(mainResponse) { mainResponse ->
            val totalRentActiveListings =
                mainResponse?.rentListingsBreakdown?.sumBy { it.total } ?: return@map ""
            applicationContext.resources.getQuantityString(
                R.plurals.title_rent_listings_summary,
                totalRentActiveListings,
                totalRentActiveListings
            )
        }

    val hdbSale: LiveData<ListingsSummaryResponse.Breakdown> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.saleListingsBreakdown?.getOrNull(0)
        }

    val condoSale: LiveData<ListingsSummaryResponse.Breakdown> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.saleListingsBreakdown?.getOrNull(1)
        }

    val landedSale: LiveData<ListingsSummaryResponse.Breakdown> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.saleListingsBreakdown?.getOrNull(2)
        }

    val commercialSale: LiveData<ListingsSummaryResponse.Breakdown> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.saleListingsBreakdown?.getOrNull(3)
        }

    val hdbRent: LiveData<ListingsSummaryResponse.Breakdown> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.rentListingsBreakdown?.getOrNull(0)
        }

    val condoRent: LiveData<ListingsSummaryResponse.Breakdown> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.rentListingsBreakdown?.getOrNull(1)
        }

    val landedRent: LiveData<ListingsSummaryResponse.Breakdown> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.rentListingsBreakdown?.getOrNull(2)
        }

    val commercialRent: LiveData<ListingsSummaryResponse.Breakdown> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.rentListingsBreakdown?.getOrNull(3)
        }

    init {
        viewModelComponent.inject(this)
    }

    override fun shouldResponseBeOccupied(response: ListingsSummaryResponse): Boolean {
        val rentTotal = response.rentListingsBreakdown.sumBy { breakdown -> breakdown.total }
        val saleTotal = response.saleListingsBreakdown.sumBy { breakdown -> breakdown.total }
        return rentTotal + saleTotal > 0
    }

    fun getListingSummary() {
        performRequest(listingManagementRepository.getListingsSummary())
    }

    fun onClickBreakdown(breakdown: ListingsSummaryResponse.Breakdown, ownershipType: ListingEnum.OwnershipType) {
        val mainType = ListingEnum.PropertyMainType.values().find {
            it.primarySubType.type == breakdown.cdResearchSubtype
        } ?: throw IllegalArgumentException("Invalid property main type ${breakdown.cdResearchSubtype}")
        RxBus.publish(ViewMyListingsEvent(ownershipType, mainType))
    }
}