package sg.searchhouse.agentconnect.view.helper.marketwatch

import android.content.Context
import android.text.TextUtils
import androidx.appcompat.widget.AppCompatTextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.PropertyIndexEnum
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchGraphResponse
import sg.searchhouse.agentconnect.util.NumberUtil
import kotlin.math.max

class MarketWatchBarGraphWrapper(
    private val context: Context,
    private val chart: BarChart,
    private val globalGraphDataList: List<LoadMarketWatchGraphResponse.GraphData>
) {
    init {
        setupXAxis()
        setupYAxis()
        setupMisc()
        setupViewPort()
        setupInteraction()
    }

    private fun setupInteraction() {
        chart.setPinchZoom(false)
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false
        chart.isDoubleTapToZoomEnabled = false
        chart.isDragXEnabled = true
        chart.isDragYEnabled = false
    }

    private fun setupViewPort() {
        val viewPortOffsetTop = context.resources.getDimension(R.dimen.spacing_xl)
        val viewPortOffsetBottom =
            context.resources.getDimension(R.dimen.margin_bottom_market_watch_graph)
        val viewPortOffsetLeft = context.resources.getDimension(R.dimen.spacing_m)
        val viewPortOffsetRight =
            context.resources.getDimension(R.dimen.margin_end_market_watch_graph)
        chart.setViewPortOffsets(
            viewPortOffsetLeft,
            viewPortOffsetTop,
            viewPortOffsetRight,
            viewPortOffsetBottom
        )
    }

    private fun setupXAxis() {
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.labelRotationAngle = -90f
        chart.xAxis.setDrawGridLines(false)
    }

    private fun setupYAxis() {
        chart.axisRight.isEnabled = true
        chart.axisRight.setDrawGridLines(true)
        chart.axisRight.setDrawLabels(true)
        chart.axisRight.gridColor = context.getColor(R.color.gray)
        chart.axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        chart.axisLeft.isEnabled = false
    }

    private fun setupMisc() {
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.setDrawBorders(true)
        chart.setBorderColor(context.getColor(R.color.light_gray))
        chart.minOffset = 5f
    }

    private fun getGlobalDataPoints(indicator: PropertyIndexEnum.Indicator): List<DataPoint> {
        return getDataPoints(globalGraphDataList, indicator)
    }

    fun updateGraph(
        indicator: PropertyIndexEnum.Indicator,
        timeScale: PropertyIndexEnum.TimeScale
    ) {
        if (globalGraphDataList.isEmpty()) return
        val end = globalGraphDataList.size
        val start = max(end - (timeScale.numMonths), 0)

        val availableDataPoints = getGlobalDataPoints(indicator).subList(start, end)

        // !! checked
        if (timeScale != PropertyIndexEnum.TimeScale.ONE_YEAR && availableDataPoints.size < timeScale.smallerNeighbor!!.numMonths) {
            val optimalTimeScale = getOptimalTimeScale(availableDataPoints.size)
            return updateGraph(indicator, optimalTimeScale)
        } else {
            setData(indicator, timeScale, availableDataPoints)
        }

        val range = (timeScale.numMonths - 1).toFloat()

        chart.setVisibleXRangeMaximum(range)
        chart.setVisibleXRangeMinimum(range)

        setXAxisGranularity(timeScale)

        chart.moveViewToX((end - 1).toFloat()) // Move to end
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    // Must call lineChart.invalidate() to take effect
    private fun setXAxisGranularity(timeScale: PropertyIndexEnum.TimeScale) {
        chart.xAxis.granularity = when (timeScale) {
            PropertyIndexEnum.TimeScale.TEN_YEARS -> 12f // 1 label span 12 months
            else -> 1f
        }
    }

    private fun getOptimalTimeScale(dataCount: Int): PropertyIndexEnum.TimeScale {
        return when {
            dataCount < PropertyIndexEnum.TimeScale.ONE_YEAR.numMonths -> {
                PropertyIndexEnum.TimeScale.ONE_YEAR
            }
            dataCount < PropertyIndexEnum.TimeScale.THREE_YEARS.numMonths -> {
                PropertyIndexEnum.TimeScale.THREE_YEARS
            }
            dataCount < PropertyIndexEnum.TimeScale.FIVE_YEARS.numMonths -> {
                PropertyIndexEnum.TimeScale.FIVE_YEARS
            }
            else -> {
                PropertyIndexEnum.TimeScale.TEN_YEARS
            }
        }
    }

    private fun setData(
        indicator: PropertyIndexEnum.Indicator,
        timeScale: PropertyIndexEnum.TimeScale,
        dataPoints: List<DataPoint>
    ) {
        val lineDataSet = BarDataSet(dataPoints.map { it.entry }, "")
        setBarData(lineDataSet)
        setBarStyle(lineDataSet)

        val globalDataPoints = getDataPoints(globalGraphDataList, indicator)

        val axisValueFormatter =
            AxisValueFormatter(context, timeScale, dataPoints, globalDataPoints.size)

        chart.xAxis.valueFormatter = axisValueFormatter
        chart.xAxis.labelCount = dataPoints.size
        chart.axisRight.valueFormatter = axisValueFormatter

        val markerView = MarketWatchMarkerView(
            context,
            timeScale,
            indicator,
            dataPoints,
            globalDataPoints.size
        )
        markerView.chartView = chart
        chart.marker = markerView
    }

    private fun setBarData(lineDataSet: BarDataSet) {
        chart.data = BarData(lineDataSet)
        chart.data.setDrawValues(false)
    }

    private fun setBarStyle(barDataSet: BarDataSet) {
        barDataSet.color = context.getColor(R.color.cyan)
        barDataSet.highLightColor = context.getColor(R.color.cyan_dark)
    }

    private fun getDataPoints(
        graphDataList: List<LoadMarketWatchGraphResponse.GraphData>,
        indicator: PropertyIndexEnum.Indicator
    ): List<DataPoint> {
        val sortedList = graphDataList.sortedBy {
            it.getMillis()
        }
        return sortedList.mapIndexed { index, graphData ->
            val value = when (indicator) {
                PropertyIndexEnum.Indicator.PRICE -> graphData.value
                PropertyIndexEnum.Indicator.VOLUME -> graphData.volume
            }.toFloat()
            DataPoint(graphData, BarEntry(index.toFloat(), value))
        }
    }

    private class AxisValueFormatter(
        val context: Context,
        val timeScale: PropertyIndexEnum.TimeScale,
        val dataPoints: List<DataPoint>,
        val allDataSize: Int
    ) :
        ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return when (axis) {
                is XAxis -> getXAxisLabel(value)
                is YAxis -> getYAxisLabel(value)
                else -> super.getAxisLabel(value, axis)
            }
        }

        private fun getYAxisLabel(yAxisValue: Float): String {
            return NumberUtil.formatThousand(yAxisValue)
        }

        private fun getXAxisLabel(xAxisValue: Float): String {
            return when (val allDataPosition = xAxisValue.toInt()) {
                allDataSize - 1 -> {
                    // The last label
                    context.getString(R.string.label_latest)
                }
                else -> {
                    // Normal
                    getDateLabel(allDataPosition)
                }
            }
        }

        private fun getDateLabel(allDataPosition: Int): String {
            val thisGraphData = getGraphData(allDataPosition) ?: return ""
            return when (timeScale) {
                PropertyIndexEnum.TimeScale.ONE_YEAR -> {
                    if (allDataPosition == 0) return thisGraphData.getYearMonth()
                    val previousGraphData =
                        getGraphData(allDataPosition - 1) ?: return thisGraphData.getYearMonth()
                    val thisGraphYear = thisGraphData.getYear()
                    val previousGraphYear = previousGraphData.getYear()
                    if (!TextUtils.equals(thisGraphYear, previousGraphYear)) {
                        thisGraphData.getYearMonth()
                    } else {
                        thisGraphData.getMonth()
                    }
                }
                PropertyIndexEnum.TimeScale.TEN_YEARS -> {
                    thisGraphData.getYear()
                }
                else -> {
                    if (thisGraphData.isJanuary()) {
                        thisGraphData.getYear()
                    } else {
                        ""
                    }
                }
            }
        }

        private fun getGraphData(allDataPosition: Int): LoadMarketWatchGraphResponse.GraphData? {
            return getGraphData(allDataPosition, timeScale, dataPoints, allDataSize)
        }
    }

    private class MarketWatchMarkerView(
        context: Context,
        val timeScale: PropertyIndexEnum.TimeScale,
        val indicator: PropertyIndexEnum.Indicator,
        val dataPoints: List<DataPoint>,
        val allDataSize: Int
    ) : MarkerView(context, R.layout.marker_view_bar) {
        val text1: AppCompatTextView = findViewById(R.id.text1)

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            val allDataPosition = e?.x?.toInt() ?: return
            val graphData =
                getGraphData(allDataPosition, timeScale, dataPoints, allDataSize) ?: return
            text1.text = graphData.getMarkerLabel(context, indicator)
            super.refreshContent(e, highlight)
        }
    }

    class DataPoint(val graphData: LoadMarketWatchGraphResponse.GraphData, val entry: BarEntry)

    companion object {
        fun getGraphData(
            allDataPosition: Int,
            timeScale: PropertyIndexEnum.TimeScale,
            dataPoints: List<DataPoint>,
            allDataSize: Int
        ): LoadMarketWatchGraphResponse.GraphData? {
            val start = max(allDataSize - timeScale.numMonths, 0)
            val position = allDataPosition - start
            return dataPoints.getOrNull(position)?.graphData
        }
    }
}