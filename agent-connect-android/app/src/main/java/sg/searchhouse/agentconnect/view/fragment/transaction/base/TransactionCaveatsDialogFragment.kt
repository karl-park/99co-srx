package sg.searchhouse.agentconnect.view.fragment.transaction.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_fragment_transaction_caveats.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentTransactionCaveatsBinding
import sg.searchhouse.agentconnect.model.api.transaction.TowerViewForLastSoldTransactionResponse
import sg.searchhouse.agentconnect.view.adapter.transaction.caveat.TransactionCaveatAdapter
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment

class TransactionCaveatsDialogFragment : FullScreenDialogFragment() {

    companion object {
        const val TAG = "transaction_caveats_dialog_fragment"
        private const val ARGUMENT_KEY_UNIT = "ARGUMENT_KEY_UNIT"

        fun newInstance(unit: TowerViewForLastSoldTransactionResponse.UnitsItem): TransactionCaveatsDialogFragment {
            val dialogFragment =
                TransactionCaveatsDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_UNIT, unit)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    private lateinit var unit: TowerViewForLastSoldTransactionResponse.UnitsItem
    private val adapter = TransactionCaveatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit =
            arguments?.getSerializable(ARGUMENT_KEY_UNIT) as TowerViewForLastSoldTransactionResponse.UnitsItem
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<DialogFragmentTransactionCaveatsBinding>(
            inflater,
            R.layout.dialog_fragment_transaction_caveats,
            container,
            false
        )
        binding.unit = unit
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setupList()
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
        adapter.items = unit.previousUnitTransactions ?: emptyList()
        adapter.notifyDataSetChanged()
    }

    private fun setOnClickListeners() {
        toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }
    }
}