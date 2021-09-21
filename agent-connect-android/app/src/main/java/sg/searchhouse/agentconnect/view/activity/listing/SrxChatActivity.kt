package sg.searchhouse.agentconnect.view.activity.listing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_srx_chat.*
import kotlinx.android.synthetic.main.layout_action_success.*
import kotlinx.android.synthetic.main.layout_srx_chat_selected_listings.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySrxChatBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.event.chat.ChatRefreshEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.listing.search.SrxChatBlastListingAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.listing.SrxChatViewModel
import java.io.Serializable

class SrxChatActivity :
    ViewModelActivity<SrxChatViewModel, ActivitySrxChatBinding>(isSliding = true) {

    private lateinit var adapter: SrxChatBlastListingAdapter

    companion object {
        private const val EXTRA_KEY_LISTINGS = "EXTRA_KEY_LISTINGS"

        fun launch(
            activity: Activity,
            listingIds: List<Pair<String, String>>?
        ) {
            val intent = Intent(activity, SrxChatActivity::class.java)
            listingIds?.run { intent.putExtra(EXTRA_KEY_LISTINGS, this as Serializable) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraParams()
        setupListAndAdapter()
        observeLiveData()
        handleListeners()
    }

    private fun setupExtraParams() {
        intent.getSerializableExtra(EXTRA_KEY_LISTINGS)?.run {
            val list = arrayListOf<Pair<String, String>>()
            (this as List<*>).filterIsInstance<Pair<String, String>>().map { list.add(it) }
            viewModel.listingIds.value = list
        }
    }

    private fun setupListAndAdapter() {
        adapter = SrxChatBlastListingAdapter()
        list_listings.layoutManager = LinearLayoutManager(this)
        list_listings.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer {
            val response = it ?: return@Observer
            val tempListings = arrayListOf<ListingPO>()
            response.listings.srxStpListings?.listingPOs?.run { tempListings.addAll(this) }
            response.listings.listingPropertyListings?.listingPOs?.run { tempListings.addAll(this) }
            viewModel.listings.value = tempListings
        })

        viewModel.listings.observe(this, Observer {
            val list = it ?: return@Observer
            adapter.updateListings(list)
        })

        viewModel.listingIds.observe(this, Observer { listings ->
            val items = listings ?: return@Observer
            if (items.isNotEmpty()) {
                viewModel.srxstp.value =
                    items.filter { it.second == ListingType.SRX_LISTING.value }
                        .map { it.first }
                viewModel.tableLP.value =
                    items.filter { it.second == ListingType.PUBLIC_LISTING.value }
                        .map { it.first }
                performRequest()
            }
        })

        viewModel.createSsmBlastStatus.observe(this, Observer { status ->
            when (status.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    RxBus.publish(ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER)
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(status.error?.error)
                }
                else -> {
                    println("do nothing")
                }
            }
        })
    }

    private fun handleListeners() {
        layout_toggle_listings.setOnClickListener {
            when (viewModel.isBlastListingsShowed.value) {
                true -> viewModel.isBlastListingsShowed.value = false
                else -> viewModel.isBlastListingsShowed.value = true
            }
        }
        btn_send.setOnClickListener { viewModel.createSsmBlastConversation() }
        btn_back.setOnClickListener { onBackPressed() }
    }

    private fun performRequest() {
        viewModel.getListingsByListingIds()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_srx_chat
    }

    override fun getViewModelClass(): Class<SrxChatViewModel> {
        return SrxChatViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}