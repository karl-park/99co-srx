package sg.searchhouse.agentconnect.view.activity.project

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_project_map.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityProjectMapBinding
import sg.searchhouse.agentconnect.model.app.ProjectMap
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.MapUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.ProjectMapViewModel

class ProjectMapActivity : ViewModelActivity<ProjectMapViewModel, ActivityProjectMapBinding>(),
    OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        setupMap()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.map.observe(this, Observer {
            when {
                it.url != null -> {
                    val baseUrl = ApiUtil.getBaseUrl(this)
                    val postalCode = intent.getStringExtra(EXTRA_POSTAL_CODE) ?: return@Observer
                    val url = "$baseUrl${it.url}$postalCode"
                    ViewUtil.loadWebView(web_view, url)
                }
                else -> {
                }
            }
        })
    }

    private fun setupMap() {
        (map as SupportMapFragment).getMapAsync(this)
    }

    private fun setupActionBar() {
        toolbar?.navigationIcon = getDrawable(R.drawable.ic_cancel)
        supportActionBar?.title = intent.getStringExtra(EXTRA_PROJECT_NAME)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_project_map
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.run { populateMap(this) }
    }

    private fun populateMap(map: GoogleMap) {
        val latLng = try {
            getLatLng()
        } catch (e: IllegalArgumentException) {
            return ViewUtil.showMessage(R.string.error_position_not_available)
        }
        val mapUtil = MapUtil(map)
        mapUtil.addDefaultMarker(latLng)
        mapUtil.moveCameraToPosition(latLng)
    }

    @Throws(IllegalArgumentException::class)
    private fun getLatLng(): LatLng {
        return intent.getParcelableExtra(EXTRA_POSITION)
            ?: throw IllegalArgumentException("Position not available")
    }

    companion object {
        private const val EXTRA_PROJECT_NAME = "EXTRA_PROJECT_NAME"
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        private const val EXTRA_POSTAL_CODE = "EXTRA_POSTAL_CODE"

        fun launch(
            baseActivity: BaseActivity,
            projectName: String,
            position: LatLng,
            postalCode: String
        ) {
            val extras = Bundle()
            extras.putString(EXTRA_PROJECT_NAME, projectName)
            extras.putString(EXTRA_POSTAL_CODE, postalCode)
            extras.putParcelable(EXTRA_POSITION, position)
            baseActivity.launchActivity(ProjectMapActivity::class.java, extras)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_project_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_switch_map -> {
                val maps = ProjectMap.values().filter {
                    it != viewModel.map.value
                }
                val labels = maps.map { it.label }
                dialogUtil.showListDialog(labels, { _, position ->
                    viewModel.map.postValue(maps[position])
                }, R.string.menu_item_switch_map)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun getViewModelClass(): Class<ProjectMapViewModel> {
        return ProjectMapViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }
}