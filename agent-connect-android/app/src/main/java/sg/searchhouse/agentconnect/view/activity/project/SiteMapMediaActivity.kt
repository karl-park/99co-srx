package sg.searchhouse.agentconnect.view.activity.project

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_listing_media.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySiteMapMediaBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.model.api.project.PhotoPO
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.listing.media.ListingMediaPickerAdapter
import sg.searchhouse.agentconnect.view.adapter.project.SiteMapMediaMainAdapter
import sg.searchhouse.agentconnect.view.helper.common.CenterSmoothScroller
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.SiteMapMediaViewModel

class SiteMapMediaActivity :
    ViewModelActivity<SiteMapMediaViewModel, ActivitySiteMapMediaBinding>() {
    private val mainAdapter = SiteMapMediaMainAdapter()

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
        viewModel.photoPOs.observeNotNull(this) { mPhotoPOs ->
            populateMainList(mPhotoPOs)
            populatePicker(mPhotoPOs)
            val position = intent.getIntExtra(EXTRA_POSITION, 0)
            viewModel.position.postValue(position)
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
        val photoPOs = intent.getStringExtra(EXTRA_PHOTO_POS)?.split(";")?.map {
            val urls = it.split(",")
            if (urls.size != 2) throw IllegalArgumentException("Invalid extras photo PO format")
            val url = urls[0]
            val thumbnailUrl = urls[1]
            PhotoPO(url, thumbnailUrl, "")
        }
        viewModel.photoPOs.postValue(photoPOs)
    }

    private fun populatePicker(listingMedias: List<PhotoPO>) {
        viewModel.position.value?.let { pickerAdapter.selectedPosition = it }
        pickerAdapter.items = listingMedias.toList()
        pickerAdapter.notifyDataSetChanged()
    }

    private fun populateMainList(listingMedias: List<PhotoPO>) {
        mainAdapter.listItems = listingMedias.toList()
        mainAdapter.notifyDataSetChanged()
    }

    private fun setupActionBar() {
        ViewUtil.setToolbarHomeIconColor(toolbar, R.color.white)
    }

    companion object {
        const val EXTRA_PHOTO_POS = "EXTRA_PHOTO_POS"
        const val EXTRA_POSITION = "EXTRA_POSITION"

        fun launch(baseActivity: BaseActivity, photoPOs: List<PhotoPO>, position: Int) {
            val extras = Bundle()
            extras.putInt(EXTRA_POSITION, position)
            // Assume each URLs were encoded
            val photoPOListString = photoPOs.joinToString(";") {
                "${it.url},${it.thumbnailUrl}"
            }
            extras.putString(EXTRA_PHOTO_POS, photoPOListString)
            baseActivity.launchActivity(SiteMapMediaActivity::class.java, extras)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_site_map_media
    }

    override fun getViewModelClass(): Class<SiteMapMediaViewModel> {
        return SiteMapMediaViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}
