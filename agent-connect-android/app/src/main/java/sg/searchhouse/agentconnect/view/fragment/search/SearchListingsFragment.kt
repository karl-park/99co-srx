package sg.searchhouse.agentconnect.view.fragment.search

import android.os.Bundle
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

/**
 * Search listings
 */
class SearchListingsFragment : SearchCommonFragment() {
    companion object {
        @JvmStatic
        fun newInstance(propertyPurpose: ListingEnum.PropertyPurpose): SearchListingsFragment {
            val fragment = SearchListingsFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
            bundle.putSerializable(EXTRA_KEY_SEARCH_RESULT_TYPE, SearchCommonViewModel.SearchResultType.LISTINGS)
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
        return null
    }
}