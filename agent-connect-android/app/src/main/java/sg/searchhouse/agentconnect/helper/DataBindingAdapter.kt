package sg.searchhouse.agentconnect.helper

import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.edit_text_search_white.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.util.ImageUtil
import sg.searchhouse.agentconnect.view.widget.agent.cv.TransactionTypeTabLayout
import sg.searchhouse.agentconnect.view.widget.common.*
import sg.searchhouse.agentconnect.view.widget.listing.MyListingsDraftModeTabLayout
import sg.searchhouse.agentconnect.view.widget.listing.MyListingsOwnershipRadioGroup
import sg.searchhouse.agentconnect.view.widget.listing.create.ListingQualityBar
import sg.searchhouse.agentconnect.view.widget.listing.create.PostListingFacilitiesCard
import sg.searchhouse.agentconnect.view.widget.property.ResidentialMainSelector
import sg.searchhouse.agentconnect.view.widget.search.SearchListingShortcutLayout
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel


object DataBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(imageView: ImageView, url: String?) {
        url?.let {
            Glide.with(imageView.context)
                .load(ImageUtil.maybeAppendBaseUrl(imageView.context, it))
                .placeholder(R.drawable.ic_square_image_placeholder)
                .error(R.drawable.ic_square_image_placeholder)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("imageBitmap")
    fun loadImage(iv: ImageView, bitmap: Bitmap?) {
        bitmap?.let { iv.setImageBitmap(it) }
    }

    @JvmStatic
    @BindingAdapter("resourceText")
    fun convertStringResToString(textView: TextView, @StringRes res: Int) {
        if (res == 0) {
            textView.text = ""
        } else {
            textView.text = textView.resources.getString(res)
        }
    }

    @JvmStatic
    @BindingAdapter("isSelected")
    fun selectButton(view: View, isSelected: Boolean?) {
        view.isSelected = isSelected == true
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(profileIconLayout: ProfileIconLayout, imageUrl: String?) {
        if (imageUrl?.isNotEmpty() == true) {
            profileIconLayout.setImageUrl(imageUrl)
        }
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(circleImageView: CircleImageView, imageUrl: String?) {
        if (imageUrl?.isNotEmpty() == true) {
            circleImageView.url = imageUrl
        }
    }

    @JvmStatic
    @BindingAdapter("nameForInitial")
    fun setInitial(profileIconLayout: ProfileIconLayout, nameForInitial: String?) {
        if (nameForInitial != null) {
            profileIconLayout.setNameForInitial(nameForInitial)
        }
    }

    @JvmStatic
    @BindingAdapter("searchResultType")
    fun setSearchResultType(
        searchListingShortcutLayout: SearchListingShortcutLayout,
        searchResultType: SearchCommonViewModel.SearchResultType?
    ) {
        searchListingShortcutLayout.binding.searchResultType = searchResultType
    }

    @JvmStatic
    @BindingAdapter("displayMode")
    fun setDisplayMode(
        searchListingShortcutLayout: SearchListingShortcutLayout,
        displayMode: SearchCommonViewModel.DisplayMode?
    ) {
        searchListingShortcutLayout.binding.displayMode = displayMode
    }

    @JvmStatic
    @BindingAdapter("propertyPurpose")
    fun setPropertyPurpose(
        searchListingShortcutLayout: SearchListingShortcutLayout,
        propertyPurpose: ListingEnum.PropertyPurpose?
    ) {
        searchListingShortcutLayout.binding.propertyPurpose = propertyPurpose
    }

    @JvmStatic
    @BindingAdapter("ownershipType")
    fun setOwnershipType(
        myListingsOwnershipRadioGroup: MyListingsOwnershipRadioGroup,
        ownershipType: ListingEnum.OwnershipType?
    ) {
        myListingsOwnershipRadioGroup.binding.ownershipType = ownershipType
    }

    @JvmStatic
    @BindingAdapter("transactionType")
    fun setTransactionType(
        transactionTypeLayout: TransactionTypeTabLayout,
        transactionType: AgentProfileAndCvEnum.TransactionType?
    ) {
        transactionTypeLayout.binding.transactionType = transactionType
    }

    @JvmStatic
    @BindingAdapter("draftMode")
    fun setDraftMode(
        draftModeTabLayout: MyListingsDraftModeTabLayout,
        draftMode: ListingManagementEnum.ListingDraftMode?
    ) {
        draftModeTabLayout.binding.draftMode = draftMode
    }

    @JvmStatic
    @BindingAdapter("showLabel")
    fun setShowLabel(listingQualityBar: ListingQualityBar, showLabel: Boolean?) {
        if (showLabel != null) {
            listingQualityBar.showLabel = showLabel
        }
    }

    @JvmStatic
    @BindingAdapter("isBold")
    fun setBold(view: TextView, isBold: Boolean) {
        if (isBold) {
            view.setTypeface(null, Typeface.BOLD)
        } else {
            view.setTypeface(null, Typeface.NORMAL)
        }
    }

    @JvmStatic
    @BindingAdapter("textOrDash")
    fun setTextOrDash(textView: TextView, text: String?) {
        textView.text = when {
            !TextUtils.isEmpty(text?.trim()) -> text
            else -> "-"
        }
    }

    @JvmStatic
    @BindingAdapter("background")
    fun setBackground(pillNumberEditText: PillNumberEditText, background: Drawable?) {
        pillNumberEditText.background = background
    }

    @JvmStatic
    @BindingAdapter("layout_height")
    fun setLayoutHeight(view: View, height: Float) {
        val layoutParams = view.layoutParams
        layoutParams.height = height.toInt()
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("hint")
    fun setHint(searchEditText: WhiteSearchEditText, hint: String?) {
        searchEditText.edit_text.hint = hint
    }

    @JvmStatic
    @BindingAdapter("ownershipType")
    fun setOwnershipType(
        newOwnershipTypeRadioGroup: NewOwnershipTypeRadioGroup,
        ownershipType: ListingEnum.OwnershipType?
    ) {
        newOwnershipTypeRadioGroup.binding.ownershipType = ownershipType
    }

    @JvmStatic
    @BindingAdapter("propertyMainType")
    fun setPropertyMainType(
        residentialMainSelector: ResidentialMainSelector,
        propertyMainType: ListingEnum.PropertyMainType?
    ) {
        residentialMainSelector.binding.selectedPropertyMainType = propertyMainType
    }

    @JvmStatic
    @BindingAdapter("scale")
    fun setScalableLinearLayoutScale(
        scalableLinearLayout: ScalableLinearLayout,
        scale: Float? = 1.0f
    ) {
        scale?.run { scalableLinearLayout.updateScale(this) }
    }

    @JvmStatic
    @BindingAdapter("scale")
    fun setScalableFrameLayoutScale(
        scalableFrameLayout: ScalableFrameLayout,
        scale: Float? = 1.0f
    ) {
        scale?.run { scalableFrameLayout.updateScale(this) }
    }

    @JvmStatic
    @BindingAdapter("scale")
    fun setScalableLinearLayoutScale(
        scalableRelativeLayout: ScalableRelativeLayout,
        scale: Float? = 1.0f
    ) {
        scale?.run { scalableRelativeLayout.updateScale(this) }
    }

    @JvmStatic
    @BindingAdapter("isEnabled")
    fun setEnableAppHorizontalScrollView(
        appHorizontalScrollView: AppHorizontalScrollView,
        isEnabled: Boolean? = true
    ) {
        isEnabled?.run { appHorizontalScrollView.isEnabled = this }
    }

    @JvmStatic
    @BindingAdapter("layout_weight")
    fun setLayoutWeight(
        view: View,
        weight: Float
    ) {
        if (view.layoutParams is LinearLayout.LayoutParams) {
            val layoutParams = view.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = weight
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("isFeaturesApplicable")
    fun setIsFeaturesApplicable(
        postListingFacilitiesCard: PostListingFacilitiesCard,
        isFeaturesApplicable: Boolean
    ) {
        postListingFacilitiesCard.binding.isFeaturesApplicable = isFeaturesApplicable
    }
}