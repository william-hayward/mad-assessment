package com.example.assessment

import androidx.room.*

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurants WHERE id=:id")
    fun getRestaurantsById(id: Long): Restaurants?

    @Insert
    fun insert(restaurants: Restaurants) : Long

}