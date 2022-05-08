package com.example.williamhayward

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="restaurants")

data class Restaurants(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String ?,
    var address: String ?,
    var cuisine: String ?,
    var rating: Int ?,
    var lat: Double,
    var lon: Double
)