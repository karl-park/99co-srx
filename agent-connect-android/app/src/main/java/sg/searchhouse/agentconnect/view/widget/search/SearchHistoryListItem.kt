package sg.searchhouse.agentconnect.view.widget.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import sg.searchhouse.agentconnect.databinding.ListItemSearchHistoryBinding
import sg.searchhouse.agentconnect.util.SearchHistoryUtil

class SearchHistoryListItem constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    private val binding =
        ListItemSearchHistoryBinding.inflate(LayoutInflater.from(context), this, true)

    fun populate(
        history: Triple<String, String, SearchHistoryUtil.SearchType>,
        onClickListener: (label: String, value: String, searchType: SearchHistoryUtil.SearchType) -> Unit
    ) {
        binding.tvName.text = history.first
        binding.root.setOnClickListener {
            history.run { onClickListener.invoke(first, second, third) }
        }
    }
}