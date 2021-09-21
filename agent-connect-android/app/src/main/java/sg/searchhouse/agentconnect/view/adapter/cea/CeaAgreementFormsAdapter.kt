package sg.searchhouse.agentconnect.view.adapter.cea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemCeaFormTypeBinding
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.*

class CeaAgreementFormsAdapter(
    private val forms: List<CeaFormType>,
    private var onSelectForm: (form: CeaFormType) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemCeaFormTypeViewHolder(
            ListItemCeaFormTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return forms.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemCeaFormTypeViewHolder -> {
                val formType = forms[position]
                holder.binding.form = formType
                holder.binding.cardCeaForm.setOnClickListener {
                    onSelectForm.invoke(formType)
                }
            }
        }
    }

    class ListItemCeaFormTypeViewHolder(val binding: ListItemCeaFormTypeBinding) :
        RecyclerView.ViewHolder(binding.root)
}