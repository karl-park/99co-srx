package sg.searchhouse.agentconnect.view.widget.property

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import sg.searchhouse.agentconnect.databinding.SelectorBathroomBinding
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum.BathroomCount

class BathroomSelector(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    var bathroomCounts = listOf<BathroomCount>()

    val binding: SelectorBathroomBinding =
        SelectorBathroomBinding.inflate(LayoutInflater.from(context), this, true)

    var onBathroomsSelect: ((bathroomCounts: List<BathroomCount>) -> Unit)? = null

    init {
        binding.bathroomSelector = this

        binding.btnBathroomCountAny.button.setOnClickListener {
            selectBathroom(BathroomCount.ANY)
        }

        binding.btnBathroomCountOne.button.setOnClickListener {
            selectBathroom(BathroomCount.ONE)
        }

        binding.btnBathroomCountTwo.button.setOnClickListener {
            selectBathroom(BathroomCount.TWO)
        }

        binding.btnBathroomCountThree.button.setOnClickListener {
            selectBathroom(BathroomCount.THREE)
        }

        binding.btnBathroomCountFour.button.setOnClickListener {
            selectBathroom(BathroomCount.FOUR)
        }

        binding.btnBathroomCountFive.button.setOnClickListener {
            selectBathroom(BathroomCount.FIVE)
        }

        binding.btnBathroomCountSixAndAbove.button.setOnClickListener {
            selectBathroom(BathroomCount.SIX_AND_ABOVE)
        }

        selectBathroom(BathroomCount.ANY)
    }

    fun selectBathroom(bathroomCount: BathroomCount) {
        bathroomCounts = when {
            bathroomCount == BathroomCount.ANY -> // Any
                listOf(BathroomCount.ANY)
            bathroomCounts.contains(bathroomCount) -> {
                // Already have, un-select
                val filtered = bathroomCounts.filter { it != bathroomCount }
                if (filtered.isEmpty()) {
                    listOf(BathroomCount.ANY)
                } else {
                    filtered
                }
            }
            else -> {
                // Select
                (bathroomCounts + bathroomCount).filter {
                    it != BathroomCount.ANY
                }
            }
        }
        binding.bathroomSelector = this
        binding.executePendingBindings()
        onBathroomsSelect?.invoke(bathroomCounts)
    }
}