package sg.searchhouse.agentconnect.view.fragment.project

import android.os.Bundle
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.view.fragment.search.SearchCommonFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel.*
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

//TODO: TO remove this fragment use directly search common fragment
class SearchProjectsFragment : SearchCommonFragment() {

    companion object {
        const val VIEW_MODEL_KEY_SEARCH_PROJECT_FRAGMENT = "SearchProjectsFragment"
        const val TAG_PROJECT_LOCATION_SEARCH = "TAG_PROJECT_LOCATION_SEARCH"

        @JvmStatic
        fun newInstance(
            propertyPurpose: ListingEnum.PropertyPurpose,
            searchResultType: SearchResultType
        ): SearchProjectsFragment {
            val fragment = SearchProjectsFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
            bundle.putSerializable(EXTRA_KEY_SEARCH_RESULT_TYPE, searchResultType)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_search_common
    }

    override fun getViewModelClass(): Class<SearchCommonViewModel> {
        return SearchCommonViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return VIEW_MODEL_KEY_SEARCH_PROJECT_FRAGMENT
    }


}