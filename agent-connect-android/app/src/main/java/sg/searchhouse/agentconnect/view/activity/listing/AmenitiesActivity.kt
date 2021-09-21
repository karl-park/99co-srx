package sg.searchhouse.agentconnect.view.activity.listing

import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.activity_amenities.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.ActivityAmenitiesBinding
import sg.searchhouse.agentconnect.dsl.launchActivityV2
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesPO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.app.AmenityLocation
import sg.searchhouse.agentconnect.event.amenity.*
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.amenity.AmenitiesPagerAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.search.AmenityOptionAdapter
import sg.searchhouse.agentconnect.view.helper.common.CenterSmoothScroller
import sg.searchhouse.agentconnect.viewmodel.activity.listing.AmenitiesViewModel


class AmenitiesActivity : ViewModelActivity<AmenitiesViewModel, ActivityAmenitiesBinding>(),
    OnMapReadyCallback {
    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
    }

    private lateinit var amenityOptionAdapter: AmenityOptionAdapter
    private var amenitiesPagerAdapter: AmenitiesPagerAdapter? = null

    private lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null

    // The route
    private var polyline: Polyline? = null

    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var routeBeginMarker: Marker? = null
    private var routeEndMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData()
        listenRxBuses()
        setupLists()
        setupMap()
        setupFromExtras()
    }

    private fun setupMap() {
        mapFragment = map as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun listenRxBuses() {
        listenRxBus(UpdateActivityTravelModeEvent::class.java) {
            viewModel.polylinePoints.postValue(it.polylinePoints)
            viewModel.travelMode.postValue(it.travelMode)
        }
        listenRxBus(RequestActivityTransportEvent::class.java) {
            viewModel.travelMode.value?.run { RxBus.publish(NotifyFragmentTravelModeEvent(this)) }
        }
        listenRxBus(NotifyActivityDestinationEvent::class.java) {
            viewModel.destination.postValue(it.amenityLocation)
        }
    }

    private fun setupViewPager(listingPO: ListingPO) {
        val listingLocation: AmenityLocation
        try {
            listingLocation = AmenityLocation(listingPO)
        } catch (e: NumberFormatException) {
            return ErrorUtil.handleError(
                "Invalid coordinate for ListingPO with ID ${listingPO.id}, latitude = ${listingPO.latitude}, longitude = ${listingPO.longitude}",
                e
            )
        }
        amenitiesPagerAdapter = AmenitiesPagerAdapter(supportFragmentManager, listingLocation)
        view_pager.adapter = amenitiesPagerAdapter
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                // About !!: The adapter is sure already instantiated before calling
                val amenityOption = amenitiesPagerAdapter!!.getItemFromPosition(position)
                amenityOptionAdapter.selectedItem = amenityOption
                amenityOptionAdapter.notifyDataSetChanged()

                list_amenity_option.postOnAnimation {
                    // Scroller should be one instance per use, not global
                    val scroller = CenterSmoothScroller(this@AmenitiesActivity)
                    scroller.targetPosition = position
                    list_amenity_option.layoutManager?.startSmoothScroll(scroller)
                }

                RxBus.publish(
                    NotifySelectAmenitiesEvent(
                        AmenitiesPagerAdapter.amenityOptions[position],
                        getAmenities(amenityOption)
                    )
                )
            }
        })
    }

    private fun getAmenities(amenityOption: LocationEnum.AmenityOption): List<AmenitiesPO> {
        val amenityCategory = viewModel.amenityCategories.value?.find {
            TextUtils.equals(it.name, amenityOption.value)
        }
        return amenityCategory?.amenities ?: emptyList()
    }

    private fun observeLiveData() {
        viewModel.selectedAmenityOption.observeNotNull(this) {
            amenitiesPagerAdapter?.run { view_pager.currentItem = getItemPosition(it) }
        }
        viewModel.amenityCategories.observeNotNull(this) {
            if (it.isNotEmpty()) {
                selectAmenityOptionFromExtras()
            }
        }
        viewModel.travelMode.observeNotNull(this) {
            RxBus.publish(NotifyFragmentTravelModeEvent(it))
        }
        viewModel.listingPO.observeNotNull(this) { listingPO ->
            setupViewPager(listingPO)
            resetOriginMarker(AmenityLocation(listingPO))
        }
        viewModel.destination.observe(this) { destination ->
            if (destination == null) {
                removeRoute()
            }
            maybeShowMap()
            resetDestinationMarker(destination)
        }
        viewModel.polylinePoints.observe(this) { resetRoute(it) }
    }

    private fun maybeShowMap() {
        if (viewModel.shouldShowMap.value != true) {
            viewModel.shouldShowMap.postValue(true)
        }
    }

    private fun removeRoute() {
        polyline?.remove()
        routeBeginMarker?.remove()
        routeEndMarker?.remove()
    }

    private fun resetRoute(polylinePoints: String?) {
        removeRoute()

        if (polylinePoints.isNullOrEmpty()) {
            return
        }
        val route = PolyUtil.decode(polylinePoints)
        val polylineOptions =
            PolylineOptions().width(10.0f).color(resources.getColor(R.color.purple, null))
                .geodesic(true)
        polylineOptions.addAll(route)

        routeBeginMarker = addEdgeMarker(route, 0, R.drawable.start_pt)
        routeEndMarker = addEdgeMarker(route, route.size - 1, R.drawable.end_pt)

        polyline = googleMap?.addPolyline(polylineOptions)
        setBoundary(route)
    }

    private fun addEdgeMarker(
        route: List<LatLng>?,
        routeIndex: Int,
        @DrawableRes drawableResId: Int
    ): Marker? {
        return route?.getOrNull(routeIndex)?.run {
            val bitmap =
                ViewUtil.getScaledBitmapFromDrawable(resources, drawableResId, EDGE_MARKER_DOT_SIZE)
            googleMap?.addMarker(
                MarkerOptions().icon(
                    BitmapDescriptorFactory.fromBitmap(bitmap)
                ).position(this)
            )
        }
    }

    private fun setBoundary(polylinePoints: List<LatLng>) {
        val builder = LatLngBounds.Builder()
        polylinePoints.map {
            builder.include(it)
        }

        val origin = viewModel.listingPO.value?.getLatLng()
        val destination = viewModel.destination.value?.getLatLng()

        origin?.run { builder.include(this) }
        destination?.run { builder.include(this) }

        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING)
        googleMap?.moveCamera(cameraUpdate)
    }

    private fun resetOriginMarker(amenityLocation: AmenityLocation) {
        originMarker = resetMarker(originMarker, amenityLocation, BitmapDescriptorFactory.HUE_RED)
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                amenityLocation.getLatLng(),
                AppConstant.DEFAULT_MAP_ZOOM
            )
        )
    }

    private fun resetDestinationMarker(amenityLocation: AmenityLocation?) {
        destinationMarker =
            resetMarker(destinationMarker, amenityLocation, BitmapDescriptorFactory.HUE_CYAN)
    }

    // markerHue: values from BitmapDescriptorFactory
    private fun resetMarker(
        marker: Marker?,
        amenityLocation: AmenityLocation?,
        hue: Float
    ): Marker? {
        marker?.remove()
        return if (amenityLocation != null) {
            googleMap?.addMarker(
                MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(hue)).position(
                    amenityLocation.getLatLng()
                ).title(amenityLocation.address)
            )
        } else {
            null
        }
    }

    private fun selectAmenityOptionFromExtras() {
        val selectedAmenityOption =
            intent.extras?.getSerializable(EXTRA_KEY_SELECTED_AMENITY_OPTION) as LocationEnum.AmenityOption?
                ?: throw IllegalArgumentException("Missing or invalid EXTRA_KEY_SELECTED_AMENITY_OPTION")
        viewModel.selectedAmenityOption.postValue(selectedAmenityOption)
    }

    private fun setupFromExtras() {
        val listingId = intent.extras?.getString(EXTRA_KEY_LISTING_ID)
            ?: throw IllegalArgumentException("Missing or invalid `EXTRA_KEY_LISTING_ID` in `AmenitiesActivity`")
        val listingType = intent.extras?.getString(EXTRA_KEY_LISTING_TYPE)
            ?: throw IllegalArgumentException("Missing or invalid `EXTRA_KEY_LISTING_TYPE` in `AmenitiesActivity`")
        viewModel.performRequest(listingId, listingType)
    }

    private fun setupLists() {
        // Location amenities
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        list_amenity_option.layoutManager = layoutManager
        amenityOptionAdapter =
            AmenityOptionAdapter {
                viewModel.selectedAmenityOption.postValue(it)
            }
        list_amenity_option.adapter = amenityOptionAdapter
    }

    companion object {
        const val EXTRA_KEY_LISTING_ID = "EXTRA_KEY_LISTING_ID"
        const val EXTRA_KEY_LISTING_TYPE = "EXTRA_KEY_LISTING_TYPE"
        const val EXTRA_KEY_SELECTED_AMENITY_OPTION = "EXTRA_KEY_SELECTED_AMENITY_OPTION"

        private const val MAP_PADDING = 150
        private const val EDGE_MARKER_DOT_SIZE = 24

        fun launch(
            activity: BaseActivity,
            listingId: String,
            listingType: String,
            amenityOption: LocationEnum.AmenityOption
        ) {
            activity.launchActivityV2<AmenitiesActivity> {
                putExtra(EXTRA_KEY_LISTING_ID, listingId)
                putExtra(EXTRA_KEY_LISTING_TYPE, listingType)
                putExtra(EXTRA_KEY_SELECTED_AMENITY_OPTION, amenityOption)
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_amenities
    }

    override fun getViewModelClass(): Class<AmenitiesViewModel> {
        return AmenitiesViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}
