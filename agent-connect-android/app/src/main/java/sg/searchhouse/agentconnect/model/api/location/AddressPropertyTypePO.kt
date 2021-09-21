package sg.searchhouse.agentconnect.model.api.location

import java.io.Serializable

data class AddressPropertyTypePO(
    val buildingNum: String,
    val buildingName: String,
    val streetName: String,
    val postalCode: Int,
    val buildingKey: String,
    val streetKey: String,
    val propertyType: Int,
    val propertySubType: Int,
    val propertySubTypeList: List<Int>,
    val district: Int,
    val hdbTownId: Int
) : Serializable
