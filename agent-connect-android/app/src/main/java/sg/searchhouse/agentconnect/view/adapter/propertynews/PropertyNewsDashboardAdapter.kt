package sg.searchhouse.agentconnect.view.adapter.propertynews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemPropertyNews3Binding
import sg.searchhouse.agentconnect.databinding.ListItemPropertyNews4Binding
import sg.searchhouse.agentconnect.model.api.propertynews.OnlineCommunicationPO

class PropertyNewsDashboardAdapter(
    private val propertyNews: List<OnlineCommunicationPO>,
    val onItemClickListener: ((Int) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    companion object {
        const val VIEW_TYPE_PROPERTY_NEWS_BIG = 0
        const val VIEW_TYPE_PROPERTY_NEWS_SMALL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROPERTY_NEWS_BIG -> {
                ListItemPropertyNews3ViewHolder(
                    ListItemPropertyNews3Binding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
            VIEW_TYPE_PROPERTY_NEWS_SMALL -> {
                ListItemPropertyNews4ViewHolder(
                    ListItemPropertyNews4Binding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
            else -> {
                throw Throwable("Wrong View Type in property news dashboard adapter")
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is ListItemPropertyNews3ViewHolder -> {
                val onlineCommunicationPO = propertyNews[position]
                holder.binding.onlineCommunicationPO = onlineCommunicationPO
                holder.binding.layoutNewsListItem.setOnClickListener {
                    onItemClickListener.invoke(position)
                }
            }
            is ListItemPropertyNews4ViewHolder -> {
                val onlineCommunicationPO = propertyNews[position]
                holder.binding.onlineCommunicationPO = onlineCommunicationPO
                holder.binding.layoutPropertyNews.setOnClickListener {
                    onItemClickListener.invoke(
                        position
                    )
                }
            }
            else -> {
                throw Throwable("Wrong view type in property new onBind View holder")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_PROPERTY_NEWS_BIG
            else -> VIEW_TYPE_PROPERTY_NEWS_SMALL
        }
    }

    override fun getItemCount(): Int {
        return propertyNews.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ListItemPropertyNews3ViewHolder(val binding: ListItemPropertyNews3Binding) :
        RecyclerView.ViewHolder(binding.root)

    class ListItemPropertyNews4ViewHolder(val binding: ListItemPropertyNews4Binding) :
        RecyclerView.ViewHolder(binding.root)

}