package sg.searchhouse.agentconnect.view.adapter.propertynews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemPropertyNews1Binding
import sg.searchhouse.agentconnect.databinding.ListItemPropertyNews2Binding
import sg.searchhouse.agentconnect.model.api.propertynews.OnlineCommunicationPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class PropertyNewsAdapter(
    private val propertyNews: List<Any>,
    private val onItemClickListener: ((OnlineCommunicationPO, Int) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    companion object {
        const val VIEW_TYPE_PROPERTY_NEWS_FIRST = 0
        const val VIEW_TYPE_PROPERTY_NEWS_SECOND = 1
        const val VIEW_TYPE_LOADING = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROPERTY_NEWS_FIRST -> {
                ListItemPropertyNews1ViewHolder(
                    ListItemPropertyNews1Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_PROPERTY_NEWS_SECOND -> {
                ListItemPropertyNews2ViewHolder(
                    ListItemPropertyNews2Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
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
            is OnlineCommunicationPO -> {
                if (position == 0) {
                    VIEW_TYPE_PROPERTY_NEWS_FIRST
                } else {
                    VIEW_TYPE_PROPERTY_NEWS_SECOND
                }
            }
            else -> {
                throw Throwable("Wrong view type in property news adapter")
            }
        }
    }

    override fun getItemCount(): Int {
        return propertyNews.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is ListItemPropertyNews1ViewHolder -> {
                val onlineCommunicationPO = propertyNews[position]
                holder.binding.onlineCommunicationPO =
                    onlineCommunicationPO as OnlineCommunicationPO
                holder.itemView.setOnClickListener {
                    onItemClickListener.invoke(onlineCommunicationPO, position)
                }
            }
            is ListItemPropertyNews2ViewHolder -> {
                val onlineCommunicationPO = propertyNews[position]
                holder.binding.onlineCommunicationPO =
                    onlineCommunicationPO as OnlineCommunicationPO
                holder.itemView.setOnClickListener {
                    onItemClickListener.invoke(onlineCommunicationPO, position)
                }
            }
            is LoadingViewHolder -> {
                println("Doing nothing and showing loading indicator")
            }
            else -> {
                throw Throwable("Wrong View Type in Property News Search Adapter")
            }
        }
    }

    class ListItemPropertyNews1ViewHolder(val binding: ListItemPropertyNews1Binding) :
        RecyclerView.ViewHolder(binding.root)

    class ListItemPropertyNews2ViewHolder(val binding: ListItemPropertyNews2Binding) :
        RecyclerView.ViewHolder(binding.root)
}

