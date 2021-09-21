package sg.searchhouse.agentconnect.view.adapter.calculator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemCalculatorBinding
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum.CalculatorType

class CalculatorMenuAdapter(
    private val items: List<CalculatorType>,
    private var onSelect: (type: CalculatorType) -> Unit
) : RecyclerView.Adapter<CalculatorMenuAdapter.CalculatorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculatorViewHolder {
        return CalculatorViewHolder(
            ListItemCalculatorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: CalculatorViewHolder, position: Int) {
        val calculatorType = items[position]
        holder.binding.calculatorType = calculatorType
        holder.binding.layoutCard.setOnClickListener { onSelect.invoke(calculatorType) }
    }

    class CalculatorViewHolder(val binding: ListItemCalculatorBinding) :
        RecyclerView.ViewHolder(binding.root)

}