package sg.searchhouse.agentconnect.dsl

import sg.searchhouse.agentconnect.enumeration.api.CalculatorEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum

fun CalculatorEnum.BuyerProfile.isCpfApplicable(): Boolean =
    this == CalculatorEnum.BuyerProfile.SINGAPOREAN || this == CalculatorEnum.BuyerProfile.SPR

fun ListingEnum.PropertyPurpose.toProjectSearchMode(): ProjectEnum.SearchMode = when (this) {
    ListingEnum.PropertyPurpose.RESIDENTIAL -> ProjectEnum.SearchMode.RESIDENTIAL
    ListingEnum.PropertyPurpose.COMMERCIAL -> ProjectEnum.SearchMode.COMMERCIAL
}

fun ListingEnum.PropertyMainType.isResidential(): Boolean =
    this != ListingEnum.PropertyMainType.COMMERCIAL
