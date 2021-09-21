package sg.searchhouse.agentconnect.view.activity.agent.cv

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_agent_cv.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityAgentCvBinding
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.*
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.AddressSearchSource
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.event.agent.UpdateAgentCv
import sg.searchhouse.agentconnect.event.agent.UpdateCvTestimonial
import sg.searchhouse.agentconnect.event.agent.UpdateCvTestimonialList
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateListingTrackerPO
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.listing.MyListingsActivity
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingActivity
import sg.searchhouse.agentconnect.view.fragment.agent.cv.CvEditGeneralInfoDialogFragment
import sg.searchhouse.agentconnect.view.fragment.agent.cv.CvEditTestimonialDialogFragment
import sg.searchhouse.agentconnect.view.fragment.agent.cv.CvEditTrackRecordDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.create.ListingAddressSearchDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.agent.cv.AgentCvViewModel


class AgentCvActivity : BaseActivity() {

    companion object {
        const val AGENT_ID = "AGENT_ID"

        fun launch(activity: Activity, agentId: Int) {
            val intent = Intent(activity, AgentCvActivity::class.java)
            intent.putExtra(AGENT_ID, agentId)
            activity.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityAgentCvBinding
    private lateinit var viewModel: AgentCvViewModel
    private val currentUser = SessionUtil.getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_agent_cv)
        viewModel = ViewModelProvider(this).get(AgentCvViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupParamsFromExtra()
        observeStatusLiveData()
        observeRxBuses()
        handleViewListeners()
    }

    private fun observeRxBuses() {
        listenRxBus(UpdateAgentCv::class.java) {
            viewModel.agentCvPO.value = it.agentCvPO
            viewModel.saveAgentCv()
        }

        listenRxBus(UpdateCvTestimonial::class.java) { updateCvTestimonial ->
            viewModel.agentCvPO.value?.let { cv ->

                if (updateCvTestimonial.isDelete) {
                    cv.testimonials.find { it.id == updateCvTestimonial.testimonial.id }?.deleted =
                        true
                } else {
                    if (cv.testimonials.isEmpty()) {
                        cv.testimonials.add(updateCvTestimonial.testimonial)
                    } else {
                        cv.testimonials.find { it.id == updateCvTestimonial.testimonial.id }?.let {
                            it.clientName = updateCvTestimonial.testimonial.clientName
                            it.testimonial = updateCvTestimonial.testimonial.testimonial
                        } ?: cv.testimonials.add(updateCvTestimonial.testimonial)
                    }
                }
                viewModel.agentCvPO.value = cv
                viewModel.saveAgentCv()
                RxBus.publish(
                    UpdateCvTestimonialList(
                        cv.testimonials.filter { !it.deleted } as ArrayList<AgentCvPO.Testimonial>
                    )
                )
            }
        }
    }

    private fun setupParamsFromExtra() {
        if (intent.hasExtra(AGENT_ID)) {
            val agentId = intent.getIntExtra(AGENT_ID, 0)
            if (agentId > 0) {
                currentUser?.let { user ->
                    if (user.id == agentId) {
                        viewModel.isOwnCv.postValue(true)
                    } else {
                        viewModel.isOwnCv.postValue(false)
                    }
                    viewModel.getAgentCv(agentId)
                }
            }
        }
    }

    private fun handleViewListeners() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        layout_general_info.binding.btnEdit.setOnClickListener {
            showGeneralInfoDialog()
        }
        layout_general_info.binding.btnAddNow.setOnClickListener {
            showGeneralInfoDialog()
        }
        layout_listing.binding.tabSale.setOnClickListener {
            showCvListings(ListingEnum.OwnershipType.SALE)
        }
        layout_listing.binding.tabRent.setOnClickListener {
            showCvListings(ListingEnum.OwnershipType.RENT)
        }
        layout_listing.binding.tvViewAllListings.setOnClickListener {
            showMyListings()
        }
        layout_listing.binding.btnCreateListing.setOnClickListener {
            showCreateListingDialog()
        }
        layout_track_record.binding.btnCreateListing.setOnClickListener {
            showCreateListingDialog()
        }
        layout_track_record.binding.tvViewAllTrackRecord.setOnClickListener {
            showEditTrackRecord()
        }
        layout_track_record.binding.tabHdbSold.setOnClickListener {
            val ownCv = viewModel.isOwnCv.value ?: false
            if (ownCv) {
                showEditTrackRecord(TransactionType.SOLD, TransactionPropertyType.HDB)
            } else {
                val agentId = viewModel.agentPO.value?.userId ?: 0
                val agentName = viewModel.agentPO.value?.name ?: ""
                CvTransactionsActivity.launch(
                    this,
                    agentUserId = agentId,
                    type = TransactionOwnershipType.SOLD,
                    propertyType = TransactionPropertyType.HDB,
                    agentName = agentName
                )
            }
        }
        layout_track_record.binding.tabHdbRented.setOnClickListener {
            val ownCv = viewModel.isOwnCv.value ?: false
            if (ownCv) {
                showEditTrackRecord(TransactionType.RENTED, TransactionPropertyType.HDB)
            } else {
                val agentId = viewModel.agentPO.value?.userId ?: 0
                val agentName = viewModel.agentPO.value?.name ?: ""
                CvTransactionsActivity.launch(
                    this,
                    agentUserId = agentId,
                    type = TransactionOwnershipType.RENTED,
                    propertyType = TransactionPropertyType.HDB,
                    agentName = agentName
                )
            }
        }
        layout_track_record.binding.tabPrivateSold.setOnClickListener {
            val ownCv = viewModel.isOwnCv.value ?: false
            if (ownCv) {
                showEditTrackRecord(TransactionType.SOLD, TransactionPropertyType.PRIVATE)
            } else {
                val agentId = viewModel.agentPO.value?.userId ?: 0
                val agentName = viewModel.agentPO.value?.name ?: ""
                CvTransactionsActivity.launch(
                    this,
                    agentUserId = agentId,
                    type = TransactionOwnershipType.SOLD,
                    propertyType = TransactionPropertyType.PRIVATE,
                    agentName = agentName
                )
            }

        }
        layout_track_record.binding.tabPrivateRented.setOnClickListener {
            val ownCv = viewModel.isOwnCv.value ?: false
            if (ownCv) {
                showEditTrackRecord(TransactionType.RENTED, TransactionPropertyType.PRIVATE)
            } else {
                val agentId = viewModel.agentPO.value?.userId ?: 0
                val agentName = viewModel.agentPO.value?.name ?: ""
                CvTransactionsActivity.launch(
                    this,
                    agentUserId = agentId,
                    type = TransactionOwnershipType.RENTED,
                    propertyType = TransactionPropertyType.PRIVATE,
                    agentName = agentName
                )
            }
        }
        layout_testimonials.binding.tvViewAllTestimonials.setOnClickListener {
            viewAllTestimonials()
        }
        layout_testimonials.binding.btnAddNow.setOnClickListener {
            showEditTestimonial()
        }

        layout_cv_profile.binding.tvAgentPhone.setOnClickListener {
            val agentPhone = viewModel.agentPO.value?.mobile ?: return@setOnClickListener
            IntentUtil.dialPhoneNumber(this, agentPhone)
        }

        binding.ibMessageChat.setOnClickListener {
            directToChatScreen()
        }
    }

