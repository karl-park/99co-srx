package sg.searchhouse.agentconnect.view.adapter.listing.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemCreditDroneBinding
import sg.searchhouse.agentconnect.databinding.ListItemCreditFeaturedListingBinding
import sg.searchhouse.agentconnect.databinding.ListItemCreditV360Binding
import sg.searchhouse.agentconnect.databinding.ListItemCreditValuationBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.SrxCreditMainType
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO.ActivateSrxCreditListingPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO.ActivateSrxCreditListingPO.VirtualTourNewProjectPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO.ActivateSrxCreditListingPO.FeaturedType
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.SrxCreditListingActivationPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.SrxCreditListingActivationPO.ListingActivationPO.ListingActivationVirtualTour
import sg.searchhouse.agentconnect.util.NumberUtil

class FeaturesCreditApplicationListingsAdapter(
    private val creditType: SrxCreditMainType?,
    private val items: List<ActivateSrxCreditListingPO>,
    private val onRemoveListing: (ActivateSrxCreditListingPO, Int) -> Unit,
    private val onSelectEachItemOptions: (SrxCreditListingActivationPO.ListingActivationPO) -> Unit,
    private val onClickBookAppointment: (ActivateSrxCreditListingPO) -> Unit,
    private val onRemoveBooking: (ActivateSrxCreditListingPO) -> Unit,
    private val onUpdateValuation: (ActivateSrxCreditListingPO) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val v360SelectedOptions = LinkedHashMap<String, VirtualTourNewProjectPO>()
    private val featureSelectedOptions = LinkedHashMap<String, FeaturedType?>()

    companion object {
        const val VIEW_TYPE_VALUATION = 1
        const val VIEW_TYPE_DRONE = 2
        const val VIEW_TYPE_V360 = 3
        const val VIEW_TYPE_FEATURED_LISTING = 4
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_VALUATION -> {
                ValuationListingViewHolder(
                    ListItemCreditValuationBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_V360 -> {
                V360ListingViewHolder(
                    ListItemCreditV360Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_DRONE -> {
                DroneListingViewHolder(
                    ListItemCreditDroneBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_FEATURED_LISTING -> {
                FeaturedListingViewHolder(
                    ListItemCreditFeaturedListingBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> throw Throwable("Invalid view type in onCreateViewHolder")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemViewType(position: Int): Int {
        return when (creditType) {
            SrxCreditMainType.VALUATION -> VIEW_TYPE_VALUATION
            SrxCreditMainType.DRONE -> VIEW_TYPE_DRONE
            SrxCreditMainType.V360 -> VIEW_TYPE_V360
            SrxCreditMainType.FEATURED_LISTING -> VIEW_TYPE_FEATURED_LISTING
            else -> throw Throwable("Invalid list item class in Featured Credit Application Listings Adapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ValuationListingViewHolder -> {
                val item = items[position]

                holder.binding.srxCreditListingPO = item
                holder.binding.showOptionalRemark = item.activationListing?.valuation != null

                if (item.availableBookingSlots.isNotEmpty()) {
                    holder.binding.showBookInspectionDate = true
                } else {
                    holder.binding.showBookInspectionDate = false
                    onUpdateValuation.invoke(item)
                }
                holder.binding.tvBookInspectionDate.setOnClickListener {
                    onClickBookAppointment.invoke(item)
                }
                holder.binding.tvRemoveTimeSlot.setOnClickListener {
                    item.selectedTimeSlot = null
                    onRemoveBooking.invoke(item)
                    notifyItemChanged(position)
                }
                holder.binding.ibRemoveSelectedListing.setOnClickListener {
                    onRemoveListing.invoke(item, position)
                }
            }
            is DroneListingViewHolder -> {
                val item = items[position]
                holder.binding.srxCreditListingPO = item
                holder.binding.ibRemoveSelectedListing.setOnClickListener {
                    onRemoveListing.invoke(item, position)
                }
            }
            is V360ListingViewHolder -> {
                val item = items[position]

                holder.binding.srxCreditListingPO = item
                holder.binding.showOptionalRemark = item.activationListing?.virtualTour != null
                holder.binding.showAvailableVirtualTour =
                    item.virtualTourNewProjects.isNotEmpty() && item.srxCreditActivationAvailability

                val selectedV360Type =
                    if (item.activationListing?.virtualTour?.bookingSlot != null) {
                        null
                    } else {
                        v360SelectedOptions[item.getListingIdType()]
                    }

                if (item.virtualTourNewProjects.isNotEmpty()) {
                    holder.binding.rgV360.setupV360RadioGroup(
                        item.virtualTourNewProjects,
                        selectedV360Type
                    ) {
                        v360SelectedOptions[item.getListingIdType()] = it
                        if (NumberUtil.isNaturalNumber(item.id)) {
                            onSelectEachItemOptions.invoke(
                                SrxCreditListingActivationPO.ListingActivationPO(
                                    id = item.id.toInt(),
                                    value = null,
                                    virtualTour = ListingActivationVirtualTour(
                                        virtualTourNewProjectId = it.id,
                                        bookingSlot = null,
                                        remarks = item.optionalRemark
                                    )
                                )
                            )
                        }
                    }
                    holder.binding.rgV360.enableDisableRadioGroup(item.srxCreditActivationAvailability)
                }

                holder.binding.tvRemoveTimeSlot.setOnClickListener {
                    item.selectedTimeSlot = null
                    onRemoveBooking.invoke(item)
                    notifyItemChanged(position)
                }

                holder.binding.tvBookAppointment.setOnClickListener {
                    onClickBookAppointment.invoke(item)
                }

                holder.binding.ibRemoveSelectedListing.setOnClickListener {
                    onRemoveListing.invoke(item, position)
                }
            }
            is FeaturedListingViewHolder -> {
                val item = items[position]
                holder.binding.srxCreditListingPO = item
                //setup radio group
                holder.binding.rgFeatureListing.setup(
                    item.featuredTypeList,
                    featureSelectedOptions[item.getListingIdType()]
                ) { featuredType ->
                    featureSelectedOptions[item.getListingIdType()] = featuredType
                    if (NumberUtil.isNaturalNumber(item.id)) {
                        onSelectEachItemOptions.invoke(
                            SrxCreditListingActivationPO.ListingActivationPO(
                                id = item.id.toInt(),
                                value = featuredType.type
                            )
                        )
                    }
                }
                holder.binding.rgFeatureListing.enableDisableRadioGroup(item.srxCreditActivationAvailability)
                holder.binding.ibRemoveSelectedListing.setOnClickListener {
                    onRemoveListing.invoke(
                        item,
                        position
                    )
                }
            }
        }
    }

    class ValuationListingViewHolder(val binding: ListItemCreditValuationBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DroneListingViewHolder(val binding: ListItemCreditDroneBinding) :
        RecyclerView.ViewHolder(binding.root)

    class V360ListingViewHolder(val binding: ListItemCreditV360Binding) :
        RecyclerView.ViewHolder(binding.root)

    class FeaturedListingViewHolder(val binding: ListItemCreditFeaturedListingBinding) :
        RecyclerView.ViewHolder(binding.root)
}