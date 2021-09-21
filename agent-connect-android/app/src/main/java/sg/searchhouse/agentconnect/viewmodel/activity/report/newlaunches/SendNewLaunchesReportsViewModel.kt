package sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ProjectRepository
import sg.searchhouse.agentconnect.model.api.project.GetNewLaunchesNotificationTemplates
import sg.searchhouse.agentconnect.model.api.project.GetNewLaunchesResponse
import sg.searchhouse.agentconnect.model.api.project.SendReportToClientResponse
import sg.searchhouse.agentconnect.model.app.DeviceContact
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class SendNewLaunchesReportsViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var projectRepository: ProjectRepository

    @Inject
    lateinit var applicationContext: Context

    var tempMobileNumber: String? = null
    val selectedReports = MutableLiveData<List<GetNewLaunchesResponse.NewLaunchProject>>()
    val attachmentLabel = MutableLiveData<String>()
    val projectsSelectedLabel = MutableLiveData<String>()
    val contacts = MutableLiveData<ArrayList<DeviceContact>>()
    val template = MutableLiveData<String>()
    val successMessage = MutableLiveData<String>()
    val attemptedClients = MutableLiveData<String>()
    val isExpanded = MutableLiveData<Boolean>()
    val expandCollapseLabel: LiveData<String> = Transformations.map(isExpanded) {
        return@map when (it) {
            true -> applicationContext.getString(R.string.label_collapse)
            else -> applicationContext.getString(R.string.label_expand)
        }
    }

    private val btnState = MutableLiveData<ButtonState>()
    val isBtnEnabled: LiveData<Boolean> =
        Transformations.map(btnState) { response ->
            return@map when (response) {
                ButtonState.SUBMITTING -> {
                    false
                }
                else -> {
                    true
                }
            }
        }
    val btnLabel: LiveData<String> =
        Transformations.map(btnState) { response ->
            return@map when (response) {
                ButtonState.SUBMITTING -> {
                    applicationContext.getString(R.string.action_sending)
                }
                else -> {
                    applicationContext.getString(R.string.action_send)
                }
            }
        }

    val getTemplateStatus = MutableLiveData<ApiStatus<GetNewLaunchesNotificationTemplates>>()
    val sendReportToClientResponse = MutableLiveData<ApiStatus<SendReportToClientResponse>>()
    val sendReportStatus = MutableLiveData<ApiStatus.StatusKey>()

    init {
        viewModelComponent.inject(this)

        btnState.value = ButtonState.NORMAL
        isExpanded.value = false
    }

    fun getTemplates() {
        ApiUtil.performRequest(
            applicationContext,
            projectRepository.getNewLaunchesNotificationTemplate(),
            onSuccess = { getTemplateStatus.postValue(ApiStatus.successInstance(it)) },
            onFail = { getTemplateStatus.postValue(ApiStatus.failInstance(it)) },
            onError = { getTemplateStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun sendReportToClient() {
        val reports = selectedReports.value ?: listOf()
        val crunchResearchStreetIds = reports.map { it.crsId }.joinToString(",")

        val mobileNumberList = contacts.value ?: arrayListOf()
        val mobileNumbersString =
            mobileNumberList.map { it.getMobileNumberInteger() }.joinToString(",")

        btnState.postValue(ButtonState.SUBMITTING)
        sendReportStatus.postValue(ApiStatus.StatusKey.LOADING)

        ApiUtil.performRequest(
            applicationContext,
            projectRepository.sendReportToClient(
                crunchResearchStreetIds = crunchResearchStreetIds,
                mobileNumbers = mobileNumbersString
            ),
            onSuccess = {
                //Success Report Count
                if (it.reportAvailableSuccess.isNotEmpty()) {
                    successMessage.postValue(
                        applicationContext.resources.getQuantityString(
                            R.plurals.label_success_report_count,
                            it.reportAvailableSuccess.size,
                            NumberUtil.formatThousand(it.reportAvailableSuccess.size)

                        )
                    )
                }
                //mobile number attempted
                if (it.mobileNumbersAttempted.isNotEmpty()) {
                    attemptedClients.postValue(
                        applicationContext.resources.getQuantityString(
                            R.plurals.label_clients_count,
                            it.mobileNumbersAttempted.size,
                            NumberUtil.formatThousand(it.mobileNumbersAttempted.size)
                        )
                    )
                }
                btnState.postValue(ButtonState.SUBMITTED)
                sendReportToClientResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                btnState.postValue(ButtonState.ERROR)
                sendReportStatus.postValue(ApiStatus.StatusKey.ERROR)
                sendReportToClientResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                btnState.postValue(ButtonState.ERROR)
                sendReportStatus.postValue(ApiStatus.StatusKey.FAIL)
                sendReportToClientResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }
}