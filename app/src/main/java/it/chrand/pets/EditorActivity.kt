package it.chrand.pets

import android.app.AlertDialog
import android.app.LoaderManager
import android.content.ContentValues
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.database.DatabaseUtils
import android.support.v4.app.NavUtils
import android.text.TextUtils
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import it.chrand.pets.data.PetContract
import android.view.MotionEvent
import android.content.DialogInterface
import android.R.string.cancel
import android.annotation.SuppressLint
import java.nio.file.Files.delete






/**
 * Allows user to create a new pet or edit an existing one.
 */
class EditorActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    private val LOG_TAG = EditorActivity::class.java.simpleName
    private val PET_LOADER = 1
    private var isNamed: Boolean = false
    private var mPetHasChanged = false

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
        mNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val oldIsNamed = isNamed
                isNamed = s.length > 0
                if (oldIsNamed != isNamed)
                    invalidateOptionsMenu()
            }
        })
        mNameEditText.setOnTouchListener(mTouchListener)

        mBreedEditText = findViewById(R.id.edit_pet_breed) as EditText
        mBreedEditText.setOnTouchListener(mTouchListener)
        mWeightEditText = findViewById(R.id.edit_pet_weight) as EditText
        mWeightEditText.setOnTouchListener(mTouchListener)
        mGenderSpinner = findViewById(R.id.spinner_gender) as Spinner
        mGenderSpinner.setOnTouchListener(mTouchListener)

        setupSpinner()

        loaderManager.initLoader(PET_LOADER, null, this)

        if (intent.data != null) {
            setTitle(getString(R.string.editor_activity_title_edit_pet))
            isNamed = true
            invalidateOptionsMenu()
        }
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
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
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

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.
    private val mTouchListener = View.OnTouchListener { view, motionEvent ->
        mPetHasChanged = true
        false
    }

    private fun showUnsavedChangesDialog(
            discardButtonClickListener: DialogInterface.OnClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
        builder.setPositiveButton(R.string.discard, discardButtonClickListener)
        builder.setNegativeButton(R.string.keep_editing, DialogInterface.OnClickListener { dialog, id ->
            // User clicked the "Keep editing" button, so dismiss the dialog
            // and continue editing the pet.
            dialog?.dismiss()
        })

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed()
            return
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        val discardButtonClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
            // User clicked "Discard" button, close the current activity.
            finish()
        }

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        if (intent.data != null)
            return CursorLoader(this, intent.data, null, null, null, null)
        else
            return null
    }

    // Called when a previously created loader has finished loading
    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        if (cursor.count > 0) {
            cursor.moveToFirst()
            mNameEditText.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)))
            mBreedEditText.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)))
            val weight = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT))
            mWeightEditText.setText(if (weight == 0) "" else weight.toString())
            mGender = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER))
            mGenderSpinner.setSelection(mGender)
        }
    }

    // Called when a previously created loader is reset, making the data unavailable
    override fun onLoaderReset(loader: Loader<Cursor>) {
        mNameEditText.setText("")
        mBreedEditText.setText("")
        mWeightEditText.setText("")
        mGender = PetContract.PetEntry.GENDER_UNKNOWN
        mGenderSpinner.setSelection(mGender)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

        val menuItemSave = menu.findItem(R.id.action_save)
        menuItemSave.setEnabled(isNamed)
        if (isNamed)
            menuItemSave.icon.alpha = 255
        else
            menuItemSave.icon.alpha = 50

        val menuItemDelete = menu.findItem(R.id.action_delete)
        menuItemDelete.setVisible(intent.data != null)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // User clicked on a menu option in the app bar overflow menu
        when (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            R.id.action_save -> {
                savePet()
                finish()
                return true
            }
            // Respond to a click on the "Delete" menu option
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                return true
            }
            // Respond to a click on the "Up" arrow button in the app bar
            android.R.id.home -> {
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(this)
                    return true
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                val discardButtonClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
                    // User clicked "Discard" button, navigate to parent activity.
                    NavUtils.navigateUpFromSameTask(this)
                }

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun savePet() {
        // Create a ContentValues object where column names are the keys,
        // and pet attributes are the values.
        val values = ContentValues()
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, mNameEditText.text.toString().trim())
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, mBreedEditText.text.toString().trim())
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, mGender)

        var weight: Int = 0
        val weightString = mWeightEditText.text.toString()
        if (!TextUtils.isEmpty(weightString))
            weight = Integer.parseInt(weightString)
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight)

        if (intent.data == null) {
            val newUri = contentResolver.insert(PetContract.PetEntry.CONTENT_URI, values)

            if (newUri == null)
                giveAToast(getString(R.string.pet_not_saved))
            else
                giveAToast(getString(R.string.pet_saved))

            Log.v(LOG_TAG, "New uri with Id " + newUri)
        } else {
            val countUpdated = contentResolver.update(intent.data, values, null, null)

            if (countUpdated == 0)
                giveAToast(getString(R.string.pet_not_updated))
            else
                giveAToast(getString(R.string.pet_updated))

            Log.v(LOG_TAG, "Updated " + countUpdated)
        }
    }

    private fun giveAToast(message: String) {
        val context = applicationContext
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, message, duration)
        toast.show()
    }

    private fun showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_dialog_msg)
        builder.setPositiveButton(R.string.delete, DialogInterface.OnClickListener { dialog, id ->
            // User clicked the "Delete" button, so delete the pet.
            deletePet()
            finish()
        })
        builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, id ->
            // User clicked the "Cancel" button, so dismiss the dialog
            // and continue editing the pet.
            dialog?.dismiss()
        })

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    @SuppressLint("StringFormatInvalid")
    private fun deletePet() {

            val deleteCount = contentResolver.delete(intent.data, null, null)

            if (deleteCount == 0)
                giveAToast(getString(R.string.pet_not_deleted))
            else
                giveAToast(getString(R.string.pet_deleted, deleteCount))

            Log.v(LOG_TAG, "Deleted " + deleteCount)
    }
}