package sg.searchhouse.agentconnect.view.helper.xvalue

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.model.api.xvalue.XTrendKeyValue
import sg.searchhouse.agentconnect.util.NumberUtil
import kotlin.math.max

class XTrendGraphWrapper constructor(
    private val context: Context,
    private val lineChart: LineChart,
    xTrendKeyValues: List<XTrendKeyValue>
) {
    private val globalDataPoints: List<DataPoint>

    init {
        setupXAxis()
        setupYAxis()
        setupMisc()
        setupViewPort()
        setupInteraction()
        globalDataPoints = getDataPoints(xTrendKeyValues)
    }

    private fun setupInteraction() {
        lineChart.setPinchZoom(false)
        lineChart.isScaleXEnabled = false
        lineChart.isScaleYEnabled = false
        lineChart.isDoubleTapToZoomEnabled = false
        lineChart.isDragXEnabled = true
        lineChart.isDragYEnabled = false
    }

    private fun setupViewPort() {
        val viewPortOffsetVertical = context.resources.getDimension(R.dimen.spacing_xl)
        val viewPortOffsetLeft = context.resources.getDimension(R.dimen.spacing_m)
        val viewPortOffsetRight = context.resources.getDimension(R.dimen.spacing_m)
        lineChart.setViewPortOffsets(
            viewPortOffsetLeft,
            viewPortOffsetVertical,
            viewPortOffsetRight,
            viewPortOffsetVertical
        )
    }

    private fun setupXAxis() {
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.labelRotationAngle = -90f
        lineChart.xAxis.setDrawGridLines(false)
    }

    private fun setupYAxis() {
        lineChart.axisLeft.isEnabled = true
        lineChart.axisLeft.setDrawGridLines(true)
        lineChart.axisLeft.setDrawLabels(true)
        lineChart.axisLeft.gridColor = context.getColor(R.color.light_gray)
        lineChart.axisRight.isEnabled = false
        lineChart.axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
    }

    private fun setupMisc() {
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.setDrawBorders(true)
        lineChart.setBorderColor(context.getColor(R.color.light_gray))
        lineChart.minOffset = 5f
    }

    fun updateTimeScale(timeScale: XValueEnum.TimeScale) {
        if (globalDataPoints.isEmpty()) return
        val end = globalDataPoints.size
        val start = max(end - timeScale.numQuarters, 0)
        val availableDataPoints = globalDataPoints.subList(start, end)

        // !! checked
        if (timeScale != XValueEnum.TimeScale.ONE_YEAR && availableDataPoints.size < timeScale.smallerNeighbor!!.numQuarters) {
            val optimalTimeScale = getOptimalTimeScale(availableDataPoints.size)
            return updateTimeScale(optimalTimeScale)
        } else {
            setData(timeScale, availableDataPoints)
        }

        val range = (timeScale.numQuarters - 1).toFloat()

        lineChart.setVisibleXRangeMaximum(range)
        lineChart.setVisibleXRangeMinimum(range)

        lineChart.moveViewToX((end - 1).toFloat()) // Move to end
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    private fun getOptimalTimeScale(dataCount: Int): XValueEnum.TimeScale {
        return when {
            dataCount < XValueEnum.TimeScale.ONE_YEAR.numQuarters -> {
                XValueEnum.TimeScale.ONE_YEAR
            }
            dataCount < XValueEnum.TimeScale.THREE_YEARS.numQuarters -> {
                XValueEnum.TimeScale.THREE_YEARS
            }
            dataCount < XValueEnum.TimeScale.FIVE_YEARS.numQuarters -> {
                XValueEnum.TimeScale.FIVE_YEARS
            }
            else -> {
                XValueEnum.TimeScale.TEN_YEARS
            }
        }
    }

    private fun setData(
        timeScale: XValueEnum.TimeScale,
        dataPoints: List<DataPoint>
    ) {
        val lineDataSet = LineDataSet(dataPoints.map { it.entry }, "")
        setLineData(lineDataSet)
        setLineStyle(lineDataSet)

        val axisValueFormatter =
            AxisValueFormatter(context, timeScale, dataPoints, globalDataPoints.size)

        lineChart.xAxis.valueFormatter = axisValueFormatter
        lineChart.xAxis.labelCount = dataPoints.size
        lineChart.axisLeft.valueFormatter = axisValueFormatter
        val markerView = XTrendMarkerView(context, timeScale, dataPoints, globalDataPoints.size)
        markerView.chartView = lineChart
        lineChart.marker = markerView
    }

    private class XTrendMarkerView(
        context: Context,
        val timeScale: XValueEnum.TimeScale,
        val dataPoints: List<DataPoint>,
        val allDataSize: Int
    ) : MarkerView(context, R.layout.marker_view_x_trend) {
        val text1: TextView = findViewById(R.id.text1)

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            val allDataPosition = e?.x?.toInt() ?: return
            val xTrendKeyValue = getXTrendKeyValue(allDataPosition) ?: return
            text1.text = xTrendKeyValue.getMarkerLabel()
            super.refreshContent(e, highlight)
        }

        private fun getXTrendKeyValue(allDataPosition: Int): XTrendKeyValue? {
            return getXTrendKeyValue(allDataPosition, timeScale, dataPoints, allDataSize)
        }
    }

    private fun setLineData(lineDataSet: LineDataSet) {
        lineChart.data = LineData(lineDataSet)
        lineChart.data.setDrawValues(false)
    }

    private fun setLineStyle(lineDataSet: LineDataSet) {
        lineDataSet.circleHoleColor = Color.CYAN
        lineDataSet.valueTextSize = 12f
        lineDataSet.lineWidth = 2f
        lineDataSet.setCircleColor(Color.TRANSPARENT)
        lineDataSet.circleHoleColor = Color.TRANSPARENT
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.color = context.getColor(R.color.cyan)
        lineDataSet.highLightColor = context.getColor(R.color.card_border)
    }

    private fun getDataPoints(xTrendKeyValues: List<XTrendKeyValue>): List<DataPoint> {
        val sortedList = xTrendKeyValues.sortedBy {
            it.getMillis()
        }
        // TODO Drop the 2nd last item at X Value activity
        return sortedList.mapIndexed { index, xTrendKeyValue ->
            DataPoint(xTrendKeyValue, Entry(index.toFloat(), xTrendKeyValue.value.toFloat()))
        }
    }

    private class AxisValueFormatter(
        val context: Context,
        val timeScale: XValueEnum.TimeScale,
        val dataPoints: List<DataPoint>,
        val allDataSize: Int
    ) :
        ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return when (axis) {
                is XAxis -> getXAxisLabel(value)
                is YAxis -> "\$${NumberUtil.getThousandMillionShortForm(value)}"
                else -> super.getAxisLabel(value, axis)
            }
        }

        private fun getXAxisLabel(xAxisValue: Float): String {
            return when (val allDataPosition = xAxisValue.toInt()) {
                allDataSize - 1 -> {
                    // The last label
                    context.getString(R.string.label_latest)
                }
                else -> {
                    // Normal
                    getYearLabel(allDataPosition)
                }
            }
        }

        private fun getYearLabel(allDataPosition: Int): String {
            val xTrendKeyValue = getXTrendKeyValue(allDataPosition) ?: return ""

            val lastPosition = max(allDataPosition - 1, 0)
            val lastXTrendKeyValue = getXTrendKeyValue(lastPosition)

            val thisYear = xTrendKeyValue.getYear()
            val lastYear = lastXTrendKeyValue?.getYear()

            return if (thisYear != lastYear) {
                xTrendKeyValue.getYear().toString()
            } else {
                ""
            }
        }

        private fun getXTrendKeyValue(allDataPosition: Int): XTrendKeyValue? {
            return getXTrendKeyValue(allDataPosition, timeScale, dataPoints, allDataSize)
        }
    }

    class DataPoint(val xTrendKeyValue: XTrendKeyValue, val entry: Entry)

    companion object {
        fun getXTrendKeyValue(
            allDataPosition: Int,
            timeScale: XValueEnum.TimeScale,
            dataPoints: List<DataPoint>,
            allDataSize: Int
        ): XTrendKeyValue? {
            val start = max(allDataSize - timeScale.numQuarters, 0)
            val position = allDataPosition - start
            return dataPoints.getOrNull(position)?.xTrendKeyValue
        }
    }
}