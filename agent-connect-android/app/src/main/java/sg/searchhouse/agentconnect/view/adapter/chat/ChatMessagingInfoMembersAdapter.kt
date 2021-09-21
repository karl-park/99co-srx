package sg.searchhouse.agentconnect.view.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemChatMessageMemberBinding
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.util.SessionUtil

class ChatMessagingInfoMembersAdapter(
    private val members: ArrayList<UserPO>,
    private val onCallMember: ((Int) -> Unit),
    private val onMessageMember: ((UserPO) -> Unit),
    private val onRemoveMember: ((ArrayList<UserPO>) -> Unit),
    private val onViewAgentCv: ((UserPO) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val currentUser = SessionUtil.getCurrentUser()
    private val expandedMembers = arrayListOf<Int>()
    private var isShowRemoveButton: Boolean = false

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemChatMessagingMemberViewHolder(
            ListItemChatMessageMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return members.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemChatMessagingMemberViewHolder -> {
                val member = members[position]

                holder.binding.layoutProfileIcon.populateByInitialLetter(member.photo, member.name)
                holder.binding.member = member
                holder.binding.isExpand = expandedMembers.contains(member.id)

                holder.binding.isShowCallButton = true
                holder.binding.isShowChatButton = true
                holder.binding.isShowRemoveButton = isShowRemoveButton

                //if member id is same with login id -> hide call and chat button
                currentUser?.let {
                    if (it.id == member.id) {
                        holder.binding.isShowCallButton = false
                        holder.binding.isShowChatButton = false
                        holder.binding.isShowRemoveButton = false
                    }
                }

                //Event lists
                holder.binding.rlMemberInfo.setOnClickListener {
                    //Add remove expanded
                    if (expandedMembers.contains(member.id)) {
                        expandedMembers.remove(member.id)
                    } else {
                        expandedMembers.add(member.id)
                    }
                    holder.binding.isExpand = expandedMembers.contains(member.id)
                }
                holder.binding.ibCallMember.setOnClickListener {
                    onCallMember.invoke(member.mobileLocalNum)
                }
                holder.binding.btnMemberCv.setOnClickListener {
                    onViewAgentCv.invoke(member)
                }
                holder.binding.ibMessageMember.setOnClickListener {
                    onMessageMember.invoke(member)
                }
                holder.binding.ibRemoveMember.setOnClickListener {
                    onRemoveMember.invoke(arrayListOf(member))
                }
            }
        }
    }

    fun setShowingRemoveButtonFlag(isShowRemoveButton: Boolean) {
        this.isShowRemoveButton = isShowRemoveButton
        notifyDataSetChanged()
    }

    class ListItemChatMessagingMemberViewHolder(val binding: ListItemChatMessageMemberBinding) :
        RecyclerView.ViewHolder(binding.root)
}