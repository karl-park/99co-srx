package sg.searchhouse.agentconnect.view.activity.chat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_chat_messaging.*
import kotlinx.android.synthetic.main.layout_chat_listing_preview.*
import kotlinx.android.synthetic.main.layout_messaging_screen_action_bar.*
import kotlinx.android.synthetic.main.layout_messaging_screen_sender.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityChatMessagingBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum.ChatMessagingParamType
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum.ConversationType
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.livedata.NetworkAvailabilityLiveData
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationListingBlastPO
import sg.searchhouse.agentconnect.model.api.chat.SsmMessagePO
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.chat.ChatNewNotificationEvent
import sg.searchhouse.agentconnect.event.chat.ChatRefreshEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.agent.cv.AgentCvActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.listing.ListingDetailsActivity
import sg.searchhouse.agentconnect.view.adapter.chat.ChatMessagingListAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.search.SrxChatBlastMessagingListingAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.chat.ChatMessagingViewModel
import java.io.File
import java.util.*

class ChatMessagingActivity :
    ViewModelActivity<ChatMessagingViewModel, ActivityChatMessagingBinding>() {

    private lateinit var adapter: ChatMessagingListAdapter
    private lateinit var listingAdapter: SrxChatBlastMessagingListingAdapter

    private var imageUri: Uri? = null

    private var uploadImageProgressDialog: AlertDialog? = null

    companion object {
        const val EXTRA_KEY_USER = "EXTRA_KEY_USER"
        const val EXTRA_KEY_AGENT = "EXTRA_KEY_AGENT"
        const val EXTRA_KEY_CONVERSATION_ID = "EXTRA_KEY_CONVERSATION_ID"

        private const val EXTRA_KEY_LISTING_TYPE_ID = "EXTRA_KEY_LISTING_ID"

        const val REQUEST_CODE_CAMERA = 1
        const val REQUEST_CODE_GALLERY = 2
        const val REQUEST_CODE_IMAGE_CAPTION = 3

        // listingTypeId: Listing type and ID separated by comma, example: P,12349876
        fun launch(
            activity: Activity,
            user: UserPO? = null,
            agent: AgentPO? = null,
            conversationId: String? = null,
            listingTypeId: String? = null
        ) {
            val intent = Intent(activity, ChatMessagingActivity::class.java)
            user?.run { intent.putExtra(EXTRA_KEY_USER, this) }
            agent?.run { intent.putExtra(EXTRA_KEY_AGENT, this) }
            conversationId?.run { intent.putExtra(EXTRA_KEY_CONVERSATION_ID, this) }
            listingTypeId?.run {
                val list = split(",")
                if (list.size != 2 && list[1].toIntOrNull() == null) {
                    throw IllegalArgumentException("Wrong format of `listingTypeId`! Input: $this, the valid format is e.g. P,123456")
                }
                intent.putExtra(EXTRA_KEY_LISTING_TYPE_ID, this)
            }
            activity.startActivity(intent)
        }

        fun launchByClearTop(
            activity: Activity,
            user: UserPO? = null,
            conversationId: String? = null
        ) {
            val intent = Intent(activity, ChatMessagingActivity::class.java)
            user?.run { intent.putExtra(EXTRA_KEY_USER, this) }
            conversationId?.run { intent.putExtra(EXTRA_KEY_CONVERSATION_ID, this) }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraIntent()
        setupViews()
        observeRxBuses()
        observeLiveData()
    }

    private fun setupViews() {
        layout_chat_sender.binding.layoutRoot.setupLayoutAnimation()
        setupListAndAdapter()
        setupOnClickListeners()
    }

    private fun setupExtraIntent() {
        when {
            intent.hasExtra(EXTRA_KEY_USER) -> {
                intent.getSerializableExtra(EXTRA_KEY_USER)?.run {
                    viewModel.paramType = ChatMessagingParamType.USER
                    viewModel.user.postValue(this as UserPO)
                } ?: ErrorUtil.handleError(this, R.string.error_missing_user)
            }
            intent.hasExtra(EXTRA_KEY_AGENT) -> {
                intent.getSerializableExtra(EXTRA_KEY_AGENT)?.run {
                    viewModel.paramType = ChatMessagingParamType.AGENT
                    viewModel.agent.postValue(this as AgentPO)
                } ?: ErrorUtil.handleError(this, R.string.error_missing_agent)
            }
            intent.hasExtra(EXTRA_KEY_CONVERSATION_ID) -> {
                intent.getStringExtra(EXTRA_KEY_CONVERSATION_ID)?.run {
                    viewModel.paramType = ChatMessagingParamType.CONVERSATION_ID
                    viewModel.conversationId.postValue(this)
                } ?: ErrorUtil.handleError(this, R.string.error_missing_conversation)
            }
            else -> {
                throw Throwable("Wrong Intent type")
            }
        }
        val listingTypeId =
            intent.getStringExtra(EXTRA_KEY_LISTING_TYPE_ID)?.split(",") ?: emptyList()
        if (listingTypeId.size == 2) {
            val listingType = listingTypeId[0]
            val listingId = listingTypeId[1].toIntOrNull()
                ?: throw IllegalArgumentException("Listing ID must be integer!")
            viewModel.listingTypeId.postValue(Pair(listingType, listingId))
            et_messaging_text.setText(R.string.template_message_enquire_listing)
        }
    }

    private fun setupListAndAdapter() {
        //message list adapter
        adapter = ChatMessagingListAdapter(
            this,
            viewModel.messages,
            onListingClickListener = { listingType, listingId ->
                ListingDetailsActivity.launch(this, listingId, listingType)
            })
        list_messages.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        list_messages.adapter = adapter

        listingAdapter =
            SrxChatBlastMessagingListingAdapter(this) { showChatMessagingScreen(it) }
        list_listings.layoutManager = LinearLayoutManager(this)
        list_listings.adapter = listingAdapter
    }

    private fun observeRxBuses() {
        listenRxBus(ChatNewNotificationEvent::class.java) { event ->
            val conversationId = viewModel.conversation.value?.conversationId ?: return@listenRxBus
            if (conversationId == event.conversationId) {
                viewModel.loadMessagesByMessageIdFromServer(
                    conversationId = conversationId,
                    messageId = event.messageId
                )
            }
        }
    }

    private fun observeLiveData() {
        NetworkAvailabilityLiveData(this).observeNotNull(this) {
            viewModel.isNetworkConnected.postValue(it)
        }

        viewModel.user.observeNotNull(this) {
            layout_messaging_action_bar.populateUserInfo(userPO = it)
            viewModel.findSsmConversationIdByUserId(it.id)
        }

        viewModel.agent.observeNotNull(this) {
            layout_messaging_action_bar.populateAgentInfo(agentPO = it)
            viewModel.findSsmConversationIdByUserId(it.userId)
        }

        viewModel.conversationId.observeNotNull(this) { conversationId ->
            if (viewModel.isConversationRetrieved.value != true) {
                viewModel.getConversationById(
                    conversationId = conversationId.toIntOrNull() ?: return@observeNotNull
                )
            }
        }

        viewModel.conversation.observeNotNull(this) { conversation ->
            layout_messaging_action_bar.populateConversationInfo(conversation)
            //call load message api except blast new type
            if (conversation.getSsmConversationType() != ConversationType.BLAST_NEW_TYPE) {
                viewModel.initialLoadMessages()
                adapter.setConversationType(conversation.getSsmConversationType())
            } else {
                val convoId = conversation.conversationId.toIntOrNull() ?: return@observeNotNull
                viewModel.performGetListingBlastConversations(convoId)
            }
        }

        viewModel.getConversationResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    val response = it.body ?: return@observeNotNull
                    listingAdapter.updateConversationList(response.listingsBlastConvos)
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.resetUnreadCountResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.findSsmMessagesResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.apiCallDefaultResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    onBackPressed()
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.createSsmConversationResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    // do nothing
                }
            }
        }

        viewModel.findMessagesByMessageIdResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> ViewUtil.showMessage(it.error?.error)
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.sendMessageResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    uploadImageProgressDialog?.dismiss()
                    val response = it.body ?: return@observeNotNull
                    addAllNewMessages(response.newMessages)
                    layout_chat_sender.clearMessageAfterSending()
                    clearPreviewListing()
                }
                FAIL -> {
                    uploadImageProgressDialog?.dismiss()
                    ViewUtil.showMessage(it.error?.error)
                }
                ERROR -> {
                    uploadImageProgressDialog?.dismiss()
                }
                else -> {
                    //do nothing
                }
            }
        }

        //Start managing messages
        viewModel.storedMessages.observeNotNull(this) { items ->
            viewModel.removeLoadingIndicator()
            if (items?.isNotEmpty() == true) {
                addAllNewMessages(items)
            }
        }

        viewModel.serverMessages.observeNotNull(this) { items ->
            viewModel.removeLoadingIndicator()
            if (items?.isNotEmpty() == true) {
                addAllNewMessages(items)
            }
        }

        viewModel.newMessages.removeObservers(this)
        viewModel.newMessages.observeNotNull(this) { newMessages ->
            if (newMessages.isNotEmpty()) {
                addAllNewMessages(newMessages)
            }
        }

        viewModel.listingTypeId.observeNotNull(this) {
            viewModel.performGetPreviewListing(it.first, it.second)
        }

        viewModel.previewListing.observe(this) {
            layout_chat_sender.binding.listingPO = it?.fullListing?.listingDetailPO?.listingPO
            layout_chat_sender.binding.invalidateAll()
        }

        viewModel.getPreviewListingResponse.observe(this) {
            layout_chat_sender.binding.previewStatus = it?.key
            layout_chat_sender.binding.invalidateAll()
        }
    }

    private fun addAllNewMessages(newMessages: List<SsmMessagePO>) {
        // Prevent duplicate
        val existingMessages = viewModel.messages.filterIsInstance(SsmMessagePO::class.java)
        val messagesToInsert = if (existingMessages.isNotEmpty()) {
            newMessages.filter { thisMessage ->
                !existingMessages.any { it.messageId == thisMessage.messageId }
            }
        } else {
            newMessages
        }
        viewModel.messages.addAll(0, populateMessagesWithGroupedDate(messagesToInsert))
        adapter.notifyDataSetChanged()
    }

    private fun clearPreviewListing() {
        viewModel.listingTypeId.postValue(null)
        viewModel.getPreviewListingResponse.postValue(null)
        viewModel.previewListing.postValue(null)
    }

    private fun setupOnClickListeners() {
        tb_messaging_header.setNavigationOnClickListener { onBackPressed() }

        ib_messaging_phone.setOnClickListener { onCallUser() }

        tv_messaging_name.setOnClickListener { showMessagingInfoOrAgentCv() }

        ib_messaging_upload.setOnClickListener { showImagePickerDialog() }

        ib_messaging_info.setOnClickListener { showInfoListDialog() }

        ViewUtil.listenVerticalScrollTopEnd(list_messages, reachTop = {
            if (viewModel.canLoadMore() && viewModel.messages.last() !is Loading) {
                viewModel.messages.add(Loading())
                adapter.notifyItemChanged(0)
                viewModel.loadMoreMessages()
            }
        })

        layout_chat_sender.onSendMessage = { message -> sendMessage(message) }

        btn_cancel.setOnClickListener {
            viewModel.getPreviewListingResponse.postValue(null)
            viewModel.previewListing.postValue(null)
            et_messaging_text.setText("")
        }
    }

    private fun populateMessagesWithGroupedDate(
        messageList: List<SsmMessagePO>,
        isNewMessages: Boolean = true
    ): ArrayList<Any> {
        val mapByDateSent =
            messageList.asSequence().groupBy { it.getFormattedDateSent(this) }.toList()
        val result: ArrayList<Any> = arrayListOf()
        mapByDateSent.map { date ->
            result.addAll(date.second)
            if (!viewModel.messages.contains(date.first)) {
                return@map result.add(date.first)
            } else if (!isNewMessages) {
                viewModel.messages.remove(date.first)
                return@map result.add(date.first)
            } else {
                return@map println(date.first)
            }
        }
        return result
    }

    private fun showImagePickerDialog() {
        dialogUtil.showImagePickerDialog { _, selectedOption ->
            when (selectedOption) {
                resources.getString(R.string.label_gallery) -> {
                    IntentUtil.openGallery(
                        activity = this,
                        requestCode = REQUEST_CODE_GALLERY
                    )
                }
                resources.getString(R.string.label_camera) -> {
                    imageUri = ImageUtil.generateImageFile(this)
                    IntentUtil.openCamera(this, REQUEST_CODE_CAMERA, imageFileUri = imageUri)
                }
                else -> throw Throwable("Wrong image picker type")
            }
        }
    }

    private fun showInfoListDialog() {
        dialogUtil.showListDialog(
            listOf(R.string.label_blacklist, R.string.action_delete),
            { _, position ->
                when (position) {
                    0 -> {
                        dialogUtil.showActionDialog(R.string.msg_blacklist_one_agent) {
                            viewModel.blackListAgent()
                        }
                    }
                    1 -> {
                        dialogUtil.showActionDialog(R.string.msg_delete_one_conversation) {
                            viewModel.deleteConversation()
                        }
                    }
                    else -> throw Throwable("Wrong Type")
                }
            },
            null
        )
    }

    private fun sendMessage(inputText: String) {
        when (viewModel.paramType) {
            ChatMessagingParamType.USER, ChatMessagingParamType.AGENT -> viewModel.createSsmConversation {
                viewModel.sendMessage(message = inputText)
            }
            ChatMessagingParamType.CONVERSATION_ID -> viewModel.sendMessage(message = inputText)
        }
    }

    private fun showChatMessagingScreen(conversation: SsmConversationListingBlastPO) {
        launch(this, conversationId = conversation.conversationId)
        finish()
    }

    private fun showMessagingInfoOrAgentCv() {
        val conversationPO = viewModel.conversation.value ?: return
        when (conversationPO.getSsmConversationType()) {
            ConversationType.GROUP_CHAT, ConversationType.BLAST -> {
                ChatMessagingInfoActivity.launch(this, conversationPO)
            }
            ConversationType.ONE_TO_ONE -> {
                if (conversationPO.otherUserAgentInd) {
                    AuthUtil.checkModuleAccessibility(
                        module = AccessibilityEnum.AdvisorModule.AGENT_CV,
                        onSuccessAccessibility = {
                            val agentId = NumberUtil.toInt(conversationPO.otherUser)
                                ?: return@checkModuleAccessibility
                            AgentCvActivity.launch(this, agentId)
                        }, onFailAccessibility = {
                            //do nothing and won't go to agent cv
                        })
                }
            }
            else -> {
                //do nothing
            }
        }
    }

    private fun onCallUser() {
        val phoneNumber = viewModel.conversation.value?.otherUserNumber ?: return
        IntentUtil.dialPhoneNumber(this, phoneNumber)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_CALL -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.conversation.value?.let {
                        IntentUtil.dialPhoneNumber(this, it.otherUserNumber)
                    }
                }
                return
            }
            PermissionUtil.REQUEST_CODE_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    imageUri = ImageUtil.generateImageFile(this)
                    IntentUtil.openCamera(this, REQUEST_CODE_CAMERA, imageFileUri = imageUri)
                }
            }
            PermissionUtil.REQUEST_CODE_READ_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    IntentUtil.openGallery(
                        activity = this,
                        requestCode = REQUEST_CODE_GALLERY
                    )
                }
            }

        }//end of when
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    ChatImageCaptionActivity.launch(
                        this@ChatMessagingActivity,
                        imageUri.toString(),
                        REQUEST_CODE_IMAGE_CAPTION
                    )
                }
                REQUEST_CODE_GALLERY -> {
                    imageUri = data?.data ?: return
                    ChatImageCaptionActivity.launch(
                        this,
                        imageUri.toString(),
                        REQUEST_CODE_IMAGE_CAPTION
                    )
                }
                REQUEST_CODE_IMAGE_CAPTION -> {
                    val caption =
                        data?.getStringExtra(ChatImageCaptionActivity.EXTRA_OUTPUT_CAPTION)
                    val rotateAngle =
                        data?.getFloatExtra(ChatImageCaptionActivity.EXTRA_OUTPUT_ROTATE_ANGLE, 0f)
                            ?: 0f

                    imageUri?.let {
                        uploadImageProgressDialog =
                            dialogUtil.showProgressDialog(R.string.progress_dialog_upload_image)
                        maybeRotateImageFileAsync(rotateAngle) {
                            val maybeRotatedFile = it ?: run {
                                uploadImageProgressDialog?.dismiss()
                                return@maybeRotateImageFileAsync
                            }
                            sendImage(maybeRotatedFile, caption = caption)
                            imageUri = null
                        }
                    }
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun maybeRotateImageFileAsync(rotateAngle: Float, onComplete: (File?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val thisImageUri = imageUri ?: run {
                onComplete.invoke(null)
                return@launch
            }
            val bitmap =
                ImageUtil.getBitmapFromImageUrl(applicationContext, thisImageUri.toString())
                    ?: run {
                        onComplete.invoke(null)
                        return@launch
                    }
            if (rotateAngle != 0f) {
                val rotated = ImageUtil.rotateBitmap(bitmap, rotateAngle)
                onComplete.invoke(
                    ImageUtil.getFileFromBitmap(
                        context = this@ChatMessagingActivity,
                        bitmap = rotated
                    )
                )
            } else {
                onComplete.invoke(ImageUtil.getFileFromUri(applicationContext, thisImageUri))
            }
        }
    }

    private fun sendImage(inputFile: File?, caption: String? = null) {
        val file = inputFile ?: return ViewUtil.showMessage("Cannot upload photo")
        when (viewModel.paramType) {
            ChatMessagingParamType.USER, ChatMessagingParamType.AGENT -> viewModel.createSsmConversation {
                viewModel.sendImage(
                    image = file,
                    caption = caption
                )
            }
            ChatMessagingParamType.CONVERSATION_ID -> viewModel.sendImage(
                image = file,
                caption = caption
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        RxBus.publish(ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_chat_messaging
    }

    override fun getViewModelClass(): Class<ChatMessagingViewModel> {
        return ChatMessagingViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}