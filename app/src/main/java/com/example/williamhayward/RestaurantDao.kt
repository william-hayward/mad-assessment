package com.example.williamhayward

import androidx.room.*

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurants WHERE id=:id")
    fun getRestaurantsById(id: Long): Restaurants?

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<Restaurants>

    @Insert
    fun insert(restaurants: Restaurants) : Long

}