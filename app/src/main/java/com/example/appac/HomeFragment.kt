package com.example.appac

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.number.Scale
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import coil.load
import com.example.appac.API.PostClient
import com.example.appac.API.WeatherClient
import com.example.appac.LoginAct.Companion.shared
import com.example.appac.database.dataP
import com.example.appac.database.weather_data
import com.example.appac.databinding.FragmentHomeBinding
import com.example.movieapp.API.ApiServices
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Constants.API_KEY
import com.example.movieapp.utils.Constants.curdust
import com.example.movieapp.utils.Constants.curmode
import com.example.movieapp.utils.Constants.curspeed
import com.example.movieapp.utils.Constants.curtem
import com.example.movieapp.utils.Constants.curtype
import com.example.movieapp.utils.Constants.dustMode
import com.example.movieapp.utils.Constants.loca
import com.example.movieapp.utils.Constants.speedMode
import com.example.movieapp.utils.Constants.typeMode
import com.example.movieapp.utils.Constants.typeWind
import com.example.myapplication.API.ApiClient
import com.example.myapplication.database.database
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val api: ApiServices by lazy {
        ApiClient().getClient().create(ApiServices::class.java)
    }
    private val put: ApiServices by lazy {
        PostClient().getClient().create(ApiServices::class.java)
    }
    private val wea: ApiServices by lazy {
        WeatherClient().getClient().create(ApiServices::class.java)
    }
    companion object {
        var ON_KEY = "{\"enable\": \"0\"}"
        var onoff = 0
        var auto = 0
        var day = ""
    }
    lateinit var btn_onoff : ImageButton
    lateinit var btn_more : ImageButton
    lateinit var temp : TextView
    lateinit var humd : TextView
    lateinit var dust : TextView
    lateinit var curr : TextView
    lateinit var time : TextView
    private var mhandler : Handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view : View = binding.getRoot()
        temp = view.findViewById(R.id.temp)
        humd = view.findViewById(R.id.humd)
        dust = view.findViewById(R.id.dust)
        curr = view.findViewById(R.id.current)
        time = view.findViewById(R.id.time)
        btn_onoff = view.findViewById(R.id.btnOnOff)
        btn_more = view.findViewById(R.id.btnMore)
        btn_more.setOnClickListener() {
            DialogSetting()
        }
        btn_onoff.setOnClickListener() {
            onoff()
        }
        binding.btnAuto.setOnClickListener() {
            Automode()
        }
        Constants.API_KEY = shared.getString("id", "12")
        if (Constants.save == 1) {
            Constants.autoLogin = 1
        }
        else {
            Constants.autoLogin = 0
        }
        val editor =shared.edit()
        editor.putInt("autoLogin", Constants.autoLogin)
        editor.apply()
        editor.commit()
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
                                    val dataclass = Gson().fromJson(dataString, Datass::class.java)
                                    temp.text = dataclass.tem!! + "℃"
                                    humd.text = dataclass.hum!! + "%"
                                    binding.current.text = dataclass.cur!! + "W"
                                    dust.text = dataclass.dus!! + "mg/m3"
                                    binding.timetem.text = dataclass.timetem!! + "giây"
                                    binding.timedus.text = dataclass.timedus!! + "giây"
                                    binding.power.text = dataclass.power!! + "kWh"
                                    if (dataclass.dus!!.toInt() > 70) {
                                        dust.setTextColor(Color.RED)
                                    }
                                    if (dataclass.cur!!.toFloat() < 600 || dataclass.cur!!.toFloat() > 1500) {
                                        curr.setTextColor(Color.RED)
                                    }
                                    if (dataclass.timedus!!.toInt() > 900) {
                                        binding.timedus.setTextColor(Color.RED)
                                    }
                                    if (dataclass.timetem!!.toInt() > 900) {
                                        binding.timetem.setTextColor(Color.RED)
                                    }
                                    if (dataclass.tem!!.toInt() > (shared.getInt("curtem", 24) + 1) || dataclass.tem!!.toInt() < (shared.getInt("tem", 24) - 1)) {
                                        temp.setTextColor(Color.RED)
                                    }
                                    if (dataclass.hum!!.toInt() > 50) {
                                        humd.setTextColor(Color.RED)
                                    }

                                    time.text = "Cập nhật lúc: " + FormatTime(itBody.createdAt)
                                    day = itBody.createdAt.substring(0,11)
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
                var location = shared.getString("location", "...")
                val callWea = wea.getWeaData(location!!,"yes", "yes")
                callWea.enqueue(object : Callback<weather_data> {


                    override fun onResponse(call: Call<weather_data>, response: Response<weather_data>) {
                        Log.e("onFailure", "Err : ${response.code()}")

                        when (response.code()) {
                            in 200..299 -> {
                                response.body()?.let { itBody ->
                                    binding.area.setText(itBody.location.name)
                                    binding.temoutside.setText(itBody.current.temp_c.toString()!! + "℃")
                                    binding.humoutside.setText(itBody.current.humidity.toString()!! + "%")
                                    binding.dusoutside.setText(itBody.current.air_quality.pm2_5.toString()!!.substring(0,3) + "mg/m3")
                                    val iconURL = "https:" + itBody.current.condition.icon
                                    binding.iconwea.load(iconURL){
                                        crossfade(true)
                                        placeholder(R.drawable.ic_baseline_wb_sunny_24)
                                        scale(coil.size.Scale.FILL)
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

                    override fun onFailure(call: Call<weather_data>, t: Throwable) {
                        Log.e("onFailure", "Err : ${t.message}")
                    }
                })
                mhandler.postDelayed(this, 5000)
            }

        }
        runnable.run()
        return view
    }
    inner class Datass {
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
    private fun FormatTime(date : String) : String {
            var newdate = date
            try {

                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value = formatter.parse(newdate.toString())
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a") //this format changeable
                dateFormatter.timeZone = TimeZone.getDefault()
                newdate = dateFormatter.format(value)
            } catch (e: Exception) {
                newdate = "00-00-0000 00:00"
            }
            return newdate
    }
    private fun DialogSetting() {
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.ac_setting)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val saveBtn = dialog.findViewById(R.id.buttonSave) as ImageButton
        val cancelBtn = dialog.findViewById(R.id.buttonCancel) as ImageButton
        val uptem = dialog.findViewById<ImageButton>(R.id.uptem)
        val downtem = dialog.findViewById<ImageButton>(R.id.downtem)
        val uptype = dialog.findViewById<ImageButton>(R.id.nexttype)
        val downtype = dialog.findViewById<ImageButton>(R.id.backtype)
        val upmode = dialog.findViewById<ImageButton>(R.id.upmode)
        val downmode = dialog.findViewById<ImageButton>(R.id.downmode)
        val typecur = dialog.findViewById<ImageView>(R.id.curtype)
        val temcur = dialog.findViewById<TextView>(R.id.temcur)
        val modecur = dialog.findViewById<ImageView>(R.id.curmode)
        val speedcur = dialog.findViewById<TextView>(R.id.curspeed)
        val upspeed = dialog.findViewById<ImageButton>(R.id.upspeed)
        val downspeed = dialog.findViewById<ImageButton>(R.id.downspeed)
        val dustcur = dialog.findViewById<TextView>(R.id.curdust)
        val ondust = dialog.findViewById<ImageButton>(R.id.onDust)
        val offdust = dialog.findViewById<ImageButton>(R.id.offdust)

        temcur.setText("${shared.getInt("curtem", 24)}℃")
        typecur.setImageResource(typeWind[shared.getInt("curtype", 0)])
        modecur.setImageResource(typeMode[shared.getInt("curmode", 0)])
        speedcur.setText(speedMode[shared.getInt("curspeed", 0)])
        dustcur.setText(dustMode[shared.getInt("curdust", 0)])

        uptem.setOnClickListener() {
            if (curtem > 30) {
                curtem = 30
            }
            if (curtem >= 16 && curtem < 30) {
                curtem += 1
            }
            temcur.setText("$curtem℃")
        }
        downtem.setOnClickListener() {
            if (curtem < 16) {
                curtem = 16
            }
            if (curtem > 16 && curtem <= 30) {
                curtem -= 1
            }
            temcur.setText("$curtem℃")
        }
        uptype.setOnClickListener() {
            if (curtype > 5) {
                curtype = 5
            }
            if (curtype >=0 && curtype < 5) {
                curtype += 1
            }
            typecur.setImageResource(typeWind[curtype])
        }
        downtype.setOnClickListener() {
            if (curtype < 0) {
                curtype = 0
            }
            if (curtype >0 && curtype <= 5) {
                curtype -= 1
            }
            typecur.setImageResource(typeWind[curtype])
        }
        upmode.setOnClickListener() {
            if (curmode > 3) {
                curmode = 3
            }
            if (curmode >=0 && curmode < 3) {
                curmode += 1
            }
            modecur.setImageResource(typeMode[curmode])
        }
        downmode.setOnClickListener() {
            if (curmode < 0) {
                curmode = 0
            }
            if (curmode >0 && curmode <= 3) {
                curmode -= 1
            }
            modecur.setImageResource(typeMode[curmode])
        }
        upspeed.setOnClickListener() {
            if (curspeed > 3) {
                curspeed = 3
            }
            if (curspeed >=0 && curspeed < 3) {
                curspeed += 1
            }
            speedcur.setText(speedMode[curspeed])
        }
        downspeed.setOnClickListener() {
            if (curspeed < 0) {
                curspeed = 0
            }
            if (curspeed >0 && curspeed <= 3) {
                curspeed -= 1
            }
            speedcur.setText(speedMode[curspeed])
        }
        ondust.setOnClickListener() {
            if (curdust > 1) {
                curdust = 1
            }
            if (curdust >=0 && curdust < 1) {
                curdust += 1
            }
            dustcur.setText(dustMode[curdust])
        }
        offdust.setOnClickListener() {
            if (curdust < 0) {
                curdust = 0
            }
            if (curdust >0 && curdust <= 1) {
                curdust -= 1
            }
            dustcur.setText(dustMode[curdust])
        }
        saveBtn.setOnClickListener {
            val editor =LoginAct.shared.edit()
            editor.putInt("curmode", curmode)
            editor.putInt("curtype", curtype)
            editor.putInt("curtem", curtem)
            editor.putInt("curspeed", curspeed)
            editor.putInt("curdust", curdust)
            editor.apply()
            editor.commit()
            ON_KEY = "{\"tem\": \"${shared.getInt("curtem", 24)}\",\"auto\": \"${auto}\",\"mode\": \"${shared.getInt("curmode", 0)}\",\"type\": \"${shared.getString("type", "10")}\",\"wind\": \"${shared.getInt("curtype", 0)}\",\"speed\": \"${shared.getInt("curspeed", 0)}\",\"enable\": \"${onoff}\",\"airfilter\": \"${shared.getInt("curdust", 0)}\"}"
            val putApi = put.controlDt(API_KEY.toString())
            putApi.enqueue(object : Callback<dataP> {
                override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                    Log.e("onFailure", "Err : ${response.code()}")
                }

                override fun onFailure(call: Call<dataP>, t: Throwable) {
                    Log.e("onFailure", "Err : ${t.message}")
                }

            })
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun onoff() {
        if (onoff == 0) {
            ON_KEY = "{\"tem\": \"${shared.getInt("curtem", 24)}\",\"auto\": \"${auto}\",\"mode\": \"${shared.getInt("curmode", 0)}\",\"type\": \"${shared.getString("type", "10")}\",\"wind\": \"${shared.getInt("curtype", 0)}\",\"speed\": \"${shared.getInt("curspeed",0)}\",\"enable\": \"${onoff + 1}\",\"airfilter\": \"${
                shared.getInt(
                    "curdust",
                    0
                )
            }\"}"
            onoff += 1
            binding.btnOnOff.setImageResource(R.drawable.ic_baseline_flash_on_24)
        }
        else {
            ON_KEY = "{\"tem\": \"${shared.getInt("curtem", 24)}\",\"auto\": \"${auto}\",\"mode\": \"${shared.getInt("curmode", 0)}\",\"type\": \"${shared.getString("type", "10")}\",\"wind\": \"${shared.getInt("curtype", 0)}\",\"speed\": \"${shared.getInt("curspeed",0)}\",\"enable\": \"${onoff - 1}\",\"airfilter\": \"${
                shared.getInt(
                    "curdust",
                    0
                )
            }\"}"
            onoff -= 1
            binding.btnOnOff.setImageResource(R.drawable.ic_baseline_flash_off_24)
        }
        val putApi = put.controlDt(API_KEY.toString())
        putApi.enqueue(object : Callback<dataP> {
            override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                Log.e("onFailure", "Err : ${response.code()}")
            }

            override fun onFailure(call: Call<dataP>, t: Throwable) {
                Log.e("onFailure", "Err : ${t.message}")
            }

        })
    }
    private fun Automode() {
        if (auto == 0) {
            ON_KEY = "{\"tem\": \"${shared.getInt("curtem", 24)}\",\"auto\": \"${auto + 1}\",\"mode\": \"${shared.getInt("curmode", 0)}\",\"type\": \"${shared.getString("type", "10")}\",\"wind\": \"${shared.getInt("curtype", 0)}\",\"speed\": \"${shared.getInt("curspeed",0)}\",\"enable\": \"${onoff}\",\"airfilter\": \"${
                shared.getInt(
                    "curdust",
                    0
                )
            }\"}"
            auto += 1
            binding.btnAuto.setImageResource(R.drawable.ic_baseline_do_disturb_alt_24)
            Toast.makeText(context, "Bật chế độ tự động", Toast.LENGTH_SHORT).show()
        }
        else {
            ON_KEY = "{\"tem\": \"${shared.getInt("curtem", 24)}\",\"auto\": \"${auto - 1}\",\"mode\": \"${shared.getInt("curmode", 0)}\",\"type\": \"${shared.getString("type", "10")}\",\"wind\": \"${shared.getInt("curtype", 0)}\",\"speed\": \"${shared.getInt("curspeed",0)}\",\"enable\": \"${onoff}\",\"airfilter\": \"${
                shared.getInt(
                    "curdust",
                    0
                )
            }\"}"
            auto -= 1
            binding.btnAuto.setImageResource(R.drawable.ic_baseline_auto_mode_24)
            Toast.makeText(context, "Tắt chế độ tự động", Toast.LENGTH_SHORT).show()
        }
        val putApi = put.controlDt(API_KEY.toString())
        putApi.enqueue(object : Callback<dataP> {
            override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                Log.e("onFailure", "Err : ${response.code()}")
            }

            override fun onFailure(call: Call<dataP>, t: Throwable) {
                Log.e("onFailure", "Err : ${t.message}")
            }

        })
    }
}