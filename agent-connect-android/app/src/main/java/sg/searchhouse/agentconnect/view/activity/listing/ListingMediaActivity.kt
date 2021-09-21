package sg.searchhouse.agentconnect.view.activity.listing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_listing_media.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityListingMediaBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.model.api.listing.ListingMedia
import sg.searchhouse.agentconnect.model.api.listing.ListingVideoPO
import sg.searchhouse.agentconnect.model.api.listing.ListingVirtualTourPO
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.common.WebViewActivity
import sg.searchhouse.agentconnect.view.adapter.listing.media.ListingMediaMainAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.media.ListingMediaPickerAdapter
import sg.searchhouse.agentconnect.view.helper.common.CenterSmoothScroller
import sg.searchhouse.agentconnect.viewmodel.activity.listing.ListingMediaViewModel

class ListingMediaActivity :
    ViewModelActivity<ListingMediaViewModel, ActivityListingMediaBinding>() {
    private val mainAdapter =
        ListingMediaMainAdapter { listingMedia ->
            when (listingMedia) {
                is ListingVideoPO -> {
                    IntentUtil.visitUrl(this, listingMedia.getMediaUrl())
                }
                is ListingVirtualTourPO -> {
                    WebViewActivity.launch(this, listingMedia.getMediaUrl())
                }
                else -> {
                    IntentUtil.encodeAndVisitUrl(this, listingMedia.getMediaUrl())
                }
            }
        }
    private val pickerAdapter =
        ListingMediaPickerAdapter {
            viewModel.position.postValue(it)
        }

    private var pickerLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        setupLists()
        observeParams()
        setupParams()
    }

    private fun setupLists() {
        view_pager.adapter = mainAdapter
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.position.postValue(position)
            }
        })
        pickerLayoutManager = LinearLayoutManager(list_picker.context)
        pickerLayoutManager?.orientation = LinearLayoutManager.HORIZONTAL
        list_picker.layoutManager = pickerLayoutManager
        list_picker.adapter = pickerAdapter
    }

    private fun observeParams() {
        viewModel.listingMedias.observe(this) { mListingMedias ->
            mListingMedias?.let {
                populateMainList(it)
                populatePicker(it)
                val position = intent.getIntExtra(EXTRA_KEY_POSITION, 0)
                viewModel.position.postValue(position)
            }
        }
        viewModel.position.observeNotNull(this) { mPosition ->
            setPosition(mPosition)
        }
    }

    private fun setPosition(position: Int) {
        view_pager.currentItem = position

        pickerAdapter.selectedPosition = position
        pickerAdapter.notifyDataSetChanged()

        // Bug fixed: Smooth scroll not working when it is just after notifyDataSetChanged
        list_picker.postOnAnimation {
            // Scroller should be one instance per use, not global
            val scroller = CenterSmoothScroller(this)
            scroller.targetPosition = position
            list_picker.layoutManager?.startSmoothScroll(scroller)
        }
    }

    private fun setupParams() {
        val listingId = intent.getStringExtra(EXTRA_KEY_LISTING_ID) ?: return finish()
        val listingType = intent.getStringExtra(EXTRA_KEY_LISTING_TYPE) ?: return finish()
        viewModel.performRequest(listingId, listingType)
    }

    private fun populatePicker(listingMedias: List<ListingMedia>) {
        viewModel.position.value?.let { pickerAdapter.selectedPosition = it }
        pickerAdapter.items = listingMedias.toList()
        pickerAdapter.notifyDataSetChanged()
    }

    private fun populateMainList(listingMedias: List<ListingMedia>) {
        mainAdapter.listItems = listingMedias.toList()
        mainAdapter.notifyDataSetChanged()
    }

    private fun setupActionBar() {
        ViewUtil.setToolbarHomeIconColor(toolbar, R.color.white)
    }

    override fun finish() {
        val intent = Intent()
        intent.putExtra(EXTRA_KEY_POSITION, view_pager.currentItem)
        setResult(RESULT_OK, intent)
        super.finish()
    }

    companion object {
        const val EXTRA_KEY_LISTING_ID = "EXTRA_KEY_LISTING_ID"
        const val EXTRA_KEY_LISTING_TYPE = "EXTRA_KEY_LISTING_TYPE"
        const val EXTRA_KEY_POSITION = "EXTRA_KEY_POSITION"
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_listing_media
    }

    override fun getViewModelClass(): Class<ListingMediaViewModel> {
        return ListingMediaViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}
