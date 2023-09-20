package com.example.movieapp.utils

import android.os.Message
import com.example.appac.FloatingIcon
import com.example.appac.LoginAct.Companion.shared
import com.example.appac.R

object Constants {
    const val BASE_URL = "https://aqua-iot.pro/api/"
    const val WEA_URL = "https://api.weatherapi.com/"
    var API_KEY = shared.getString("id", "12")
    const val POST_URL = "https://aqua-iot.pro/api/"
    val typeWind : List<Int> = listOf(R.drawable.auto, R.drawable.five, R.drawable.four, R.drawable.three, R.drawable.two, R.drawable.one)
    val typeMode : List<Int> = listOf(R.drawable.ic_baseline_autorenew_24, R.drawable.ic_baseline_ac_unit_24, R.drawable.ic_baseline_wb_sunny_24, R.drawable.ic_baseline_water_drop_24)
    val helpPage : List<Int> = listOf(R.drawable.hd1, R.drawable.hd2, R.drawable.hd3, R.drawable.hd4, R.drawable.hd5, R.drawable.hd6)
    val speedMode : List<String> = listOf("Tự động", "Thấp", "Trung bình", "Cao")
    val dustMode : List<String> = listOf("Tắt", "Bật")
    var curtem = shared.getInt("curtem", 24)
    var curtype = shared.getInt("curtype", 0)
    var curmode = shared.getInt("curmode", 0)
    var curspeed = shared.getInt("curspeed", 0)
    var curdust = shared.getInt("curdust", 0)
    var loca = shared.getString("location", "...")
    var max = shared.getInt("max", 10)
    var WEA_KEY = "dca892fc2d534382a1461857211207"
    var Password = ""
    var Message = ""
    var numMess = shared.getInt("num", 0)
    var adminMessage = shared.getString("admin", "")
    var save = shared.getInt("save", 0)
    var autoLogin = shared.getInt("autoLogin", 0)
}