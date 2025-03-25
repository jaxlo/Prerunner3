package net.jaxlo.jl.prerunner3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.jaxlo.jl.prerunner3.ui.theme.Prerunner3Theme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import org.json.JSONArray




class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent { // Put this in a different file?
            Prerunner3Theme {
                JsonTextView()
            }
        }
    }
}


@Composable
fun JsonTextView() {
    var jsonText by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        jsonText = try {
            val jsonData = getJson()
            val thirdSatelliteInfo = getThirdSatelliteOfJupiter(jsonData)
            thirdSatelliteInfo
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = jsonText,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

suspend fun getJson(): String {
    return withContext(Dispatchers.IO) {
        URL("https://roversgame.net/cs3680/planets.json").readText()
    }
}

fun getThirdSatelliteOfJupiter(jsonData: String): String {
    val planets = JSONArray(jsonData)
    for (i in 0 until planets.length()) {
        val planet = planets.getJSONObject(i)
        if (planet.getString("name") == "Jupiter") {
            val satellites = planet.optJSONArray("satellites")
            if (satellites != null && satellites.length() >= 3) {
                val thirdSatellite = satellites.getJSONObject(2) // Index 2 for the third satellite
                val name = thirdSatellite.getString("name")
                val diameterKm = thirdSatellite.getString("diameterKm")
                return "Third satellite of Jupiter: \nName: $name\nDiameter: $diameterKm km"
            } else {
                return "Jupiter has less than three satellites in the data."
            }
        }
    }
    return "Jupiter is not found in the JSON data."
}

@Preview(showBackground = true)
@Composable
fun PreviewJsonTextView() {
    // Display static content for preview
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sample JSON data",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
