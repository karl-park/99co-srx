package sg.searchhouse.agentconnect.view.widget.property

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import sg.searchhouse.agentconnect.databinding.SelectorBedroomBinding
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum.BedroomCount

class BedroomSelector(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    var bedroomCounts = listOf<BedroomCount>()

    val binding: SelectorBedroomBinding =
        SelectorBedroomBinding.inflate(LayoutInflater.from(context), this, true)

    var onBedroomsSelect: ((bedroomCounts: List<BedroomCount>) -> Unit)? = null

    init {
        binding.bedroomSelector = this
        binding.btnBedroomCountAny.button.setOnClickListener {
            selectBedroom(BedroomCount.ANY)
        }
        binding.btnBedroomCountOne.button.setOnClickListener {
            selectBedroom(BedroomCount.ONE)
        }
        binding.btnBedroomCountTwo.button.setOnClickListener {
            selectBedroom(BedroomCount.TWO)
        }
        binding.btnBedroomCountThree.button.setOnClickListener {
            selectBedroom(BedroomCount.THREE)
        }
        binding.btnBedroomCountFour.button.setOnClickListener {
            selectBedroom(BedroomCount.FOUR)
        }
        binding.btnBedroomCountFive.button.setOnClickListener {
            selectBedroom(BedroomCount.FIVE)
        }
        binding.btnBedroomCountSixAndAbove.button.setOnClickListener {
            selectBedroom(BedroomCount.SIX_AND_ABOVE)
        }
        selectBedroom(BedroomCount.ANY)
    }

    fun selectBedroom(bedroomCount: BedroomCount) {
        bedroomCounts = when {
            bedroomCount == BedroomCount.ANY -> // Any
                listOf(BedroomCount.ANY)
            bedroomCounts.contains(bedroomCount) -> {
                // Already have, un-select
                val filtered = bedroomCounts.filter { it != bedroomCount }
                if (filtered.isEmpty()) {
                    listOf(BedroomCount.ANY)
                } else {
                    filtered
                }
            }
            else -> {
                // Select
                (bedroomCounts + bedroomCount).filter {
                    it != BedroomCount.ANY
                }
            }
        }
        binding.bedroomSelector = this
        binding.executePendingBindings()
        onBedroomsSelect?.invoke(bedroomCounts)
    }
}