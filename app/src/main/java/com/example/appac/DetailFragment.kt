package com.example.appac

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.androidplot.pie.PieChart
import com.androidplot.pie.Segment
import com.androidplot.pie.SegmentFormatter
import com.androidplot.xy.*
import com.example.appac.HomeFragment.Companion.day
import com.example.appac.LoginAct.Companion.shared
import com.example.appac.database.graphday
import com.example.appac.databinding.FragmentDetailBinding
import com.example.movieapp.API.ApiServices
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Constants.API_KEY
import com.example.movieapp.utils.Constants.max
import com.example.myapplication.API.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailFragment : Fragment() {
    private lateinit var binding : FragmentDetailBinding

    private val api: ApiServices by lazy {
        ApiClient().getClient().create(ApiServices::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater)
        val view : View = binding.getRoot()
        Constants.API_KEY = shared.getString("id", "12")
        var power : Int? = null
        var listData : MutableList<Number> = mutableListOf()
        var temData : MutableList<Number> = mutableListOf()
        var humData : MutableList<Number> = mutableListOf()
        var curData : MutableList<Number> = mutableListOf()
        var listTime : MutableList<String> = mutableListOf()
        val callApi = api.getDayData("2023-03-28T")
        callApi.enqueue(object : Callback<graphday> {
            override fun onResponse(call: Call<graphday>, response: Response<graphday>) {
                Log.e("onFailure", "Err : ${response.code()}")

                when (response.code()) {
                    in 200..299 -> {
                        response.body()?.let { itBody ->
                            var dataString = itBody.data
                            try {
                                for (i in 0..(dataString.size-1)) {
                                    var dataday = dataString[i].data
                                    var dataclass = Gson().fromJson(dataday, DataDay::class.java)
                                    listData.add(dataclass.dus!!.toInt())
                                    temData.add(dataclass.tem!!.toInt())
                                    humData.add(dataclass.hum!!.toInt())
                                    curData.add(dataclass.cur!!.toInt())
                                    listTime.add(FormatTime(dataString[i].createdAt).substring(11,19))
                                    if (i == (dataString.size - 1)) {
                                        power = dataclass.power!!.toInt()
                                    }
                                }
                            }
                            catch (e : NullPointerException) {
                                Toast.makeText(context, "Vui lòng đợi mạng!!!", Toast.LENGTH_SHORT).show()
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

            override fun onFailure(call: Call<graphday>, t: Throwable) {
                Log.e("onFailure", "Err : ${t.message}")
            }
        })
        binding.btnXYdus.setOnClickListener() {
            XYplotChart(listData, "nồng độ bụi", "mg/m3", listTime)
        }
        binding.btnXYtem.setOnClickListener() {
            XYplotChart(temData, "nhiệt độ", "℃", listTime)
        }
        binding.btnXYhum.setOnClickListener() {
            XYplotChart(humData, "độ ẩm", "%", listTime)
        }
        binding.btnXYcurrent.setOnClickListener() {
            XYplotChart(curData, "điện thế dòng", "V", listTime)
        }
        if (power == null) {
            power = 0
        }
        binding.btnXYpower.setOnClickListener() {
            PieChart(power!!, "điện thế dòng", "V")
        }

        return view
    }
    inner class DataDay {
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

    fun XYplotChart(data : MutableList<Number>, name : String, type : String, listTime : MutableList<String>) {
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dust_chart)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dusChart : XYSeries = SimpleXYSeries(data, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, name)
        val plot = dialog.findViewById<XYPlot>(R.id.plotchart)
        val btnclose = dialog.findViewById<Button>(R.id.close)
        val series1Format = LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels)
        plot.setTitle("Biểu đồ " + name)
        plot.setRangeLabel(type)
        series1Format.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
        plot.addSeries(dusChart, series1Format)
        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format =
            object : Format() {
                override fun format(
                    obj: Any,
                    toAppendTo: StringBuffer,
                    pos: FieldPosition
                ): StringBuffer {
                    val i = Math.round((obj as Number).toFloat())
                    return toAppendTo.append(listTime[i])
                }

                override fun parseObject(
                    source: String,
                    pos: ParsePosition
                ): Any? {
                    return null
                }
            }
        PanZoom.attach(plot)
        btnclose.setOnClickListener() {
            dialog.dismiss()
        }
        dialog.show()
    }
    fun PieChart(data : Int, name : String, type : String) {
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.pie_chart)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val pie = dialog.findViewById<PieChart>(R.id.pie)
        val btnclose = dialog.findViewById<Button>(R.id.close)
        val max = dialog.findViewById<EditText>(R.id.energy_set)
        val set = dialog.findViewById<ImageButton>(R.id.set)
        set.setOnClickListener() {
            if (max.text.toString().toInt() < 10 || max.text.toString() == "") {
                Toast.makeText(context, "Vui lòng nhập giá trị lớn hơn bằng 10!", Toast.LENGTH_SHORT).show()
                max.setText("")
            }
            else {
                val editor = shared.edit()
                editor.putInt("max", max.text.toString().toInt())
                editor.apply()
                editor.commit()
                max.setText("")
                Toast.makeText(context, "Đã lưu!", Toast.LENGTH_SHORT).show()
            }
        }
        val s1 = Segment("Đã sử dụng", data)
        val s2 = Segment("Còn lại", Constants.max)
        val sf1 = SegmentFormatter(Color.RED)
        val sf2 = SegmentFormatter(Color.GREEN)
        pie.addSegment(s1, sf1)
        pie.addSegment(s2, sf2)
        btnclose.setOnClickListener() {
            dialog.dismiss()
        }
        dialog.show()
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
}