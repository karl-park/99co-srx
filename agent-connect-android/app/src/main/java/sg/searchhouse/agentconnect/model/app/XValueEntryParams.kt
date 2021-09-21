package sg.searchhouse.agentconnect.model.app

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.model.api.location.AddressPropertyTypePO
import java.io.Serializable

class XValueEntryParams(
    val propertyType: XValueEnum.PropertyType?,
    val propertySubType: ListingEnum.PropertySubType,
    val unitFloor: String?,
    val unitNumber: String?,
    val areaType: XValueEnum.AreaType?,
    val tenure: XValueEnum.Tenure?,
    val builtYear: Int?,
    val area: Int,
    val areaExt: Int?,
    val areaGfa: Int?,
    val address: AddressPropertyTypePO
) : Serializable