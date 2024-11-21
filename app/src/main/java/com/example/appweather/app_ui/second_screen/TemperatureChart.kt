package com.example.appweather.app_ui.second_screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.appweather.R
import com.example.appweather.api.models.Forecastday
import com.example.appweather.utils.formatDate
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter


@Composable
fun TemperatureChart(data: List<Forecastday>) {
    val maxTempEntries = data.mapIndexed { index, day ->
        Entry(index.toFloat(), day.day.maxtemp_c.toFloatOrNull() ?: 0f)
    }
    val minTempEntries = data.mapIndexed { index, day ->
        Entry(index.toFloat(), day.day.mintemp_c.toFloatOrNull() ?: 0f)
    }


    val maxTempDataSet = LineDataSet(maxTempEntries, stringResource(id = R.string.temp_day)).apply {
        color = android.graphics.Color.RED
        lineWidth = 4f
        setDrawCircles(true)
        setDrawValues(false)
        setCircleColor(android.graphics.Color.RED)
        circleRadius = 5f
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }
    val minTempDataSet = LineDataSet(minTempEntries, stringResource(id = R.string.temp_night)).apply {
        color = android.graphics.Color.BLUE
        lineWidth = 4f
        setDrawCircles(true)
        setDrawValues(false)
        setCircleColor(android.graphics.Color.BLUE)
        circleRadius = 5f
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }


    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                this.data = LineData(maxTempDataSet, minTempDataSet)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = android.graphics.Color.DKGRAY
                    textSize = 14f
                    granularity = 1f
                    labelCount = data.size
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt().coerceIn(0, data.size - 1)
                            return formatDate(data[index].date)
                        }
                    }
                }

                axisLeft.apply {
                    setDrawGridLines(false)
                    textColor = android.graphics.Color.DKGRAY
                    textSize = 14f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()}°C"
                        }
                    }
                    //axisMinimum = 0f
                }

                axisRight.isEnabled = false


                legend.apply {
                    textColor = android.graphics.Color.DKGRAY
                    textSize = 14f
                    form = Legend.LegendForm.LINE
                }

                description.isEnabled = false

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}