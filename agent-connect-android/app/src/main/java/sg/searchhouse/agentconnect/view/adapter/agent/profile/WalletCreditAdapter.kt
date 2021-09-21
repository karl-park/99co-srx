package sg.searchhouse.agentconnect.view.adapter.agent.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemCreditBinding
import sg.searchhouse.agentconnect.model.api.agent.UserCreditPO


class WalletCreditAdapter(
    private var onSelectWalletCredit: (UserCreditPO) -> Unit,
    private var onClickLearnMore: (UserCreditPO) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items: ArrayList<UserCreditPO> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemCreditViewHolder(
            ListItemCreditBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemCreditViewHolder -> {
                val credit = items[position]
                holder.binding.credit = credit
                holder.binding.showDivider = position != items.size - 1

                holder.binding.ibTopUpCredits.setOnClickListener {
                    onSelectWalletCredit.invoke(credit)
                }
                holder.binding.btnLearnMore.setOnClickListener {
                    onClickLearnMore.invoke(credit)
                }
            }
        }
    }

    class ListItemCreditViewHolder(val binding: ListItemCreditBinding) :
        RecyclerView.ViewHolder(binding.root)
}