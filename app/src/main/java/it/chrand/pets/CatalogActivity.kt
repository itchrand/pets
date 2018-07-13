package it.chrand.pets

import android.support.v7.app.AppCompatActivity
import android.support.design.widget.FloatingActionButton
import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View


class CatalogActivity : AppCompatActivity() {

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
        fab.setOnClickListener{ view ->
                val intent = Intent(this@CatalogActivity, EditorActivity::class.java)
                startActivity(intent)
        }
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
            R.id.action_insert_dummy_data ->
                // Do nothing for now
                return true
        // Respond to a click on the "Delete all entries" menu option
            R.id.action_delete_all_entries ->
                // Do nothing for now
                return true
        }
        return super.onOptionsItemSelected(item)
    }
}
