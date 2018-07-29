package it.chrand.pets.data

import android.provider.BaseColumns

object PetContract {

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    class PetEntry : BaseColumns {
        companion object {

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
        }
    }
}