package com.example.assessment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf

class saveRestaurantActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addrestaurant)

        val addButton = findViewById<Button>(R.id.btn1)
        addButton.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onClick(v: View?) {
        val name = findViewById<EditText>(R.id.et1)
        val address = findViewById<EditText>(R.id.et2)
        val cuisine = findViewById<EditText>(R.id.et3)
        val rating = findViewById<EditText>(R.id.et4)

        var Name = ""
        var Address = ""
        var Cuisine = ""
        var Rating = 0.0

        when (v?.id) {
            R.id.btn1 -> {
                Name = name.text.toString()
                Address = address.text.toString()
                Cuisine = cuisine.text.toString()
                Rating = rating.text.toString().toDouble()
                sendBackValues(Name, Address, Cuisine, Rating)
            }
        }
    }

    fun sendBackValues(name: String, address: String, cuisine: String, rating: Double ) {
        val intent = Intent()
        val bundle = bundleOf("com.example.name" to name, "com.example.address" to address,
            "com.example.cuisine" to cuisine, "com.example.rating" to rating)
        intent.putExtras(bundle)
        setResult(RESULT_OK, intent)
        finish()
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.addRestaurant -> {
                val intent = Intent(this,saveRestaurantActivity::class.java)
                startActivity(intent)
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
}
