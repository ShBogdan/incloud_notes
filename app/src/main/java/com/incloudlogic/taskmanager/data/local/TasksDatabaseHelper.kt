package com.incloudlogic.taskmanager.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TasksDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                content TEXT,
                priority INTEGER NOT NULL,
                is_local INTEGER NOT NULL,
                is_completed INTEGER NOT NULL,
                sort_order INTEGER NOT NULL DEFAULT 0
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add sort_order column to existing table
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0")
        }
    }

    companion object {
        const val DB_NAME = "tasks6.db"
        const val DB_VERSION = 2
        const val TABLE_NAME = "tasks6"
    }

}
