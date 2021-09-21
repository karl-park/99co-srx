package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.model.api.xvalue.*
import sg.searchhouse.agentconnect.model.app.XValueEntryParams
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * API doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/736952331/XValue+V1+API
 */
@Singleton
class XValueRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun getExistingXValues(
        search: String? = null,
        page: Int,
        property: XValueEnum.GetExistingXValuesProperty,
        order: XValueEnum.GetExistingXValuesOrder
    ): Call<GetExistingXValuesResponse> {
        return srxDataSource.getExistingXValues(
            search,
            page,
            AppConstant.DEFAULT_BATCH_SIZE,
            property.value,
            order.value
        )
    }

    fun searchWithWalkup(query: String): Call<SearchWithWalkupResponse> {
        return srxDataSource.searchWithWalkup(query)
    }

    fun getProject(
        postal: Int,
        propertySubType: ListingEnum.PropertySubType,
        floor: String,
        unit: String,
        buildingNum: String
    ): Call<GetProjectResponse> {
        return srxDataSource.getProject(postal, propertySubType.type, floor, unit, buildingNum)
    }

    fun calculate(
        streetId: Int,
        ownershipType: XValueEnum.OwnershipType,
        floor: String,
        postal: Int,
        buildingNum: String,
        size: Int, // in sqft
        unit: String,
        propertySubType: ListingEnum.PropertySubType,
        includePes: Boolean,
        pesSize: Int?,
        landType: XValueEnum.AreaType?,
        lastConstructed: Int?, // year in yyyy, e.g. 2003
        tenure: XValueEnum.Tenure?,
        builtArea: Int?
    ): Call<CalculateResponse> {
        return if (!PropertyTypeUtil.isLanded(propertySubType.type)) {
            calculateNonLanded(
                streetId,
                ownershipType,
                floor,
                postal,
                buildingNum,
                size,
                unit,
                propertySubType,
                includePes,
                pesSize
            )
        } else {
            if (landType == null) throw IllegalArgumentException("Missing `landType` param for landed property")
            if (lastConstructed == null) throw IllegalArgumentException("Missing `lastConstructed` param for landed property")
            if (tenure == null) throw IllegalArgumentException("Missing `tenure` param for landed property")
            calculateLanded(
                streetId,
                ownershipType,
                floor,
                postal,
                buildingNum,
                size,
                unit,
                propertySubType,
                includePes,
                pesSize,
                landType,
                lastConstructed,
                tenure,
                builtArea
            )
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun calculateNonLanded(
        streetId: Int,
        ownershipType: XValueEnum.OwnershipType,
        floor: String,
        postal: Int,
        buildingNum: String,
        size: Int, // in sqft
        unit: String,
        propertySubType: ListingEnum.PropertySubType,
        includePes: Boolean,
        pesSize: Int?
    ): Call<CalculateResponse> {
        if (PropertyTypeUtil.isLanded(propertySubType.type)) {
            throw IllegalArgumentException("Use `calculateLanded` endpoint instead for landed sub type ${propertySubType.type}")
        }
        return srxDataSource.calculate(
            streetId,
            ownershipType.value,
            floor,
            postal,
            buildingNum,
            size,
            unit,
            propertySubType.type,
            includePes,
            pesSize,
            null,
            null,
            null,
            null
        )
    }

    @Throws(IllegalArgumentException::class)
    private fun calculateLanded(
        streetId: Int,
        ownershipType: XValueEnum.OwnershipType,
        floor: String,
        postal: Int,
        buildingNum: String,
        size: Int, // in sqft
        unit: String,
        propertySubType: ListingEnum.PropertySubType,
        includePes: Boolean,
        pesSize: Int?,
        landType: XValueEnum.AreaType,
        lastConstructed: Int, // year in yyyy, e.g. 2003
        tenure: XValueEnum.Tenure,
        builtArea: Int?
    ): Call<CalculateResponse> {
        if (!PropertyTypeUtil.isLanded(propertySubType.type)) {
            throw IllegalArgumentException("Use `calculateLanded` endpoint instead for non-landed sub type ${propertySubType.type}")
        }
        return srxDataSource.calculate(
            streetId,
            ownershipType.value,
            floor,
            postal,
            buildingNum,
            size,
            unit,
            propertySubType.type,
            includePes,
            pesSize,
            landType.stringValue,
            lastConstructed,
            tenure.value,
            builtArea
        )
    }

    @Throws(IllegalArgumentException::class)
    fun calculate(
        xValueEntryParams: XValueEntryParams,
        getProjectResponse: GetProjectResponse
    ): Call<CalculateResponse> {
        val streetId = getProjectResponse.data.getOrNull(0)?.streetId
            ?: throw IllegalArgumentException("Missing `streetId` in calculate X-Value endpoint")
        val address = xValueEntryParams.address
        val pesSize = xValueEntryParams.areaExt

        val area = if (PropertyTypeUtil.isHDB(xValueEntryParams.propertySubType.type)) {
            NumberUtil.getSqftFromSqm(xValueEntryParams.area)
        } else {
            xValueEntryParams.area
        }

        return calculate(
            streetId = streetId,
            ownershipType = XValueEnum.OwnershipType.BOTH,
            floor = xValueEntryParams.unitFloor ?: "",
            postal = address.postalCode,
            buildingNum = address.buildingNum,
            size = area,
            unit = xValueEntryParams.unitNumber ?: "",
            propertySubType = xValueEntryParams.propertySubType,
            includePes = true,
            pesSize = pesSize,
            landType = xValueEntryParams.areaType,
            lastConstructed = xValueEntryParams.builtYear,
            tenure = xValueEntryParams.tenure,
            builtArea = xValueEntryParams.areaGfa
        )
    }

    @Throws(IllegalArgumentException::class)
    fun updateRequest(
        sveValuationId: Int,
        renovationCost: Int?,
        renovationYear: Int?,
        goodWill: Int
    ): Call<UpdateRequestResponse> {
        if (abs(goodWill) > 100) throw IllegalArgumentException("Good will cannot exceed 100")
        return srxDataSource.updateRequest(
            sveValuationId,
            renovationCost,
            renovationYear,
            goodWill
        )
    }

    fun promotionGetXValueTrend(
        postalCode: Int,
        unitNum: String,
        type: String,
        floorNum: String,
        buildingNum: String,
        builtYear: Int?,
        propertySubType: ListingEnum.PropertySubType,
        crunchResearchStreetId: Int,
        landedXValue: Boolean,
        size: Int // in sqft
    ): Call<PromotionGetXvalueTrendResponse> {
        return srxDataSource.promotionGetXValueTrend(
            postalCode,
            unitNum,
            type,
            floorNum,
            buildingNum,
            builtYear,
            propertySubType.type,
            crunchResearchStreetId,
            landedXValue,
            size
        )
    }

    fun loadHomeReportSaleTransactionRevised(
        crunchResearchStreetId: Int
    ): Call<LoadHomeReportSaleTransactionRevisedResponse> {
        return srxDataSource.loadHomeReportSaleTransactionRevised(crunchResearchStreetId)
    }

    fun loadHomeReportRentalInformationRevised(
        crunchResearchStreetId: Int
    ): Call<LoadHomeReportRentalInformationRevisedResponse> {
        return srxDataSource.loadHomeReportRentalInformationRevised(crunchResearchStreetId)
    }

    fun getValuationDetail(
        sveRequestId: Int
    ): Call<GetValuationDetailResponse> {
        return srxDataSource.getValuationDetail(sveRequestId)
    }

    fun generateXvaluePropertyReport(
        requestId: Int,
        crunchResearchStreetId: Int,
        showFullReport: Boolean
    ): Call<GenerateXvaluePropertyReportResponse> {
        return srxDataSource.generateXvaluePropertyReport(
            requestId,
            crunchResearchStreetId,
            showFullReport
        )
    }
}