package sg.searchhouse.agentconnect.view.activity.agent.cv

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCvTransactionsBinding
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.*
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.PermissionUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.adapter.agent.CvTransactionAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.agent.cv.CvTransactionsViewModel

class CvTransactionsActivity : BaseActivity() {

    private lateinit var binding: ActivityCvTransactionsBinding
    private lateinit var viewModel: CvTransactionsViewModel
    private lateinit var adapter: CvTransactionAdapter

    private var agentUserId: Int = 0
    private lateinit var type: TransactionOwnershipType
    private lateinit var propertyType: TransactionPropertyType
    private lateinit var agentName: String
    private var agentMobile: String = ""
    private var listings: ArrayList<ListingPO> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cv_transactions)
        viewModel = ViewModelProvider(this).get(CvTransactionsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupParamsExtra()
        setupListAndAdapter()
        observeLiveData()
        handleListeners()
    }

    private fun setupParamsExtra() {
        //intent data..
        agentUserId = intent.getIntExtra(EXTRA_KEY_AGENT_ID, 0)
        type = intent.getSerializableExtra(EXTRA_KEY_TYPE) as TransactionOwnershipType?
            ?: return finish()
        propertyType =
            intent.getSerializableExtra(EXTRA_KEY_PROPERTY_TYPE) as TransactionPropertyType?
                ?: return finish()
        agentName = intent.getStringExtra(EXTRA_KEY_AGENT_NAME) ?: return finish()

        if (type == TransactionOwnershipType.SOLD) {
            viewModel.title.value = getString(R.string.label_cv_sold_transactions)
        } else {
            viewModel.title.value = getString(R.string.label_cv_rented_transactions)
        }
        viewModel.findOtherAgentTransactions(agentUserId, type, propertyType)
    }

    private fun setupListAndAdapter() {
        adapter = CvTransactionAdapter(listings) { mobile ->
            agentMobile = mobile
            IntentUtil.dialPhoneNumber(this, mobile)
        }
        binding.listTransactedListings.layoutManager = LinearLayoutManager(this)
        binding.listTransactedListings.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.let {
                if (it.transactions.total > 0) {
                    if (it.transactions.total == 1) {
                        viewModel.totalProperties.value =
                            getString(R.string.msg_property, it.transactions.total.toString())
                    } else {
                        viewModel.totalProperties.value =
                            getString(R.string.msg_properties, it.transactions.total.toString())
                    }
                }
                listings.clear()
                listings.addAll(it.transactions.listingPOs)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_CALL -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    IntentUtil.dialPhoneNumber(this, agentMobile)
                }
                return
            }
        }//end of when
    }

    companion object {
        private const val EXTRA_KEY_AGENT_ID = "agent_id"
        private const val EXTRA_KEY_TYPE = "transaction_ownership_type"
        private const val EXTRA_KEY_PROPERTY_TYPE = "transaction_property_type"
        private const val EXTRA_KEY_AGENT_NAME = "agent_name"

        fun launch(
            activity: BaseActivity,
            agentUserId: Int,
            type: TransactionOwnershipType,
            propertyType: TransactionPropertyType,
            agentName: String
        ) {
            val extras = Bundle()
            extras.putInt(EXTRA_KEY_AGENT_ID, agentUserId)
            extras.putSerializable(EXTRA_KEY_TYPE, type)
            extras.putSerializable(EXTRA_KEY_PROPERTY_TYPE, propertyType)
            extras.putString(EXTRA_KEY_AGENT_NAME, agentName)
            activity.launchActivity(CvTransactionsActivity::class.java, extras)
        }
    }
}