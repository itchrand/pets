package it.chrand.pets.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import it.chrand.pets.data.PetContract.PetEntry
import android.database.sqlite.SQLiteOpenHelper

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
/**
 * Constructs a new instance of [PetDbHelper].
 *
 * @param context of the app
 */
class PetDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val LOG_TAG = PetDbHelper::class.java.simpleName
    companion object {

        /** Name of the database file  */
        private val DATABASE_NAME = "shelter.db"

        /**
         * Database version. If you change the database schema, you must increment the database version.
         */
        private val DATABASE_VERSION = 1
    }

    /**
     * This is called when the database is created for the first time.
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Create a String that contains the SQL statement to create the pets table
        val SQL_CREATE_PETS_TABLE = ("CREATE TABLE " + PetEntry.TABLE_NAME + " ("
                + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetEntry.COLUMN_PET_BREED + " TEXT, "
                + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);")

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE)
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

 }