package sg.searchhouse.agentconnect.view.fragment.amenity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_group_transaction_projects.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentAmenitiesGoogleBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleNearByResponse
import sg.searchhouse.agentconnect.model.app.AmenityLocation
import sg.searchhouse.agentconnect.event.amenity.NotifyActivityDestinationEvent
import sg.searchhouse.agentconnect.event.amenity.NotifyFragmentTravelModeEvent
import sg.searchhouse.agentconnect.event.amenity.RequestActivityTransportEvent
import sg.searchhouse.agentconnect.event.amenity.UpdateActivityTravelModeEvent
import sg.searchhouse.agentconnect.view.adapter.amenity.AmenityGoogleAdapter
import sg.searchhouse.agentconnect.view.fragment.base.BaseFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.amenity.AmenitiesGoogleFragmentViewModel

// TODO Convert to inherit `ViewModelFragment`
class AmenitiesGoogleFragment : BaseFragment() {

    companion object {
        private const val ARGUMENT_KEY_AMENITY_OPTION = "ARGUMENT_KEY_AMENITY_OPTION"
        private const val ARGUMENT_KEY_LISTING_LOCATION = "ARGUMENT_KEY_LISTING_LOCATION"
        private const val ARGUMENT_KEY_PLACE_TYPES = "ARGUMENT_KEY_PLACE_TYPES"

        // placeTypes: PlaceType values in comma-separated string
        fun newInstance(
            amenityOption: LocationEnum.AmenityOption,
            listingLocation: AmenityLocation,
            placeTypes: String
        ): AmenitiesGoogleFragment {
            val fragment = AmenitiesGoogleFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_AMENITY_OPTION, amenityOption)
            bundle.putSerializable(ARGUMENT_KEY_LISTING_LOCATION, listingLocation)
            bundle.putString(ARGUMENT_KEY_PLACE_TYPES, placeTypes)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentAmenitiesGoogleBinding
    private lateinit var viewModel: AmenitiesGoogleFragmentViewModel
    private lateinit var adapter: AmenityGoogleAdapter
    private lateinit var amenityOption: LocationEnum.AmenityOption
    private lateinit var listingLocation: AmenityLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFromArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = setupBinding(container)
        viewModel.listingLocation = listingLocation
        initAdapter()
        adapter.listingLatLng = listingLocation.getLatLng()
        return binding.root
    }

    private fun initAdapter() {
        adapter = AmenityGoogleAdapter(
            locationUtil = locationUtil,
            onAmenitySelectListener = { amenitiesPO ->
                viewModel.toggleSelectNearByResult(amenitiesPO)
            },
            onTransportSelectListener = { travelMode ->
                updateActivityTravelMode(travelMode)
            })
    }

    private fun getPolylinePoints(travelMode: LocationEnum.TravelMode): String? {
        return when (travelMode) {
            LocationEnum.TravelMode.WALKING -> viewModel.walkingDurationPolylinePoints.value?.second
            LocationEnum.TravelMode.DRIVING -> viewModel.drivingDurationPolylinePoints.value?.second
            LocationEnum.TravelMode.TRANSIT -> viewModel.transitDurationPolylinePoints.value?.second
        }
    }

    private fun setupBinding(container: ViewGroup?): FragmentAmenitiesGoogleBinding {
        val binding = DataBindingUtil.inflate<FragmentAmenitiesGoogleBinding>(
            LayoutInflater.from(activity),
            R.layout.fragment_amenities_google,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(AmenitiesGoogleFragmentViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding
    }

    private fun setupFromArguments() {
        amenityOption =
            arguments?.getSerializable(ARGUMENT_KEY_AMENITY_OPTION) as LocationEnum.AmenityOption?
                ?: throw IllegalArgumentException("Missing amenity option argument")
        listingLocation =
            arguments?.getSerializable(ARGUMENT_KEY_LISTING_LOCATION) as AmenityLocation?
                ?: throw IllegalArgumentException("Missing listing location argument")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        listenRxBuses()
        setPlaceTypes()
    }

    private fun setPlaceTypes() {
        val placeTypesString = arguments?.getString(ARGUMENT_KEY_PLACE_TYPES)
            ?: throw IllegalArgumentException("Missing place types argument")
        viewModel.setPlaceTypes(placeTypesString)
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
        publishRxBus(RequestActivityTransportEvent(), onResumeOnly = false)
        resumeSelection()
    }

    // Redraw map again when back from other page
    private fun resumeSelection() {
        viewModel.travelMode.value?.run { updateActivityTravelMode(this) }
        notifyActivityDestination(viewModel.selectedNearByResult.value)
    }

    private fun notifyActivityDestination(selectedNearByResult: GoogleNearByResponse.Result?) {
        publishRxBus(
            NotifyActivityDestinationEvent(
                selectedNearByResult?.run {
                    AmenityLocation(this)
                }
            )
        )
    }

    private fun getSortedResults(results: List<GoogleNearByResponse.Result>): List<GoogleNearByResponse.Result> {
        return results.sortedBy { result ->
            val resultLatLng = result.getLatLng()
            val listingLatLng = viewModel.listingLocation?.getLatLng()
            if (resultLatLng != null && listingLatLng != null) {
                locationUtil.getDistance(resultLatLng, listingLatLng)
            } else {
                0.0
            }
        }
    }

    private fun observeLiveData() {
        viewModel.listItems.observe(viewLifecycleOwner) {
            val results = getSortedResults(it ?: emptyList())
            adapter.populate(results)
            if (results.isNotEmpty()) {
                viewModel.selectedNearByResult.postValue(results[0])
            } else {
                viewModel.selectedNearByResult.postValue(null)
            }
        }
        viewModel.selectedNearByResult.observe(viewLifecycleOwner) { result ->
            adapter.selectResult(result)
            notifyActivityDestination(result)
        }
        viewModel.isRouteReady.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.performRequests()
            }
        }
        viewModel.isAllTravelModesReady.observe(viewLifecycleOwner) {
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
        viewModel.placeTypes.observe(viewLifecycleOwner) {
            if (it?.isNotEmpty() == true) {
                viewModel.performRequest(it)
            }
        }
        viewModel.drivingDurationPolylinePoints.observe(viewLifecycleOwner) {
            maybeUpdateActivityTravelMode(LocationEnum.TravelMode.DRIVING)
        }
        viewModel.walkingDurationPolylinePoints.observe(viewLifecycleOwner) {
            maybeUpdateActivityTravelMode(LocationEnum.TravelMode.WALKING)
        }
        viewModel.transitDurationPolylinePoints.observe(viewLifecycleOwner) {
            maybeUpdateActivityTravelMode(LocationEnum.TravelMode.TRANSIT)
        }
        viewModel.travelMode.observeNotNull(viewLifecycleOwner) {
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
        listenRxBus(NotifyFragmentTravelModeEvent::class.java) {
            viewModel.travelMode.postValue(it.travelMode)
        }
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
    }
}
