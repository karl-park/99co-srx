package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.CobrokeSmsRepository
import sg.searchhouse.agentconnect.enumeration.app.CobrokeSmsTemplate
import sg.searchhouse.agentconnect.model.api.cobrokesms.CobrokeSmsListingPO
import sg.searchhouse.agentconnect.model.api.cobrokesms.GetSmsTemplatesResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.net.HttpURLConnection.HTTP_PRECON_FAILED
import java.util.*
import javax.inject.Inject

class SmsBlastViewModel constructor(application: Application) :
    ApiBaseViewModel<GetSmsTemplatesResponse>(application) {
    val propertyAvailableStatus = MutableLiveData<ApiStatus.StatusKey>()
    val scheduleViewingStatus = MutableLiveData<ApiStatus.StatusKey>()

    val listings = MutableLiveData<List<CobrokeSmsListingPO>>()

    val selectedTemplate = MutableLiveData<CobrokeSmsTemplate>()

    val successMessage: LiveData<String> = Transformations.map(listings) { listings ->
        val listingCount = listings?.size ?: 0
        applicationContext.resources.getQuantityString(
            R.plurals.description_sms_blast_success,
            listingCount,
            listingCount.toString()
        )
    }

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var cobrokeSmsRepository: CobrokeSmsRepository

    val propertyAvailableTemplate: LiveData<GetSmsTemplatesResponse.TemplatesItem> =
        Transformations.map(mainResponse) { response ->
            response?.templates?.find {
                TextUtils.equals(
                    it.templateId,
                    CobrokeSmsTemplate.PROPERTY_AVAILABLE.id.toString()
                )
            }
        }

    val scheduleViewingTemplate: LiveData<GetSmsTemplatesResponse.TemplatesItem> =
        Transformations.map(mainResponse) { response ->
            response?.templates?.find {
                TextUtils.equals(
                    it.templateId,
                    CobrokeSmsTemplate.SCHEDULE_VIEWING.id.toString()
                )
            }
        }

    val showTitleIcon = MutableLiveData<Boolean>().apply { value = true }

    init {
        viewModelComponent.inject(this)
        selectedTemplate.value = CobrokeSmsTemplate.PROPERTY_AVAILABLE
    }

    fun performGetSmsTemplates(listingPOs: List<CobrokeSmsListingPO>) {
        performRequest(cobrokeSmsRepository.getSmsTemplates(listingPOs))
    }

    fun performBlastPropertyAvailable() {
        val listings = listings.value ?: return

        val templateId = try {
            propertyAvailableTemplate.value?.templateId?.toInt() ?: return
        } catch (e: NumberFormatException) {
            return ErrorUtil.handleError("Cobroke SMS template ID must be integer", e)
        }

        propertyAvailableStatus.run {
            postValue(ApiStatus.StatusKey.LOADING)
            ApiUtil.performRequest(
                applicationContext,
                cobrokeSmsRepository.sendSms(listings, templateId),
                onSuccess = {
                    postValue(ApiStatus.StatusKey.SUCCESS)
                },
                onFail = {
                    postValue(ApiStatus.StatusKey.FAIL)
                },
                onError = {
                    postValue(ApiStatus.StatusKey.ERROR)
                })
        }
    }

    fun performBlastScheduleViewing(timeCalendar: Calendar) {
        val listings = listings.value ?: return

        val templateId = try {
            scheduleViewingTemplate.value?.templateId?.toInt() ?: return
        } catch (e: NumberFormatException) {
            return ErrorUtil.handleError("Cobroke SMS template ID must be integer", e)
        }

        val minuteDifference = DateTimeUtil.getMinutesBetweenTimes(
            Calendar.getInstance().timeInMillis,
            timeCalendar.timeInMillis
        )

        if (minuteDifference + 1 >= MIN_SCHEDULE_TIME_IN_MINUTES) {
            scheduleViewingStatus.run {
                postValue(ApiStatus.StatusKey.LOADING)
                ApiUtil.performRequest(
                    applicationContext,
                    cobrokeSmsRepository.sendSms(listings, templateId, timeCalendar.timeInMillis),
                    onSuccess = {
                        postValue(ApiStatus.StatusKey.SUCCESS)
                    },
                    onFail = {
                        if (HTTP_PRECON_FAILED.toString() == it.errorCode) {
                            // Message: Your selections hit your FREE SMS Limit. Please limit your selections.
                            ViewUtil.showMessage(it.error)
                        }
                        postValue(ApiStatus.StatusKey.FAIL)
                    },
                    onError = {
                        postValue(ApiStatus.StatusKey.ERROR)
                    })
            }
        } else {
            ViewUtil.showMessage(R.string.warning_schedule_viewing_earliest_time)
        }
    }

    fun toggleIsExpandPropertyAvailability() {
        selectedTemplate.postValue(getToggledTemplate(CobrokeSmsTemplate.PROPERTY_AVAILABLE))
    }

    fun toggleIsExpandScheduleViewing() {
        selectedTemplate.postValue(getToggledTemplate(CobrokeSmsTemplate.SCHEDULE_VIEWING))
    }

    private fun getToggledTemplate(template: CobrokeSmsTemplate): CobrokeSmsTemplate? {
        return if (selectedTemplate.value == template) {
            null
        } else {
            template
        }
    }

    override fun shouldResponseBeOccupied(response: GetSmsTemplatesResponse): Boolean {
        return response.templates.isNotEmpty()
    }

    companion object {
        const val MIN_SCHEDULE_TIME_IN_MINUTES = 30
    }
}