package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.layout_my_listings_fab_add_on.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.listing.user.FabAddOnOptionAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.user.MyListingsPagerAdapter.MyListingsTab

//TODO: will change to data binding way after phase 1
class MyListingsFabAddOn @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private val adapter = FabAddOnOptionAdapter(context)

    init {
        View.inflate(context, R.layout.layout_my_listings_fab_add_on, this)

        fab_main.setOnClickListener {
            showSubAddOnFloatingActionButtons(layout_sub_add_on.visibility != View.VISIBLE)
        }
        layout_add_on_overlay.setOnClickListener {
            showSubAddOnFloatingActionButtons(false)
        }

        list_add_on_options.layoutManager = LinearLayoutManager(context)
        list_add_on_options.adapter = adapter
    }

    fun showSubAddOnFloatingActionButtons(isShow: Boolean) {
        if (isShow) {
            //expand
            ViewCompat.animate(fab_main).rotation(45.0F).withLayer().setDuration(300)
                .setInterpolator(OvershootInterpolator(10.0F)).start()
            //TODO: don't know add animation in whole list or each item.currently,add animation to each item
            //list_add_on_options.startAnimation(fabOpenAnimation)
            adapter.isOpenAnimation = true
            adapter.notifyDataSetChanged()
            layout_sub_add_on.visibility = View.VISIBLE
            ViewUtil.fadeInView(layout_add_on_overlay)
        } else {
            //collapse
            ViewCompat.animate(fab_main).rotation(0.0F).withLayer().setDuration(300)
                .setInterpolator(OvershootInterpolator(10.0F)).start()
            //TODO: don't know add animation in whole list or each item. currently,add animation to each item
            //list_add_on_options.startAnimation(fabCloseAnimation)
            adapter.isOpenAnimation = false
            adapter.notifyDataSetChanged()
            layout_sub_add_on.visibility = View.GONE
            ViewUtil.fadeOutView(layout_add_on_overlay)
        }
    }

    fun populateAddOnOptions(
        tab: MyListingsTab? = null,
        selectedListingSize: Int = 0,
        isCommercial: Boolean = false
    ) {
        //note: hide certified listing option for commercial properties
        val defaultList = listOf(
            AddOnFab.FEATURED_LISTING,
            getRefreshOrRepost(),
            AddOnFab.V360,
            AddOnFab.X_DRONE
        )
        when (tab) {
            MyListingsTab.ACTIVE -> {
                adapter.options = if (selectedListingSize == 1) {
                    if (isCommercial) {
                        defaultList + listOf(
                            AddOnFab.COMMUNITY_POST,
                            AddOnFab.COPY,
                            AddOnFab.TAKE_DOWN
                        )
                    } else {
                        listOf(AddOnFab.CERTIFIED_LISTING) + defaultList + listOf(
                            AddOnFab.COMMUNITY_POST,
                            AddOnFab.COPY,
                            AddOnFab.TAKE_DOWN
                        )
                    }
                } else {
                    defaultList + listOf(AddOnFab.TAKE_DOWN)
                }
                fab_main.visibility = View.VISIBLE
            }
            MyListingsTab.PAST -> {
                adapter.options = if (selectedListingSize == 1) {
                    if (isCommercial) {
                        defaultList + listOf(AddOnFab.COPY, AddOnFab.DELETE)
                    } else {
                        listOf(AddOnFab.CERTIFIED_LISTING) + defaultList + listOf(
                            AddOnFab.COPY,
                            AddOnFab.DELETE
                        )
                    }
                } else {
                    defaultList + listOf(AddOnFab.DELETE)
                }
                fab_main.visibility = View.VISIBLE
            }
            MyListingsTab.DRAFT -> {
                adapter.options = if (selectedListingSize == 1) {
                    listOf(AddOnFab.COPY, AddOnFab.DELETE)
                } else {
                    listOf(AddOnFab.DELETE)
                }
                fab_main.visibility = View.VISIBLE
            }
            MyListingsTab.TRANSACTION -> {
                if (selectedListingSize == 1) {
                    adapter.options = listOf(AddOnFab.COPY)
                    fab_main.visibility = View.VISIBLE
                } else {
                    adapter.options = emptyList()
                    fab_main.visibility = View.GONE
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    enum class AddOnFab(val value: Int, @StringRes val label: Int, @StringRes val subLabel: Int) {
        //TODO: DO NOT REMOVE COMMENT
        // add all options by mock up. need export pdf, feature listing and repost in this phase
        ////EXPORT_PDF(0, R.string.sub_add_on_export_pdf),
        //NO_DEPOSIT(1, R.string.sub_add_on_no_deposit),
        CERTIFIED_LISTING(
            2,
            R.string.sub_add_on_certified_listing,
            R.string.label_add_on_certified_listing
        ),

        //FEATURED_LISTING_PLUS(3, R.string.sub_add_on_feature_listing_plus),
        FEATURED_LISTING(
            4,
            R.string.sub_add_on_feature_listing,
            R.string.label_add_on_feature_listing
        ),
        REPOST(5, R.string.sub_add_on_repost, R.string.sub_add_on_repost),
        REFRESH(5, R.string.sub_add_on_refresh, R.string.sub_add_on_refresh),
        TAKE_DOWN(6, R.string.sub_add_on_take_down, R.string.sub_add_on_take_down),
        DELETE(7, R.string.sub_add_on_delete, R.string.sub_add_on_delete),
        COPY(8, R.string.sub_add_on_copy, R.string.sub_add_on_copy),
        V360(9, R.string.sub_add_on_v360, R.string.label_add_on_v360),
        X_DRONE(10, R.string.sub_add_on_x_drone, R.string.label_add_on_drone),
        COMMUNITY_POST(12, R.string.sub_add_on_community_post, R.string.label_add_on_community_post)
    }

    companion object {
        fun getRefreshOrRepost(): AddOnFab {
            return when (SessionUtil.getSubscriptionConfig().srx99CombinedSub) {
                true -> AddOnFab.REFRESH
                false -> AddOnFab.REPOST
            }
        }
    }
}