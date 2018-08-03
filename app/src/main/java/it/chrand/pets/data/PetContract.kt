package it.chrand.pets.data

import android.net.Uri
import android.provider.BaseColumns
import android.content.ContentResolver

object PetContract {

    val CONTENT_AUTHORITY = "it.chrand.pets"
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
    val PATH_PETS = "pets"

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    object PetEntry : BaseColumns {
        val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS)
        /**
         * The MIME type of the [.CONTENT_URI] for a list of pets.
         */
        val CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + "/" + PATH_PETS

        /**
         * The MIME type of the [.CONTENT_URI] for a single pet.
         */
        val CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + "/" + PATH_PETS

        /** Name of database table for pets  */
        val TABLE_NAME = "pets"

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        val _ID = BaseColumns._ID

        /**
         * Name of the pet.
         *
         * Type: TEXT
         */
        val COLUMN_PET_NAME = "name"

        /**
         * Breed of the pet.
         *
         * Type: TEXT
         */
        val COLUMN_PET_BREED = "breed"

        /**
         * Gender of the pet.
         *
         * The only possible values are [.GENDER_UNKNOWN], [.GENDER_MALE],
         * or [.GENDER_FEMALE].
         *
         * Type: INTEGER
         */
        val COLUMN_PET_GENDER = "gender"

        /**
         * Weight of the pet.
         *
         * Type: INTEGER
         */
        val COLUMN_PET_WEIGHT = "weight"

        /**
         * Possible values for the gender of the pet.
         */
        val GENDER_UNKNOWN = 0
        val GENDER_MALE = 1
        val GENDER_FEMALE = 2

        fun isValidGender(gender: Int): Boolean {
            return gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE
        }
    }
}