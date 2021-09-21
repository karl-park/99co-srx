package sg.searchhouse.agentconnect.view.fragment.listing.portal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.dialog_fragment_auto_import_certified_listings.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentAutoImportCertifiedListingsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.model.app.DeviceContact
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting.CertifiedListingHelpDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal.AutoImportCertifiedListingsViewModel

class AutoImportCertifiedListingsDialogFragment :
    ViewModelFullWidthDialogFragment<AutoImportCertifiedListingsViewModel, DialogFragmentAutoImportCertifiedListingsBinding>() {

    companion object {
        const val TAG = "AUTO_IMPORT_CERTIFIED_LISTINGS_DIALOG_FRAGMENT"
        const val REQUEST_CODE_PHONE_BOOK = 200

        fun newInstance() = AutoImportCertifiedListingsDialogFragment()

        fun launch(fragmentManager: FragmentManager) {
            newInstance().show(fragmentManager, TAG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performRequest()
        observeLiveData()
        setupListeners()
        populateDescription()
    }

    private fun performRequest() {
        viewModel.performGetTemplate()
    }

    private fun populateDescription() {
        StringUtil.enableTextViewLink(
            tv_certified_listing_description,
            R.string.label_auto_import_certified_listings,
            R.string.label_learn_more,
            onClickListener = {
                activity?.run { CertifiedListingHelpDialogFragment.launch(supportFragmentManager) }
            }
        )
    }

    private fun observeLiveData() {
        viewModel.getTemplateResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }
        viewModel.sendOwnerCertificationResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    ViewUtil.showMessage("Successfully sent invitations")
                    dialog?.dismiss()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun setupListeners() {
        ib_add_contact.setOnClickListener {
            activity?.run {
                IntentUtil.openPhoneBook(this, REQUEST_CODE_PHONE_BOOK)
            }
        }

        btn_send_invite.setOnClickListener { viewModel.performSendInvite() }
    }

    private fun updateContactLists(contact: DeviceContact) {
        val items = viewModel.contacts.value ?: mutableListOf()
        val contain = items.any { it.mobileNumber == contact.mobileNumber }
        if (!contain) {
            items.add(contact)
            addChipContact(contact)
        }
        viewModel.contacts.value = items
    }

    private fun addChipContact(contact: DeviceContact) {
        val chip = Chip(activity)
        chip.text = contact.mobileNumber
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            val items = viewModel.contacts.value ?: mutableListOf()
            items.remove(contact)
            binding.tagChipGroup.removeView(chip)
            viewModel.contacts.value = items
        }
        binding.tagChipGroup.addView(chip)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_PHONE_BOOK -> {
                if (resultCode == Activity.RESULT_OK) {
                    val contactData = data?.data ?: return
                    val contentResolver = activity?.contentResolver ?: return
                    IntentUtil.getDeviceContactListFromIntentData(contentResolver, contactData)
                        .map {
                            updateContactLists(contact = it)
                        }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_auto_import_certified_listings
    }

    override fun getViewModelClass(): Class<AutoImportCertifiedListingsViewModel> {
        return AutoImportCertifiedListingsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}