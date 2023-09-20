package com.example.appac

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.example.appac.utils.Error._000001
import com.example.appac.utils.Error._000001_
import com.example.appac.utils.Error._000010
import com.example.appac.utils.Error._000010_
import com.example.appac.utils.Error._000011
import com.example.appac.utils.Error._000011_
import com.example.appac.utils.Error._000100
import com.example.appac.utils.Error._000100_
import com.example.appac.utils.Error._000101
import com.example.appac.utils.Error._000101_
import com.example.appac.utils.Error._000110
import com.example.appac.utils.Error._000110_
import com.example.appac.utils.Error._000111
import com.example.appac.utils.Error._000111_
import com.example.appac.utils.Error._001000
import com.example.appac.utils.Error._001000_
import com.example.appac.utils.Error._001001
import com.example.appac.utils.Error._001001_
import com.example.appac.utils.Error._001010
import com.example.appac.utils.Error._001010_
import com.example.appac.utils.Error._001011
import com.example.appac.utils.Error._001011_
import com.example.appac.utils.Error._010000
import com.example.appac.utils.Error._010000_
import com.example.appac.utils.Error._010001
import com.example.appac.utils.Error._010001_
import com.example.appac.utils.Error._010010
import com.example.appac.utils.Error._010010_
import com.example.appac.utils.Error._010100
import com.example.appac.utils.Error._010100_
import com.example.appac.utils.Error._010101
import com.example.appac.utils.Error._010101_
import com.example.appac.utils.Error._010110
import com.example.appac.utils.Error._010110_
import com.example.appac.utils.Error._010111
import com.example.appac.utils.Error._010111_
import com.example.appac.utils.Error._011000
import com.example.appac.utils.Error._011000_
import com.example.appac.utils.Error._011001
import com.example.appac.utils.Error._011001_
import com.example.appac.utils.Error._011010
import com.example.appac.utils.Error._011010_
import com.example.appac.utils.Error._011011
import com.example.appac.utils.Error._011011_
import com.example.appac.utils.Error._100000
import com.example.appac.utils.Error._100000_
import com.example.appac.utils.Error._100001
import com.example.appac.utils.Error._100001_
import com.example.appac.utils.Error._100010
import com.example.appac.utils.Error._100010_
import com.example.appac.utils.Error._100011
import com.example.appac.utils.Error._100011_
import com.example.appac.utils.Error._100100
import com.example.appac.utils.Error._100100_
import com.example.appac.utils.Error._100101
import com.example.appac.utils.Error._100101_
import com.example.appac.utils.Error._100110
import com.example.appac.utils.Error._100110_
import com.example.appac.utils.Error._101000
import com.example.appac.utils.Error._101000_
import com.example.appac.utils.Error._101001
import com.example.appac.utils.Error._101001_
import com.example.appac.utils.Error._101010
import com.example.appac.utils.Error._101010_
import com.example.appac.utils.Error._110000
import com.example.appac.utils.Error._110000_
import com.example.appac.utils.Error._110001
import com.example.appac.utils.Error._110001_
import com.example.appac.utils.Error._110010
import com.example.appac.utils.Error._110010_
import com.example.appac.utils.Error._110100
import com.example.appac.utils.Error._110100_
import com.example.appac.utils.Error._110101
import com.example.appac.utils.Error._110101_
import com.example.appac.utils.Error._110110
import com.example.appac.utils.Error._110110_
import com.example.appac.utils.Error._110111
import com.example.appac.utils.Error._110111_
import com.example.appac.utils.Error._111000
import com.example.appac.utils.Error._111000_
import com.example.appac.utils.Error._111001
import com.example.appac.utils.Error._111001_
import com.example.appac.utils.Error._111010
import com.example.appac.utils.Error._111010_
import com.example.appac.utils.Error._111011
import com.example.appac.utils.Error._111011_
import com.example.movieapp.API.ApiServices
import com.example.myapplication.API.ApiClient
import com.example.myapplication.database.database
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FloatingIcon :Service(){
    private lateinit var floatView: ViewGroup
    private lateinit var floatWindowLayoutParams: WindowManager.LayoutParams
    private var LAYOUT_TYPE: Int? = null
    private lateinit var windowManager: WindowManager
    private lateinit var icon: ImageView
    private var nhandler : Handler = Handler()
    private val api: ApiServices by lazy {
        ApiClient().getClient().create(ApiServices::class.java)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val metrics = applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatView = inflater.inflate(R.layout.floating_layout, null) as ViewGroup
        icon = floatView.findViewById(R.id.icon)
        if (!MainActivity2.error) {
            icon.visibility = View.GONE
        }
        else {
            icon.visibility = View.VISIBLE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST

        floatWindowLayoutParams = WindowManager.LayoutParams(
            (width * 0.12f).toInt(),
            (height * 0.08f).toInt(),
            LAYOUT_TYPE!!,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParams.gravity = Gravity.START
        floatWindowLayoutParams.x = 0
        floatWindowLayoutParams.y = 0

        windowManager.addView(floatView, floatWindowLayoutParams)
        floatView.setOnClickListener() {
            stopSelf()
            windowManager.removeView(floatView)
            val back = Intent(this@FloatingIcon, MainActivity2::class.java)
            back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(back)
        }
        floatView.setOnTouchListener(object : View.OnTouchListener{
            val updatelocate = floatWindowLayoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = updatelocate.x.toDouble()
                        y = updatelocate.y.toDouble()
                        px = event.rawX.toDouble()
                        py = event.rawY.toDouble()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        updatelocate.x = (x + event.rawX - px).toInt()
                        updatelocate.y = (y + event.rawY - py).toInt()
                        windowManager.updateViewLayout(floatView, updatelocate)
                    }
                }
                return false
            }

        })
        val runnable = object : Runnable {
            override fun run() {
                val callApi = api.getData(true)
                callApi.enqueue(object : Callback<database> {


                    override fun onResponse(call: Call<database>, response: Response<database>) {
                        Log.e("onFailure", "Err : ${response.code()}")

                        when (response.code()) {
                            in 200..299 -> {
                                response.body()?.let { itBody ->
                                    var dataString = itBody.data
                                    val dataclass = Gson().fromJson(dataString, Datax::class.java)
                                    var x : Boolean = dataclass.dus!!.toInt() > 70
                                    var y : Boolean = dataclass.hum!!.toInt() > 50
                                    var z : Boolean = dataclass.cur!!.toInt() < 600
                                    var w : Boolean = dataclass.cur!!.toInt() > 1500
                                    var t : Boolean = dataclass.timetem!!.toInt() > 900
                                    var k : Boolean = dataclass.timedus!!.toInt() > 900
                                    var state : String = "${x.toInt()}${y.toInt()}${z.toInt()}${w.toInt()}${t.toInt()}${k.toInt()}"
                                    var error : String = ""
                                    var tip : String = ""
                                    if (state != "000000") {
                                        icon.visibility = View.VISIBLE
                                        when(state) {
                                            "100000" -> {
                                                error = _100000_
                                                tip= _100000}
                                            "010000" -> {
                                                error = _010000_
                                                tip= _010000
                                            }
                                            "001000" -> {
                                                error = _001000_
                                                tip= _001000
                                            }
                                            "000100" -> {
                                                error = _000100_
                                                tip = _000100
                                            }
                                            "000010" -> {
                                                error = _000010_
                                                tip = _000010
                                            }
                                            "000001" -> {
                                                error = _000001_
                                                tip = _000001
                                            }
                                            "110000" -> {
                                                error = _110000_
                                                tip = _110000
                                            }
                                            "101000" -> {
                                                error = _101000_
                                                tip = _101000
                                            }
                                            "100100" -> {
                                                error = _100100_
                                                tip = _100100
                                            }
                                            "100010" -> {
                                                error = _100010_
                                                tip = _100010
                                            }
                                            "100001" -> {
                                                error = _100001_
                                                tip = _100001
                                            }
                                            "011000" -> {
                                                error = _011000_
                                                tip = _011000
                                            }
                                            "010100" -> {
                                                error = _010100_
                                                tip = _010100
                                            }
                                            "010010" -> {
                                                error = _010010_
                                                tip = _010010
                                            }
                                            "010001" -> {
                                                error = _010001_
                                                tip = _010001
                                            }
                                            "001010" -> {
                                                error = _001010_
                                                tip = _001010
                                            }
                                            "001001" -> {
                                                error = _001001_
                                                tip = _001001
                                            }
                                            "000110" -> {
                                                error = _000110_
                                                tip = _000110
                                            }
                                            "000101" -> {
                                                error = _000101_
                                                tip = _000101
                                            }
                                            "000011" -> {
                                                error = _000011_
                                                tip = _000011
                                            }
                                            "111000" -> {
                                                error = _111000_
                                                tip = _111000
                                            }
                                            "110100" -> {
                                                error = _110100_
                                                tip = _110100
                                            }
                                            "110010" -> {
                                                error = _110010_
                                                tip = _110010
                                            }
                                            "110001" -> {
                                                error = _110001_
                                                tip = _110001
                                            }
                                            "101010" -> {
                                                error = _101010_
                                                tip = _101010
                                            }
                                            "101001" -> {
                                                error = _101001_
                                                tip = _101001
                                            }
                                            "100110" -> {
                                                error = _100110_
                                                tip = _100110
                                            }
                                            "100101" -> {
                                                error = _100101_
                                                tip = _100101
                                            }
                                            "100011" -> {
                                                error = _100011_
                                                tip = _100011
                                            }
                                            "011010" -> {
                                                error = _011010_
                                                tip = _011010
                                            }
                                            "011001" -> {
                                                error = _011001_
                                                tip = _011001
                                            }
                                            "010110" -> {
                                                error = _010110_
                                                tip = _010110
                                            }
                                            "010101" -> {
                                                error = _010101_
                                                tip = _010101
                                            }
                                            "001011" -> {
                                                error = _001011_
                                                tip = _001011
                                            }
                                            "000111" -> {
                                                error = _000111_
                                                tip = _000111
                                            }
                                            "111010" -> {
                                                error = _111010_
                                                tip = _111010
                                            }
                                            "111001" -> {
                                                error = _111001_
                                                tip = _111001
                                            }
                                            "110110" -> {
                                                error = _110110_
                                                tip = _110110
                                            }
                                            "110101" -> {
                                                error = _110101_
                                                tip = _110101
                                            }
                                            "011011" -> {
                                                error = _011011_
                                                tip = _011011
                                            }
                                            "010111" -> {
                                                error = _010111_
                                                tip = _010111
                                            }
                                            "111011" -> {
                                                error = _111011_
                                                tip = _111011
                                            }
                                            "110111" -> {
                                                error = _110111_
                                                tip = _110111
                                            }
                                            else -> {
                                                error = "..."
                                                tip = "..."
                                            }
                                        }
                                        var intent: Intent = Intent(applicationContext, MyService::class.java)
                                        var bundle: Bundle = Bundle()
                                        bundle.putString("error", error)
                                        bundle.putString("tip", tip)
                                        intent.putExtras(bundle)
                                        startService(intent)
                                        Thread {
                                            Thread.sleep(10000)
                                            stopService(intent)
                                        }.start()
                                    }
                                    else {
                                        icon.visibility = View.GONE
                                    }

                                }
                            }
                            in 300..399 -> {
                                Log.d("Response Code", " Redirection messages : ${response.code()}")
                            }
                            in 400..499 -> {
                                Log.d("Response Code", " Client error responses : ${response.code()}")
                            }
                            in 500..599 -> {
                                Log.d("Response Code", " Server error responses : ${response.code()}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<database>, t: Throwable) {
                        Log.e("onFailure", "Err : ${t.message}")
                    }
                })
                nhandler.postDelayed(this, 5000)
            }

        }
        runnable.run()
    }
    inner class Datax {
        var tem: String? = null
        var hum: String? = null
        var cur: String? = null
        var dus: String? = null
        var timetem: String? = null
        var timedus: String? = null
        var power: String? = null

        override fun toString(): String {
            return "Data(tem=$tem, hum=$hum, cur=$cur, dus=$dus, timetem=$timetem, timedus=$timedus, power=$power)"
        }
    }
    fun Boolean.toInt() = if (this) 1 else 0
}