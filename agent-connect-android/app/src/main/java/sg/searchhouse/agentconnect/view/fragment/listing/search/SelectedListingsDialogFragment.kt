package sg.searchhouse.agentconnect.view.fragment.listing.search

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.dialog_fragment_selected_listings.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentSelectedListingsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.event.listing.search.SelectListingEvent
import sg.searchhouse.agentconnect.view.adapter.listing.search.SelectedListingAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.search.SelectedListingsDialogViewModel
import java.io.Serializable

class SelectedListingsDialogFragment :
    ViewModelFullWidthDialogFragment<SelectedListingsDialogViewModel, DialogFragmentSelectedListingsBinding>() {
    companion object {
        const val TAG = "SelectedListingsDialogFragment"
        private const val ARGUMENT_SELECTED_LISTINGS = "ARGUMENT_SELECTED_LISTINGS"

        fun newInstance(listingPOs: List<ListingPO>): SelectedListingsDialogFragment {
            val arguments = Bundle()
            arguments.putSerializable(ARGUMENT_SELECTED_LISTINGS, listingPOs as Serializable)
            val fragment = SelectedListingsDialogFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    private val adapter = SelectedListingAdapter { listingId, listingType ->
        RxBus.publish(SelectListingEvent(listingId, listingType))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArguments()
        setupViews()
        listenRxBuses()
        observeLiveData()
    }

    private fun listenRxBuses() {
        listenRxBus(SelectListingEvent::class.java) {
            val position =
                adapter.getListingPosition(it.listingId, it.listingType) ?: return@listenRxBus
            val newItems =
                viewModel.selectedListings.value?.filterIndexed { index, _ -> index != position } ?: emptyList()
            viewModel.selectedListings.postValue(newItems)
        }
    }

    private fun setupViews() {
        list.setupLayoutManager()
        list.adapter = adapter
        btn_ok.setOnClickListener { dismiss() }
    }

    private fun populateItems(newListings: List<ListingPO>) {
        if (newListings.size == adapter.items.size - 1) {
            val removedPosition = adapter.items.indexOfFirst { !newListings.contains(it) }
            adapter.items = newListings
            adapter.notifyItemRemoved(removedPosition)
        } else {
            adapter.items = newListings
            adapter.notifyDataSetChanged()
        }
    }

    private fun observeLiveData() {
        viewModel.selectedListings.observeNotNull(this) {
            populateItems(it ?: emptyList())
        }
    }

    // TODO Maybe fix `UNCHECKED_CAST`
    @Suppress("UNCHECKED_CAST")
    private fun setupArguments() {
        val selectedListings =
            arguments?.getSerializable(ARGUMENT_SELECTED_LISTINGS) as ArrayList<ListingPO>
        viewModel.selectedListings.postValue(selectedListings)
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_selected_listings
    }

    override fun getViewModelClass(): Class<SelectedListingsDialogViewModel> {
        return SelectedListingsDialogViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}