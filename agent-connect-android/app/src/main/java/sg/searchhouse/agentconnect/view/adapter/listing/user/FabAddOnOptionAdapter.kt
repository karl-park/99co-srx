package sg.searchhouse.agentconnect.view.adapter.listing.user

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemFabOptionBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.listing.user.SelectFeatureAddOnOptionEvent
import sg.searchhouse.agentconnect.view.widget.listing.MyListingsFabAddOn.AddOnFab

class FabAddOnOptionAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var options = listOf<AddOnFab>()

    //TODO: remove animation for now. if need, uncomment it
    var isOpenAnimation: Boolean = true
    private val fabOpenAnimation: Animation =
        AnimationUtils.loadAnimation(context, R.anim.fade_in)
    private val fabCloseAnimation: Animation =
        AnimationUtils.loadAnimation(context, R.anim.fade_out)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FabAddOnViewHolder(
            ListItemFabOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return options.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FabAddOnViewHolder -> {
                val option = options[position]
                holder.binding.optionLabel = context.getString(option.label)
                if (isOpenAnimation) {
                    holder.binding.tvFabOption.startAnimation(fabOpenAnimation)
                } else {
                    holder.binding.tvFabOption.startAnimation(fabCloseAnimation)
                }
                holder.binding.tvFabOption.setOnClickListener {
                    RxBus.publish(SelectFeatureAddOnOptionEvent(option))
                }
                holder.binding.isNew = option == AddOnFab.COMMUNITY_POST
                holder.binding.executePendingBindings()
            }
            else -> {
                throw Throwable("Wrong view type in FabAddOn Adapter")
            }
        }
    }

    class FabAddOnViewHolder(val binding: ListItemFabOptionBinding) :
        RecyclerView.ViewHolder(binding.root)
}