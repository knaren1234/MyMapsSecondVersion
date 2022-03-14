package com.example.mymapssecondversion

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mymapssecondversion.databinding.ActivityCreateMapBinding
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar

private const val TAG = "CreateMapActivity"
class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapBinding
    private val markers: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = intent.getStringExtra(EXTRA_MAP_TITLE)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.let {
            Snackbar.make(it, "Long-press to add a marker!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", {})
                    .setActionTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save_places, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mSave) {
            Log.i(TAG, "Tapped on Save!")

            if (markers.isEmpty()) {
                Toast.makeText(this, "There must be atleast one marker!", Toast.LENGTH_LONG).show()
                return true
            }

            val places = markers.map {marker -> marker.title?.let { marker.snippet?.let { it1 ->
                Place(it,
                    it1, marker.position.latitude, marker.position.longitude)
            } } }
            val data = Intent()
            data.putExtra(EXTRA_USER_MAP,
                intent.getStringExtra(EXTRA_MAP_TITLE)?.let { UserMap(it, places as List<Place>) })
            setResult(Activity.RESULT_OK, data)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener { marker ->
            Log.i(TAG, "setOnInfoWindowClickListener")
            markers.remove(marker)
            marker.remove()
        }

        mMap.setOnMapLongClickListener { latLng ->
            Log.i(TAG, "setOnMapClickListner  ${latLng}")
//            val marker = mMap.addMarker(MarkerOptions().position(latLng).title("new marker").snippet("a cool snippet"))
//            if (marker != null) {
//                markers.add(marker)
//            }
            showAlretDialog(latLng)
        }
    }

    private fun showAlretDialog(latLng: LatLng) {
        val placeHolderView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(this)
                    .setTitle("new marker")
                    .setView(placeHolderView)
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Cancel", null)
                    .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeHolderView.findViewById<EditText>(R.id.etTitle).text.toString()
            val description = placeHolderView.findViewById<EditText>(R.id.etDescription).text.toString()
            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(this, "Title and Description must be non-empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val marker = mMap.addMarker(MarkerOptions().position(latLng).title(title).snippet(description))
            if (marker != null) {
                markers.add(marker)
            }
            dialog.dismiss()
        }

    }
}