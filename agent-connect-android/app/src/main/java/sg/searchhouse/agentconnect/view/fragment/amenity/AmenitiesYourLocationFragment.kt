package sg.searchhouse.agentconnect.view.fragment.amenity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_amenities_your_location.*
import kotlinx.android.synthetic.main.layout_select_travel_mode.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentAmenitiesYourLocationBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.location.LocationEntryPO
import sg.searchhouse.agentconnect.model.app.AmenityLocation
import sg.searchhouse.agentconnect.event.amenity.NotifyActivityDestinationEvent
import sg.searchhouse.agentconnect.event.amenity.NotifyFragmentTravelModeEvent
import sg.searchhouse.agentconnect.event.amenity.RequestActivityTransportEvent
import sg.searchhouse.agentconnect.event.amenity.UpdateActivityTravelModeEvent
import sg.searchhouse.agentconnect.util.PermissionUtil
import sg.searchhouse.agentconnect.view.activity.common.LocationSearchActivity
import sg.searchhouse.agentconnect.view.activity.common.LocationSearchActivity.Companion.EXTRA_RESULT_LOCATION_ENTRY
import sg.searchhouse.agentconnect.view.fragment.base.BaseFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.amenity.AmenitiesYourLocationFragmentViewModel

class AmenitiesYourLocationFragment : BaseFragment() {

    companion object {
        private const val REQUEST_CODE_SEARCH_DESTINATION = 1
        private const val ARGUMENT_KEY_LISTING_LOCATION = "ARGUMENT_KEY_LISTING_LOCATION"

        fun newInstance(listingLocation: AmenityLocation): AmenitiesYourLocationFragment {
            val fragment = AmenitiesYourLocationFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_LISTING_LOCATION, listingLocation)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentAmenitiesYourLocationBinding
    private lateinit var viewModel: AmenitiesYourLocationFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.fragment_amenities_your_location,
            container,
            false
        )
        viewModel =
            ViewModelProvider(this).get(AmenitiesYourLocationFragmentViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setupFromArguments()
        return binding.root
    }

    private fun setupFromArguments() {
        val listingLocation =
            arguments?.getSerializable(ARGUMENT_KEY_LISTING_LOCATION) as AmenityLocation
        viewModel.listingLocation.postValue(listingLocation)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenRxBuses()
        observeLiveData()
        setOnClickListeners()
        maybeGetCurrentLocation()
    }

    private fun setOnClickListeners() {
        btn_transport_bus_mrt.setOnClickListener {
            updateActivityTravelMode(LocationEnum.TravelMode.TRANSIT)
        }
        btn_transport_drive.setOnClickListener {
            updateActivityTravelMode(LocationEnum.TravelMode.DRIVING)
        }
        btn_transport_walk.setOnClickListener {
            updateActivityTravelMode(LocationEnum.TravelMode.WALKING)
        }
        btn_to.setOnClickListener {
            if (activity == null) return@setOnClickListener
            val intent = Intent(activity, LocationSearchActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SEARCH_DESTINATION)
        }
    }

    private fun updateActivityTravelMode(travelMode: LocationEnum.TravelMode) {
        publishRxBus(UpdateActivityTravelModeEvent(travelMode, getPolylinePoints(travelMode)))
    }

    private fun getPolylinePoints(travelMode: LocationEnum.TravelMode): String? {
        return when (travelMode) {
            LocationEnum.TravelMode.WALKING -> viewModel.walkingDurationPolylinePoints.value?.second
            LocationEnum.TravelMode.DRIVING -> viewModel.drivingDurationPolylinePoints.value?.second
            LocationEnum.TravelMode.TRANSIT -> viewModel.transitDurationPolylinePoints.value?.second
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SEARCH_DESTINATION -> {
                val locationEntryPO =
                    data?.extras?.getSerializable(EXTRA_RESULT_LOCATION_ENTRY) as LocationEntryPO?
                locationEntryPO?.run { viewModel.destination.postValue(AmenityLocation(this)) }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun maybeGetCurrentLocation() {
        val mActivity = activity ?: return
        val isPermissionGranted =
            PermissionUtil.isLocationPermissionsGranted(mActivity) // TODO: Replace activity by applicationContext
        if (isPermissionGranted) {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        locationUtil.getCurrentLocation({
            viewModel.findCurrentLocation(it)
        }, hasQuickUpdate = true)
    }

    override fun onResume() {
        super.onResume()
        updateOrRequestActivityTravelMode()
        updateActivityDestination()
    }

    private fun updateActivityDestination() {
        publishRxBus(NotifyActivityDestinationEvent(viewModel.destination.value?.run { this }))
    }

    private fun updateOrRequestActivityTravelMode() {
        viewModel.travelMode.value?.run {
            updateActivityTravelMode(this)
        } ?: run {
            RxBus.publish(RequestActivityTransportEvent())
        }
    }

    private fun observeLiveData() {
        viewModel.destination.observe(viewLifecycleOwner) {
            publishRxBus(NotifyActivityDestinationEvent(it))
        }
        viewModel.isRouteReady.observeNotNull(viewLifecycleOwner) {
            if (it) {
                viewModel.performRequests()
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
    }

    private fun maybeUpdateActivityTravelMode(travelMode: LocationEnum.TravelMode) {
        if (viewModel.travelMode.value == travelMode) {
            updateActivityTravelMode(travelMode)
        }
    }

    private fun listenRxBuses() {
        listenRxBus(NotifyFragmentTravelModeEvent::class.java) {
            viewModel.travelMode.postValue(it.travelMode)
        }
    }
}
