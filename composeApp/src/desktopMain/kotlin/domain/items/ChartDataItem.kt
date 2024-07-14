package domain.items

data class ChartDataItem(
    val nameDataLabel: String,
    val yAxis: List<Double>,
    val xAxis: List<String>
)
