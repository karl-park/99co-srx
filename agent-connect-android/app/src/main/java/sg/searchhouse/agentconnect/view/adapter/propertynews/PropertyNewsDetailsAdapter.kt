package sg.searchhouse.agentconnect.view.adapter.propertynews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemPropertyNewsDetailBinding
import sg.searchhouse.agentconnect.model.api.propertynews.OnlineCommunicationPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class PropertyNewsDetailsAdapter(
    private val propertyNews: List<Any>,
    private val onShareButtonClickListener: ((OnlineCommunicationPO, Int) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_PROPERTY_NEWS_DETAILS = 0
        const val VIEW_TYPE_LOADING = 1
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROPERTY_NEWS_DETAILS -> {
                ListItemPropertyNewsDetailsViewHolder(
                    ListItemPropertyNewsDetailBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            VIEW_TYPE_LOADING -> {
                LoadingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.loading_indicator, parent, false)
                )
            }
            else -> {
                throw Throwable("View Type Incorrect in property news adapter")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (propertyNews[position]) {
            is Loading -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_PROPERTY_NEWS_DETAILS
        }
    }

    override fun getItemCount(): Int {
        return propertyNews.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is ListItemPropertyNewsDetailsViewHolder -> {
                val onlineCommunicationPO = propertyNews[position]
                holder.binding.onlineCommunicationPO =
                    onlineCommunicationPO as OnlineCommunicationPO
                holder.binding.webViewContent.loadData(
                    onlineCommunicationPO.getFormattedContent(),
                    "text/html",
                    "utf-8"
                )
                holder.binding.webViewContent.settings.javaScriptEnabled = true
                holder.binding.btnShare.setOnClickListener {
                    onShareButtonClickListener.invoke(onlineCommunicationPO, position)
                }
            }
            is LoadingViewHolder -> {
                println("Doing nothing and showing loading indicator")
            }
            else -> {
                throw Throwable("Wrong View Type in Property News Details Adapter")
            }
        }
    }

    class ListItemPropertyNewsDetailsViewHolder(val binding: ListItemPropertyNewsDetailBinding) :
        RecyclerView.ViewHolder(binding.root)
}
