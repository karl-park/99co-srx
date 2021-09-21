package sg.searchhouse.agentconnect.view.adapter.chat

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.ApiConstant
import sg.searchhouse.agentconnect.databinding.ListItemChatMessageDateBinding
import sg.searchhouse.agentconnect.databinding.ListItemChatMessageReceivedBinding
import sg.searchhouse.agentconnect.databinding.ListItemChatMessageSentBinding
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.chat.SsmMessagePO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder
import java.text.ParseException

class ChatMessagingListAdapter(
    private val context: Context,
    private val messages: List<Any>,
    private val onListingClickListener: (listingType: String, listingId: String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dialogNewUtil = DialogUtil(context)
    private var conversationType: ChatEnum.ConversationType? = null

    companion object {
        const val VIEW_TYPE_RECEIVED = 1
        const val VIEW_TYPE_SENT = 0
        const val VIEW_TYPE_DATE = 2
        const val VIEW_TYPE_LOADING = 3
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_RECEIVED -> return ReceivedMessageViewHolder(
                ListItemChatMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_SENT -> return SentMessageViewHolder(
                ListItemChatMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_DATE -> return DateMessageViewHolder(
                ListItemChatMessageDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_LOADING -> return LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.loading_indicator,
                    parent,
                    false
                )
            )
            else -> {
                throw Throwable("Wrong View TYpe in ChatMessaging List Adapter")
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun getItemId(position: Int): Long {
        return when (messages[position]) {
            is SsmMessagePO -> {
                val message = messages[position] as SsmMessagePO
                message.messageId.toLongOrNull() ?: message.hashCode().toLong()
            }
            else -> {
                position.toLong()
            }
        }
    }

    fun setConversationType(type: ChatEnum.ConversationType?) {
        conversationType = type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReceivedMessageViewHolder -> {
                val message = messages[position] as SsmMessagePO
                holder.binding.message = message

                holder.binding.showOtherUserName =
                    !(conversationType == ChatEnum.ConversationType.ONE_TO_ONE || conversationType == ChatEnum.ConversationType.SRX_ANNOUNCEMENTS)

                holder.binding.isSystemMessage =
                    message.sourceUserId == ApiConstant.SSM_SYSTEM_SOURCE_ID

                holder.binding.tvReceivedMessage.visibility = when {
                    message.getImageCaption() != null -> View.VISIBLE
                    else -> View.GONE
                }

                holder.binding.cardReceivedImage.visibility = when {
                    !TextUtils.isEmpty(message.thumbUrl) -> View.VISIBLE
                    else -> View.GONE
                }

                try {
                    holder.binding.receivedDate = DateTimeUtil.convertDateToString(
                        DateTimeUtil.convertStringToDate(
                            message.dateSent,
                            DateTimeUtil.FORMAT_DATE_TIME_FULL
                        ), DateTimeUtil.FORMAT_TIME
                    )
                } catch (e: ParseException) {
                    ErrorUtil.handleError(context, R.string.exception_parse, e)
                } catch (e: IllegalArgumentException) {
                    ErrorUtil.handleError(context, R.string.exception_illegal_argument, e)
                }
                holder.binding.cardReceivedImage.setOnClickListener {
                    dialogNewUtil.showFullScreenImageDialog(
                        message.thumbUrl,
                        message.getImageCaption()
                    )
                }

                holder.binding.layoutListing.layoutChatListingDefault.layoutRoot.setOnClickListener {
                    invokeOnClickListing(message)
                }

                holder.binding.layoutListing.layoutChatListingDefault.layoutRoot.setOnClickListener {
                    invokeOnClickListing(message)
                }
            }
            is SentMessageViewHolder -> {
                val message = messages[position] as SsmMessagePO
                holder.binding.message = message

                holder.binding.tvSentMessage.visibility = when {
                    message.getImageCaption() != null -> View.VISIBLE
                    else -> View.GONE
                }

                holder.binding.cardSentImage.visibility = when {
                    !TextUtils.isEmpty(message.thumbUrl) -> View.VISIBLE
                    else -> View.GONE
                }

                try {
                    holder.binding.sendDate = DateTimeUtil.convertDateToString(
                        DateTimeUtil.convertStringToDate(
                            message.dateSent,
                            DateTimeUtil.FORMAT_DATE_TIME_FULL
                        ), DateTimeUtil.FORMAT_TIME
                    )
                } catch (e: ParseException) {
                    ErrorUtil.handleError(context, R.string.exception_parse, e)
                } catch (e: IllegalArgumentException) {
                    ErrorUtil.handleError(context, R.string.exception_illegal_argument, e)
                }

                holder.binding.cardSentImage.setOnClickListener {
                    dialogNewUtil.showFullScreenImageDialog(
                        message.thumbUrl,
                        message.getImageCaption()
                    )
                }

                holder.binding.layoutListing.layoutChatListingDefault.layoutRoot.setOnClickListener {
                    invokeOnClickListing(message)
                }

                holder.binding.layoutListing.layoutChatListingDefault.layoutRoot.setOnClickListener {
                    invokeOnClickListing(message)
                }
            }
            is DateMessageViewHolder -> {
                val dateString = messages[position] as String
                holder.binding.date = dateString
            }
            is LoadingViewHolder -> {
                println("Show loading indicator")
            }
            else -> {
                throw Throwable("Wrong Types in ChatMessagingAdapter..")
            }
        }
    }

    private fun invokeOnClickListing(message: SsmMessagePO) {
        message.listing?.let { listing ->
            onListingClickListener.invoke(
                listing.listingType,
                listing.id
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position]) {
            is SsmMessagePO -> {
                val message = messages[position] as SsmMessagePO
                if (message.isFromOtherUser) {
                    VIEW_TYPE_RECEIVED
                } else {
                    VIEW_TYPE_SENT
                }
            }
            is String -> VIEW_TYPE_DATE
            is Loading -> VIEW_TYPE_LOADING
            else -> throw  ClassCastException("Wrong view type in chat messaging list adapter")
        }
    }

    class SentMessageViewHolder(val binding: ListItemChatMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ReceivedMessageViewHolder(val binding: ListItemChatMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DateMessageViewHolder(val binding: ListItemChatMessageDateBinding) :
        RecyclerView.ViewHolder(binding.root)
}