package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillCreateListingAssignmentTypeBinding

class CreateListingAssignmentTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillCreateListingAssignmentTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_create_listing_assignment_type,
        this,
        true
    )
}