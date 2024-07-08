package com.example.gdaltest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gdaltest.ui.theme.GDAL4AndroidTheme
import org.gdal.gdal.gdal
import org.gdal.ogr.ogr
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Copy GDAL test data to to the app's private storage
        val gdalTestDataDir = File(filesDir, "gdal_test_data")
        if (!gdalTestDataDir.exists()) {
            gdalTestDataDir.mkdir()
        }
        val GDAL_TEST_DATA_ASSET_DIR = "gdal_test_data"
        assets.list(GDAL_TEST_DATA_ASSET_DIR)?.forEach {
            val localFile = File(gdalTestDataDir, it)
            if (!localFile.exists()) {
                assets.open("${GDAL_TEST_DATA_ASSET_DIR}/${it}").copyTo(localFile.outputStream())
            }
        }

        setContent {
            GDAL4AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    gdal.AllRegister()
                    ogr.RegisterAll()

                    val sb = StringBuilder()
                    sb.append("######################################################\n")
                    sb.append("\nGDAL Version is ${gdal.VersionInfo()}\n\n")
                    sb.append("######################################################\n")
                    sb.append("Supported Drivers:\n")
                    for (i in 1..gdal.GetDriverCount()) {
                        sb.append("\t")
                        val driver = gdal.GetDriver(i - 1)
                        sb.append("${driver.longName}\n")
                    }
                    sb.append("######################################################\n")

                    val chineseShpPath = File(gdalTestDataDir, "shp中文测试.zip").absolutePath
                    val chineseShpDataset = ogr.Open("/vsizip/${chineseShpPath}/shp中文测试.shp")
                    // Open standalone Shpefile also ok.
//                    val chineseShpPath = File(gdalTestDataDir, "shp中文测试2.shp").absolutePath
//                    val chineseShpDataset = ogr.Open(chineseShpPath)

                    val chineseShpLayer = chineseShpDataset.GetLayer(0)
                    chineseShpLayer.ResetReading()
                    var chineseShpFeat = chineseShpLayer.GetNextFeature()
                    while (chineseShpFeat != null) {
                        sb.append("Feature: ")
                        sb.append(chineseShpFeat.GetGeometryRef().ExportToWkt())
                        sb.append("\n")
                        sb.append("Property: ")
                        sb.append(chineseShpFeat.GetFieldAsString("name"))
                        sb.append("\n")
                        chineseShpFeat = chineseShpLayer.GetNextFeature()
                    }
                    chineseShpDataset.delete()

                    ShowText(sb.toString())
                }
            }
        }
    }
}

@Composable
fun ShowText(message: String) {
    Text(
        text = message,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState()),
    )
}