package sg.searchhouse.agentconnect.view.activity.agent.profile

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_subscription_package_details.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySubscriptionPackageDetailsBinding
import sg.searchhouse.agentconnect.dsl.getIntExtraOrNull
import sg.searchhouse.agentconnect.dsl.launchActivity
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum.SubscriptionTypeEnum
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPurpose
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.SubscriptionCreditSource
import sg.searchhouse.agentconnect.helper.DataBindingAdapter
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.agent.profile.SubscriptionCreditPackageDetailAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.agent.profile.SubscriptionPackageDetailsViewModel

class SubscriptionCreditPackageDetailsActivity :
    ViewModelActivity<SubscriptionPackageDetailsViewModel, ActivitySubscriptionPackageDetailsBinding>() {

    private lateinit var adapter: SubscriptionCreditPackageDetailAdapter
    private var dialogProgressPayment: AlertDialog? = null

    companion object {
        private const val EXTRA_KEY_PAYMENT_PURPOSE = "EXTRA_KEY_PAYMENT_PURPOSE"
        private const val EXTRA_KEY_SOURCE = "EXTRA_KEY_SOURCE"
        private const val EXTRA_KEY_CREDIT_PACKAGE_TYPE = "EXTRA_KEY_CREDIT_PACKAGE_TYPE"

        const val BANK_LOGO_SIZE = 200
        const val BANK_LOGO_MARGIN_RIGHT = 40

        fun launch(
            context: Context,
            paymentPurpose: PaymentPurpose,
            source: SubscriptionCreditSource,
            creditType: Int? = null
        ) {
            context.launchActivity(
                SubscriptionCreditPackageDetailsActivity::class.java,
                getExtras(paymentPurpose, source, creditType)
            )
        }

        fun launchForResult(
            context: Activity,
            paymentPurpose: PaymentPurpose,
            source: SubscriptionCreditSource,
            creditType: Int? = null,
            requestCode: Int
        ) {
            context.launchActivityForResult(
                SubscriptionCreditPackageDetailsActivity::class.java,
                getExtras(paymentPurpose, source, creditType),
                requestCode
            )
        }

        fun launchForResult(
            fragment: Fragment,
            paymentPurpose: PaymentPurpose,
            source: SubscriptionCreditSource,
            creditType: Int? = null,
            requestCode: Int
        ) {
            fragment.launchActivityForResult(
                SubscriptionCreditPackageDetailsActivity::class.java,
                getExtras(paymentPurpose, source, creditType),
                requestCode
            )
        }

        private fun getExtras(
            paymentPurpose: PaymentPurpose,
            source: SubscriptionCreditSource,
            creditType: Int? = null
        ): Bundle {
            val extras = Bundle()
            extras.putSerializable(EXTRA_KEY_PAYMENT_PURPOSE, paymentPurpose)
            extras.putSerializable(EXTRA_KEY_SOURCE, source)
            creditType?.run { extras.putInt(EXTRA_KEY_CREDIT_PACKAGE_TYPE, this) }
            return extras
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraParams()
        setupAdapterAndList()
        observeLiveData()
    }

    private fun setupExtraParams() {
        intent.getSerializableExtra(EXTRA_KEY_SOURCE)?.run {
            viewModel.source = this as SubscriptionCreditSource
        } ?: throw Throwable("Missing subscription credit source")

        intent.getSerializableExtra(EXTRA_KEY_PAYMENT_PURPOSE)?.run {
            viewModel.paymentPurpose.value = this as PaymentPurpose
        } ?: throw Throwable("Missing payment purpose param")
    }

    private fun setupAdapterAndList() {
        adapter = SubscriptionCreditPackageDetailAdapter(
            this,
            viewModel.packages
        )
        { packageId: String, isInstallment: Boolean, bankId: Int?, installmentMonth: Int? ->
            when (packageId) {
                SubscriptionTypeEnum.TRIAL.value -> {
                    viewModel.performCreateTrialAccount()
                }
                else -> {
                    proceedToPayment(packageId, isInstallment, bankId, installmentMonth)
                }
            }
        }
        list_subscription_packages.layoutManager = LinearLayoutManager(this)
        list_subscription_packages.adapter = adapter
    }

    private fun observeLiveData() {
        //Payment Purpose
        viewModel.paymentPurpose.observeNotNull(this) {
            when (it) {
                PaymentPurpose.SUBSCRIPTION -> viewModel.getSubscriptionPackageDetails()
                PaymentPurpose.CREDIT -> {
                    intent.getIntExtraOrNull(EXTRA_KEY_CREDIT_PACKAGE_TYPE)?.run {
                        viewModel.getSrxCreditPackageDetails(this)
                    } ?: ErrorUtil.handleError("Missing credit package type")
                }
                else -> {
                    println("do nothing in case null")
                }
            }
        }

        viewModel.mainResponse.observeNotNull(this) { response ->
            //update title
            collapsingToolbar.title = response.title

            //populate logos
            if (response.installmentOptions.isNotEmpty()) {
                val logos = arrayListOf<String>()
                response.installmentOptions.map { installment -> logos.addAll(installment.logos) }
                populateLogos(logos)
                //set payment options to adapter
                adapter.setInstallmentPaymentOptions(response.installmentOptions)
            }

            //assign payment type
            viewModel.paymentType.value = response.paymentType

            //add subscription packages list
            viewModel.packages.clear()
            viewModel.packages.addAll(response.packages)
            adapter.preselectTrialPackage()
            adapter.notifyDataSetChanged()
        }

        viewModel.paymentLinkResult.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val result = it.body ?: return@observeNotNull
                    dialogProgressPayment?.dismiss()
                    PaymentActivity.launch(
                        activity = this,
                        paymentLink = result.getResultWithoutBanner(),
                        paymentPurpose = viewModel.paymentPurpose.value,
                        source = viewModel.source
                    )
                    finish()
                }
                ApiStatus.StatusKey.FAIL -> {
                    dialogProgressPayment?.dismiss()
                    ViewUtil.showMessage(it.error?.error)
                }
                ApiStatus.StatusKey.ERROR -> {
                    dialogProgressPayment?.dismiss()
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.createTrialAccountResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    onTrialActivated()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                }
            }
        }
    }

    private fun onTrialActivated() {
        ViewUtil.showMessage(R.string.toast_trial_subscription_activated)
        setResult(RESULT_OK)
        finish()
    }

    private fun proceedToPayment(
        packageId: String,
        isInstallment: Boolean,
        bankId: Int?,
        installmentMonth: Int?
    ) {
        dialogProgressPayment =
            dialogUtil.showProgressDialog(R.string.progress_dialog_message)
        viewModel.generatePaymentLink(
            packageId,
            isInstallment,
            bankId,
            installmentMonth
        )
    }

    private fun populateLogos(items: ArrayList<String>) {
        items.forEach { logo ->
            val logoImage = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(BANK_LOGO_SIZE, BANK_LOGO_SIZE)
            layoutParams.setMargins(0, 0, BANK_LOGO_MARGIN_RIGHT, 0)
            logoImage.layoutParams = layoutParams
            DataBindingAdapter.loadImage(logoImage, logo)
            binding.layoutBankLogos.addView(logoImage)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_subscription_package_details
    }

    override fun getViewModelClass(): Class<SubscriptionPackageDetailsViewModel> {
        return SubscriptionPackageDetailsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

}