package sg.searchhouse.agentconnect.view.fragment.main.chat

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.dialog_fragment_chat_customer_enquiry.*
import kotlinx.android.synthetic.main.dialog_fragment_chat_customer_enquiry.view.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentChatCustomerEnquiryBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.event.chat.ChatRefreshEvent
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.PermissionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.viewmodel.fragment.main.chat.ChatCustomerEnquiryViewModel

class ChatCustomerEnquiryDialogFragment : DialogFragment() {

    private var ssmConversationPO: SsmConversationPO? = null

    private lateinit var mContext: Context
    private lateinit var binding: DialogFragmentChatCustomerEnquiryBinding
    private lateinit var viewModel: ChatCustomerEnquiryViewModel

    companion object {
        private const val TAG_CHAT_CUSTOMER_ENQUIRY = "tag_chat_customer_enquiry"
        private const val SSM_CONVERSATION = "ssm_conversation"

        //getting ssm conversation po
        fun newInstance(conversation: SsmConversationPO): ChatCustomerEnquiryDialogFragment {
            val dialogFragment = ChatCustomerEnquiryDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(SSM_CONVERSATION, conversation)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(fragmentManager: FragmentManager, conversation: SsmConversationPO) {
            newInstance(conversation).show(fragmentManager, TAG_CHAT_CUSTOMER_ENQUIRY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getSerializable(SSM_CONVERSATION)?.let { data ->
                ssmConversationPO = data as SsmConversationPO
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_fragment_chat_customer_enquiry,
            container,
            false
        )
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(ChatCustomerEnquiryViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ssmConversationPO?.let { conversation ->
            //RESET unread count
            if (conversation.unreadCount > 0) {
                viewModel.resetUnreadCount(conversation)
            }
            //GET enquiry
            conversation.enquiry?.let { srxAgentEnquiryPO ->
                viewModel.srxAgentEnquiryPO.postValue(srxAgentEnquiryPO)

                if (!TextUtils.isEmpty(srxAgentEnquiryPO.name)) {
                    binding.root.layout_consumer_profile.populateByInitialLetter(
                        "",
                        srxAgentEnquiryPO.name
                    )
                }
                if (!TextUtils.isEmpty(srxAgentEnquiryPO.listingId)) {
                    //listing Id in srx agent enquiry po is string
                    //listing id in getListing api is integer
                    viewModel.populateListingInformation(
                        srxAgentEnquiryPO.listingId,
                        srxAgentEnquiryPO.listingType
                    )
                    viewModel.showListingInfo.postValue(true)
                } else {
                    viewModel.showListingInfo.postValue(false)
                }
            }
        }

        handleViewsListener()
        observeStatusLiveData()
    }

    private fun handleViewsListener() {
        ib_phone.setOnClickListener {
            if (PermissionUtil.requestCallPermission(requireActivity())) {
                callToEnquirer()
            }
        }

        ib_message.setOnClickListener {
            ssmConversationPO?.enquiry?.let {
                IntentUtil.openWhatsApp(
                    mContext,
                    "+${it.mobileCountryCode}${it.mobileLocalNum}",
                    ""
                )
            }
        }

        ib_delete_conversation.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun observeStatusLiveData() {
        viewModel.deleteEnquiryStatus.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> {
                    ssmConversationPO?.let { viewModel.deleteConversationsFromLocalDB(arrayListOf(it)) }
                    dialog?.dismiss()
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //do nothing include ERROR, LOADING and NEXT_ITEM_LOAD
                }
            }
        })
    }

    private fun showDeleteConfirmationDialog() {
        DialogUtil(mContext)
            .showActionDialog(R.string.msg_delete_enquiry) {
                viewModel.deleteEnquiry()
            }
    }

    @Throws(SecurityException::class)
    private fun callToEnquirer() {
        viewModel.srxAgentEnquiryPO.value?.let { it ->
            //intent
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + it.mobileLocalNum)
            )
            startActivity(intent)
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
                    callToEnquirer()
                }
                return
            }
        }//end of when
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        RxBus.publish(ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER)
    }
}