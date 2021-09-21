package sg.searchhouse.agentconnect.view.activity.listing

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_sms_blast.*
import kotlinx.android.synthetic.main.layout_action_success.*
import kotlinx.android.synthetic.main.layout_sms_blast_schedule_viewing.*
import kotlinx.android.synthetic.main.layout_sms_blast_schedule_viewing.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySmsBlastBinding
import sg.searchhouse.agentconnect.dsl.widget.addOnOffsetChangedListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.model.api.cobrokesms.CobrokeSmsListingPO
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.listing.SmsBlastViewModel

class SmsBlastActivity :
    ViewModelActivity<SmsBlastViewModel, ActivitySmsBlastBinding>(isSliding = true) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupView()
        observeLiveDataHere()
    }


    private fun setupView() {
        layout_container.setupLayoutAnimation()
        appBarLayout.addOnOffsetChangedListener(
            onCollapse = {
                viewModel.showTitleIcon.value = false
            }, onExpand = {
                viewModel.showTitleIcon.value = true
            })
        setOnClickListeners()
    }

    private fun observeLiveDataHere() {
        viewModel.listings.observe(this) {
            viewModel.performGetSmsTemplates(it ?: emptyList())
        }
    }

    private fun setOnClickListeners() {
        layout_sms_blast_available.run {
            layout_header.setOnClickListener {
                viewModel.toggleIsExpandPropertyAvailability()
            }
            btn_submit.setOnClickListener {
                viewModel.performBlastPropertyAvailable()
            }
        }

        layout_sms_blast_schedule_viewing.run {
            layout_header.setOnClickListener {
                viewModel.toggleIsExpandScheduleViewing()
            }
            btn_submit.setOnClickListener {
                val selectedTime = date_time_picker.getSelectedTime()
                viewModel.performBlastScheduleViewing(selectedTime)
            }
        }

        btn_back.setOnClickListener { finish() }
        date_time_picker.setupMinOffset(SmsBlastViewModel.MIN_SCHEDULE_TIME_IN_MINUTES)
    }

    @Throws(JsonSyntaxException::class, IllegalArgumentException::class)
    private fun setupExtras() {
        val gson = Gson()
        val listings = intent.extras?.getStringArrayList(EXTRA_KEY_LISTINGS)?.mapNotNull {
            gson.fromJson(it, CobrokeSmsListingPO::class.java)
        } ?: throw IllegalArgumentException("Select at least one listing")

        viewModel.listings.postValue(listings)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_sms_blast
    }

    override fun getViewModelClass(): Class<SmsBlastViewModel> {
        return SmsBlastViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    companion object {
        private const val EXTRA_KEY_LISTINGS = "EXTRA_KEY_LISTINGS"

        @Throws(IllegalArgumentException::class)
        fun launch(activity: BaseActivity, listings: List<CobrokeSmsListingPO>) {
            if (listings.isEmpty()) throw IllegalArgumentException("Select at least one agent")

            val selectedListings = listings.map {
                Gson().toJson(it, CobrokeSmsListingPO::class.java)
            }
            val extras = Bundle()
            extras.putStringArrayList(
                EXTRA_KEY_LISTINGS,
                ArrayList(selectedListings)
            )
            activity.launchActivity(SmsBlastActivity::class.java, extras)
        }
    }
}