package sg.searchhouse.agentconnect.view.widget.agent.cv

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutCvTestimonialsBinding
import sg.searchhouse.agentconnect.dependency.component.DaggerViewComponent
import sg.searchhouse.agentconnect.dependency.module.ViewModule
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO.*
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.view.adapter.agent.CvTestimonialAdapter
import javax.inject.Inject

class CvTestimonials(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    @Inject
    lateinit var dialogUtil: DialogUtil

    private var adapter: CvTestimonialAdapter
    private val testimonials: ArrayList<Testimonial> = arrayListOf()

    val binding: LayoutCvTestimonialsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_cv_testimonials,
        this,
        true
    )

    init {
        val viewComponent =
            DaggerViewComponent.builder().viewModule(ViewModule(context)).build()
        viewComponent.inject(this)

        //initialize adapter
        adapter = CvTestimonialAdapter(testimonials)
        binding.listTestimonials.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.listTestimonials.adapter = adapter
        binding.listIndicator.attachToRecyclerView(binding.listTestimonials)

        binding.switchTestimonials.setOnCheckedChangeListener { compoundButton, b ->
            binding.viewModel?.let { viewModel ->
                viewModel.agentCvPO.value?.let { cv ->
                    if (compoundButton.isPressed) {
                        cv.showTestimonials = b
                        viewModel.saveAgentCv()
                    }
                }
            }
        }
    }

    private val onItemClickListener: (Testimonial) -> Unit = {
        dialogUtil.showInformationDialogWithSpanned(StringUtil.getSpannedFromHtml(it.testimonial))
    }

    fun populateTestimonials(clientTestimonials: ArrayList<Testimonial>) {
        if (clientTestimonials.isNotEmpty()) {
            //initialize listener
            adapter.onItemClickListener = onItemClickListener
            //clear and add testimonials
            testimonials.clear()
            testimonials.addAll(clientTestimonials)
            adapter.notifyDataSetChanged()
        }
    }
}