import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import domain.items.ChartDataItem
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.components.LineChartSample
import presentation.theme.mediumPadding
import presentation.theme.smallPadding
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

fun openFileDialog(): String? {
    val fileDialog = FileDialog(Frame(), "Выберите данные", FileDialog.LOAD)
    fileDialog.isVisible = true
    return if (fileDialog.file != null) {
        "${fileDialog.directory}${fileDialog.file}"
    } else {
        null
    }
}

fun readCsv(file: File): List<List<String>> {
    return csvReader().readAll(file)
}

fun prepareDataFromCsvForChart(data: List<List<String>>): ChartDataItem {
    var nameDataLabel: String = data[0][0]
    val yAxis: List<Double> =List<Double>(data.size-1){0.0}
    val xAxis: List<String> =List<String>(data.size-1){""}
    for (i in 1..<data.size){
        xAxis.plus(data[i][2])
        yAxis.plus(data[i][4].toFloat())
    }
    return ChartDataItem(nameDataLabel, yAxis, xAxis)



}

@Composable
fun CsvTable(data: List<List<String>>) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        userScrollEnabled = true,
        ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(.9F),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Строк: ${data.size}")
            }
        }
        items(data){row ->
            LazyRow(
                modifier = Modifier.fillMaxWidth(.9F),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                items(row){ cell ->
                    Text(
                        text = cell,
                        modifier = Modifier.width(128.dp).padding(smallPadding),
                        textAlign = TextAlign.Center
                    )
                }

            }

        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface{
            Column(
                modifier = Modifier.padding(mediumPadding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var filePath by remember { mutableStateOf<String?>(null) }
                var loadData by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                Button(onClick = {
                    scope.launch {
                        loadData = true
                        filePath = openFileDialog()
                    }.invokeOnCompletion { loadData = false }
                }) {
                    if(loadData){
                        CircularProgressIndicator(color= Color.White)
                    }
                    else{
                        Text("Выбрать CSV файл")
                    }

                }

                val csvData = remember { mutableStateOf<List<List<String>>>(emptyList()) }
                val isChart = remember { mutableStateOf(false) }

                filePath?.let {path ->
                    val file = File(path)
                    csvData.value = readCsv(file)
                    val chartData: ChartDataItem = prepareDataFromCsvForChart(csvData.value)
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text("График")
                        Switch(checked=isChart.value, onCheckedChange = {
                            isChart.value = it
                        })

                    }

                    if(isChart.value){
                        LineChartSample(chartData)
                    }
                    else{
                        CsvTable(csvData.value)
                    }

                }

            }

        }

    }
}