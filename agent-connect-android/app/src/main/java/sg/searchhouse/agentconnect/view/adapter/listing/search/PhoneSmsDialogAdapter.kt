package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import sg.searchhouse.agentconnect.R

// TODO: This is very traditional, maybe upgrade when new adapter available for alert dialog list
class PhoneSmsDialogAdapter(
    context: Context,
    private val resolveInfos: List<ResolveInfo>,
    val onClickListener: (ResolveInfo) -> Unit
) : ArrayAdapter<ResolveInfo>(context, R.layout.list_item_phone_sms, resolveInfos) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_phone_sms, parent, false)
        val text1: TextView = view.findViewById(R.id.text1)
        val icon1: ImageView = view.findViewById(R.id.icon1)

        val resolveInfo = resolveInfos[position]

        val name = resolveInfo.activityInfo.applicationInfo.loadLabel(parent.context.packageManager)
            .toString()
        val icon = resolveInfo.activityInfo.applicationInfo.loadIcon(parent.context.packageManager)

        text1.text = name
        icon1.setImageDrawable(icon)

        view.setOnClickListener { onClickListener.invoke(resolveInfo) }

        return view
    }
}