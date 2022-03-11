package com.udacity.asteroidradar.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.TimeFrameFilter
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

class AsteroidRepository(private val database: AsteroidDatabase){

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeFrameAsteroid(timeFrame: TimeFrameFilter) : LiveData<List<Asteroid>>{
        return when (timeFrame){
            TimeFrameFilter.SHOW_TODAY -> Transformations.map(database.asteroidDao.getTodayAsteroids(
                TimeFrameFilter.SHOW_TODAY.value)){it.asDomainModel()}
            TimeFrameFilter.SHOW_WEEK -> Transformations.map(database.asteroidDao.getWeekAsteroids(
                TimeFrameFilter.SHOW_WEEK.value, TimeFrameFilter.SHOW_TODAY.value)){it.asDomainModel()}
            else -> Transformations.map(database.asteroidDao.getAllAsteroids()){it.asDomainModel()}
        }
    }

    suspend fun refreshAsteroids(){
        withContext(Dispatchers.IO){
            try{
                val asteroidData = AsteroidApi.retrofitService.getAstroids(API_KEY)
                val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidData))
                database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
            } catch (e: Exception){
                Log.e("repository", e.cause.toString())
            }
        }
    }
}