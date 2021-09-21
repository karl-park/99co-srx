package sg.searchhouse.agentconnect.view.activity.agent.cv

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_cv_testimonials.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCvTestimonialsBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.event.agent.UpdateCvTestimonial
import sg.searchhouse.agentconnect.event.agent.UpdateCvTestimonialList
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.adapter.agent.CvEditTestimonialAdapter
import sg.searchhouse.agentconnect.view.fragment.agent.cv.CvEditTestimonialDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.agent.cv.CvTestimonialsViewModel


class CvTestimonialsActivity : BaseActivity() {

    companion object {
        const val AGENT_ID = "agent_id"
    }

    private lateinit var viewModel: CvTestimonialsViewModel
    private lateinit var adapter: CvEditTestimonialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityCvTestimonialsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_cv_testimonials)
        viewModel = ViewModelProvider(this).get(CvTestimonialsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        observeRxBus()
        setSupportActionBar(toolbar)
        setupParamsFromExtra()
        setupList()
        observeLiveData()
    }

    private fun observeRxBus() {
        listenRxBus(UpdateCvTestimonialList::class.java) {
            viewModel.testimonials.value = it.testimonials
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cv_testimonial, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_add -> {
                launchEditTestimonialDialog()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setupParamsFromExtra() {
        if (intent.hasExtra(AGENT_ID)) {
            val agentId = intent.getIntExtra(AGENT_ID, 0)
            if (agentId > 0) {
                viewModel.getAgentCv(agentId)
            }
        }
    }

    private fun setupList() {
        adapter = CvEditTestimonialAdapter()
        adapter.onEditTestimonial = onEditTestimonial
        list_testimonials.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        list_testimonials.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.testimonials.observe(this, Observer { testimonials ->
            testimonials?.let {
                adapter.items.clear()
                adapter.items.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
    }


    private val onEditTestimonial: (AgentCvPO.Testimonial) -> Unit = { testimonial ->
        dialogUtil.showListDialog(
            listOf(R.string.label_edit, R.string.action_delete),
            { _, position ->
                when (position) {
                    0 -> editTestimonial(testimonial)
                    1 -> deleteTestimonial(testimonial)
                    else -> throw Throwable("Wrong Type")
                }
            },
            null
        )
    }

    private fun launchEditTestimonialDialog(testimonial: AgentCvPO.Testimonial? = null) {
        CvEditTestimonialDialogFragment.newInstance(testimonial)
            .show(supportFragmentManager, CvEditTestimonialDialogFragment.TAG)
    }

    private fun deleteTestimonial(testimonial: AgentCvPO.Testimonial) {
        DialogUtil(this)
            .showActionDialog(R.string.msg_delete_testimonial) {
                RxBus.publish(UpdateCvTestimonial(testimonial, isDelete = true))
            }
    }

    private fun editTestimonial(testimonial: AgentCvPO.Testimonial) {
        launchEditTestimonialDialog(testimonial)
    }
}