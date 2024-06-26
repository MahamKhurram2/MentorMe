package com.mahamkhurram.i210681

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_MENTORS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $MENTORS_TABLE")
        onCreate(db)
    }

    fun insertOrUpdateMentor(mentor: Mentor): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_ID, mentor.id)
            put(KEY_NAME, mentor.name)
            put(KEY_DESCRIPTION, mentor.description)
            put(KEY_STATUS, mentor.status)
            put(KEY_SESSION_PRICE, mentor.sessionPrice)
        }


        val existingMentor = getMentorById(mentor.id)
        return if (existingMentor == null) {
            // Mentor doesn't exist, insert a new one
            val id = db.insert(MENTORS_TABLE, null, values)
            Log.d("DatabaseHelper", "Inserted mentor with ID ${mentor.id}")
            db.close()
            id
        } else {
            // Mentor already exists, update it
            val rowsAffected = db.update(
                MENTORS_TABLE,
                values,
                "$KEY_ID = ?",
                arrayOf(mentor.id)
            )
            Log.d("DatabaseHelper", "Updated mentor with ID ${mentor.id}")
            db.close()
            rowsAffected.toLong()
        }
    }
    fun insertOrUpdateMentorInfo(mentorInfo: mentorinfo): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, mentorInfo.name)
            put(KEY_DESCRIPTION, mentorInfo.description)
            put(KEY_STATUS, mentorInfo.availability)
            put(KEY_SESSION_PRICE, mentorInfo.sessionPrice)
        }
        return db.insertWithOnConflict(MENTORS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun synchronizeMentorData(context: Context) {
        val mentors = getLocalMentors()
        Log.d("DatabaseHelper", "Found ${mentors.size} local mentors to synchronize")
        for (mentor in mentors) {
            isMentorExist(mentor.id, context) { exists ->
                if (!exists) {
                    Log.d("DatabaseHelper", "Mentor with ID ${mentor.id} does not exist in server, inserting...")
                    val insertedId = insertOrUpdateMentor(mentor)
                    if (insertedId != -1L) {
                        deleteLocalMentor(mentor.id)
                    }
                } else {
                    Log.d("DatabaseHelper", "Mentor with ID ${mentor.id} already exists in server, skipping...")
                }
            }
        }
    }

    private fun isMentorExist(mentorId: String, context: Context, callback: (Boolean) -> Unit) {
        val db = DatabaseHelper(context).readableDatabase
        val selection = "$KEY_ID = ?"
        val selectionArgs = arrayOf(mentorId)
        val cursor = db.query(MENTORS_TABLE, null, selection, selectionArgs, null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        callback(exists)
    }

    @SuppressLint("Range")
    private fun getMentorById(mentorId: String): Mentor? {
        val db = this.readableDatabase
        val cursor = db.query(
            MENTORS_TABLE,
            null,
            "$KEY_ID = ?",
            arrayOf(mentorId),
            null,
            null,
            null
        )
        val mentor: Mentor? = if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndex(KEY_ID))
            val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
            val description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))
            val status = cursor.getString(cursor.getColumnIndex(KEY_STATUS))
            val sessionPrice = cursor.getString(cursor.getColumnIndex(KEY_SESSION_PRICE))
            Mentor(id, name, description, status, sessionPrice)
        } else {
            null
        }
        cursor.close()
        return mentor
    }

    @SuppressLint("Range")
    private fun getLocalMentors(): List<Mentor> {
        val mentors = mutableListOf<Mentor>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $MENTORS_TABLE", null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(KEY_ID))
                val name = it.getString(it.getColumnIndex(KEY_NAME))
                val description = it.getString(it.getColumnIndex(KEY_DESCRIPTION))
                val status = it.getString(it.getColumnIndex(KEY_STATUS))
                val sessionPrice = it.getString(it.getColumnIndex(KEY_SESSION_PRICE))
                val mentor = Mentor(id, name, description, status, sessionPrice)
                mentors.add(mentor)
            }
        }
        return mentors
    }

    private fun deleteLocalMentor(id: String) {
        val db = this.writableDatabase
        db.delete(MENTORS_TABLE, "$KEY_ID = ?", arrayOf(id))
    }
    @SuppressLint("Range")
    fun getAllMentors(): ArrayList<mentorinfo> {
        val mentorsList = ArrayList<mentorinfo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $MENTORS_TABLE", null)
        cursor.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndex(KEY_NAME))
                val description = it.getString(it.getColumnIndex(KEY_DESCRIPTION))
                val sessionPrice = it.getString(it.getColumnIndex(KEY_SESSION_PRICE))
                val status = it.getString(it.getColumnIndex(KEY_STATUS))
                mentorsList.add(mentorinfo(name, description, sessionPrice, status))
            }
        }
        db.close() // Close the database connection
        return mentorsList
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MentorsDB"
        private const val MENTORS_TABLE = "mentors"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_STATUS = "status"
        private const val KEY_SESSION_PRICE = "session_price"

        private const val CREATE_MENTORS_TABLE = (
                "CREATE TABLE $MENTORS_TABLE("
                        + "$KEY_ID TEXT PRIMARY KEY,"
                        + "$KEY_NAME TEXT,"
                        + "$KEY_DESCRIPTION TEXT,"
                        + "$KEY_STATUS TEXT,"
                        + "$KEY_SESSION_PRICE TEXT)"
                )

    }

}
