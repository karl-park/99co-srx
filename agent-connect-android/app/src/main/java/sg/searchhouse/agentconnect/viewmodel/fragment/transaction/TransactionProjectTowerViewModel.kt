package sg.searchhouse.agentconnect.viewmodel.fragment.transaction

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.TransactionRepository
import sg.searchhouse.agentconnect.model.api.transaction.TowerViewForLastSoldTransactionResponse
import sg.searchhouse.agentconnect.model.app.Empty
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class TransactionProjectTowerViewModel constructor(application: Application) :
    ApiBaseViewModel<TowerViewForLastSoldTransactionResponse>(
        application
    ) {

    @Inject
    lateinit var transactionRepository: TransactionRepository

    @Inject
    lateinit var applicationContext: Context

    val projectId = MutableLiveData<Int>()

    val blocks: LiveData<List<TowerViewForLastSoldTransactionResponse.BlocksItem>> =
        Transformations.map(mainResponse) { response ->
            response?.let { it -> getBlocks(it) }
        }

    val selectedBlock = MutableLiveData<TowerViewForLastSoldTransactionResponse.BlocksItem>()

    val isShowFloors: LiveData<Boolean> = Transformations.map(selectedBlock) {
        val floors = getSelectedFloors(it)
        val isEmpty = floors.isEmpty()
        val isSingle = floors.size == 1 && TextUtils.isEmpty(floors[0].first)
        !isEmpty && !isSingle
    }

    val selectedFloors: LiveData<List<Pair<String, List<Any>>>> =
        Transformations.map(selectedBlock) { block ->
            getSelectedFloors(block)
        }

    private fun getSelectedFloors(block: TowerViewForLastSoldTransactionResponse.BlocksItem): List<Pair<String, List<Any>>> {
        val normalisedUnits = getNormalisedUnitNumbers(block)
        return getValidUnits(block).sortedBy { unit ->
            unit.unitFloor.length
        }.sortedBy { unit ->
            unit.unitFloor
        }.groupBy { unit ->
            unit.unitFloor
        }.toList().reversed().map { floorUnits ->
            val units = normalisedUnits.map { unitNo ->
                floorUnits.second.find { unit ->
                    TextUtils.equals(unit.unitNo, unitNo)
                } ?: Empty()
            }
            Pair(floorUnits.first, units)
        }
    }

    val normalisedUnitNumbers: LiveData<List<String>> =
        Transformations.map(selectedBlock) { block ->
            getNormalisedUnitNumbers(block)
        }

    private fun getNormalisedUnitNumbers(block: TowerViewForLastSoldTransactionResponse.BlocksItem): List<String> {
        return getValidUnits(block).sortedBy { unit ->
            unit.unitNo
        }.sortedBy { unit ->
            unit.unitNo.length
        }.groupBy { unit ->
            unit.unitNo
        }.map { unit ->
            unit.key
        }.toList()
    }

    // Get only units with transactions available
    private fun getValidUnits(block: TowerViewForLastSoldTransactionResponse.BlocksItem): Sequence<TowerViewForLastSoldTransactionResponse.UnitsItem> {
        return block.units?.asSequence()?.filter { unit ->
            unit.lastSoldPrice > 0
        }?.toList()?.asSequence() ?: emptySequence()
    }

    init {
        viewModelComponent.inject(this)
    }

    private fun getBlocks(response: TowerViewForLastSoldTransactionResponse): List<TowerViewForLastSoldTransactionResponse.BlocksItem> {
        return response.results.towerAnalysisResult.blocks?.filter { block ->
            getValidUnits(block).count() > 0
        }?.sortedBy {
            it.block
        }?.sortedBy {
            it.block.length
        } ?: emptyList()
    }

    fun performRequest(projectId: Int) {
        performRequest(transactionRepository.loadTowerViewForLastSoldTransaction(projectId))
    }

    override fun shouldResponseBeOccupied(response: TowerViewForLastSoldTransactionResponse): Boolean {
        return getBlocks(response).isNotEmpty()
    }
}