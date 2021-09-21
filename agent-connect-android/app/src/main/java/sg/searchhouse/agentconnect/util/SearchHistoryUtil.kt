package sg.searchhouse.agentconnect.util

import android.content.Context
import android.text.TextUtils
import com.pixplicity.easyprefs.library.Prefs
import sg.searchhouse.agentconnect.R

// History format example: "changi:changi:QUERY;Bishan(NS11), Toa Payoh (NS12):11,12:AMENITY_ID;"
object SearchHistoryUtil {
    // Add the current query into history
    // History contains at most 10 entries
    @Throws(IllegalArgumentException::class)
    fun maybeAddHistory(
        context: Context,
        label: String,
        value: String,
        searchType: SearchType,
        extraKey: String
    ) {
        var filteredLabel: String = label.replace(";", "").replace(":", "")
        val filteredValue: String = value.replace(";", "").replace(":", "")

        var searchHistory = getHistory(extraKey).reversed()

        // Remove first item if items more than 9
        if (searchHistory.size > SEARCH_HISTORY_SIZE - 1) {
            searchHistory = searchHistory.drop(searchHistory.size - SEARCH_HISTORY_SIZE + 1)
        }

        // For query with empty value, label it as "Everywhere in Singapore"
        if (filteredValue.isEmpty() && searchType == SearchType.QUERY) {
            filteredLabel = context.getString(R.string.label_listings_everywhere)
        }

        val existingItem = searchHistory.find {
            isHistoryItemMatched(filteredLabel, filteredValue, searchType, it)
        }

        searchHistory = if (existingItem == null) {
            // No existing, add new
            searchHistory.plus(Triple(filteredLabel, filteredValue, searchType))
        } else {
            // Swap to top if exist already
            searchHistory.filter {
                !isHistoryItemMatched(filteredLabel, filteredValue, searchType, it)
            }.plus(existingItem)
        }

        val searchHistoryString =
            searchHistory.joinToString(";") { entry -> "${entry.first}:${entry.second}:${entry.third}" }
        Prefs.putString(extraKey, searchHistoryString)
    }

    private fun isHistoryItemMatched(
        filteredLabel: String,
        filteredValue: String,
        searchType: SearchType,
        triple: Triple<String, String, SearchType>
    ): Boolean {
        return TextUtils.equals(triple.first, filteredLabel) && TextUtils.equals(
            triple.second,
            filteredValue
        ) && triple.third == searchType
    }

    @Throws(IllegalArgumentException::class)
    fun getHistory(extraKey: String): List<Triple<String, String, SearchType>> {
        return Prefs.getString(extraKey, "")
            .split(";").filter { it.isNotEmpty() }.map { keyValue ->
                val triple = keyValue.split(":")
                when (triple.size) {
                    3 -> {
                        try {
                            val searchType = SearchType.valueOf(triple[2])
                            Triple(triple[0], triple[1], searchType)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Invalid history search type")
                        }
                    }
                    else -> throw IllegalArgumentException("Missing key, value, or search type, in one or more PREF_SEARCH_HISTORY item")
                }
            }.reversed()
    }

    enum class SearchType {
        QUERY, AMENITY_ID, DISTRICT_ID, HDB_TOWN_ID, PROPERTY_MAIN_TYPE
    }

    private const val SEARCH_HISTORY_SIZE = 10
}