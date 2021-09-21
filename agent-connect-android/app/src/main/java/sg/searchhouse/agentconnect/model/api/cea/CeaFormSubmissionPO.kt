package sg.searchhouse.agentconnect.model.api.cea


import java.io.Serializable

//Editable & submission PO
//Note => please don't do any changes for this PO. there are a lot of backend issue
//TODO: revamp this PO -> need backend to check this PO
//TODO: backend issue -> cannot set default value to null
//TODO: CHECK with backend
data class CeaFormSubmissionPO(
    var formId: Int? = 0,
    var formType: Int? = 0,
    var renewalSeq: Int? = 0,
    var test: Boolean? = false,
    var cdResearchSubtype: Int? = 0,
    var postalCode: String? = "",
    var floor: String? = "",
    var unit: String? = "",
    var model: String? = "",
    var builtMin: String? = "",
    var landMin: String? = "",
    var tenure: Int? = 0,
    var bedroomMin: Int? = 0,
    var tenanted: Boolean? = false,
    var tenantedAmount: Int? = 0,
    var roomRental: Boolean? = false,
    var propertyDescription: String? = "",
    var agreementDate: String? = "",
    var commencementDate: String? = "",
    var expiryDate: String? = "",
    var price: Int? = 0,
    var listPrice: Int? = 0,
    var priceCommission: Double? = 0.0,
    var rateCommission: String? = "",
    var gstPayable: String? = "",
    var gstInclusive: String? = "",
    var cobroke: String? = "",
    var isOwner: Boolean? = false,
    var estateAgentDisclosure: String? = "",
    var leaseDuration: Int? = 0,
    var rentalFrequency: Int? = 0,
    var renewalCommissionLiable: Boolean? = false,
    var renewalCommissionTime: String? = "",
    var priceRenewalCommission: Double? = 0.0,
    var rateRenewalCommission: String? = "",
    var selectedAgencyTerms: List<AgencyTerms>? = listOf(),
    var termsArray: List<Term>? = listOf(),
    var estateAgent: CeaFormEstateAgentPO? = null,
    var clients: ArrayList<CeaFormClientPO>? = arrayListOf(),
    var valuation: Int? = 0,
    var numOfStorey: Int? = 0,
    var conflictOfInterest: String? = ""

) : Serializable {
    data class AgencyTerms(
        var id: Int
    ) : Serializable

    data class Term(
        var term: String
    ) : Serializable

    //TODO: not a good idea by doing this way but need to check with backend. Revamp this code
    fun createCeaSubmissionFromValues(
        items: Map<String, CeaFormRowPO>,
        termsArray: List<Term>? = null
    ): CeaFormSubmissionPO {

        return CeaFormSubmissionPO(
            formId = items["formId"]?.rowValue?.toIntOrNull(),
            formType = items["formType"]?.rowValue?.toIntOrNull(),
            renewalSeq = items["renewalSeq"]?.rowValue?.toIntOrNull(),
            test = items["test"]?.rowValue?.toBoolean(),
            cdResearchSubtype = items["cdResearchSubtype"]?.rowValue?.toIntOrNull(),
            postalCode = items["postalCode"]?.rowValue,
            floor = items["floor"]?.rowValue,
            unit = items["unit"]?.rowValue,
            model = items["model"]?.rowValue,
            builtMin = items["builtMin"]?.rowValue,
            landMin = items["landMin"]?.rowValue,
            tenure = items["tenure"]?.rowValue?.toIntOrNull(),
            bedroomMin = items["bedroomMin"]?.rowValue?.toIntOrNull(),
            tenanted = items["tenanted"]?.rowValue?.toBoolean(),
            tenantedAmount = items["tenantedAmount"]?.rowValue?.toIntOrNull(),
            roomRental = items["roomRental"]?.rowValue?.toBoolean(),
            propertyDescription = items["propertyDescription"]?.rowValue,
            agreementDate = items["agreementDate"]?.rowValue ?: "",
            commencementDate = items["commencementDate"]?.rowValue ?: "",
            expiryDate = items["expiryDate"]?.rowValue ?: "",
            price = items["price"]?.rowValue?.toIntOrNull(),
            listPrice = items["listPrice"]?.rowValue?.toIntOrNull(),
            priceCommission = items["priceCommission"]?.rowValue?.toDoubleOrNull() ?: 0.0,
            rateCommission = items["rateCommission"]?.rowValue ?: "",
            gstPayable = items["gstPayable"]?.rowValue,
            gstInclusive = items["gstInclusive"]?.rowValue,
            cobroke = items["cobroke"]?.rowValue,
            isOwner = items["isOwner"]?.isOwner(),
            estateAgentDisclosure = items["estateAgentDisclosure"]?.rowValue ?: "",
            leaseDuration = items["leaseDuration"]?.getLeaseDurationValue(),
            rentalFrequency = items["rentalFrequency"]?.getRentalFrequency(),
            renewalCommissionLiable = items["renewalCommissionLiable"]?.rowValue?.toBoolean(),
            renewalCommissionTime = items["renewalCommissionTime"]?.rowValue,
            priceRenewalCommission = items["priceRenewalCommission"]?.rowValue?.toDoubleOrNull()
                ?: 0.0,
            rateRenewalCommission = items["rateRenewalCommission"]?.rowValue ?: "",
            selectedAgencyTerms = listOf(),
            termsArray = termsArray ?: emptyList(),
            estateAgent = null,
            clients = arrayListOf(),
            valuation = items["valuation"]?.rowValue?.toIntOrNull(),
            numOfStorey = items["numOfStorey"]?.rowValue?.toIntOrNull(),
            conflictOfInterest = items["conflictOfInterest"]?.rowValue
        )
    }
}