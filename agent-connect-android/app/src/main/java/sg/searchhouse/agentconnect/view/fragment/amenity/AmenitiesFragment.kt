package sg.searchhouse.agentconnect.view.fragment.amenity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_group_transaction_projects.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentAmenitiesBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.app.AmenityLocation
import sg.searchhouse.agentconnect.event.amenity.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.view.adapter.amenity.AmenityAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.amenity.AmenitiesFragmentViewModel

class AmenitiesFragment :
    ViewModelFragment<AmenitiesFragmentViewModel, FragmentAmenitiesBinding>() {

    companion object {
        private const val ARGUMENT_KEY_LISTING_LOCATION = "ARGUMENT_KEY_LISTING_LOCATION"
        private const val ARGUMENT_KEY_AMENITY_OPTION = "ARGUMENT_KEY_AMENITY_OPTION"

        fun newInstance(
            amenityOption: LocationEnum.AmenityOption,
            listingLocation: AmenityLocation
        ): AmenitiesFragment {
            val fragment = AmenitiesFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_AMENITY_OPTION, amenityOption)
            bundle.putSerializable(ARGUMENT_KEY_LISTING_LOCATION, listingLocation)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var amenityOption: LocationEnum.AmenityOption
    private lateinit var listingLocation: AmenityLocation

    private val adapter = AmenityAdapter(onAmenitySelectListener = { amenitiesPO ->
        viewModel.toggleSelectAmenity(amenitiesPO)
    }, onTransportSelectListener = { travelMode ->
        updateActivityTravelMode(travelMode)
    })

    private fun getPolylinePoints(travelMode: LocationEnum.TravelMode): String? {
        return when (travelMode) {
            LocationEnum.TravelMode.WALKING -> viewModel.getWalkingPolylinePoints()
            LocationEnum.TravelMode.DRIVING -> viewModel.getDrivingPolylinePoints()
            LocationEnum.TravelMode.TRANSIT -> viewModel.getTransitPolylinePoints()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFromArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewModel.listingLocation = listingLocation
        return binding.root
    }

    private fun setupFromArguments() {
        amenityOption =
            arguments?.getSerializable(ARGUMENT_KEY_AMENITY_OPTION) as LocationEnum.AmenityOption
        listingLocation =
            arguments?.getSerializable(ARGUMENT_KEY_LISTING_LOCATION) as AmenityLocation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        listenRxBuses()
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
        publishRxBus(RequestActivityTransportEvent(), onResumeOnly = false)
    }

    private fun observeLiveData() {
        viewModel.listItems.observe(this) { amenitiesPOs ->
            val listItems = (amenitiesPOs ?: emptyList()).sortedBy { amenitiesPO ->
                amenitiesPO.distance
            }
            adapter.populate(listItems)
            if (listItems.isNotEmpty()) {
                viewModel.selectedAmenity.postValue(listItems[0])
            } else {
                viewModel.selectedAmenity.postValue(null)
            }
        }
        viewModel.selectedAmenity.observe(this) { selectedAmenity ->
            adapter.selectAmenity(selectedAmenity)
            publishRxBus(NotifyActivityDestinationEvent(selectedAmenity?.let { AmenityLocation(it) }))
        }
        viewModel.isRouteReady.observe(this) {
            if (it == true) {
                viewModel.performRequests()
            }
        }
        viewModel.isAllTravelModesReady.observe(this) {
            if (it == true) {
                adapter.transitDuration =
                    viewModel.getTravelModeDuration(LocationEnum.TravelMode.TRANSIT)
                adapter.walkingDuration =
                    viewModel.getTravelModeDuration(LocationEnum.TravelMode.WALKING)
                adapter.drivingDuration =
                    viewModel.getTravelModeDuration(LocationEnum.TravelMode.DRIVING)
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.drivingDurationPolylinePoints.observe(this) {
            maybeUpdateActivityTravelMode(LocationEnum.TravelMode.DRIVING)
        }
        viewModel.walkingDurationPolylinePoints.observe(this) {
            maybeUpdateActivityTravelMode(LocationEnum.TravelMode.WALKING)
        }
        viewModel.transitDurationPolylinePoints.observe(this) {
            maybeUpdateActivityTravelMode(LocationEnum.TravelMode.TRANSIT)
        }
        viewModel.travelMode.observeNotNull(this) {
            adapter.selectTravelMode(it)
        }
    }

    private fun maybeUpdateActivityTravelMode(travelMode: LocationEnum.TravelMode) {
        if (viewModel.travelMode.value == travelMode) {
            updateActivityTravelMode(travelMode)
        }
    }

    private fun updateActivityTravelMode(travelMode: LocationEnum.TravelMode) {
        publishRxBus(UpdateActivityTravelModeEvent(travelMode, getPolylinePoints(travelMode)))
    }

    private fun listenRxBuses() {
        listenRxBus(NotifySelectAmenitiesEvent::class.java) {
            if (it.amenityOption == amenityOption) {
                viewModel.mainStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                viewModel.listItems.postValue(it.amenities)
            }
        }
        listenRxBus(NotifyFragmentTravelModeEvent::class.java) {
            viewModel.travelMode.postValue(it.travelMode)
        }
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_amenities
    }

    override fun getViewModelClass(): Class<AmenitiesFragmentViewModel> {
        return AmenitiesFragmentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return amenityOption.value
    }
}
