package sg.searchhouse.agentconnect.view.adapter.main

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemSetting1Binding
import sg.searchhouse.agentconnect.databinding.ListItemSetting2Binding
import sg.searchhouse.agentconnect.databinding.ListItemSettingCalculatorsBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum.CalculatorType
import sg.searchhouse.agentconnect.enumeration.app.SettingMenu
import sg.searchhouse.agentconnect.tracking.MenuListTracker
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.view.widget.common.ProfileIconLayout

class MenuAdapter(
    private val context: Context,
    private val onItemClickListener: ((SettingMenu) -> Unit),
    private val onCalculatorClickListener: ((CalculatorType?) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var currentUser = SessionUtil.getCurrentUser()
    private var streetSineUser = SessionUtil.getStreetSineUser()
    private var settings = listOf<SettingMenu>()

    private var isCalculatorsExpand = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROFILE -> {
                ProfileSettingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_setting_profile, parent, false)
                )
            }
            VIEW_TYPE_FIRST -> {
                FirstSettingViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.list_item_setting_1,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_SECOND -> {
                SecondSettingViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.list_item_setting_2,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_CALCULATORS -> {
                CalculatorsSettingViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.list_item_setting_calculators,
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw Throwable("Wrong view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return settings.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProfileSettingViewHolder -> {
                currentUser?.let {
                    holder.tvName.text = it.name
                    holder.tvEmail.text = it.email
                    holder.layoutProfile.populateByInitialLetter(it.photo, it.name)
                }
            }
            is FirstSettingViewHolder -> {
                holder.tvTitle.text = context.getString(settings[position].label)
                holder.ivIcon.setImageResource(settings[position].icon)
                holder.binding.isShowFullDivider = position == FIRST_TYPE_COUNT - 1
            }
            is SecondSettingViewHolder -> {
                val menu = settings[position]
                holder.ivIcon.setImageResource(menu.icon)
                if (menu == SettingMenu.SIGN_OUT) {
                    holder.binding.color = Color.RED
                } else {
                    holder.binding.color = context.getColor(R.color.black_invertible)
                }
                if (menu == SettingMenu.SWITCH_SERVER) {
                    holder.tvTitle.text = ApiUtil.getBaseUrl(context)
                } else {
                    holder.tvTitle.text = context.getString(menu.label)
                }

                if (menu == SettingMenu.LOGIN_AS_AGENT) {
                    if (streetSineUser != null) {
                        holder.tvTitle.text =
                            context.getString(R.string.setting_item_logout_from_agent)
                    } else {
                        holder.tvTitle.text = context.getString(menu.label)
                    }
                }
            }
            is CalculatorsSettingViewHolder -> {
                holder.tvTitle.text = context.getString(settings[position].label)
                holder.ivIcon.setImageResource(settings[position].icon)
                holder.binding.isExpand = isCalculatorsExpand
                holder.binding.layoutMain.setupLayoutAnimation()
                holder.binding.layoutCalculatorsMain.setOnClickListener {
                    // tracking for calculators
                    MenuListTracker.trackMenuItemClicked(context, SettingMenu.CALCULATORS)
                    it.postDelayed({
                        isCalculatorsExpand = !isCalculatorsExpand
                        notifyItemChanged(position)
                    }, it.resources.getInteger(R.integer.anim_time_fast).toLong())
                }
                holder.binding.layoutCalculatorAll.root.setOnClickListener {
                    onCalculatorClickListener.invoke(null)
                }
                holder.binding.layoutCalculatorAffordabilityQuick.root.setOnClickListener {
                    onCalculatorClickListener.invoke(CalculatorType.AFFORDABILITY_QUICK)
                }
                holder.binding.layoutCalculatorAffordabilityAdvanced.root.setOnClickListener {
                    onCalculatorClickListener.invoke(CalculatorType.AFFORDABILITY_ADVANCED)
                }
                holder.binding.layoutCalculatorSelling.root.setOnClickListener {
                    onCalculatorClickListener.invoke(CalculatorType.SELLING)
                }
                holder.binding.layoutCalculatorStampDuty.root.setOnClickListener {
                    onCalculatorClickListener.invoke(CalculatorType.STAMP_DUTY)
                }
            }
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.invoke(settings[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == SettingMenu.MY_PROFILE.value -> VIEW_TYPE_PROFILE
            position == SettingMenu.CALCULATORS.value -> VIEW_TYPE_CALCULATORS
            position < FIRST_TYPE_COUNT -> VIEW_TYPE_FIRST
            else -> VIEW_TYPE_SECOND
        }
    }

    fun updateUserProfile() {
        currentUser = SessionUtil.getCurrentUser()
        streetSineUser = SessionUtil.getStreetSineUser()
        notifyDataSetChanged()
    }

    fun updateMenuItems(items: List<SettingMenu>) {
        this.settings = items
        notifyDataSetChanged()
    }

    class ProfileSettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        val layoutProfile: ProfileIconLayout = itemView.findViewById(R.id.layout_profile)
    }

    open class SettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
    }

    class FirstSettingViewHolder(val binding: ListItemSetting1Binding) :
        SettingViewHolder(binding.root)

    class SecondSettingViewHolder(val binding: ListItemSetting2Binding) :
        SettingViewHolder(binding.root)

    class CalculatorsSettingViewHolder(val binding: ListItemSettingCalculatorsBinding) :
        SettingViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_PROFILE = 1
        const val VIEW_TYPE_FIRST = 2
        const val VIEW_TYPE_SECOND = 3
        const val FIRST_TYPE_COUNT = 4
        const val VIEW_TYPE_CALCULATORS = 5
    }
}