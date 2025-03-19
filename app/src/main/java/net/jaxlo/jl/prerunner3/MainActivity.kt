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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment


class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        // Store today's date
        val today = java.time.LocalDate.now().toString()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the DatabaseHelper and get the writable database
        dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase

        // Insert dummy data
        dummyData(db)

        // Display tasks
        val tasks = getTasks(db)
        tasks.forEach { task ->
            Log.d("CS3680", "Task: $task")
        }

        // Display encouragement
        val encouragements = getEncouragementForUserById(db, 1)
        // Using id=1 because that is the local user
        for (encouragement in encouragements) {
            Log.d("CS3680", "Encouragement: ${encouragement["task_name"]} by ${encouragement["encourager_name"]}")
        }

        // Testing functions
        // editTask(db, 1, "Complete Kotlin tutorial updated", true)
        // deleteTask(db, 2)
        // editUsername(db, "Updated User Name")

        setContent { // Put this in a different file?
            Prerunner3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "dude",
                        modifier = Modifier.padding(innerPadding)
                    )

                    ClearDatabaseButton(db)
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

// TODO move this to another file
@Composable
fun ClearDatabaseButton(db: SQLiteDatabase) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Button(onClick = {
            // Call the function to clear the database when the button is clicked
            clearDatabase(db)
        }) {
            Text(text = "Clear Database")
        }
    }
}

// Preview function for the UI
@Preview
@Composable
fun ClearDatabaseButtonPreview() {
    // Replace with your actual database instance in real usage
    val db: SQLiteDatabase = SQLiteDatabase.create(null) // Example: Create an in-memory DB
    ClearDatabaseButton(db)
}