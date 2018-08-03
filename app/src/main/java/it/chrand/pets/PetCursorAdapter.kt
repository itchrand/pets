package it.chrand.pets

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import it.chrand.pets.data.PetContract


/**
 * [PetCursorAdapter] is an adapter for a list or grid view
 * that uses a [Cursor] of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the [Cursor].
 */
class PetCursorAdapter (context: Context, c: Cursor?)/* flags */ : CursorAdapter(context, c, 0) {
    /**
     * Constructs a new [PetCursorAdapter].
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     * moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View? {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     * correct row.
     */
    override fun bindView(view: View, context: Context, cursor: Cursor) {
        // Find fields to populate in inflated template
        val petNameView = view.findViewById(R.id.name) as TextView
        val petSummaryView = view.findViewById(R.id.summary) as TextView
        // Extract properties from cursor
        val petName = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PET_NAME))
        val petBreed = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PET_BREED))
        // Populate fields with extracted properties
        petNameView.text = petName
        petSummaryView.text = if (petBreed.length > 0) petBreed else context.getString(R.string.unknown_pet_breed)
    }
}