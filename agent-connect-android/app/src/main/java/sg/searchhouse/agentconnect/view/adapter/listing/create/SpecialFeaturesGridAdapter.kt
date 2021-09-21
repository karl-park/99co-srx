package sg.searchhouse.agentconnect.view.adapter.listing.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.GridItemSpecialFeatureBinding
import sg.searchhouse.agentconnect.databinding.GridItemSpecialFeatureTitleBinding
import sg.searchhouse.agentconnect.model.api.lookup.LookupListingFeaturesFixturesAreasResponse
import java.lang.ClassCastException

class SpecialFeaturesGridAdapter(
    val onSelectItem: ((LookupListingFeaturesFixturesAreasResponse.Data) -> Unit),
    val onSelectAllByEachCategory: ((Boolean) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = listOf<Any>()
    var selectedItems = listOf<String>()

    companion object {
        const val VIEW_ITEM_FEATURE = 1
        const val VIEW_ITEM_FEATURE_TITLE = 2
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_ITEM_FEATURE -> {
                FeatureItemViewHolder(
                    GridItemSpecialFeatureBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_ITEM_FEATURE_TITLE -> {
                FeatureItemTitleViewHolder(
                    GridItemSpecialFeatureTitleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw Throwable("Wrong View Type in SpecialFeaturesGridAdapter")
            }
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FeatureItemViewHolder -> {
                val item = items[position] as LookupListingFeaturesFixturesAreasResponse.Data
                holder.binding.data = item
                holder.binding.cbFeatureItem.isChecked = selectedItems.contains(item.id)
                holder.binding.cbFeatureItem.setOnClickListener { onSelectItem.invoke(item) }
            }
            is FeatureItemTitleViewHolder -> {
                val title = items[position] as String
                holder.binding.title = title

                //Toggle select all or unselect all
                val facilitiesSize =
                    items.filterIsInstance<LookupListingFeaturesFixturesAreasResponse.Data>().size
                holder.binding.isSelectAll = facilitiesSize == selectedItems.size

                holder.binding.tvSelectState.setOnClickListener {
                    //Note: isSelectAll won't be null since already initialized selectAll values by sizes
                    holder.binding.isSelectAll?.run {
                        holder.binding.isSelectAll = !this
                        onSelectAllByEachCategory.invoke(!this)
                    }
                } //end of click listener
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is LookupListingFeaturesFixturesAreasResponse.Data -> VIEW_ITEM_FEATURE
            is String -> VIEW_ITEM_FEATURE_TITLE
            else -> throw  ClassCastException("Wrong list item in special features grid item")
        }
    }

    class FeatureItemViewHolder(val binding: GridItemSpecialFeatureBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FeatureItemTitleViewHolder(val binding: GridItemSpecialFeatureTitleBinding) :
        RecyclerView.ViewHolder(binding.root)
}