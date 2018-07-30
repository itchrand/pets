package it.chrand.pets

import android.support.v7.app.AppCompatActivity
import android.support.design.widget.FloatingActionButton
import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import it.chrand.pets.data.PetContract.PetEntry
import it.chrand.pets.data.PetDbHelper
import android.content.ContentValues
import android.util.Log


class CatalogActivity : AppCompatActivity() {
    val LOG_TAG = CatalogActivity::class.java.simpleName

    private lateinit var mDbHelper: PetDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        // Setup FAB to open EditorActivity
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        // fab.setOnClickListener(object : View.OnClickListener() {
        //    override fun onClick(view: View) {
        //        val intent = Intent(this@CatalogActivity, EditorActivity::class.java)
        //        startActivity(intent)
        //    }
        //})
        fab.setOnClickListener { view ->
            val intent = Intent(this@CatalogActivity, EditorActivity::class.java)
            startActivity(intent)
        }
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = PetDbHelper(this)
    }

    override fun onStart() {
        super.onStart()
        displayDatabaseInfo()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // User clicked on a menu option in the app bar overflow menu
        when (item.getItemId()) {
        // Respond to a click on the "Insert dummy data" menu option
            R.id.action_insert_dummy_data -> {
                insertPet()
                displayDatabaseInfo()
                return true
            }
        // Respond to a click on the "Delete all entries" menu option
            R.id.action_delete_all_entries ->
                // Do nothing for now
                return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private fun insertPet() {
        val db = mDbHelper.writableDatabase

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        val values = ContentValues()
        values.put(PetEntry.COLUMN_PET_NAME, "Toto")
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier")
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE)
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7)

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        val newRowId = db.insert(PetEntry.TABLE_NAME, null, values)
        Log.v(LOG_TAG, "New row Id " + newRowId)
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private fun displayDatabaseInfo() {
        // Create and/or open a database to read from it
        val db = mDbHelper.readableDatabase

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        //val cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null)
        val cursor = db.query(PetEntry.TABLE_NAME, null, null, null, null, null, null)

        val displayView = findViewById<View>(R.id.text_view_pet) as TextView

        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n")
            displayView.append(PetEntry._ID + " - " +
                    PetEntry.COLUMN_PET_NAME + " - " +
                    PetEntry.COLUMN_PET_BREED + " - " +
                    PetEntry.COLUMN_PET_GENDER + " - " +
                    PetEntry.COLUMN_PET_WEIGHT + "\n")

            // Figure out the index of each column
            val idColumnIndex = cursor.getColumnIndex(PetEntry._ID)
            val nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME)
            val breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED)
            val genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER)
            val weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT)

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                val currentID = cursor.getInt(idColumnIndex)
                val currentName = cursor.getString(nameColumnIndex)
                val currentBreed = cursor.getString(breedColumnIndex)
                val currentGender = cursor.getString(genderColumnIndex)
                val currentWeight = cursor.getInt(weightColumnIndex)
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " + currentBreed + " - " + currentGender + " - " + currentWeight))
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close()
        }
    }
}
