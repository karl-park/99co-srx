package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import java.io.Serializable

data class CeaFormRowPO(
    @SerializedName("rowTitle")
    var rowTitle: String,
    @SerializedName("rowValue")
    var rowValue: String,
    @SerializedName("keyValue")
    val keyValue: String,
    @SerializedName("placeHolder")
    val placeHolder: String,
    @SerializedName("required")
    var required: Boolean,
    @SerializedName("visible")
    var visible: Boolean,
    @SerializedName("currencyFormat")
    val currencyFormat: Boolean,
    @SerializedName("highlighted")
    val highlighted: UIColorPO?,
    @SerializedName("rowType")
    var rowType: Int,
    @SerializedName("pickerSelection")
    val pickerSelection: List<String>,
    @SerializedName("mapper")
    val mapper: Map<String, String>?,
    @SerializedName("info")
    val info: String,
    @SerializedName("title")
    val title: String
) : Serializable {

    fun getCeaFormRowType(): CeaExclusiveEnum.CeaFormRowType? {
        return CeaExclusiveEnum.CeaFormRowType.values().find { it.value == rowType }
    }

    fun getRowValueBooleanForToggleType(): Boolean {
        if (getCeaFormRowType() == CeaExclusiveEnum.CeaFormRowType.TOGGLE) {
            return rowValue == "true"
        }
        return false
    }

    //Note : adding this because of backend legacy
    fun getTrialSubmissionValue(): Boolean {
        //Convert Trial submission value to boolean type
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.TEST.value) {
            if (rowValue == "true") {
                return true
            } else if (rowValue == "false") {
                return false
            }
        }
        return false
    }

    //Note : adding this because of backend legacy
    fun getRenewalKeyIntByValue(): Int {
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.RENEWAL.value) {
            return mapper?.getOrElse(rowValue) { "" }?.toIntOrNull() ?: 0
        }
        return 0
    }

    //Note : adding this because of backend legacy
    fun getBedroomKeyIntByValue(): Int {
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.BEDROOM.value) {
            return mapper?.getOrElse(rowValue) { "" }?.toIntOrNull() ?: 0
        }
        return 0
    }

    //Note: adding this because of backend legacy
    fun getTenureKeyIntByValue(): Int {
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.TENURE.value) {
            return mapper?.getOrElse(rowValue) { "" }?.toIntOrNull() ?: 0
        }
        return 0
    }

    //Note : adding this because of backend legacy
    fun getTenantedValue(): Boolean {
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.TENANTED.value) {
            if (rowValue == "true") {
                return true
            } else if (rowValue == "false") {
                return false
            }
        }
        return false
    }

    //Note : adding this because of backend legacy
    fun getNumberOfStory(): Int {
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.NUM_OF_STOREY.value) {
            return mapper?.getOrElse(rowValue) { "" }?.toIntOrNull() ?: 0
        }
        return 0
    }

    //Note : adding this because of backend legacy
    fun isOwner(): Boolean {
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.IS_OWNER.value) {
            return mapper?.getOrElse(rowValue) { "false" } == "true"
        }
        return false
    }

    fun getLeaseDurationValue(): Int {
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.LEASE_DURATION.value) {
            return mapper?.getOrElse(rowValue) { "" }?.toIntOrNull() ?: 0
        }
        return 0
    }

    fun getRentalFrequency(): Int {
        if (keyValue == CeaExclusiveEnum.CeaFormRowTypeKeyValue.RENTAL_FREQUENCY.value) {
            return mapper?.getOrElse(rowValue) { "" }?.toIntOrNull() ?: 0
        }
        return 0
    }

}