package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.SeekBarLabeledBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnSeekBarProgressChangeListener
import kotlin.math.max

class LabeledSeekBar(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    val binding: SeekBarLabeledBinding =
        SeekBarLabeledBinding.inflate(LayoutInflater.from(context), this, true)

    private var labels: List<String>? = null

    // If you want to set default value, use set `binding.seekBar.progress` in `postDelayed` outside
    // Because the label position can't be initialized correctly if progress is not zero
    fun <T> setup(
        labels: List<String>?,
        values: List<T>,
        onSelect: (T) -> Unit
    ) {
        if (labels != null && labels.size != values.size) throw IllegalStateException("Size of labels and values should be the same!")
        if (values.size < 2) throw IllegalStateException("values must be at least 2!")
        // Note: Order-sensitive
        this.labels = labels
        setupSeekBar(values, onSelect)
    }

    private fun <T> setupSeekBar(values: List<T>, onSelect: (T) -> Unit) {
        binding.seekBar.max = values.size - 1
        binding.seekBar.incrementProgressBy(1)
        binding.seekBar.setOnSeekBarProgressChangeListener { progress, _ ->
            onSelect.invoke(values[progress])
            updateLabel(progress)
        }
    }

    private fun updateLabel(progress: Int) {
        labels?.run {
            val label = this[progress]
            binding.tvSeek.text = label
            post { updateLabelPosition() }
        }
    }

    fun updateLabelPosition() {
        val currentPosition = binding.seekBar.progress
        val total = labels?.size ?: return
        if (total < 2) return
        val layoutParams = getTextLayoutParams()
        val seekBarRadius = context.resources.getDimensionPixelSize(R.dimen.size_seek_handle) * 0.5
        val layoutWidth = binding.seekBar.width - 2 * seekBarRadius
        val textWidth = binding.tvSeek.width
        when {
            currentPosition < (total - 1) / 2 -> {
                val rawStartMargin =
                    (layoutWidth * (currentPosition * 1.0 / (total - 1)) - 0.5 * textWidth + seekBarRadius).toInt()
                layoutParams.gravity = Gravity.START
                layoutParams.marginStart = max(rawStartMargin, 0)
                layoutParams.marginEnd = 0
                binding.tvSeek.layoutParams = layoutParams
            }
            currentPosition == (total - 1) / 2 -> {
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                binding.tvSeek.layoutParams = layoutParams
            }
            else -> {
                val rawEndMargin =
                    (layoutWidth * ((total - currentPosition - 1) * 1.0 / (total - 1)) - 0.5 * textWidth + seekBarRadius).toInt()
                layoutParams.gravity = Gravity.END
                layoutParams.marginStart = 0
                layoutParams.marginEnd = max(rawEndMargin, 0)
                binding.tvSeek.layoutParams = layoutParams
            }
        }
    }

    private fun getTextLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }
}