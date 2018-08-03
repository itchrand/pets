package it.chrand.pets.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.content.UriMatcher
import it.chrand.pets.data.PetContract.CONTENT_AUTHORITY
import it.chrand.pets.data.PetContract.PetEntry

/**
 * {@link ContentProvider} for Pets app.
 */
class PetProvider : ContentProvider() {

    val LOG_TAG = PetProvider::class.java.simpleName

    private lateinit var mDbHelper: PetDbHelper

    companion object {
        /**
         * UriMatcher object to match a content URI to a corresponding code.
         * The input passed into the constructor represents the code to return for the root URI.
         * It's common to use NO_MATCH as the input for this case.
         */
        val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        /** URI matcher code for the content URI for the pets table  */
        private val PETS = 100

        /** URI matcher code for the content URI for a single pet in the pets table  */
        private val PET_ID = 101

        init {
            // The calls to addURI() go here, for all of the content URI patterns that the provider
            // should recognize. All paths added to the UriMatcher have a corresponding code to return
            // when a match is found.
            sUriMatcher.addURI(CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS)
            sUriMatcher.addURI(CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID)
        }
    }

    /**
     * Initialize the provider and the database helper object.
     */
    override fun onCreate(): Boolean {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = PetDbHelper(context)
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {
        val database = mDbHelper.readableDatabase
        var cursor: Cursor? = null

        when (sUriMatcher.match(uri)) {
            PETS -> {
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            PET_ID -> {
                val selection = PetContract.PetEntry._ID + "=?"
                val selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
        }

        if (cursor != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri)

        return cursor
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    override fun insert(uri: Uri, contentValues: ContentValues): Uri? {
        sanityCheckValues(contentValues)

        val database = mDbHelper.writableDatabase
        var newUri: Uri? = null

        when (sUriMatcher.match(uri)) {
            PETS -> {
                val newRowId = database.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues)
                if (newRowId > -1)
                    newUri = Uri.withAppendedPath(PetContract.PetEntry.CONTENT_URI, PetContract.PATH_PETS + "/$newRowId")
            }
        }

        if (newUri != null)
            getContext().getContentResolver().notifyChange(uri, null)

        return newUri
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    override fun update(uri: Uri, contentValues: ContentValues, whereClause: String?, whereClauseArgs: Array<String>?): Int {
        sanityCheckValues(contentValues, mustCheck = false)

        val database = mDbHelper.writableDatabase
        var updateCount = 0

        when (sUriMatcher.match(uri)) {
            PETS -> {
                updateCount = database.update(PetContract.PetEntry.TABLE_NAME, contentValues, whereClause, whereClauseArgs)
            }
            PET_ID -> {
                val whereClause = PetContract.PetEntry._ID + "=?"
                val whereClauseArgs = arrayOf(ContentUris.parseId(uri).toString())
                updateCount = database.update(PetContract.PetEntry.TABLE_NAME, contentValues, whereClause, whereClauseArgs)
            }
        }

        if (updateCount != 0)
            getContext().getContentResolver().notifyChange(uri, null)

        return updateCount
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    override fun delete(uri: Uri, whereClause: String?, whereClauseArgs: Array<String>?): Int {

        val database = mDbHelper.writableDatabase
        var deleteCount = 0

        when (sUriMatcher.match(uri)) {
            PETS -> {
                deleteCount = database.delete(PetContract.PetEntry.TABLE_NAME, whereClause, whereClauseArgs)
            }
            PET_ID -> {
                val whereClause = PetContract.PetEntry._ID + "=?"
                val whereClauseArgs = arrayOf(ContentUris.parseId(uri).toString())
                deleteCount = database.delete(PetContract.PetEntry.TABLE_NAME, whereClause, whereClauseArgs)
            }
        }

        if (deleteCount != 0)
            getContext().getContentResolver().notifyChange(uri, null)

        return deleteCount
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    override fun getType(uri: Uri): String? {
        when (sUriMatcher.match(uri)) {
            PETS ->
                return PetEntry.CONTENT_LIST_TYPE
            PET_ID ->
                return PetEntry.CONTENT_ITEM_TYPE
            else ->
                throw IllegalStateException("Unknown URI $uri with match")
        }
    }

    private fun sanityCheckValues(contentValues: ContentValues, mustCheck: Boolean = true) {
        // Check that the name is not null
        if (contentValues.containsKey(PetEntry.COLUMN_PET_NAME) || mustCheck) {
            val name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME)
            if (name == null)
                throw IllegalArgumentException("Pet requires a name")
        }

        if (contentValues.containsKey(PetEntry.COLUMN_PET_GENDER) || mustCheck) {
            val gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER)
            if (gender == null || !PetEntry.isValidGender(gender))
                throw IllegalArgumentException("Pet requires a valid gender")
        }

        if (contentValues.containsKey(PetEntry.COLUMN_PET_WEIGHT) || mustCheck) {
            val weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT)
            if (weight != null && weight < 0)
                throw IllegalArgumentException("Pet requires valid weight")
        }
    }
}