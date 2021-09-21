package sg.searchhouse.agentconnect.util

import android.content.Context
import sg.searchhouse.agentconnect.R

object ListingInfoUtil {

    //get completion dates
    fun getGeneratedCompletionDates(context: Context): List<String> {
        val year = DateTimeUtil.getCurrentYear()
        val completionDates: ArrayList<String> = arrayListOf()
        completionDates.add(context.getString(R.string.hint_completion_year))
        for (n in year + 16 downTo 1960) {
            completionDates.add(n.toString())
        }
        return completionDates
    }

    //get Bathrooms list for listing management
    fun getBathRooms(context: Context): List<String> {
        val bathrooms = arrayListOf<String>()
        bathrooms.add(context.getString(R.string.label_select))
        for (i in 1..12) {
            bathrooms.add(i.toString())
        }
        return bathrooms
    }
}