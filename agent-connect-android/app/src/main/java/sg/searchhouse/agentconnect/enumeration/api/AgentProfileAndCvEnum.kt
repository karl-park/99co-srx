package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class AgentProfileAndCvEnum {

    enum class TransactionType(val value: String, @StringRes val label: Int) {
        SOLD("sold", R.string.label_sold),
        RENTED("rented", R.string.label_rented)
    }

    enum class TransactionOwnershipType(val value: String) {
        SOLD("S"),
        RENTED("R")
    }

    enum class OrderCriteria(val value: String, @StringRes val label: Int) {
        CATEGORY("category", R.string.order_criteria_category),
        PRICE("price", R.string.order_criteria_price),
        PSF("psf", R.string.order_criteria_psf)
    }

    enum class TransactionPropertyType(val value: String) {
        HDB("hdb"),
        PRIVATE("private")
    }

    enum class PaymentPlan(val value: String, @StringRes val label: Int) {
        SINGLE("single", R.string.label_payment_single),
        INSTALMENT("instalment", R.string.label_payment_installment)
    }

    enum class PaymentPurpose(val value: String) {
        SUBSCRIPTION("subscription"),
        CREDIT("credit")
    }

    enum class SubscriptionCreditSource(val screen: Int) {
        NON_SUBSCRIBER_PROMPT(1),
        AGENT_CV(2),
        MY_LISTING_FEATURE(3),
        SPONSOR_LISTING(4)
    }
}