    //general info dialog
    private fun showGeneralInfoDialog() {
        supportFragmentManager.let {
            viewModel.agentCvPO.value?.let { cv ->
                CvEditGeneralInfoDialogFragment.newInstance(cv)
                    .show(it, CvEditGeneralInfoDialogFragment.TAG)
            }
        }
        layout_track_record.binding.tvViewAllTrackRecord.setOnClickListener {
            showEditTrackRecord()
        }
    }

    private fun showEditTrackRecord(
        transactionType: TransactionType = TransactionType.SOLD,
        transactionPropertyType: TransactionPropertyType = TransactionPropertyType.HDB
    ) {
        supportFragmentManager.let {
            viewModel.agentCvPO.value?.let { cv ->
                CvEditTrackRecordDialogFragment.newInstance(
                    cv,
                    transactionType,
                    transactionPropertyType
                ).show(it, CvEditTrackRecordDialogFragment.TAG)
            }
        }
    }

    private fun showCreateListingDialog() {
        ListingAddressSearchDialogFragment.launch(
            fragmentManager = supportFragmentManager,
            addressSearchSource = AddressSearchSource.MAIN_SCREEN,
            createListingTracker = CreateListingTrackerPO(DateTimeUtil.getCurrentTimeInMillis())
        )
    }

