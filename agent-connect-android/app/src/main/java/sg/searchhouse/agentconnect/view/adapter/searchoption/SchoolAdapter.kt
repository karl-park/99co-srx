package sg.searchhouse.agentconnect.view.adapter.searchoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemSchoolBinding
import sg.searchhouse.agentconnect.databinding.ListItemSchoolIndexBinding
import sg.searchhouse.agentconnect.model.api.lookup.LookupSchoolsResponse

class SchoolAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init { setHasStableIds(true) }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    var items: List<Any> = listOf()

    var selectedSchools: ArrayList<LookupSchoolsResponse.Schools.School> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SCHOOL -> {
                val binding = ListItemSchoolBinding.inflate(LayoutInflater.from(parent.context))
                SchoolViewHolder(binding)
            }
            VIEW_TYPE_INDEX -> {
                val binding = ListItemSchoolIndexBinding.inflate(LayoutInflater.from(parent.context))
                IndexViewHolder(binding)
            }
            else -> throw Throwable("Wrong view type")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SchoolViewHolder -> {
                val school = items[position] as LookupSchoolsResponse.Schools.School
                holder.binding.school = school
                holder.binding.isSelected = isSelected(school)
                holder.binding.root.setOnClickListener {
                    if (isSelected(school)) {
                        selectedSchools.remove(school)
                    } else {
                        selectedSchools.add(school)
                    }
                    notifyDataSetChanged()
                }
            }
            is IndexViewHolder -> {
                val index = items[position] as Index
                holder.binding.index = index.index
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is LookupSchoolsResponse.Schools.School -> {
                VIEW_TYPE_SCHOOL
            }
            is Index -> {
                VIEW_TYPE_INDEX
            }
            else -> {
                super.getItemViewType(position)
            }
        }
    }

    private fun isSelected(school: LookupSchoolsResponse.Schools.School): Boolean {
        return selectedSchools.any { it.id == school.id }
    }

    class Index(val index: String)

    class SchoolViewHolder(val binding: ListItemSchoolBinding) : RecyclerView.ViewHolder(binding.root)
    class IndexViewHolder(val binding: ListItemSchoolIndexBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_SCHOOL = 1
        const val VIEW_TYPE_INDEX = 2
    }
}