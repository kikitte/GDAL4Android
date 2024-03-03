package com.example.gdaltest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gdaltest.ui.theme.GDAL4AndroidTheme

import org.gdal.gdal.gdal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GDAL4AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    gdal.AllRegister()

                    val sb = StringBuilder()
                    sb.append("\nGDAL Version is ${gdal.VersionInfo()}\n\n")
                    sb.append("Supported Drivers:\n")
                    for (i in 1..gdal.GetDriverCount()) {
                        sb.append("\t")
                        val driver = gdal.GetDriver(i-1)
                        sb.append("${driver.longName}\n")
                    }
                    Greeting(sb.toString())
                }
            }
        }
    }
}

@Composable
fun Greeting(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GDAL4AndroidTheme {
        Greeting("Android")
    }
}