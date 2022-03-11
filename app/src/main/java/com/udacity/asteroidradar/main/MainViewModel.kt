package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.TimeFrameFilter
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(application: Application) : AndroidViewModel(application) {

//    private val _asteroids = MutableLiveData<List<Asteroid>>()
//    val asteroids : LiveData<List<Asteroid>>
//        get() = _asteroids

    private val _image = MutableLiveData<PictureOfDay?>()
    val image : LiveData<PictureOfDay?>
        get() = _image

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetail : LiveData<Asteroid?>
        get() = _navigateToAsteroidDetail

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    var asteroids : LiveData<List<Asteroid>>

    init{
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            getPicture()
        }
        asteroids = asteroidRepository.getTimeFrameAsteroid(TimeFrameFilter.SHOW_WEEK)
    }

    fun displayAsteroidDetails(asteroid : Asteroid){
        _navigateToAsteroidDetail.value = asteroid
    }

    fun displayAsteroidDetailsComplete(){
        _navigateToAsteroidDetail.value = null
    }

    private suspend fun getPicture(){
        withContext(Dispatchers.IO){
            try{
                val result = AsteroidApi.retrofitService.getPictureForToday(API_KEY)
                if (result.mediaType == "image"){
                    _image.postValue(result)
                } else{
                    _image.postValue(null)
                }
            } catch (e : Exception){
                Log.e("getPicture", e.message.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAsteroidData(timeFrame: TimeFrameFilter){
        asteroids = asteroidRepository.getTimeFrameAsteroid(timeFrame)
    }
}