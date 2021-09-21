package sg.searchhouse.agentconnect.view.activity.searchoption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_mrt_search.list
import kotlinx.android.synthetic.main.activity_mrt_search.toolbar
import kotlinx.android.synthetic.main.activity_school_search.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySchoolSearchBinding
import sg.searchhouse.agentconnect.dsl.getIntList
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.model.api.lookup.LookupSchoolsResponse.Schools.School
import sg.searchhouse.agentconnect.model.api.lookup.LookupSchoolsResponse.Schools.SchoolType
import sg.searchhouse.agentconnect.view.adapter.searchoption.SchoolAdapter
import sg.searchhouse.agentconnect.view.adapter.searchoption.SchoolTypeAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.searchoption.SchoolSearchViewModel

class SchoolSearchActivity :
    LookupBaseActivity<SchoolSearchViewModel, ActivitySchoolSearchBinding>() {
    private val schoolTypeAdapter = SchoolTypeAdapter()
    private val schoolAdapter = SchoolAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupList()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.isShowSchool.observe(this) {
            invalidateOptionsMenu()
            // TODO: Animate slide up/down list of schools
        }
        viewModel.mainResponse.observeNotNull(this) {
            setDefaultSchools()
        }
    }

    private fun setDefaultSchools() {
        val schools = viewModel.mainResponse.value?.schools?.getAll() ?: return
        val defaultSchoolIds =
            intent.extras?.getString(EXTRA_INPUT_SCHOOL_IDS)?.getIntList() ?: emptyList()
        schoolAdapter.selectedSchools = ArrayList(defaultSchoolIds.mapNotNull { id ->
            schools.find { it.id == id }
        })
        schoolAdapter.notifyDataSetChanged()
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = schoolTypeAdapter

        list_school.layoutManager = LinearLayoutManager(this)
        list_school.adapter = schoolAdapter
        list_school.itemAnimator = null

        schoolTypeAdapter.items = SchoolType.values().toList()
        schoolTypeAdapter.notifyDataSetChanged()
        schoolTypeAdapter.onClickListener = { schoolType ->
            val schools = getSchools(schoolType)
            schoolAdapter.items = schools
            schoolAdapter.notifyDataSetChanged()
            viewModel.isShowSchool.postValue(true)
        }
    }

    private fun getSchools(schoolType: SchoolType): List<Any> {
        val schools = viewModel.mainResponse.value?.schools?.let { theseSchoolTypes ->
            // TODO: Use better implementation when available
            when (schoolType) {
                SchoolType.INTERNATIONAL_SCHOOLS -> theseSchoolTypes.internationalSchools
                SchoolType.POLYTECHNIC -> theseSchoolTypes.polytechnic
                SchoolType.PRIMARY_SCHOOLS -> theseSchoolTypes.primarySchools
                SchoolType.PRIVATE_SCHOOLS -> theseSchoolTypes.privateSchools
                SchoolType.SECONDARY_SCHOOLS -> theseSchoolTypes.secondarySchools
                SchoolType.UNIVERSITIES -> theseSchoolTypes.universities
            }
        } ?: run {
            return emptyList()
        }

        val schoolArrayList = arrayListOf<School>()
        schoolArrayList.addAll(schools)

        schoolArrayList.sortBy { it.name }

        val schoolsByInitial = schoolArrayList.groupBy { it.name.first().toUpperCase() }

        val result = arrayListOf<Any>()

        schoolsByInitial.keys.map { key ->
            result.add(SchoolAdapter.Index(key.toString()))
            val theseSchools = schoolsByInitial[key]
            theseSchools?.let { result.addAll(it) }
        }

        return result
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        val returnMenuItem = menu?.findItem(getReturnMenuItemResId())
        returnMenuItem?.isVisible = viewModel.isShowSchool.value == true
        return true
    }

    override fun onBackPressed() {
        if (viewModel.isShowSchool.value == true) {
            viewModel.isShowSchool.postValue(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun search() {
        val selectedSchoolIds = schoolAdapter.selectedSchools.map { it.id }.joinToString(",")
        val selectedSchoolNames = schoolAdapter.selectedSchools.joinToString(", ") { it.name }

        val intent = Intent()
        intent.putExtra(EXTRA_SELECTED_SCHOOL_IDS, selectedSchoolIds)
        intent.putExtra(EXTRA_SELECTED_SCHOOL_NAMES, selectedSchoolNames)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        private const val EXTRA_INPUT_SCHOOL_IDS = "EXTRA_INPUT_SCHOOL_IDS"
        const val EXTRA_SELECTED_SCHOOL_IDS = "EXTRA_SELECTED_SCHOOL_IDS"
        const val EXTRA_SELECTED_SCHOOL_NAMES = "EXTRA_SELECTED_SCHOOL_NAMES"

        fun launchForResult(
            activity: Activity,
            defaultSchools: String? = null,
            requestCode: Int,
            returnOption: ReturnOption = ReturnOption.SEARCH
        ) {
            val extras = Bundle()
            extras.putString(EXTRA_INPUT_SCHOOL_IDS, defaultSchools)
            extras.putSerializable(EXTRA_RETURN_OPTION, returnOption)
            activity.launchActivityForResult(
                SchoolSearchActivity::class.java,
                extras,
                requestCode = requestCode
            )
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_school_search
    }

    override fun getViewModelClass(): Class<SchoolSearchViewModel> {
        return SchoolSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun onReturnMenuItemClicked() {
        search()
    }
}