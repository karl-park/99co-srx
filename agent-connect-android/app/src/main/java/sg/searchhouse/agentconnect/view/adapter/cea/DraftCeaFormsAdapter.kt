package sg.searchhouse.agentconnect.view.adapter.cea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import sg.searchhouse.agentconnect.databinding.ListItemDraftCeaFormBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.UnsubmittedCeaFormPO
import sg.searchhouse.agentconnect.event.listing.user.SelectedCeaFormIdsToRemove

class DraftCeaFormsAdapter(
    private var onClickCeaFormListener: (form: UnsubmittedCeaFormPO) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var forms = emptyList<UnsubmittedCeaFormPO>()
    var selectedCeaFormIds = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemDraftCeaFormViewHolder(
            ListItemDraftCeaFormBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return forms.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemDraftCeaFormViewHolder -> {
                val ceaForm = forms[position]
                holder.binding.ceaForm = ceaForm
                holder.binding.isSelected = selectedCeaFormIds.contains(ceaForm.formId)
                holder.binding.cardDraftCeaForm.setOnClickListener {
                    if (selectedCeaFormIds.isNotEmpty()) {
                        toggleSelection(ceaForm, position)
                    } else {
                        onClickCeaFormListener.invoke(ceaForm)
                    }
                }
                holder.binding.cardDraftCeaForm.setOnLongClickListener {
                    if (selectedCeaFormIds.isEmpty()) {
                        toggleSelection(ceaForm, position)
                        true
                    } else {
                        false
                    }
                }
                holder.binding.checkBoxDraftCeaForm.setOnClickListener {
                    toggleSelection(ceaForm, position)
                }
            }
        }
    }

    private fun toggleSelection(ceaFormPO: UnsubmittedCeaFormPO, position: Int) {
        if (selectedCeaFormIds.contains(ceaFormPO.formId)) {
            selectedCeaFormIds.remove(ceaFormPO.formId)
        } else {
            selectedCeaFormIds.add(ceaFormPO.formId)
        }
        RxBus.publish(SelectedCeaFormIdsToRemove(selectedCeaFormIds))
        notifyItemChanged(position)
    }

    fun clearSelectedIds() {
        selectedCeaFormIds.clear()
        notifyDataSetChanged()
    }

    fun updateList(items: List<UnsubmittedCeaFormPO>) {
        forms = items
        notifyDataSetChanged()
    }

    class ListItemDraftCeaFormViewHolder(val binding: ListItemDraftCeaFormBinding) :
        RecyclerView.ViewHolder(binding.root)

}