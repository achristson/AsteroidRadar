package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao{

    @Query("select * from asteroids order by closeApproachDate desc")
    fun getAllAsteroids() : LiveData<List<DatabaseAsteroid>>

    @Insert()
    suspend fun insertAll(vararg asteroid: DatabaseAsteroid)

    @Query("select * from asteroids where closeApproachDate = :startDate order by closeApproachDate desc")
    fun getTodayAsteroids(startDate: String) : LiveData<List<DatabaseAsteroid>>

    @Query("select * from asteroids where closeApproachDate between :startDate and :endDate order by closeApproachDate desc")
    fun getWeekAsteroids(startDate: String, endDate: String) : LiveData<List<DatabaseAsteroid>>

    @Query("delete from asteroids")
    suspend fun clear()
}

@Database(entities = [DatabaseAsteroid::class], version=1)
abstract class AsteroidDatabase : RoomDatabase(){
    abstract val asteroidDao : AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase{
    synchronized(AsteroidDatabase::class.java){
        if (!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AsteroidDatabase::class.java,
                        "asteroids").build()
        }
    }
    return INSTANCE
}