package com.incloudlogic.taskmanager.data

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
                state INTEGER NOT NULL
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "tasks1234.db"
        const val DB_VERSION = 1
        const val TABLE_NAME = "tasks1234"
    }

}