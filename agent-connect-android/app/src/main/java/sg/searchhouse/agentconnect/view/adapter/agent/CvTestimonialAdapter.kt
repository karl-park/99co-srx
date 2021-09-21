package sg.searchhouse.agentconnect.view.adapter.agent

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemTestimonial1Binding
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO.*
import sg.searchhouse.agentconnect.util.StringUtil

class CvTestimonialAdapter(private val testimonials: ArrayList<Testimonial>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: ((Testimonial) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemTestimonialViewHolder(
            ListItemTestimonial1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return testimonials.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemTestimonialViewHolder -> {
                val testimonial = testimonials[position]
                holder.binding.testimonial = testimonial
                //Showing testimonial description may be including html format
                holder.binding.tvTestimonial.text =
                    StringUtil.getSpannedFromHtml(testimonial.testimonial)
                holder.binding.tvTestimonial.movementMethod = LinkMovementMethod.getInstance()
                holder.binding.tvReadMore.setOnClickListener {
                    onItemClickListener?.invoke(testimonial)
                }
            }
        }
    }

    class ListItemTestimonialViewHolder(val binding: ListItemTestimonial1Binding) :
        RecyclerView.ViewHolder(binding.root)
}