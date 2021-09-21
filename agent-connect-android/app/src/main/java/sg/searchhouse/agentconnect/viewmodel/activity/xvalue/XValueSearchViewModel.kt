package sg.searchhouse.agentconnect.viewmodel.activity.xvalue

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.XValueRepository
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.model.api.xvalue.GetExistingXValuesResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class XValueSearchViewModel constructor(application: Application) :
    ApiBaseViewModel<GetExistingXValuesResponse>(application) {

    @Inject
    lateinit var repository: XValueRepository

    @Inject
    lateinit var applicationContext: Context

    val searchText: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
    }

    fun performGetExistingXValues() {
        val searchText = searchText.value ?: ""
        if (TextUtils.isEmpty(searchText)) return
        performRequest(
            repository.getExistingXValues(
                searchText,
                1,
                XValueEnum.GetExistingXValuesProperty.DATE,
                XValueEnum.GetExistingXValuesOrder.DESC
            )
        )
    }

    fun afterSearchTextChanged(editable: Editable?) {
        val text = editable?.toString() ?: ""
        searchText.postValue(text)
    }

    override fun shouldResponseBeOccupied(response: GetExistingXValuesResponse): Boolean {
        return response.xvalues.results.isNotEmpty()
    }
}