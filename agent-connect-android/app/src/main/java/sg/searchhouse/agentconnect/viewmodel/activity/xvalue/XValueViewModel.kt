package sg.searchhouse.agentconnect.viewmodel.activity.xvalue

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.XValueRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum.OwnershipType
import sg.searchhouse.agentconnect.model.api.xvalue.*
import sg.searchhouse.agentconnect.model.app.XValueEntryParams
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.service.GenerateXValueReportService
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import java.io.IOException
import javax.inject.Inject

class XValueViewModel constructor(application: Application) :
    CoreViewModel(application) {
    val isExpandComparables = MutableLiveData<Boolean>()
    val isExpandXTrend = MutableLiveData<Boolean>()
    val isExpandAskingPriceCalculator = MutableLiveData<Boolean>()

    val entryParams = MutableLiveData<XValueEntryParams>()
    val mainXValue = MutableLiveData<XValue>()

    val description = MutableLiveData<String>()

    val getProjectResponse = MutableLiveData<GetProjectResponse>()
    val calculateResponse = MutableLiveData<CalculateResponse>()
    val calculateStatus =
        MutableLiveData<ApiStatus.StatusKey>().apply { value = ApiStatus.StatusKey.LOADING }
    val calculateFailText = MutableLiveData<String>()
    val updateRequestResponse = MutableLiveData<UpdateRequestResponse>()

    val getValuationDetailResponse = MutableLiveData<GetValuationDetailResponse>()
    val getValuationDetailStatus: MutableLiveData<ApiStatus.StatusKey> by lazy {
        MutableLiveData<ApiStatus.StatusKey>().apply { value = ApiStatus.StatusKey.LOADING }
    }

    // TODO Check logic again, maybe refactor or document this
    val isShowComparablesEmpty: LiveData<Boolean> =
        Transformations.map(getValuationDetailResponse) {
            it.data.comparables.isNotEmpty() && getValuationDetailStatus.value == ApiStatus.StatusKey.SUCCESS
        }

    val shouldDisplayComparableSection: LiveData<Boolean> =
        Transformations.map(getValuationDetailResponse) {
            it?.data?.comparables?.isNotEmpty() == true && xValueOwnershipType.value != OwnershipType.RENT
        }

    val isShowSaleTransactionsEmpty = MediatorLiveData<Boolean>()

    val isShowRentalTransactionsEmpty = MediatorLiveData<Boolean>()

    val averageComparablePsf = MutableLiveData<String>()

    private val reportLocalFileName = MutableLiveData<String>()

    val loadLatestSaleTransactionsStatus: MutableLiveData<ApiStatus.StatusKey> by lazy {
        MutableLiveData<ApiStatus.StatusKey>().apply { value = ApiStatus.StatusKey.LOADING }
    }

    val loadLatestRentalTransactionsStatus: MutableLiveData<ApiStatus.StatusKey> by lazy {
        MutableLiveData<ApiStatus.StatusKey>().apply { value = ApiStatus.StatusKey.LOADING }
    }

    val loadHomeReportSaleTransactionRevisedResponse =
        MutableLiveData<LoadHomeReportSaleTransactionRevisedResponse>()
    val loadHomeReportRentalTransactionRevisedResponse =
        MutableLiveData<LoadHomeReportRentalInformationRevisedResponse>()

    val ownershipType: MutableLiveData<ListingEnum.OwnershipType> by lazy {
        MutableLiveData<ListingEnum.OwnershipType>().apply {
            value = ListingEnum.OwnershipType.SALE
        }
    }

    val renovationCost = MutableLiveData<Int>()
    val renovationYear = MutableLiveData<Int>()
    val goodWill: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply { value = 0 }
    }

    val goodWillLabel: LiveData<String> = Transformations.map(goodWill) {
        val goodWill = it ?: 0
        applicationContext.getString(R.string.label_percent, NumberUtil.formatThousand(goodWill))
    }

    val selectedTimeScale: MutableLiveData<XValueEnum.TimeScale> by lazy {
        MutableLiveData<XValueEnum.TimeScale>().apply {
            value = XValueEnum.TimeScale.TEN_YEARS
        }
    }

    val xValue = MutableLiveData<Int>()
    val xValuePlus = MutableLiveData<Int>()
    val xListingPrice = MutableLiveData<Int>()

    val xValuePlusLabel = MediatorLiveData<String>()
    val xListingPriceLabel = MediatorLiveData<String>()

    val xTrendList = MutableLiveData<List<XTrendKeyValue>>()

    val isGraphAvailable: LiveData<Boolean> = Transformations.map(xTrendList) {
        it?.isNotEmpty() == true
    }

    val graphStatusKey = MutableLiveData<ApiStatus.StatusKey>()

    val xTrendSectionTitle = MediatorLiveData<String>()

    val title = MutableLiveData<String>()

    val xValueOwnershipType: LiveData<OwnershipType> = Transformations.map(mainXValue) {
        it?.getOwnershipType()
    }

    val canSelectOwnershipType: LiveData<Boolean> = Transformations.map(mainXValue) {
        // NOTE: There is not BOTH option in x-value response
        // By returning SALE, assume that both options are available
        // Chun Hoe 20200721
        it?.getOwnershipType() == OwnershipType.SALE
    }

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var xValueRepository: XValueRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        setupRecentTransactionsEmptyLiveData()
        setupXValuePlusLabel()
        setupXListingPriceLabel()
        setupXTrendSectionTitle()
        isExpandComparables.value = true
        title.value = applicationContext.getString(R.string.activity_x_value)
    }

    private fun setupXTrendSectionTitle() {
        xTrendSectionTitle.addSource(entryParams) {
            xTrendSectionTitle.postValue(getXTrendSectionTitle())
        }
        xTrendSectionTitle.addSource(mainXValue) {
            xTrendSectionTitle.postValue(getXTrendSectionTitle())
        }
    }

    private fun getXTrendSectionTitle(): String {
        return when {
            mainXValue.value?.getOwnershipType() == OwnershipType.RENT -> {
                applicationContext.getString(R.string.title_x_value_latest_transactions)
            }
            entryParams.value != null -> {
                // New x value
                applicationContext.getString(R.string.title_x_value_x_trend)
            }
            else -> {
                // Existing x value
                applicationContext.getString(R.string.title_x_value_latest_transactions)
            }
        }
    }

    // X-Listing Price value priority: xListingPrice > xValuePlus > xValue
    private fun setupXListingPriceLabel() {
        xListingPriceLabel.addSource(xValue) {
            val isFallbackNeeded =
                NumberUtil.isZeroOrNull(xListingPrice.value) && NumberUtil.isZeroOrNull(xValuePlus.value)
            if (NumberUtil.isZeroOrNull(it)) {
                if (isFallbackNeeded) {
                    xListingPriceLabel.postValue("")
                }
                return@addSource
            }
            if (isFallbackNeeded) {
                xListingPriceLabel.postValue(
                    when {
                        it > 0 -> getFormattedCurrency(it)
                        else -> ""
                    }
                )
            }
        }
        xListingPriceLabel.addSource(xValuePlus) {
            if (NumberUtil.isZeroOrNull(it)) return@addSource
            if (NumberUtil.isZeroOrNull(xListingPrice.value)) {
                val label = getFormattedCurrency(it)
                xListingPriceLabel.postValue(label)
            }
        }
        xListingPriceLabel.addSource(xListingPrice) {
            if (NumberUtil.isZeroOrNull(it)) return@addSource
            val label = getFormattedCurrency(it)
            xListingPriceLabel.postValue(label)
        }
    }

    // X-Value Plus priority: xValuePlus > xValue
    private fun setupXValuePlusLabel() {
        xValuePlusLabel.addSource(xValue) {
            if (NumberUtil.isZeroOrNull(it)) {
                if (NumberUtil.isZeroOrNull(xValuePlus.value)) {
                    xValuePlusLabel.postValue("")
                }
                return@addSource
            }
            if (NumberUtil.isZeroOrNull(xValuePlus.value)) {
                xValuePlusLabel.postValue(
                    when {
                        it > 0 -> getFormattedCurrency(it)
                        else -> ""
                    }
                )
            }
        }
        xValuePlusLabel.addSource(xValuePlus) {
            if (NumberUtil.isZeroOrNull(it)) return@addSource
            val label = getFormattedCurrency(it)
            xValuePlusLabel.postValue(label)
        }
    }

    private fun getFormattedCurrency(priceInt: Int): String {
        return NumberUtil.getFormattedCurrency(priceInt)
    }

    private fun setupRecentTransactionsEmptyLiveData() {
        isShowSaleTransactionsEmpty.addSource(loadHomeReportSaleTransactionRevisedResponse) {
            ownershipType.value == ListingEnum.OwnershipType.SALE && it.getTransactionList()
                .isEmpty()
        }

        isShowSaleTransactionsEmpty.addSource(ownershipType) {
            it == ListingEnum.OwnershipType.SALE && loadHomeReportSaleTransactionRevisedResponse.value?.getTransactionList()
                ?.isNotEmpty() != true
        }

        isShowRentalTransactionsEmpty.addSource(loadHomeReportRentalTransactionRevisedResponse) {
            ownershipType.value == ListingEnum.OwnershipType.RENT && it.getTransactionList()
                .isEmpty()
        }

        isShowRentalTransactionsEmpty.addSource(ownershipType) {
            it == ListingEnum.OwnershipType.RENT && loadHomeReportRentalTransactionRevisedResponse.value?.getTransactionList()
                ?.isNotEmpty() != true
        }
    }

    @Suppress("unused")
    fun getXValueTitleFromEntryParams(xValueEntryParams: XValueEntryParams): String {
        val subType = xValueEntryParams.propertySubType
        val block = xValueEntryParams.address.buildingNum
        val floor = xValueEntryParams.unitFloor
        val unit = xValueEntryParams.unitNumber
        val buildingName = xValueEntryParams.address.buildingName

        val isMultiLevel =
            PropertyTypeUtil.isHDB(subType.type) || PropertyTypeUtil.isCondo(subType.type)
        val hasBuildingName = !TextUtils.isEmpty(buildingName)
        val hasBlock = !TextUtils.isEmpty(block)
        val hasFloorAndUnit = !TextUtils.isEmpty(floor) && !TextUtils.isEmpty(unit)

        val streetName = xValueEntryParams.address.streetName
        val postalCode = xValueEntryParams.address.postalCode.toString()

        return when {
            isMultiLevel && hasBuildingName && hasBlock && hasFloorAndUnit -> "$buildingName, $block $streetName ($postalCode), #$floor-$unit"
            isMultiLevel && hasBlock && hasFloorAndUnit -> "$block $streetName ($postalCode), #$floor-$unit"
            isMultiLevel && hasBlock -> "$block $streetName ($postalCode)"
            else -> "$streetName ($postalCode)"
        }
    }

    fun performGetProject() {
        val entryParams = entryParams.value ?: return
        ApiUtil.performRequest(applicationContext, xValueRepository.getProject(
            postal = entryParams.address.postalCode,
            propertySubType = entryParams.propertySubType,
            floor = entryParams.unitFloor ?: "",
            unit = entryParams.unitNumber ?: "",
            buildingNum = entryParams.address.buildingNum
        ), onSuccess = {
            getProjectResponse.postValue(it)
        }, onFail = {
            // Do nothing
        }, onError = {
            // Do nothing
        })
    }

    fun performCalculate() {
        val entryParams = entryParams.value ?: return
        val streetId = getProjectResponse.value?.data?.getOrNull(0)?.streetId ?: return
        val address = entryParams.address
        val pesSize = entryParams.areaExt

        val area = if (PropertyTypeUtil.isHDB(entryParams.propertySubType.type)) {
            NumberUtil.getSqftFromSqm(entryParams.area)
        } else {
            entryParams.area
        }

        calculateStatus.postValue(ApiStatus.StatusKey.LOADING)
        // PES: private enclosed space, i.e. non-livable space for landed prop. e.g. garden, not included by default
        ApiUtil.performRequest(applicationContext, xValueRepository.calculate(
            streetId = streetId,
            ownershipType = OwnershipType.BOTH,
            floor = entryParams.unitFloor ?: "",
            postal = address.postalCode,
            buildingNum = address.buildingNum,
            size = area,
            unit = entryParams.unitNumber ?: "",
            propertySubType = entryParams.propertySubType,
            includePes = true,
            pesSize = pesSize,
            landType = entryParams.areaType,
            lastConstructed = entryParams.builtYear,
            tenure = entryParams.tenure,
            builtArea = entryParams.areaGfa
        ), onSuccess = {
            calculateResponse.postValue(it)
            calculateStatus.postValue(ApiStatus.StatusKey.SUCCESS)
        }, onFail = {
            calculateStatus.postValue(ApiStatus.StatusKey.FAIL)
            calculateFailText.postValue(it.error)
        }, onError = {
            calculateStatus.postValue(ApiStatus.StatusKey.ERROR)
        })
    }

    fun performLoadHomeReportTransaction(streetId: Int) {
        when (ownershipType.value) {
            ListingEnum.OwnershipType.SALE -> {
                loadLatestSaleTransactionsStatus.postValue(ApiStatus.StatusKey.LOADING)
                ApiUtil.performRequest(
                    applicationContext,
                    xValueRepository.loadHomeReportSaleTransactionRevised(streetId),
                    onSuccess = {
                        loadHomeReportSaleTransactionRevisedResponse.postValue(it)
                        loadLatestSaleTransactionsStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                    },
                    onFail = {
                        loadLatestSaleTransactionsStatus.postValue(ApiStatus.StatusKey.FAIL)
                    },
                    onError = {
                        loadLatestSaleTransactionsStatus.postValue(ApiStatus.StatusKey.ERROR)
                    })
            }
            ListingEnum.OwnershipType.RENT -> {
                loadLatestRentalTransactionsStatus.postValue(ApiStatus.StatusKey.LOADING)
                ApiUtil.performRequest(
                    applicationContext,
                    xValueRepository.loadHomeReportRentalInformationRevised(streetId),
                    onSuccess = {
                        loadHomeReportRentalTransactionRevisedResponse.postValue(it)
                        loadLatestRentalTransactionsStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                    },
                    onFail = {
                        loadLatestRentalTransactionsStatus.postValue(ApiStatus.StatusKey.FAIL)
                    },
                    onError = {
                        loadLatestRentalTransactionsStatus.postValue(ApiStatus.StatusKey.ERROR)
                    })
            }
            else -> {
            }
        }
    }

    // NOTE `performUpdateRequest` is available for sale only
    // DO NOT fallback for rent only mode
    fun performUpdateRequest() {
        val sveValuationId = mainXValue.value?.requestId ?: return
        if (sveValuationId <= 0) return

        val renovationYear = renovationYear.value
        val renovationCost = when {
            renovationYear != null -> renovationCost.value ?: 0
            else -> null
        }

        val goodWill = goodWill.value ?: return

        ApiUtil.performRequest(applicationContext, xValueRepository.updateRequest(
            sveValuationId = sveValuationId,
            renovationCost = renovationCost,
            renovationYear = renovationYear,
            goodWill = goodWill
        ), onSuccess = {
            updateRequestResponse.postValue(it)
        }, onFail = {
            // Do nothing
        }, onError = {
            // Do nothing
        })
    }

    fun performGenerateXValuePropertyReport() {
        mainXValue.value?.run {
            ViewUtil.showMessage(
                applicationContext.getString(
                    R.string.toast_generating_x_value_report,
                    shortAddress
                )
            )
            // Fix case of `requestId` = 0: https://app.asana.com/0/1141565313504065/1179892014771868
            val mRequestId = if (requestId > 0) {
                requestId
            } else {
                rentalId
            }
            if (mRequestId <= 0) {
                return ViewUtil.showMessage(R.string.toast_error_missing_id)
            }
            GenerateXValueReportService.launch(
                applicationContext,
                shortAddress,
                mRequestId,
                projectId
            )
        }
    }

    // Applicable for New X-value only
    @Suppress("unused")
    fun performGetXValueTrend() {
        val mainXValue = mainXValue.value ?: return

        val entryParams = entryParams.value ?: return
        val unitNum = entryParams.unitNumber ?: ""
        val floorNum = entryParams.unitFloor ?: ""
        val buildingNum = entryParams.address.buildingNum
        val builtYear = entryParams.builtYear

        val streetId = mainXValue.projectId
        val postalCode = mainXValue.postalCode
        val type = ownershipType.value?.value ?: ListingEnum.OwnershipType.SALE.value
        val propertySubType = PropertyTypeUtil.getPropertySubType(mainXValue.cdResearchSubtype)
            ?: return ErrorUtil.handleError("Invalid cdResearchSubType from server ${mainXValue.cdResearchSubtype}")
        val landedXValue = PropertyTypeUtil.isLanded(mainXValue.cdResearchSubtype)
        val size = if (PropertyTypeUtil.isHDB(propertySubType.type)) {
            NumberUtil.getSqftFromSqm(mainXValue.sizeSqm)
        } else {
            mainXValue.sizeSqft
        }

        graphStatusKey.postValue(ApiStatus.StatusKey.LOADING)
        ApiUtil.performRequest(applicationContext, xValueRepository.promotionGetXValueTrend(
            postalCode = postalCode,
            unitNum = unitNum,
            type = type,
            floorNum = floorNum,
            buildingNum = buildingNum,
            builtYear = builtYear,
            propertySubType = propertySubType,
            crunchResearchStreetId = streetId,
            landedXValue = landedXValue,
            size = size
        ), onSuccess = {
            graphStatusKey.postValue(ApiStatus.StatusKey.SUCCESS)
            buildXTrendList(it.getXValueObjTrendList())
        }, onFail = {
            graphStatusKey.postValue(ApiStatus.StatusKey.FAIL)
        }, onError = {
            graphStatusKey.postValue(ApiStatus.StatusKey.ERROR)
        })
    }

    private fun buildXTrendList(xValueObjTrend: List<XTrendKeyValue>?) {
        if (xValueObjTrend == null) return graphStatusKey.postValue(ApiStatus.StatusKey.ERROR)
        val formattedList = getFormattedXTrendKeyValues(xValueObjTrend)
        xTrendList.postValue(formattedList)
    }

    // TODO Same as that in ListingDetailsViewModel, refactor when free
    private fun getFormattedXTrendKeyValues(xValueTrend: List<XTrendKeyValue>): List<XTrendKeyValue> {
        val sortedList = xValueTrend.sortedBy { it.getMillis() }
        val filteredList = sortedList.filterIndexed { index, xTrendKeyValue ->
            if (index == sortedList.size - 2) {
                val theLastDataPoint = sortedList[sortedList.size - 1]
                !XTrendKeyValue.isSameYearQuarter(xTrendKeyValue, theLastDataPoint)
            } else {
                true
            }
        }
        val listSize = filteredList.size
        return filteredList.mapIndexed { index, xTrendKeyValue ->
            xTrendKeyValue.isLatest = index == listSize - 1
            xTrendKeyValue
        }
    }

    fun getPostalCode(): Int? {
        val existingResult = mainXValue.value
        val calculateResponse = calculateResponse.value
        return when {
            existingResult != null -> {
                existingResult.postalCode
            }
            calculateResponse != null -> {
                calculateResponse.xvalue?.postalCode
            }
            else -> null
        }
    }

    fun performGetValuationDetails(srxValuationRequestId: Int) {
        getValuationDetailStatus.postValue(ApiStatus.StatusKey.LOADING)
        ApiUtil.performRequest(applicationContext, xValueRepository.getValuationDetail(
            srxValuationRequestId
        ), onSuccess = {
            getValuationDetailResponse.postValue(it)
            getValuationDetailStatus.postValue(ApiStatus.StatusKey.SUCCESS)
        }, onFail = {
            getValuationDetailStatus.postValue(ApiStatus.StatusKey.FAIL)
        }, onError = {
            getValuationDetailStatus.postValue(ApiStatus.StatusKey.ERROR)
        })
    }

    fun downloadReport(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val timeStamp = DateTimeUtil.getCurrentFileNameTimeStamp()
                val fileName = "x_value_report_$timeStamp.pdf"
                try {
                    IntentUtil.downloadFile(
                        applicationContext, okHttpClient, url, fileName,
                        AppConstant.DIR_X_VALUE_REPORT
                    )
                } catch (e: IOException) {
                    return@withContext ErrorUtil.handleError(applicationContext.getString(R.string.toast_download_report_failed))
                }
                reportLocalFileName.postValue(fileName)
            }
        }
    }

    fun selectTimeScale(timeScale: XValueEnum.TimeScale) {
        selectedTimeScale.postValue(timeScale)
    }
}