package it.chrand.pets

import android.annotation.SuppressLint
import android.app.LoaderManager
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.support.design.widget.FloatingActionButton
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import it.chrand.pets.data.PetContract.PetEntry
import android.database.Cursor
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import it.chrand.pets.data.PetContract

class CatalogActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    private val LOG_TAG = CatalogActivity::class.java.simpleName
    private val PET_LOADER = 0

    lateinit var petAdapter: PetCursorAdapter

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
        fab.setOnClickListener { _view ->
            val intent = Intent(this@CatalogActivity, EditorActivity::class.java)
            startActivity(intent)
        }

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        loaderManager.initLoader(PET_LOADER, null, this)

        val petListView = findViewById<View>(R.id.list_view_pet) as ListView

        // Setup cursor adapter using cursor = null
        petAdapter = PetCursorAdapter(this, null)
        // Attach cursor adapter to the ListView
        petListView.setAdapter(petAdapter)
        petListView.setOnItemClickListener({ parent, view, position, id ->
            val intent = Intent(this@CatalogActivity, EditorActivity::class.java)
            val currentPetUri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id)
            intent.setData(currentPetUri)
            startActivity(intent)
        })

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        val emptyView = findViewById<View>(R.id.empty_view)
        petListView.setEmptyView(emptyView)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return CursorLoader(this, PetContract.PetEntry.CONTENT_URI,
                null, null, null, null)
    }

    // Called when a previously created loader has finished loading
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        petAdapter.swapCursor(data)
    }

    // Called when a previously created loader is reset, making the data unavailable
    override fun onLoaderReset(loader: Loader<Cursor>) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        petAdapter.swapCursor(null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    @SuppressLint("StringFormatInvalid")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // User clicked on a menu option in the app bar overflow menu
        when (item.getItemId()) {
        // Respond to a click on the "Insert dummy data" menu option
            R.id.action_insert_dummy_data -> {
                insertPet()
                return true
            }
        // Respond to a click on the "Delete all entries" menu option
            R.id.action_delete_all_entries -> {
                val deleteCount = contentResolver.delete(PetContract.PetEntry.CONTENT_URI, null, null)
                if (deleteCount == 0)
                    giveAToast(getString(R.string.pet_not_deleted))
                else
                    giveAToast(getString(R.string.pet_deleted, deleteCount))

                Log.v(LOG_TAG, "Deleted pets: " + deleteCount)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private fun insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        val values = ContentValues()
        values.put(PetEntry.COLUMN_PET_NAME, "Toto")
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier")
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE)
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7)

        val newUri = contentResolver.insert(PetContract.PetEntry.CONTENT_URI, values)

        Log.v(LOG_TAG, "New uri with Id " + newUri)
    }

    private fun giveAToast(message: String) {
        val context = applicationContext
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, message, duration)
        toast.show()
    }

}
