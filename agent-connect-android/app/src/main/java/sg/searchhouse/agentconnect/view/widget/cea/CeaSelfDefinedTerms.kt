package sg.searchhouse.agentconnect.view.widget.cea

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutCeaSelfDefinedTermsBinding
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTermPO
import sg.searchhouse.agentconnect.view.adapter.cea.CeaSelfDefinedTermsAdapter

class CeaSelfDefinedTerms constructor(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    var onClickCeaTermItem: ((CeaFormTermPO) -> Unit?)? = null
    var onRemoveCeaTermItem: ((Int) -> Unit?)? = null
    val items = ArrayList<CeaFormTermPO>()
    val adapter = CeaSelfDefinedTermsAdapter(
        items,
        onClickItem = { term -> onClickCeaTermItem?.invoke(term) }
    )

    val binding: LayoutCeaSelfDefinedTermsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_cea_self_defined_terms, this, true
    )

    val mBackground = ColorDrawable()
    val backgroundColor = ContextCompat.getColor(context, R.color.red)
    private val clearPaint = Paint()
    private val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_trash)
    val intrinsicWidth = deleteDrawable?.intrinsicWidth ?: 0
    val intrinsicHeight = deleteDrawable?.intrinsicHeight ?: 0

    init {
        binding.listSelfDefinedTerms.layoutManager = LinearLayoutManager(context)
        binding.listSelfDefinedTerms.adapter = adapter

        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        enableSwipe()
    }

    fun populateItems(list: ArrayList<CeaFormTermPO>) {
        items.clear()
        items.addAll(list)
        adapter.notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, items.size)
        onRemoveCeaTermItem?.invoke(position)
    }

    private fun enableSwipe() {
        //TODO: currently deleting item by swiping use only in cea defined terms -> if use in another screen, separate as file
        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                removeItem(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.height
                val isCancelled = dX == 0f && !isCurrentlyActive

                if (isCancelled) {
                    c.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        clearPaint
                    )
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return
                }
                //Change background color when swiping to delete
                mBackground.color = backgroundColor
                mBackground.setBounds(
                    (itemView.right + dX).toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                mBackground.draw(c)
                //Add delete trash icon by adjusting icon size
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight
                deleteDrawable?.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteDrawable?.setTint(ContextCompat.getColor(context, R.color.white_invertible))
                deleteDrawable?.draw(c)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(binding.listSelfDefinedTerms)
    }

}