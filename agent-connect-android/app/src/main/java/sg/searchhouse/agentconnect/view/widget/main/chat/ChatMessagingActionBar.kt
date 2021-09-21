package sg.searchhouse.agentconnect.view.widget.main.chat

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.layout_messaging_screen_action_bar.view.*
import kotlinx.android.synthetic.main.layout_ssm_profile.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutMessagingScreenActionBarBinding
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil

class ChatMessagingActionBar constructor(context: Context, attributeSet: AttributeSet? = null) :
    AppBarLayout(context, attributeSet) {

    val binding: LayoutMessagingScreenActionBarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_messaging_screen_action_bar,
        this,
        true
    )

    fun populateConversationInfo(ssmConversation: SsmConversationPO) {
        val conversationType =
            ChatEnum.ConversationType.values().find { it.value == ssmConversation.type }
        binding.conversationType = conversationType
        when (conversationType) {
            ChatEnum.ConversationType.BLAST -> {
                binding.name = ssmConversation.otherUserName
                layout_profile.layout_profile_icon.populateByInitialLetter(
                    ssmConversation.otherUserPhoto,
                    ssmConversation.otherUserName
                )
                binding.showPhoneCall = false
                binding.showInfo = true
            }
            ChatEnum.ConversationType.AGENT_ENQUIRY,
            ChatEnum.ConversationType.SRX_ANNOUNCEMENTS -> {
                binding.name = ssmConversation.otherUserName
                layout_profile.layout_profile_icon.populateByInitialLetter(
                    ssmConversation.otherUserPhoto,
                    ssmConversation.otherUserName
                )
                binding.showPhoneCall = false
                binding.showInfo = false
            }
            ChatEnum.ConversationType.GROUP_CHAT -> {
                binding.name = ssmConversation.title
                layout_profile.layout_profile_icon.populateByInitialLetter(
                    ssmConversation.otherUserPhoto,
                    ssmConversation.title
                )
                binding.showPhoneCall = false
                binding.showInfo = false
            }
            ChatEnum.ConversationType.BLAST_NEW_TYPE -> {
                binding.name = ssmConversation.title
                layout_profile.layout_profile_icon.populateByInitialLetter(
                    imageUrl = ssmConversation.otherUserPhoto,
                    name = ssmConversation.title,
                    showNormalImage = true
                )
                binding.showPhoneCall = false
                binding.showInfo = false
            }
            ChatEnum.ConversationType.ONE_TO_ONE -> {
                //for one to one type can be User or Agent
                //Messaging Name
                binding.name = ssmConversation.otherUserName
                layout_profile.layout_profile_icon.populateByInitialLetter(
                    ssmConversation.otherUserPhoto,
                    ssmConversation.otherUserName
                )
                when (ssmConversation.oneToOneType) {
                    ChatEnum.UserType.AGENT.value -> binding.showPhoneCall =
                        NumberUtil.isNaturalNumber(ssmConversation.otherUserNumber)
                    ChatEnum.UserType.PUBLIC.value -> binding.showPhoneCall = false
                    else -> println("Cannot check wrong one to one user type")
                }
                binding.showInfo = true
            }
            else -> ErrorUtil.handleError("Missing conversation type")
        }
    }

    fun populateAgentInfo(agentPO: AgentPO) {
        binding.conversationType = ChatEnum.ConversationType.ONE_TO_ONE
        binding.name = agentPO.name
        layout_profile.layout_profile_icon.populateByInitialLetter(agentPO.photo, agentPO.name)
        binding.showInfo = true
        binding.showPhoneCall =
            NumberUtil.isNaturalNumber(StringUtil.getNumbersFromString(agentPO.mobile))
    }

    fun populateUserInfo(userPO: UserPO) {
        binding.conversationType = ChatEnum.ConversationType.ONE_TO_ONE
        binding.name = userPO.name
        layout_profile.layout_profile_icon.populateByInitialLetter(userPO.photo, userPO.name)
        binding.showInfo = true
        binding.showPhoneCall = NumberUtil.isNaturalNumber(userPO.mobileLocalNum.toString())
    }

    fun populateSSMBlastInfo() {
        binding.conversationType = ChatEnum.ConversationType.BLAST
        binding.name = context.getString(R.string.label_ssm_blast)
        layout_profile.layout_profile_icon.populateByInitialLetter(
            "",
            context.getString(R.string.label_ssm_blast)
        )
        binding.showPhoneCall = false
        binding.showInfo = true
    }
}