package sg.searchhouse.agentconnect.viewmodel.activity.transaction

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.TransactionRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.TransactionsDisplayMode
import sg.searchhouse.agentconnect.model.api.transaction.ProjectTransactionRequest
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSummaryResponse
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.io.IOException
import javax.inject.Inject

class ProjectTransactionsViewModel constructor(application: Application) :
    ApiBaseViewModel<TransactionSummaryResponse>(application) {
    override fun shouldResponseBeOccupied(response: TransactionSummaryResponse): Boolean {
        return response.summary != null
    }

    val ownershipType = MutableLiveData<ListingEnum.OwnershipType>()

    val propertySubType = MutableLiveData<ListingEnum.PropertySubType>()
    val displayMode = MutableLiveData<TransactionsDisplayMode>()

    val projectId = MutableLiveData<Int>()
    val projectName = MutableLiveData<String>()
    val projectDescription = MutableLiveData<String>()
    val isShowTower = MutableLiveData<Boolean>()

    var externalPropertyMainType: ListingEnum.PropertyMainType? = null
    var externalPropertySubTypes: String? = null

    var externalRadius: TransactionEnum.Radius? = null
    var externalAreaType: TransactionEnum.AreaType? = null
    var externalTenureType: TransactionEnum.TenureType? = null

    var externalMinAge: Int? = null
    var externalMaxAge: Int? = null

    var externalMinSize: Int? = null
    var externalMaxSize: Int? = null

    val isLockScrollView = MutableLiveData<Boolean>()

    val summary: LiveData<TransactionSummaryResponse.Summary> =
        Transformations.map(mainResponse) {
            it?.summary
        }

    val propertySubTypeIcon: LiveData<Drawable> =
        Transformations.map(mainResponse) {
            it?.summary?.getPropertySubType()?.run {
                val mainType = PropertyTypeUtil.getPropertyMainType(type)
                mainType?.run { applicationContext.getDrawable(iconResId) }
            }
        }

    val summaryLabel: LiveData<String> = Transformations.map(projectName) {
        applicationContext.getString(R.string.label_project_transaction_summary_title, it)
    }

    val lastMonthChangeIcon: LiveData<Drawable> =
        Transformations.map(mainResponse) {
            val lastMonthChange = it?.summary?.lastMonthChange ?: 0.0
            when {
                lastMonthChange > 0 -> {
                    applicationContext.getDrawable(R.drawable.ic_triangle_up)
                }
                lastMonthChange < 0 -> {
                    applicationContext.getDrawable(R.drawable.ic_triangle_down)
                }
                else -> null
            }
        }

    val isHdb: LiveData<Boolean> = Transformations.map(propertySubType) {
        it?.run { PropertyTypeUtil.isHDB(type) } ?: false
    }

    val tabPositionSale = MutableLiveData<Int>().apply { value = 0 }
    val isShowExportButton = MediatorLiveData<Boolean>()

    val reportExcelLocalFileName = MutableLiveData<String>()

    @Inject
    lateinit var transactionRepository: TransactionRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        setupIsShowExportButton()
        displayMode.value = TransactionsDisplayMode.LIST
    }

    private fun setupIsShowExportButton() {
        isShowExportButton.addSource(isShowTower) {
            isShowExportButton.postValue(getIsShowExportButton())
        }
        isShowExportButton.addSource(tabPositionSale) {
            isShowExportButton.postValue(getIsShowExportButton())
        }
        isShowExportButton.addSource(ownershipType) {
            isShowExportButton.postValue(getIsShowExportButton())
        }
    }

    // Export button available for all except tower view
    private fun getIsShowExportButton(): Boolean {
        val isSale = ownershipType.value == ListingEnum.OwnershipType.SALE
        val isShowTower = isShowTower.value == true
        val isPositionTower = (tabPositionSale.value ?: 0) == 1
        return !(isSale && isShowTower && isPositionTower)
    }

    fun getProjectTransactionRequest(): ProjectTransactionRequest? {
        val projectId = projectId.value ?: return null
        val ownershipType = ownershipType.value?.value ?: return null

        val isFreeHoldTenure = when (externalTenureType) {
            TransactionEnum.TenureType.FREEHOLD -> true
            TransactionEnum.TenureType.LEASEHOLD -> false
            else -> null
        }

        return ProjectTransactionRequest(
            projectId = projectId,
            saleOrRent = ownershipType,
            propertyTypes = externalPropertySubTypes?.let { StringUtil.getIntListFromString(it) },
            radius = externalRadius?.radiusValue,
            typeOfArea = externalAreaType?.value,
            isFreeholdTenure = isFreeHoldTenure,
            ageFrom = externalMinAge,
            ageTo = externalMaxAge,
            sizeFrom = externalMinSize,
            sizeTo = externalMaxSize,
            limit = AppConstant.BATCH_SIZE_TRANSACTIONS
        )
    }

    fun performRequest() {
        val projectTransactionRequest = getProjectTransactionRequest() ?: return
        performRequest(transactionRepository.projectSummary(projectTransactionRequest))
    }

    fun exportTransactions(transactionsCsvContent: String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val userName = SessionUtil.getCurrentUser()?.name?.replace(" ", "_") ?: ""
                val fileName = "${userName}_Transaction_Report.csv"
                try {
                    FileUtil.writeTextToFile(
                        applicationContext,
                        transactionsCsvContent,
                        fileName,
                        AppConstant.DIR_TRANSACTION_REPORT
                    )
                } catch (e: IOException) {
                    return@withContext ErrorUtil.handleError(applicationContext.getString(R.string.toast_generate_report_failed))
                }
                reportExcelLocalFileName.postValue(fileName)
            }
        }
    }
}