package sg.searchhouse.agentconnect.view.adapter.agent

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemTestimonial2Binding
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.util.StringUtil

class CvEditTestimonialAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items: ArrayList<AgentCvPO.Testimonial> = arrayListOf()
    var onEditTestimonial: ((AgentCvPO.Testimonial) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemTestimonialViewHolder(
            ListItemTestimonial2Binding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemTestimonialViewHolder -> {
                val testimonial = items[position]
                holder.binding.testimonial = testimonial
                holder.binding.tvTestimonial.text =
                    StringUtil.getSpannedFromHtml(testimonial.testimonial)
                holder.binding.tvTestimonial.movementMethod = LinkMovementMethod.getInstance()
                holder.binding.ibTestimonialEdit.setOnClickListener {
                    onEditTestimonial?.invoke(testimonial)
                }
            }
        }
    }

    class ListItemTestimonialViewHolder(val binding: ListItemTestimonial2Binding) :
        RecyclerView.ViewHolder(binding.root)
}