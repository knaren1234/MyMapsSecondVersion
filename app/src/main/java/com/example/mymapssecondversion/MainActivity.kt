package com.example.mymapssecondversion

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony.Mms.Part.FILENAME
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

data class Place(val title: String, val description: String, val latitude: Double, val longitude: Double) : Serializable
data class UserMap(val title: String, val places: List<Place>) : Serializable

const val EXTRA_USER_MAP  = "EXTRA_USER_MAP"
const val EXTRA_MAP_TITLE = "EXTRA_MAP_TITLE"
private const val REQUEST_CODE = 0
private const val TAG = "MainActivity"
private const val FILE_NAME = "UserMaps.data"
class MainActivity : AppCompatActivity() {
    private lateinit var userMaps: MutableList<UserMap>
    private lateinit var adapter: MapsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getData = deSerializeUserMaps(this)
        if (getData.isEmpty()) {
            userMaps = mutableListOf()
        } else {
            userMaps = getData as MutableList<UserMap>
        }
        // attach an adapter to recycler view
        // userMaps = generateSampleData()
        adapter = MapsAdapter(this, userMaps, object: MapsAdapter.OnClickListner {
            override fun onItemClick(position: Int) {
                Log.i(TAG, "onItemClick $position")
                val intent1 = Intent(this@MainActivity, DisplayMapActivity::class.java)
                intent1.putExtra(EXTRA_USER_MAP, userMaps[position])
                startActivity(intent1)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
        rvMapTitles.adapter = adapter
        // attach a layout manager to recycler view
        rvMapTitles.layoutManager = LinearLayoutManager(this)

        fabCreateMap.setOnClickListener {
            Log.i(TAG, "Tapped on FAB")
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val placeHolderView = LayoutInflater.from(this).inflate(R.layout.dialog_map_title, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Map Title")
            .setView(placeHolderView)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeHolderView.findViewById<EditText>(R.id.etMapTitle).text.toString()
            if (title.trim().isEmpty()) {
                Toast.makeText(this, "Title must be non-empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent1 = Intent(this@MainActivity, CreateMapActivity::class.java)
            intent1.putExtra(EXTRA_MAP_TITLE, title)
            startActivityForResult(intent1, REQUEST_CODE)
            dialog.dismiss()
        }
    }


    private fun serializeUserMaps(context: Context, userMaps: List<UserMap>) {
        Log.i(TAG, "serializeUserMaps")
        ObjectOutputStream(FileOutputStream(getDataFile(context))).use {
            it.writeObject(userMaps)
        }
    }

    private fun deSerializeUserMaps(context: Context): List<UserMap>{
        Log.i(TAG, "deSerializeUserMaps")
        val dataFile = getDataFile(context)
        if (!dataFile.exists()) {
            Log.i(TAG, "datafile does not exist yet")
            return emptyList()
        }
        ObjectInputStream(FileInputStream(dataFile)).use {
            return it.readObject() as List<UserMap>
        }
    }

    private fun getDataFile(context: Context): File {
        Log.i(TAG, "getting file from ${context.filesDir}")
        return File(context.filesDir, FILE_NAME)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get new map data from the data
            val userMap =  data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
            Log.i(TAG, "returned from create map activity ${userMap.title}")
            for (place in userMap.places) {
                Log.i(TAG, "${place.latitude} ${place.longitude}")
            }
            userMaps.add(userMap)
            adapter.notifyItemInserted(userMaps.size - 1)
            serializeUserMaps(this, userMaps)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun generateSampleData(): MutableList<UserMap> {
        return mutableListOf(
            UserMap(
                "Memories from University",
                listOf(
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
                    Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173),
                    Place("Pinkberry", "First date with my wife", 37.444, -122.170)
                )
            ),
            UserMap("January vacation planning!",
                listOf(
                    Place("Tokyo", "Overnight layover", 35.67, 139.65),
                    Place("Ranchi", "Family visit + wedding!", 23.34, 85.31),
                    Place("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
                )),
            UserMap("Singapore travel itinerary",
                listOf(
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
                    Place("Jurong Bird Park", "Family-friendly park with many varieties of birds", 1.319, 103.706),
                    Place("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
                    Place("Botanic Gardens", "One of the world's greatest tropical gardens", 1.3138, 103.8159)
                )
            ),
            UserMap("My favorite places in the Midwest",
                listOf(
                    Place("Chicago", "Urban center of the midwest, the \"Windy City\"", 41.878, -87.630),
                    Place("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
                    Place("Mackinaw City", "The entrance into the Upper Peninsula", 45.777, -84.727),
                    Place("Michigan State University", "Home to the Spartans", 42.701, -84.482),
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
                )
            ),
            UserMap("Restaurants to try",
                listOf(
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
                    Place("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
                    Place("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
                    Place("Citizen Eatery", "Bright cafe in Austin with a pink rabbit", 30.322, -97.739),
                    Place("Kati Thai", "Authentic Portland Thai food, served with love", 45.505, -122.635)
                )
            )
        )
    }
}