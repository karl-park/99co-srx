package sg.searchhouse.agentconnect.viewmodel.activity.listing.community

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class CommunityHyperTargetViewModel(application: Application) : CoreViewModel(application) {
    @Inject
    lateinit var applicationContext: Context

    var isTemplateInitiated = false
    var isPropertySubTypesInitiated = false

    val templatePO = MutableLiveData<CommunityHyperTargetTemplatePO>()

    val name = Transformations.map(templatePO) { it?.name }
    val location = Transformations.map(templatePO) { it?.location }
    val radius: LiveData<Int> = Transformations.map(templatePO) { it?.radius }
    val propertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()
    val propertySubTypes: LiveData<List<ListingEnum.PropertySubType>> =
        Transformations.map(templatePO) {
            PropertyTypeUtil.getPropertySubTypes(it?.cdResearchSubtypes ?: "")
        }
    val tenureType = Transformations.map(templatePO) {
        TransactionEnum.TenureType.values().find { tenureType -> tenureType.value == it?.tenure }
            ?: TransactionEnum.TenureType.ANY
    }

    val isShowNameError = MutableLiveData<Boolean>()
    val isShowLocationError = MutableLiveData<Boolean>()
    val isShowRadiusSeekBar: LiveData<Boolean> =
        Transformations.map(location) { !TextUtils.isEmpty(it) }
    val isExpandXValue = MutableLiveData<Boolean>()
    val isExpandCapitalGain = MutableLiveData<Boolean>()
    val isExpandTenure = MutableLiveData<Boolean>()
    val isExpandHyperTargetName = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
    }

    fun setLocation(location: String) {
        setValue { it?.location = location }
    }

    fun setRadius(radius: Int?) {
        setValue { it?.radius = radius }
    }

    fun hasPropertySubType(propertySubType: ListingEnum.PropertySubType): Boolean {
        return propertySubTypes.value?.any { it == propertySubType } == true
    }

    fun togglePropertySubType(propertySubType: ListingEnum.PropertySubType) {
        val existingItems = propertySubTypes.value ?: emptyList()
        if (existingItems.contains(propertySubType)) {
            setPropertySubTypesValue(existingItems - propertySubType)
        } else {
            setPropertySubTypesValue(existingItems + propertySubType)
        }
    }

    fun setPropertySubTypesValue(subTypes: List<ListingEnum.PropertySubType>) {
        setValue { templatePO ->
            templatePO?.cdResearchSubtypes =
                subTypes.map { subType -> subType.type }.joinToString(",")
        }
    }

    fun isTenureTypeSelected(tenureType: TransactionEnum.TenureType): Boolean {
        return this.tenureType.value?.value == tenureType.value
    }

    fun toggleTenureType(tenureType: TransactionEnum.TenureType) {
        setValue { it?.tenure = tenureType.value }
    }

    fun setName(name: String) {
        setValue { it?.name = name }
    }

    private fun setValue(onSetValue: (CommunityHyperTargetTemplatePO?) -> Unit) {
        val thisTemplatePO = templatePO.value
        onSetValue.invoke(thisTemplatePO)
        templatePO.postValue(thisTemplatePO)
    }

    fun isHyperTargetValid(): Boolean {
        val templatePO = templatePO.value
        isShowNameError.postValue(templatePO?.isNameValid() != true)
        isShowLocationError.postValue(templatePO?.isLocationValid() != true)
        return templatePO?.isValid() == true
    }

    fun setMinXValue(minXValue: Int?) {
        val thisTemplatePO = templatePO.value ?: return
        thisTemplatePO.xvalueMin = minXValue
        templatePO.postValue(thisTemplatePO)
    }

    fun setMaxXValue(maxXValue: Int?) {
        val thisTemplatePO = templatePO.value ?: return
        thisTemplatePO.xvalueMax = maxXValue
        templatePO.postValue(thisTemplatePO)
    }

    fun setMinCapitalGain(minCapitalGainInteger: Int?) {
        val thisTemplatePO = templatePO.value ?: return
        thisTemplatePO.capitalGainMin = minCapitalGainInteger?.toDouble()?.run { this / 100 }
        templatePO.postValue(thisTemplatePO)
    }

    fun setMaxCapitalGain(maxCapitalGainInteger: Int?) {
        val thisTemplatePO = templatePO.value ?: return
        thisTemplatePO.capitalGainMax = maxCapitalGainInteger?.toDouble()?.run { this / 100 }
        templatePO.postValue(thisTemplatePO)
    }
}
