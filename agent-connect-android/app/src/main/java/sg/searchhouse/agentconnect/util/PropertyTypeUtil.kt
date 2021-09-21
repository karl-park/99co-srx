package sg.searchhouse.agentconnect.util

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.dsl.getIntList
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import java.util.*

object PropertyTypeUtil {
    fun isCondo(subType: Int): Boolean {
        return ListingEnum.PropertyMainType.CONDO.propertySubTypes.any { it.type == subType }
    }

    fun isHDB(subType: Int): Boolean {
        return ListingEnum.PropertyMainType.HDB.propertySubTypes.any { it.type == subType }
    }

    fun isLanded(subType: Int): Boolean {
        return ListingEnum.PropertyMainType.LANDED.propertySubTypes.any { it.type == subType }
    }

    fun isResidential(subType: Int): Boolean {
        return isCondo(subType) || isHDB(subType) || isLanded(subType)
    }

    fun isMainTypeResidential(propertyMainType: ListingEnum.PropertyMainType): Boolean {
        return propertyMainType != ListingEnum.PropertyMainType.COMMERCIAL
    }

    fun isCommercial(subType: Int): Boolean {
        return ListingEnum.PropertyMainType.COMMERCIAL.propertySubTypes.any { it.type == subType }
    }

    fun isPrivate(subType: Int): Boolean {
        return isCondo(subType) || isLanded(subType)
    }

    fun getPurposeSubType(propertyPurpose: ListingEnum.PropertyPurpose): ListingEnum.PropertySubType {
        return when (propertyPurpose) {
            ListingEnum.PropertyPurpose.RESIDENTIAL -> ListingEnum.PropertySubType.ALL_PRIVATE_RESIDENTIAL
            ListingEnum.PropertyPurpose.COMMERCIAL -> ListingEnum.PropertySubType.ALL_COMMERCIAL
        }
    }

    fun getPurposeMainType(propertyPurpose: ListingEnum.PropertyPurpose): ListingEnum.PropertyMainType {
        return when (propertyPurpose) {
            ListingEnum.PropertyPurpose.RESIDENTIAL -> ListingEnum.PropertyMainType.RESIDENTIAL
            ListingEnum.PropertyPurpose.COMMERCIAL -> ListingEnum.PropertyMainType.COMMERCIAL
        }
    }

    fun getPropertyMainType(cdResearchSubType: Int): ListingEnum.PropertyMainType? {
        return getPropertyMainTypeFromChildSubType(cdResearchSubType)
            ?: getPropertyMainTypeFromPrimarySubType(cdResearchSubType)
    }

    // NOTE Does not apply to composite main type i.e. `RESIDENTIAL`
    fun getPropertyMainType(cdResearchSubTypes: String): ListingEnum.PropertyMainType? {
        val subTypeList = cdResearchSubTypes.getIntList()
        if (subTypeList.isEmpty()) return null
        val firstMainType = getPropertyMainType(subTypeList.first()) ?: return null
        return when (subTypeList.any { getPropertyMainType(it) != firstMainType }) {
            false -> firstMainType
            true -> null
        }
    }

    fun isAllResidentialSubTypes(propertySubTypes: List<ListingEnum.PropertySubType>): Boolean {
        return when (propertySubTypes.size) {
            ListingEnum.PropertyMainType.RESIDENTIAL.propertySubTypes.size -> {
                !propertySubTypes.any { !isResidential(it.type) }
            }
            else -> false
        }
    }

    fun isAllCommercialSubTypes(propertySubTypes: List<ListingEnum.PropertySubType>): Boolean {
        return when (propertySubTypes.size) {
            ListingEnum.PropertyMainType.COMMERCIAL.propertySubTypes.size -> {
                !propertySubTypes.any { !isCommercial(it.type) }
            }
            else -> {
                false
            }
        }
    }

    private fun getPropertyMainTypeFromChildSubType(cdResearchSubType: Int): ListingEnum.PropertyMainType? {
        return ListingEnum.PropertyMainType.values().filter {
            // Filter this because overlapping children sub types between RESIDENTIAL and HDB, CONDO etc.
            it != ListingEnum.PropertyMainType.RESIDENTIAL
        }.find {
            it.propertySubTypes.any { subType -> subType.type == cdResearchSubType }
        }
    }

    private fun getPropertyMainTypeFromPrimarySubType(cdResearchSubType: Int): ListingEnum.PropertyMainType? {
        return ListingEnum.PropertyMainType.values().find {
            it.primarySubType.type == cdResearchSubType
        }
    }

    fun getPropertyMainTypeByPrimarySubType(primarySubType: Int): ListingEnum.PropertyMainType? {
        return ListingEnum.PropertyMainType.values()
            .find { it.primarySubType.type == primarySubType }
    }

    fun isPrimarySubType(cdResearchSubType: Int): Boolean {
        return getPropertyMainTypeByPrimarySubType(cdResearchSubType) != null
    }

    // TODO Throw error instead?
    fun getPropertySubType(cdResearchSubType: Int): ListingEnum.PropertySubType? {
        return ListingEnum.PropertySubType.values().find {
            it.type == cdResearchSubType
        }
    }

    // Get property sub type list from string of sub type codes
    @Throws(NumberFormatException::class)
    fun getPropertySubTypes(subTypeListString: String): List<ListingEnum.PropertySubType> {
        if (subTypeListString.isEmpty()) return emptyList()
        return subTypeListString.split(",").mapNotNull { getPropertySubType(it.toInt()) }
    }

    @StringRes
    fun getPropertyTypeDescriptionResId(subType: Int): Int? {
        return ListingEnum.PropertySubType.values().find {
            it.type == subType
        }?.label
    }

    //use from cea and create listing
    fun getModelsByPropertyType(
        propertyType: Int,
        maps: HashMap<Int, ArrayList<String>>
    ): ArrayList<String> {
        val modelsByPropertyType: ArrayList<String> = arrayListOf()
        maps.filterKeys { it == propertyType }.values.forEach {
            modelsByPropertyType.addAll(it)
        }
        return modelsByPropertyType
    }

    private val subTypesWithoutTower = listOf(
        ListingEnum.PropertySubType.ALL_LANDED,
        ListingEnum.PropertySubType.TERRACE,
        ListingEnum.PropertySubType.DETACHED,
        ListingEnum.PropertySubType.SEMI_DETACHED,
        ListingEnum.PropertySubType.LAND
    )

    // Based on its property sub type, decide whether should show tower view on project transaction page
    fun isShowTransactionsTower(cdResearchSubType: Int): Boolean {
        val propertySubType = getPropertySubType(cdResearchSubType)
        return propertySubType != null && !subTypesWithoutTower.contains(propertySubType)
    }

    fun getPropertyPurpose(cdResearchSubType: Int): ListingEnum.PropertyPurpose {
        return when {
            isCommercial(cdResearchSubType) -> ListingEnum.PropertyPurpose.COMMERCIAL
            else -> ListingEnum.PropertyPurpose.RESIDENTIAL
        }
    }
}