package sg.searchhouse.agentconnect.view.activity.report.newlaunches

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_send_new_launches_reports.*
import kotlinx.android.synthetic.main.layout_bottom_action_bar.*
import kotlinx.android.synthetic.main.layout_send_new_launches_reports_success.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySendNewLaunchesReportsBinding
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.project.GetNewLaunchesResponse
import sg.searchhouse.agentconnect.model.app.DeviceContact
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PermissionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.report.newlaunches.NewLaunchesClientsAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches.SendNewLaunchesReportsViewModel
import java.io.Serializable

class SendNewLaunchesReportsActivity :
    ViewModelActivity<SendNewLaunchesReportsViewModel, ActivitySendNewLaunchesReportsBinding>(
        isSliding = true
    ) {

    private lateinit var adapter: NewLaunchesClientsAdapter

    companion object {

        private const val EXTRA_KEY_NEW_LAUNCHES_REPORTS = "EXTRA_KEY_NEW_LAUNCHES_REPORTS"
        private const val REQUEST_CODE_PHONE_BOOK = 200

        fun launch(
            activity: Activity,
            reports: List<GetNewLaunchesResponse.NewLaunchProject>,
            requestCode: Int
        ) {
            val intent = Intent(activity, SendNewLaunchesReportsActivity::class.java)
            intent.putExtra(EXTRA_KEY_NEW_LAUNCHES_REPORTS, reports as Serializable)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListAndAdapter()
        setupExtraParams()
        performRequest()
        observeLiveData()
        handleListeners()
    }

    private fun setupListAndAdapter() {
        adapter = NewLaunchesClientsAdapter {
            viewModel.tempMobileNumber = it
            IntentUtil.dialPhoneNumber(this, it)
        }
        list_clients.layoutManager = LinearLayoutManager(this)
        list_clients.adapter = adapter
    }

    private fun setupExtraParams() {
        if (intent.hasExtra(EXTRA_KEY_NEW_LAUNCHES_REPORTS)) {
            intent.getSerializableExtra(EXTRA_KEY_NEW_LAUNCHES_REPORTS)?.run {
                viewModel.selectedReports.value =
                    (this as List<*>).filterIsInstance<GetNewLaunchesResponse.NewLaunchProject>()
            }
        }
    }

    private fun performRequest() {
        viewModel.getTemplates()
    }

    private fun observeLiveData() {
        viewModel.selectedReports.observe(this, Observer {
            val items = it ?: return@Observer
            if (items.isNotEmpty()) {
                val firstProjectName = items.first().name
                if (items.size == 1) {
                    viewModel.attachmentLabel.value = firstProjectName
                } else {
                    viewModel.attachmentLabel.value =
                        "$firstProjectName & ${items.size - 1} other projects"
                }
                viewModel.projectsSelectedLabel.value = resources.getQuantityString(
                    R.plurals.label_selected_report_count,
                    it.size,
                    NumberUtil.formatThousand(it.size)
                )
            }
        })

        viewModel.contacts.observe(this, Observer { items ->
            val contacts = items ?: return@Observer
            println(contacts)
        })

        viewModel.getTemplateStatus.observe(this, Observer { status ->
            when (status.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    viewModel.template.postValue(status.body?.newLaunchesNotificationTemplate ?: "")
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(status.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        })

        viewModel.sendReportToClientResponse.observe(this, Observer { status ->
            when (status.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val result = status.body ?: return@Observer
                    if (result.mobileNumbersAttempted.isNotEmpty()) {
                        adapter.updateClients(result.mobileNumbersAttempted)
                        viewModel.sendReportStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                    }
                    if (result.reportAvailableFailure.isNotEmpty()) {
                        val failedToSendMsg = getString(
                            R.string.msg_failed_to_send_contacts,
                            result.reportAvailableFailure.joinToString(",")
                        )
                        ViewUtil.showMessage(failedToSendMsg)
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(status.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        })
    }

    private fun addChipContact(contact: DeviceContact) {
        val chip = Chip(this)
        chip.text = contact.mobileNumber
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            val items = viewModel.contacts.value ?: arrayListOf()
            items.remove(contact)
            binding.tagChipGroup.removeView(chip)
            viewModel.contacts.value = items
        }
        binding.tagChipGroup.addView(chip)
    }


    private fun handleListeners() {
        ib_add_contact.setOnClickListener { showContactDialog() }
        btn_action.setOnClickListener {
            val contactList = viewModel.contacts.value ?: arrayListOf()
            if (contactList.isNotEmpty()) {
                viewModel.sendReportToClient()
            } else {
                ViewUtil.showMessage(R.string.msg_select_one_recipient)
            }
        }
        btn_back.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
        tv_expand.setOnClickListener {
            val isExpand = viewModel.isExpanded.value ?: false
            viewModel.isExpanded.postValue(!isExpand)
        }
    }

    private fun showContactDialog() {
        val list = ReportEnum.SendOutReportOption.values().map { it.label }
        dialogUtil.showListDialog(list, { dialogInterface, position ->
            when (position) {
                ReportEnum.SendOutReportOption.PHONE_BOOK.position -> {
                    showContacts()
                }
                else -> {
                    //do nothing for onw
                }
            }
            dialogInterface.dismiss()
        })
    }

    private fun showContacts() {
        IntentUtil.openPhoneBook(this, REQUEST_CODE_PHONE_BOOK)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_READ_CONTACTS -> {
                PermissionUtil.handlePermissionResult(
                    Manifest.permission.READ_CONTACTS,
                    permissions,
                    grantResults
                ) {
                    showContacts()
                }
            }
            PermissionUtil.REQUEST_CODE_CALL -> {
                PermissionUtil.handlePermissionResult(
                    Manifest.permission.CALL_PHONE,
                    permissions,
                    grantResults
                ) {
                    val mobileNumber = viewModel.tempMobileNumber ?: return@handlePermissionResult
                    IntentUtil.dialPhoneNumber(this, mobileNumber)
                }
                return
            } //end of request code call
        }
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_PHONE_BOOK -> {
                if (resultCode == Activity.RESULT_OK) {
                    val contactData = data?.data ?: return
                    contentResolver.query(contactData, null, null, null, null)?.run {
                        while (this.moveToNext()) {
                            val name =
                                this.getString(this.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            val phoneNo =
                                this.getString(this.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            updateContactLists(DeviceContact(name, phoneNo))
                        }
                        this.close()
                    }
                }
            }
        }
    }


    private fun updateContactLists(contact: DeviceContact) {
        val items = viewModel.contacts.value ?: arrayListOf()
        val contain = items.any { it.mobileNumber == contact.mobileNumber }
        if (!contain) {
            items.add(contact)
            addChipContact(contact)
        }
        viewModel.contacts.value = items
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_send_new_launches_reports
    }

    override fun getViewModelClass(): Class<SendNewLaunchesReportsViewModel> {
        return SendNewLaunchesReportsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}