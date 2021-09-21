package sg.searchhouse.agentconnect.view.fragment.auth

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.dialog_fragment_non_subscriber.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.Endpoint
import sg.searchhouse.agentconnect.databinding.DialogFragmentNonSubscriberBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLink
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPurpose
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.SubscriptionCreditSource
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.dashboard.SubscriberPromptDismissEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.FAIL
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.SUCCESS
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.agent.profile.SubscriptionCreditPackageDetailsActivity
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.auth.NonSubscriberViewModel
import java.util.*

class NonSubscriberDialogFragment :
    ViewModelFullWidthDialogFragment<NonSubscriberViewModel, DialogFragmentNonSubscriberBinding>() {

    private lateinit var mContext: Context

    companion object {
        const val TAG = "TAG_NON_SUBSCRIBER_PROMPT"
        const val EXTRA_KEY_FLAG_DISMISS_DIALOG = "EXTRA_KEY_FLAG_DISMISS_DIALOG"
        const val EXTRA_KEY_ADVISOR_MODULE = "EXTRA_KEY_ADVISOR_MODULE"

        private const val REQUEST_CODE_SUBSCRIPTION_PACKAGES = 1

        fun newInstance(
            allowDismiss: Boolean? = null,
            module: AccessibilityEnum.AdvisorModule? = null
        ): NonSubscriberDialogFragment {
            val dialogFragment = NonSubscriberDialogFragment()
            val bundle = Bundle()
            allowDismiss?.let { bundle.putBoolean(EXTRA_KEY_FLAG_DISMISS_DIALOG, it) }
            module?.let { bundle.putSerializable(EXTRA_KEY_ADVISOR_MODULE, it) }
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExtraParams()
        populatePrivacyPolicyAgreement()
        populateCurrentDateAndTime()
        observeLiveData()
        handleListeners()
    }

    private fun setupExtraParams() {
        val bundle = arguments ?: return
        viewModel.allowDismiss.value = bundle.getBoolean(EXTRA_KEY_FLAG_DISMISS_DIALOG, false)
        bundle.getSerializable(EXTRA_KEY_ADVISOR_MODULE)?.let { module ->
            viewModel.module.value = module as AccessibilityEnum.AdvisorModule
        } ?: run {
            setEmptyModule()
            visitSubscriptionPage()
        }
    }

    private fun setEmptyModule() {
        viewModel.module.value = null
    }

    private fun handleListeners() {
        ib_close_subscriber_prompt.setOnClickListener { dialog?.dismiss() }
        tv_selected_time.setOnClickListener { showTimePickerDialog() }
        tv_selected_date.setOnClickListener { showDatePickerDialog() }
        btn_submit_schedule.setOnClickListener {
            viewModel.purpose.value?.let {
                viewModel.submit(it.value)
            } ?: ViewUtil.showMessage(R.string.msg_choose_one_purpose)
        }
        btn_become_subscriber.setOnClickListener { visitSubscriptionPage() }
    }

    private fun visitSubscriptionPage() {
        SubscriptionCreditPackageDetailsActivity.launchForResult(
            fragment = this,
            paymentPurpose = PaymentPurpose.SUBSCRIPTION,
            source = SubscriptionCreditSource.NON_SUBSCRIBER_PROMPT,
            requestCode = REQUEST_CODE_SUBSCRIPTION_PACKAGES
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SUBSCRIPTION_PACKAGES -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        dismissNonSubscriberDialog()
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun populatePrivacyPolicyAgreement() {
        tv_agree_privacy_policy.setupLink(
            R.string.msg_agree_privacy_policy,
            R.string.label_privacy_policy
        ) {
            IntentUtil.visitSrxUrl(mContext, Endpoint.PRIVACY_POLICY)
        }
    }

    private fun populateCurrentDateAndTime() {
        viewModel.selectedDate = DateTimeUtil.getTodayDate(format = DateTimeUtil.FORMAT_DATE_12)
        viewModel.displayDate.value =
            DateTimeUtil.getTodayDate(format = DateTimeUtil.FORMAT_DATE_4)
        viewModel.selectedTime = DateTimeUtil.getCurrentTime()
        viewModel.displayTime.value =
            DateTimeUtil.getCurrentTime(format = DateTimeUtil.FORMAT_TIME_2)
    }

    private fun observeLiveData() {
        viewModel.scheduleCallbackStatus.observeNotNull(viewLifecycleOwner) { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> {
                    viewModel.scheduleCallbackShowed.value = false
                    viewModel.isPrivacyPolicyChecked.value = false
                    viewModel.purpose.value = null
                    showRequestSubmittedDialog()
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        }
    }

    private fun showRequestSubmittedDialog() {
        dialogUtil.showInformationDialog(R.string.msg_schedule_call_back_success) {
            SessionUtil.signOut(isUserAction = true)
        }
    }

    private fun showTimePickerDialog() {
        TimePickerDialog(
            mContext,
            { _, selectedHour, selectedMinute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                viewModel.selectedTime = DateTimeUtil.convertCalendarToString(
                    calendar,
                    format = DateTimeUtil.FORMAT_TIME_1
                )
                viewModel.displayTime.value = DateTimeUtil.convertCalendarToString(
                    calendar,
                    format = DateTimeUtil.FORMAT_TIME_2
                )
            },
            DateTimeUtil.getCurrentHourOfDay(),
            DateTimeUtil.getCurrentMinute(),
            true
        ).show()
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            mContext,
            { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                viewModel.selectedDate =
                    DateTimeUtil.convertCalendarToString(calendar, DateTimeUtil.FORMAT_DATE_12)
                viewModel.displayDate.value =
                    DateTimeUtil.convertCalendarToString(calendar, DateTimeUtil.FORMAT_DATE_4)
            },
            DateTimeUtil.getCurrentYear(),
            DateTimeUtil.getCurrentMonth(),
            DateTimeUtil.getCurrentDayOfMonth()
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }

    fun isDialogShowing(): Boolean {
        return dialog?.isShowing ?: false
    }

    //Note: please don't remove this method.
    fun dismissNonSubscriberDialog() {
        dialog?.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        handleDismiss()
    }

    private fun handleDismiss() {
        if (viewModel.module.value != null) {
            if (viewModel.module.value == AccessibilityEnum.AdvisorModule.AGENT_DIRECTORY) {
                //Note : if agent directory module, show dashboard again
                RxBus.publish(
                    SubscriberPromptDismissEvent(
                        launchDashboard = true,
                        showAutoImportPopup = null
                    )
                )
            }
        } else {
            //Note: if dismiss this prompt, show auto import pop up
            RxBus.publish(
                SubscriberPromptDismissEvent(
                    launchDashboard = null,
                    showAutoImportPopup = true
                )
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_non_subscriber
    }

    override fun getViewModelClass(): Class<NonSubscriberViewModel> {
        return NonSubscriberViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}