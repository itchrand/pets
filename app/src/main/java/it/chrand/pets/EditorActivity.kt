package it.chrand.pets

import android.content.ContentValues
import android.support.v4.app.NavUtils
import android.text.TextUtils
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import it.chrand.pets.data.PetContract

/**
 * Allows user to create a new pet or edit an existing one.
 */
class EditorActivity : AppCompatActivity() {
    val LOG_TAG = EditorActivity::class.java.simpleName

    /** EditText field to enter the pet's name  */
    private lateinit var mNameEditText: EditText

    /** EditText field to enter the pet's breed  */
    private lateinit var mBreedEditText: EditText

    /** EditText field to enter the pet's weight  */
    private lateinit var mWeightEditText: EditText

    /** EditText field to enter the pet's gender  */
    private lateinit var mGenderSpinner: Spinner

    private var mGender = PetContract.PetEntry.GENDER_UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name) as EditText
        mBreedEditText = findViewById(R.id.edit_pet_breed) as EditText
        mWeightEditText = findViewById(R.id.edit_pet_weight) as EditText
        mGenderSpinner = findViewById(R.id.spinner_gender) as Spinner

        setupSpinner()
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private fun setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        val genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item)

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Apply the adapter to the spinner
        mGenderSpinner.adapter = genderSpinnerAdapter

        // Set the integer mSelected to the constant values
        mGenderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selection = parent.getItemAtPosition(position) as String
                if (!TextUtils.isEmpty(selection)) {
                    when (selection) {
                        getString(R.string.gender_male) -> mGender = PetContract.PetEntry.GENDER_MALE
                        getString(R.string.gender_female) -> mGender = PetContract.PetEntry.GENDER_FEMALE
                        else -> mGender = PetContract.PetEntry.GENDER_UNKNOWN
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            override fun onNothingSelected(parent: AdapterView<*>) {
                mGender = PetContract.PetEntry.GENDER_UNKNOWN
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // User clicked on a menu option in the app bar overflow menu
        when (item.getItemId()) {
        // Respond to a click on the "Save" menu option
            R.id.action_save -> {
                insertPet()
                finish()
                return true
            }
        // Respond to a click on the "Delete" menu option
            R.id.action_delete ->
                // Do nothing for now
                return true
        // Respond to a click on the "Up" arrow button in the app bar
            android.R.id.home -> {
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and pet attributes are the values.
        val values = ContentValues()
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, mNameEditText.text.toString().trim())
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, mBreedEditText.text.toString().trim())
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, mGender)
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, Integer.parseInt(mWeightEditText.text.toString().trim()))

        val newUri = contentResolver.insert(PetContract.PetEntry.CONTENT_URI, values)

        if (newUri == null)
            giveAToast(getString(R.string.pet_not_saved))
        else
            giveAToast(getString(R.string.pet_saved))

        Log.v(LOG_TAG, "New uri with Id " + newUri)
    }

    private fun giveAToast(message: String) {
        val context = applicationContext
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, message, duration)
        toast.show()
    }
}