    private fun viewAllTestimonials() {
        viewModel.agentPO.value?.let { agent ->
            val intent = Intent(this, CvTestimonialsActivity::class.java)
            intent.putExtra(CvTestimonialsActivity.AGENT_ID, agent.userId)
            startActivity(intent)
        }

    }

    private fun directToChatScreen() {
        ChatMessagingActivity.launch(activity = this, agent = viewModel.agentPO.value)
    }

    private fun showEditTestimonial() {
        CvEditTestimonialDialogFragment.newInstance()
            .show(supportFragmentManager, CvEditTestimonialDialogFragment.TAG)
    }

    private fun showCvListings(ownershipType: ListingEnum.OwnershipType) {
        val userId = viewModel.agentPO.value?.userId ?: 0
        val userName = viewModel.agentPO.value?.name ?: ""
        val count = if (ownershipType == ListingEnum.OwnershipType.SALE) {
            viewModel.agentPO.value?.listingSummary?.saleTotal ?: 0
        } else {
            viewModel.agentPO.value?.listingSummary?.rentTotal ?: 0
        }
        val intent = Intent(this, CvListingsActivity::class.java)
        intent.putExtra(CvListingsActivity.EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        intent.putExtra(CvListingsActivity.EXTRA_KEY_AGENT_ID, userId)
        intent.putExtra(CvListingsActivity.EXTRA_KEY_AGENT_NAME, userName)
        intent.putExtra(CvListingsActivity.EXTRA_KEY_TOTAL_PROPERTIES, count)
        startActivity(intent)
    }

    private fun showMyListings() {
        MyListingsActivity.launch(this)
    }

    private fun observeStatusLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.data?.let { agentPO ->
                viewModel.agentPO.value = agentPO
                //Track record
                var hasTrackRecord = false
                agentPO.transactionSummary?.let {
                    hasTrackRecord =
                        !(it.saleHdbTotal < 1 && it.rentHdbTotal < 1 && it.salePrivateTotal < 1 && it.rentPrivateTotal < 1)
                }
                viewModel.showTrackRecordPlaceholder.value =
                    viewModel.isOwnCv.value == true && (!hasTrackRecord)

                //Listing summary
                var hasListing = false
                agentPO.listingSummary?.let {
                    hasListing = it.saleTotal > 1 || it.rentTotal > 1
                }
                viewModel.showListingPlaceholder.value =
                    viewModel.isOwnCv.value == true && hasListing == false

                agentPO.agentCvPO?.let { cv ->
                    viewModel.agentCvPO.value = cv
                } ?: bindViewModelToAllSections()

                //Track record
                layout_track_record.populateTrackRecord(agentPO)
                //Listing
                agentPO.listingSummary?.let { listingSummary ->
                    layout_listing.populateListingSummary(listingSummary)
                }

            }
        })

        viewModel.agentCvPO.observe(this, Observer { agentCvPO ->
            viewModel.showTestimonialPlaceholder.value =
                viewModel.isOwnCv.value == true && agentCvPO.testimonials.isEmpty()
            viewModel.showGeneralInfoPlaceholder.value =
                viewModel.isOwnCv.value == true && TextUtils.isEmpty(agentCvPO.aboutMe)

            bindViewModelToAllSections()
            layout_cv_profile.binding.cvUrl = agentCvPO.getAgentCvUrlWithBaseUrl(this)
            layout_testimonials.populateTestimonials(agentCvPO.testimonials)
        })
    }

    private fun bindViewModelToAllSections() {
        //bind to cv profile layout
        layout_cv_profile.binding.agentPO = viewModel.agentPO.value
        layout_cv_profile.binding.showEdit = viewModel.isOwnCv.value
        //bind to other section layouts
        layout_general_info.binding.viewModel = viewModel
        layout_track_record.binding.viewModel = viewModel
        layout_testimonials.binding.viewModel = viewModel
        layout_listing.binding.viewModel = viewModel
    }
}