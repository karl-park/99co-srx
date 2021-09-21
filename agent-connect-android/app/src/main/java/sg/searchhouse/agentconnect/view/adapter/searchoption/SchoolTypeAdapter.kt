package sg.searchhouse.agentconnect.view.adapter.searchoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemSchoolTypeBinding
import sg.searchhouse.agentconnect.model.api.lookup.LookupSchoolsResponse.Schools.SchoolType

class SchoolTypeAdapter : RecyclerView.Adapter<SchoolTypeAdapter.SchoolTypeViewHolder>() {
    var items: List<SchoolType> = listOf()
    var onClickListener: ((schoolType: SchoolType) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolTypeViewHolder {
        val binding = ListItemSchoolTypeBinding.inflate(LayoutInflater.from(parent.context))
        return SchoolTypeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: SchoolTypeViewHolder, position: Int) {
        val schoolType = items[position]
        holder.binding.schoolType = schoolType
        holder.binding.root.setOnClickListener { onClickListener?.invoke(schoolType) }
    }

    class SchoolTypeViewHolder(val binding: ListItemSchoolTypeBinding) : RecyclerView.ViewHolder(binding.root)
}