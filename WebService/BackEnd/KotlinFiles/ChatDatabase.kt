package com.mahamkhurram.i210681

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ChatDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_MESSAGES)
        Log.d("ChatDatabase", "Table '$TABLE_MESSAGES' created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ChatDatabase"
        const val TABLE_MESSAGES = "messages"
        const val KEY_ID = "id"
        const val KEY_SENDER = "sender"
        const val KEY_RECEIVER = "receiver"
        const val KEY_MESSAGE = "message"
        const val KEY_IMAGE = "image"
        private const val CREATE_TABLE_MESSAGES = (
                "CREATE TABLE $TABLE_MESSAGES ($KEY_ID INTEGER PRIMARY KEY, $KEY_SENDER INTEGER, " +
                        "$KEY_RECEIVER INTEGER, $KEY_MESSAGE TEXT, $KEY_IMAGE TEXT)"
                )
    }
}
