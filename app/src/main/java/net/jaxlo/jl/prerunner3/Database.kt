package net.jaxlo.jl.prerunner3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import java.util.UUID
import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// This file is for storing logic related to the database layer
// Is this where I should put Android's EncryptedSharedPreferences logic?

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // This class manages the database creation and migrations
    companion object {
        const val DATABASE_NAME = "sqlite3.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL, -- Display name
                    uuid TEXT NOT NULL -- Unique id for every user
                )
            """
        )
        db.execSQL(
            """
                CREATE TABLE tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    is_complete BOOLEAN NOT NULL CHECK (is_complete IN (0, 1)), -- SQLite uses 0 and 1 for boolean values
                    date TEXT NOT NULL, -- Use ISO 8601 format (YYYY-MM-DD) for storing dates
                    owner INTEGER NOT NULL, -- Foreign key referencing the users table
                    FOREIGN KEY (owner) REFERENCES users(id) ON DELETE CASCADE
                )
            """
        )
        db.execSQL(
            """
                CREATE TABLE task_encouragers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    task_id INTEGER NOT NULL, -- Foreign key referencing the tasks table
                    user_id INTEGER NOT NULL, -- Foreign key referencing the users table
                    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle schema changes
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS tasks")
        db.execSQL("DROP TABLE IF EXISTS task_encouragers")

        onCreate(db)
        createUser(db, "default_user") // Create the default user so they can go straight into making tasks
        Log.d("Database", "Database upgraded")
    }
}

fun dummyData(db: SQLiteDatabase) {
    // Get today's date and tomorrow's date
    val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE) // YYYY-MM-DD format
    val tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE)

    // Insert some dummy users
    val user1Id = createUser(db, "John Doe")
    val user2Id = createUser(db, "Jane Smith")
    val user3Id = createUser(db, "Alice Johnson")

    // Today's goals
    createTask(db, "Complete Kotlin tutorial", today, user1Id.toInt())
    createTask(db, "Buy groceries", today, user1Id.toInt())
    createTask(db, "Finish project", today, user2Id.toInt())
    // Plan tomorrow
    createTask(db, "Clean the house", tomorrow, user2Id.toInt())
    createTask(db, "Study for exam", tomorrow, user3Id.toInt())
    createTask(db, "Prepare presentation", tomorrow, user3Id.toInt())

    Log.d("Database", "Dummy data inserted.")
}

// --- --- --- Below are the functions for interacting with the database layer --- --- ---

fun createUser(db: SQLiteDatabase, name: String): Long {
    // Make a new local user
    val values = ContentValues().apply {
        put("name", name)
        put("uuid", UUID.randomUUID().toString())
    }
    Log.d("Database", "Added user to the database")
    return db.insert("users", null, values)
}

fun importUser(db: SQLiteDatabase, name: String, uuid: String): Long {
    // For use when downloading encouragement records from the api
    val values = ContentValues().apply {
        put("name", name)
        put("uuid", uuid)
    }
    Log.d("Database", "Added user to the database")
    return db.insert("users", null, values)
}

fun createTask(db: SQLiteDatabase, name: String, date: String, owner: Int): Long {
    // Make a new task
    val values = ContentValues().apply {
        put("name", name)
        put("is_complete", 0)
        put("date", date) // Format this as YYYY-MM-DD
        put("owner", owner) // Make this default to 1 for "default user"?
    }
    Log.d("Database", "Added task to the database")
    return db.insert("tasks", null, values)
}

fun editTask(db: SQLiteDatabase, taskId: Int, newName: String, newIsComplete: Boolean): Int {
    // Edit a task where the id matches
    val values = ContentValues().apply {
        put("name", newName)
        put("is_complete", if (newIsComplete) 1 else 0)
    }

    // Update the task where the id matches
    val rowsAffected = db.update("tasks", values, "id = ?", arrayOf(taskId.toString()))
    Log.d("Database", "Task $taskId updated. Rows affected: $rowsAffected")
    return rowsAffected
}

fun deleteTask(db: SQLiteDatabase, taskId: Int): Int {
    // Delete the task where the id matches
    val rowsAffected = db.delete("tasks", "id = ?", arrayOf(taskId.toString()))
    Log.d("Database", "Task $taskId deleted. Rows affected: $rowsAffected")
    return rowsAffected
}

fun editUsername(db: SQLiteDatabase, newName: String): Int {
    // change from the default username of the local user
    val values = ContentValues().apply {
        put("name", newName)
    }

    // Update the username where the id is 1
    val rowsAffected = db.update("users", values, "id = ?", arrayOf("1"))
    Log.d("Database", "User with id=1 updated. Rows affected: $rowsAffected")
    return rowsAffected
}

