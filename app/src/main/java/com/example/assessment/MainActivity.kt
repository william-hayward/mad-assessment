package com.example.assessment

import android.Manifest
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.location.LocationManager
import android.location.LocationListener
import android.location.Location
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem

class MainActivity : AppCompatActivity(), LocationListener {
    var newRestaurant: OverlayItem? = null
    var lat = 0.0
    var lon = 0.0
    lateinit var items: ItemizedIconOverlay<OverlayItem>

    // lists used to add restaurants to database
    var restaurantNameList = mutableListOf<String?>()
    var restaurantAddressList = mutableListOf<String?>()
    var restaurantCuisineList = mutableListOf<String?>()
    var restaurantRatingList = mutableListOf<Int?>()
    var restaurantLatList = mutableListOf<Double>()
    var restaurantLonList = mutableListOf<Double>()
    var addedToDatabse = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        addedToDatabse = false


        val map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(17.0)
        map1.controller.setCenter(GeoPoint(51.05, -0.72))

        val markerGestureListener = object:ItemizedIconOverlay.OnItemGestureListener<OverlayItem>
        {
            override fun onItemLongPress(i: Int, item: OverlayItem) : Boolean
            {
                AlertDialog.Builder(this@MainActivity)
                    .setPositiveButton("OK", null)
                    .setMessage(item.snippet)
                    .show()
                return true
            }
            override fun onItemSingleTapUp(i: Int, item: OverlayItem) : Boolean
            {
                AlertDialog.Builder(this@MainActivity)
                    .setPositiveButton("OK", null)
                    .setMessage(item.snippet)
                    .show()
                return true
            }
        }

        items = ItemizedIconOverlay(this, arrayListOf<OverlayItem>(), markerGestureListener)
        map1.overlays.add(items)

        requestLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(addedToDatabse == false){
            addToDatabase()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.addRestaurant -> {
                val intent = Intent(this,saveRestaurantActivity::class.java)
                addRestaurantLauncher.launch(intent)
                return true
            }
            R.id.mapActivity -> {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.AddToDatabse -> {
                addToDatabase()
                return true
            }
        }
        return false
    }

    val addRestaurantLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.apply {
                    val name = this.getStringExtra("com.example.name")
                    val address = this.getStringExtra("com.example.address")
                    val cuisine = this.getStringExtra("com.example.cuisine")
                    val rating = this.getIntExtra("com.example.rating", 1)

                    val string = "Name: $name\nAddress: $address\nCuisine: $cuisine \nStar Rating: $rating"

                    restaurantNameList.add(name)
                    restaurantAddressList.add(address)
                    restaurantCuisineList.add(cuisine)
                    restaurantRatingList.add(rating)
                    restaurantLatList.add(lat)
                    restaurantLonList.add(lon)


                    newRestaurant = OverlayItem("$name", string, GeoPoint(lat, lon))
                    items.addItem(newRestaurant)
                }
            }
        }



    fun requestLocation() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { // for you to complete!!!
            val mgr=getSystemService(Context.LOCATION_SERVICE) as LocationManager

            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0f,this)

        } else {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            0 -> {

                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation()
                } else {
                    AlertDialog.Builder(this)
                        .setPositiveButton("OK", null)
                        .setMessage("This app will not work properly without the GPS permission enabled.")
                        .show()
                }
            }


        }
    }


    override fun onLocationChanged(newLoc: Location) {
        val map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(17.0)
        map1.controller.setCenter(GeoPoint(newLoc.latitude, newLoc.longitude))
        lat = newLoc.latitude
        lon = newLoc.longitude
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText (this, "Provider disabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText (this, "Provider enabled", Toast.LENGTH_LONG).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    fun showDialog(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setPositiveButton("OK", null)
            .setMessage(message)
            .show()
    }

    fun addToDatabase(){
        lifecycleScope.launch {
            val db = RestaurantDatabase.getDatabase(application)
            for(i in 0 until restaurantNameList.size){
                var name = restaurantNameList[i]
                var address = restaurantAddressList[i]
                var cuisine = restaurantCuisineList[i]
                var rating = restaurantRatingList[i]
                var latitude = restaurantLatList[i]
                var longitude = restaurantLonList[i]

                var restaurant = Restaurants(0,name, address, cuisine, rating, latitude, longitude)
                var id = 0L

                withContext(Dispatchers.IO) {
                    id = db.RestaurantDao().insert(restaurant)
                }
            }
            addedToDatabse = true
            showDialog("Restaurants added to the database.")
        }
    }
}