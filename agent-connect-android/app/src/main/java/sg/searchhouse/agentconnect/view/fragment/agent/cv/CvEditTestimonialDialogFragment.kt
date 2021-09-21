package sg.searchhouse.agentconnect.view.fragment.agent.cv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.dialog_fragment_cv_edit_testimonial.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCvEditTestimonialBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.event.agent.UpdateCvTestimonial
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv.CvEditTestimonialFragmentViewModel

class CvEditTestimonialDialogFragment : FullScreenDialogFragment() {

    private lateinit var viewModel: CvEditTestimonialFragmentViewModel
    private lateinit var binding: DialogFragmentCvEditTestimonialBinding
    private var testimonial: AgentCvPO.Testimonial? = null

    companion object {
        const val TAG = "add_edit_testimonial"
        private const val TESTIMONIAL = "testimonial_object"
        fun newInstance(item: AgentCvPO.Testimonial? = null): CvEditTestimonialDialogFragment {
            val dialogFragment = CvEditTestimonialDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(TESTIMONIAL, item)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getSerializable(TESTIMONIAL)?.let { data ->
                testimonial = data as AgentCvPO.Testimonial
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_cv_edit_testimonial,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(CvEditTestimonialFragmentViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupParamsFromExtras()
        handleViewListeners()
    }

    private fun setupParamsFromExtras() {
        testimonial?.let { item ->
            viewModel.clientName.value = item.clientName
            viewModel.testimonial.value = item.testimonial
        }
    }

    private fun handleViewListeners() {
        toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }

        tv_save_testimonial.setOnClickListener {
            testimonial?.let { item ->
                item.clientName = viewModel.clientName.value ?: ""
                item.testimonial = viewModel.testimonial.value ?: ""
                RxBus.publish(UpdateCvTestimonial(item, isDelete = false))
                dialog?.dismiss()
            } ?: createNewTestimonial()
        }
    }

    private fun createNewTestimonial() {
        RxBus.publish(
            UpdateCvTestimonial(
                AgentCvPO.Testimonial(
                    viewModel.clientName.value ?: "",
                    false,
                    0,
                    false,
                    "",
                    0,
                    viewModel.testimonial.value ?: "",
                    ""
                ), isDelete = false
            )
        )
        dialog?.dismiss()
    }
}