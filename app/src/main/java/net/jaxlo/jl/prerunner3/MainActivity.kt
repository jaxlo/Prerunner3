package net.jaxlo.jl.prerunner3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.jaxlo.jl.prerunner3.ui.theme.Prerunner3Theme
import android.util.Log
import android.database.sqlite.SQLiteDatabase


class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the DatabaseHelper and get the writable database
        dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase

        // Insert dummy data
        dummyData(db)

        // Example of using the functions
        editTask(db, 1, "Complete Kotlin tutorial updated", true)
        deleteTask(db, 2)
        editUsername(db, "Updated User Name")

        setContent { // Put this in a different file?
            Prerunner3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "name",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prerunner3Theme {
        Greeting("Android")
    }
}