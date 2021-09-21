package sg.searchhouse.agentconnect.view.activity.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.marginBottom
import androidx.core.view.marginRight
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCropImageBinding
import sg.searchhouse.agentconnect.dsl.launchActivityForResultV2
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.moveMarginInFrameLayout
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.dsl.widget.setDimension
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.widget.common.ScrollDetectorView
import sg.searchhouse.agentconnect.viewmodel.activity.common.CropImageViewModel
import kotlin.math.abs
import kotlin.math.min

class CropImageActivity :
    ViewModelActivity<CropImageViewModel, ActivityCropImageBinding>() {

    private var cap: Float = 0f

    private var orientation = ScrollDetectorView.Orientation.VERTICAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFromExtra()
        setupViews()
        observeLiveData()
    }

    private fun setupViews() {
        binding.layoutCropper.setupLayoutAnimation()
        setupActionBar()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.scrollDetectorView.setListeners(onBeginTouch = {
            Pair(getExistingMargin(), orientation)
        }, onScroll = { distance, orientation ->
            moveImage(distance, orientation)
        })
    }

    private fun getExistingMargin(): Float {
        return when (orientation) {
            ScrollDetectorView.Orientation.VERTICAL -> binding.imageView.marginBottom
            ScrollDetectorView.Orientation.HORIZONTAL -> binding.imageView.marginRight
        }.toFloat()
    }

    private fun moveImage(distance: Float, orientation: ScrollDetectorView.Orientation) {
        when (orientation) {
            ScrollDetectorView.Orientation.HORIZONTAL -> {
                binding.imageView.moveMarginInFrameLayout(getXMovement(distance), 0)
            }
            ScrollDetectorView.Orientation.VERTICAL -> {
                binding.imageView.moveMarginInFrameLayout(0, getYMovement(distance))
            }
        }
    }

    private fun getXMovement(x: Float): Int {
        val xAbsolute = min(cap, abs(x))
        return when {
            x > 0 -> xAbsolute
            else -> -xAbsolute
        }.toInt()
    }

    private fun getYMovement(y: Float): Int {
        val yAbsolute = min(cap, abs(y))
        return when {
            y > 0 -> yAbsolute
            else -> -yAbsolute
        }.toInt()
    }

    private fun observeLiveData() {
        viewModel.imageUrl.observeNotNull(this) {
            setupCropperDimension()
        }
    }

    private fun setupCropperDimension() {
        binding.imageView.postDelayed {
            val imageHeight = binding.imageView.displayRect.height()
            val imageWidth = binding.imageView.displayRect.width()
            val cropSize = getCropSize()
            orientation = when {
                imageHeight >= imageWidth -> ScrollDetectorView.Orientation.VERTICAL
                else -> ScrollDetectorView.Orientation.HORIZONTAL
            }
            cap = (when (orientation) {
                ScrollDetectorView.Orientation.HORIZONTAL -> {
                    imageWidth
                }
                ScrollDetectorView.Orientation.VERTICAL -> {
                    imageHeight
                }
            } - cropSize) / 2
            binding.ivCropper.setDimension(cropSize, cropSize)
        }
    }

    private fun getCropSize(): Int {
        val imageHeight = binding.imageView.displayRect.height()
        val imageWidth = binding.imageView.displayRect.width()
        return min(imageHeight, imageWidth).toInt()
    }

    private fun setupFromExtra() {
        viewModel.imageUrl.postValue(intent.getStringExtra(EXTRA_IMAGE_URL))
    }

    private fun setupActionBar() {
        ViewUtil.setToolbarHomeIconColor(binding.toolbar, R.color.white)
    }

    companion object {
        const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"
        const val EXTRA_OUTPUT_COORDINATE = "EXTRA_OUTPUT_COORDINATE"
        const val EXTRA_OUTPUT_CROP_SIZE = "EXTRA_OUTPUT_CROP_SIZE"

        fun launch(baseActivity: BaseActivity, imageUrl: String, requestCode: Int) {
            baseActivity.launchActivityForResultV2<CropImageActivity>(requestCode) {
                putExtra(EXTRA_IMAGE_URL, imageUrl)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crop_image, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_done -> {
                val cropSize = getCropSize()
                val imageHeight = binding.imageView.displayRect.height()
                val imageWidth = binding.imageView.displayRect.width()

                val originalXOffset = (imageWidth - cropSize) / 2
                val originalYOffset = (imageHeight - cropSize) / 2

                val coordinate = when (orientation) {
                    ScrollDetectorView.Orientation.HORIZONTAL -> {
                        val x = (binding.imageView.marginRight + originalXOffset).toInt()
                        "$x,0"
                    }
                    ScrollDetectorView.Orientation.VERTICAL -> {
                        val y = (binding.imageView.marginBottom + originalYOffset).toInt()
                        "0,$y"
                    }
                }
                val intent = Intent()
                intent.putExtra(EXTRA_OUTPUT_COORDINATE, coordinate)
                intent.putExtra(EXTRA_OUTPUT_CROP_SIZE, getCropSize())
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_crop_image
    }

    override fun getViewModelClass(): Class<CropImageViewModel> {
        return CropImageViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }
}
