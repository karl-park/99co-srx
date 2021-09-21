package sg.searchhouse.agentconnect.model.api.project

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import java.io.Serializable

data class FilterProjectEditPO(
    var propertyMainType: ListingEnum.PropertyMainType? = null,
    var cdResearchSubtypes: String? = null,
    var radius: Int? = null,
    var tenureType: Int? = null,
    var typeOfArea: Int? = null,
    var age: Int? = null,
    var ageMax: Int? = null,
    var ageMin: Int? = null
) : Serializable