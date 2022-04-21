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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem

class MainActivity : AppCompatActivity(), LocationListener {
    var currentLocation: OverlayItem? = null
    var newRestaurant: OverlayItem? = null
    var lat = 0.0
    var lon = 0.0
    lateinit var items: ItemizedIconOverlay<OverlayItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        val map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(16.0)
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
                    val rating = this.getDoubleExtra("com.example.rating", 0.0)

                    newRestaurant = OverlayItem("Current Location", name, GeoPoint(lat, lon))
                    items.addItem(newRestaurant)
                }
            }
        }



    fun requestLocation() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { // for you to complete!!!
            // note the use of 'as' to perform type casting in Kotlin
            // getSystemService() returns a superclass type of LocationManager,
            // so we need to cast it to LocationManager.
            val mgr=getSystemService(Context.LOCATION_SERVICE) as LocationManager


            // Distance (third argument) is float.
            // In Kotlin we must explicitly use "f" to specify it's a float
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
        //Toast.makeText (this, "Location=${newLoc.latitude},${newLoc.longitude}", Toast.LENGTH_LONG).show()
        val map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(16.0)
        map1.controller.setCenter(GeoPoint(newLoc.latitude, newLoc.longitude))
        lat = newLoc.latitude
        lon = newLoc.longitude

        /*items.removeItem(currentLocation)
        currentLocation = OverlayItem("Current Location", "Your Current Location", GeoPoint(newLoc.latitude, newLoc.longitude))
        items.addItem(currentLocation)*/
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText (this, "Provider disabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText (this, "Provider enabled", Toast.LENGTH_LONG).show()
    }

    // Deprecated at API level 29, but must still be included, otherwise your
    // app will crash on lower-API devices as their API will try and call it
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }
}