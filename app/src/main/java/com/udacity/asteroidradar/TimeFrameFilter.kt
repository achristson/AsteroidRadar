package com.udacity.asteroidradar

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class TimeFrameFilter(val value: String){

    @RequiresApi(Build.VERSION_CODES.O)
    SHOW_TODAY(LocalDate.now().format(DateTimeFormatter.ISO_DATE)),

    @RequiresApi(Build.VERSION_CODES.O)
    SHOW_WEEK(LocalDate.now().minusDays(7).format(DateTimeFormatter.ISO_DATE)),

    @RequiresApi(Build.VERSION_CODES.O)
    SHOW_ALL(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
}