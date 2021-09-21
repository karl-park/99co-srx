package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class CeaExclusiveEnum {

    enum class CeaFormType(val value: Int, @StringRes val label: Int, @StringRes val title: Int, @StringRes val itemLabel: Int) {
        //DON'T REMOVE  form 5 , form 6, form 7 and form 8.....
        // If remove, will not be same with backend enum types and will be totally messed up
        FORM_5_SALE(
            5,
            R.string.label_form_5_sale,
            R.string.title_form_5_sale_of_residential_property,
            R.string.label_item_form_5
        ),
        FORM_6_PURCHASE(
            6,
            R.string.label_form_6_purchase,
            R.string.title_form_6_purchase_of_residential_property,
            R.string.label_item_form_6
        ),
        FORM_7_LEASE_BY_LANDLORD(
            7,
            R.string.label_form_7_lease_by_landlord,
            R.string.title_form_7_lease_by_landlord,
            R.string.label_item_form_7
        ),
        FORM_8_LEASE_BY_TENANT(
            8,
            R.string.label_form_8_lease_by_tenant,
            R.string.title_form_8_lease_by_tenant,
            R.string.label_item_form_8
        )
    }

    enum class CeaFormRowType(val value: Int) {
        UNCHANGED(0),
        DECIMAL_PAD(1),
        GENERIC_PICKER(2),
        ASCII(3),
        NUMBER_PAD(4),
        NOT_EDITABLE(5),
        DATE_PICKER(6),
        TOGGLE(7),
        DISCLOSURE(8),
        INFORMATION(9),
        INFORMATION_ALERT(10),
        CUSTOM_COMMISSION_NUMBER_PAD(11)
    }

    enum class CeaFormRowTypeKeyValue(val value: String) {
        PROPERTY_DESC("propertyDescription"),
        TEST("test"),
        RENEWAL("renewalSeq"),
        CD_RESEARCH_SUB_TYPE("cdResearchSubtype"),
        MODEL("model"),
        POSTAL_CODE("postalCode"),
        ADDRESS("address"),
        BLOCK_NUMBER("blockNo"),
        FLOOR("floor"),
        UNIT("unit"),
        BUILT_MIN("builtMin"),
        LAND_MIN("landMin"),
        TENURE("tenure"),
        NUM_OF_STOREY("numOfStorey"),
        BEDROOM("bedroomMin"),
        VALUATION("valuation"),
        TENANTED("tenanted"),
        TENANTED_AMOUNT("tenantedAmount"),
        PRICE_COMMISSION("priceCommission"),
        RATE_COMMISSION("rateCommission"),
        CONFLICT_OF_INTEREST("conflictOfInterest"),
        ESTATE_AGENT_DISCLOSURE("estateAgentDisclosure"),
        AGREEMENT_DATE("agreementDate"),
        COMMENCEMENT_DATE("commencementDate"),
        EXPIRY_DATE("expiryDate"),
        PRICE("price"),
        LIST_PRICE("listPrice"),
        GST_PAYABLE("gstPayable"),
        GST_INCLUSIVE("gstInclusive"),
        COBROKE("cobroke"),
        IS_OWNER("isOwner"),
        ROOM_RENTAL("roomRental"),
        //FOR PARTY SECTIONS
        REPRESENTING_SELLER("representing"),
        REPRESENTER_NAME("representerName"),
        REPRESENTER_NRIC("representerNric"),
        REPRESENTER_CONTACT("representerContact"),
        REPRESENTER_EMAIL("representerEmail"),
        REPRESNETER_POSTAL_CODE("representerPostalCode"),
        REPRESENTER_ADDRESS("representerAddress"),
        REPRESENTER_BLOCK_NUM("representerBlkNo"),
        REPRESENTER_FLOOR("representerFloor"),
        REPRESENTER_UNIT("representerUnit"),
        PARTY_TYPE("partyType"),
        PARTY_NRIC("partyNric"),
        PARTY_NAME("partyName"),
        PARTY_CONTACT("partyContact"),
        PARTY_EMAIL("partyEmail"),
        PARTY_ADDRESS("partyAddress"),
        PARTY_POSTAL_CODE("postalCode"),
        NATIONALITY("nationality"),
        PARTY_RACE("partyRace"),
        SALUTATION("salutation"),
        CITIZENSHIP("citizenship"),
        BLK_NO("blkNo"),
        IS_COMPANY("isCompany"),
        UNDERSTOOD_TERMS("understoodTerms"),
        PARTY_SIGNATURE("partySignature"),
        LEASE_DURATION("leaseDuration"),

        PARTY_AGENCY_NAME("partyAgencyName"),
        PARTY_AGENCY_LICENSE("partyAgencyLicense"),
        PARTY_AGENCY_ADDRESS("partyAgencyAddress"),
        AGENT_PARTY_TYPE("partyType"),
        AGENT_PARTY_NRIC("partyNric"),
        AGENT_PARTY_NAME("partyName"),
        AGENT_PARTY_CONTACT("partyContact"),
        AGENT_CEA_REG_NO("ceaRegNo"),
        AGENT_CD_AGENCY("cdAgency"),

        RENEWAL_COMMISSION_LIABLE("renewalCommissionLiable"),
        PRICE_RENEWAL_COMMISSION("priceRenewalCommission"),
        RATE_RENEWAL_COMMISSION("rateRenewalCommission"),
        RENTAL_FREQUENCY("rentalFrequency"),
        RENEWAL_TIME("renewalCommissionTime")

    }

    enum class CeaSection {
        PARTY_SECTION,
        AGENT_SECTION
    }

    enum class SignatureSource {
        AGENT_OWNER_SCREEN,
        AGENT_PARTY_SECTION
    }
}