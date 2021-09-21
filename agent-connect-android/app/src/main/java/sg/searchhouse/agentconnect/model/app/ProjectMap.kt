package sg.searchhouse.agentconnect.model.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.Endpoint

enum class ProjectMap(@StringRes val label: Int, val url: String?) {
    GOOGLE_MAP(R.string.select_project_map_google_map, null),
    ONE_MAP(R.string.select_project_map_one_map, Endpoint.SEARCH_ONE_MAP),
    LITHO_STREET(R.string.select_project_map_lithosheet, Endpoint.SEARCH_LITHOSHEET)
}