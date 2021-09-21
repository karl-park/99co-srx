package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.card_post_lisitng_header_and_desc.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardPostLisitngHeaderAndDescBinding

class PostListingHeaderAndDescCard(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: CardPostLisitngHeaderAndDescBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.card_post_lisitng_header_and_desc,
        this,
        true
    )

    init {
        setupHeaderListeners()
        setupDescriptionListeners()
        setupSpecialMessageListeners()
    }

    private fun setupHeaderListeners() {
        et_listing_header.setOnFocusChangeListener { _, isFocus ->
            if (!isFocus) {
                val viewModel = binding.viewModel ?: return@setOnFocusChangeListener
                val editPO = viewModel.listingEditPO.value ?: return@setOnFocusChangeListener
                if (editPO.listingHeader.trim().isEmpty()) {
                    //reset applicable
                    editPO.listingHeader = "undefined"
                }
                viewModel.updateListingEditPO()
            } //end of focus
        }
    }

    private fun setupDescriptionListeners() {
        et_listing_description.setOnFocusChangeListener { _, isFocus ->
            if (!isFocus) {
                val viewModel = binding.viewModel ?: return@setOnFocusChangeListener
                val editPO = viewModel.listingEditPO.value ?: return@setOnFocusChangeListener
                if (editPO.remarks.trim().isEmpty()) {
                    //reset applicable
                    editPO.remarks = "undefined"
                }
                viewModel.updateListingEditPO()
            }
        }

        //for scrolling issue in description
        et_listing_description.setOnTouchListener { view, motionEvent ->
            if (et_listing_description.text.toString().trim().isNotEmpty()) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                if ((motionEvent.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun setupSpecialMessageListeners() {
        //special message -> remark agents
        et_special_message.setOnFocusChangeListener { _, isFocus ->
            if (!isFocus) {
                val viewModel = binding.viewModel ?: return@setOnFocusChangeListener
                val editPO = viewModel.listingEditPO.value ?: return@setOnFocusChangeListener
                if (editPO.remarksAgent.trim().isEmpty()) {
                    //reset applicable
                    editPO.remarksAgent = "undefined"
                }
                viewModel.updateListingEditPO()
            }
        }

        //for scrolling in multiline text input of special message
        et_special_message.setOnTouchListener { view, motionEvent ->
            if (et_special_message.text.toString().trim().isNotEmpty()) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                if ((motionEvent.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return@setOnTouchListener false
        }
    }
}