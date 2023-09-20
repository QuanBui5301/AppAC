package com.example.appac

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.example.appac.API.ConfirmClient
import com.example.appac.API.PostClient
import com.example.appac.API.RegisterClient
import com.example.appac.LoginAct.Companion.shared
import com.example.appac.RegisterFragment.Companion.Hidden
import com.example.appac.database.dataP
import com.example.appac.databinding.FragmentSettingBinding
import com.example.appac.databinding.ResetFragBinding
import com.example.movieapp.API.ApiServices
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Constants.API_KEY
import com.google.gson.Gson

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


class SettingFragment : Fragment() {

    private val put: ApiServices by lazy {
        PostClient().getClient().create(ApiServices::class.java)
    }
    private val reset: ApiServices by lazy {
        RegisterClient().getClient().create(ApiServices::class.java)
    }
    private val confirm: ApiServices by lazy {
        ConfirmClient().getClient().create(ApiServices::class.java)
    }
    private var mhandler : Handler = Handler()
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentSettingBinding
        @SuppressLint("StaticFieldLeak")
        lateinit var bindings: ResetFragBinding
    }
    lateinit var id : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        val view : View = binding.getRoot()
        binding.btnQr.setOnClickListener() {
            HelpCenter()
        }
        if (Constants.autoLogin == 1) {
            binding.btncheck.isChecked = true
        }
        binding.btnarea.setOnClickListener() {
            btnLocation()
        }
        binding.btncheck.setOnClickListener() {
            if (binding.btncheck.isChecked) {
                Constants.save = 1
                Constants.autoLogin = 1
            }
            else {
                Constants.save = 0
                Constants.autoLogin = 0
            }
            val editor =shared.edit()
            editor.putInt("save", Constants.save)
            editor.putInt("autoLogin", Constants.autoLogin)
            editor.apply()
            editor.commit()
        }
        binding.actype.setText(shared.getString("ACname", "..."))
        binding.btnType.setOnClickListener() {
            btnType()
        }
        binding.btnExit.setOnClickListener() {
            getActivity()?.finish()
        }
        binding.btnreset.setOnClickListener() {
            ResetP()
        }
        binding.qrcode.setText(shared.getString("id", "..."))
        return view
    }

    private fun btnLocation() {
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.location)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btncancel = dialog.findViewById<ImageButton>(R.id.buttonCancel1)
        val btnsave = dialog.findViewById<ImageButton>(R.id.buttonSave1)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup1)
        btncancel.setOnClickListener() {
            dialog.dismiss()
        }
        btnsave.setOnClickListener() {
            try {
                var selectID = radioGroup.checkedRadioButtonId
                val radio = dialog.findViewById<RadioButton>(selectID)
                var idAC = radio.hint.toString()
                val editor = shared.edit()
                editor.putString("location", idAC)
                editor.apply()
                editor.commit()
            }
            catch (e:Exception) {
                Toast.makeText(context, "Vui lòng hoàn thành lựa chọn!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun btnType() {
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.ac_type)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btncancel = dialog.findViewById<ImageButton>(R.id.buttonCancel)
        val btnsave = dialog.findViewById<ImageButton>(R.id.buttonSave)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        btncancel.setOnClickListener() {
            dialog.dismiss()
        }
        btnsave.setOnClickListener() {
            try {
                var selectID = radioGroup.checkedRadioButtonId
                val radio = dialog.findViewById<RadioButton>(selectID)
                var typeac = radio.text.toString()
                var idAC = radio.hint.toString()
                val editor = shared.edit()
                HomeFragment.ON_KEY = "{\"tem\": \"${shared.getInt("curtem", 24)}\",\"auto\": \"${HomeFragment.auto}\",\"mode\": \"${shared.getInt("curmode", 0)}\",\"type\": \"$idAC\",\"wind\": \"${shared.getInt("curtype", 0)}\",\"speed\": \"${shared.getInt("curspeed",0)}\",\"enable\": \"${HomeFragment.onoff}\",\"airfilter\": \"${
                    shared.getInt(
                        "curdust",
                        0
                    )
                }\"}"
                val putApi = put.controlDt(API_KEY.toString())
                putApi.enqueue(object : Callback<dataP> {
                    override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                        Log.e("onFailure", "Err : ${response.code()}")
                    }

                    override fun onFailure(call: Call<dataP>, t: Throwable) {
                        Log.e("onFailure", "Err : ${t.message}")
                    }

                })
                editor.putString("type", idAC)
                editor.putString("ACname", typeac)
                editor.apply()
                editor.commit()
                binding.actype.setText(shared.getString("ACname", "..."))
            }
            catch (e:Exception) {
                Toast.makeText(context, "Vui lòng chọn loại điều hòa", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun ResetP() {
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.reset_frag)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btncancel = dialog.findViewById<ImageButton>(R.id.btnclos)
        val btnsave = dialog.findViewById<Button>(R.id.register)
        val progB = dialog.findViewById<ProgressBar>(R.id.progressbar)
        val qrid = dialog.findViewById<ImageButton>(R.id.qrid)
        val id = dialog.findViewById<EditText>(R.id.idLogin1)
        val hidden1 = dialog.findViewById<ImageButton>(R.id.hidden1)
        val hidden2 = dialog.findViewById<ImageButton>(R.id.hidden2)
        val pOld = dialog.findViewById<EditText>(R.id.idPasswordOld)
        val pNew = dialog.findViewById<EditText>(R.id.idPasswordNew)
        progB.visibility = View.GONE
        btncancel.setOnClickListener() {
            dialog.dismiss()
        }
        hidden1.setOnClickListener() {
            Hidden(pOld)
        }
        hidden2.setOnClickListener() {
            Hidden(pNew)
        }
        qrid.setOnClickListener() {
            var intent : Intent = Intent(context, QrScanner::class.java)
            startActivityForResult(intent, 1)
        }
        btnsave.setOnClickListener() {
            progB.visibility = View.VISIBLE
            Confirm(id.text.toString(), pOld.text.toString(), pNew.text.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                var result : String = data!!.getStringExtra("result")!!
                bindings.apply {
                    idLogin1.setText(result)
                }
            }
        }
    }
    private fun Confirm(id : String, passwordOld : String, passwordNew : String) {
        val getApi = confirm.confirm(id)
        getApi.enqueue(object : Callback<dataP> {
            override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                Log.e("onFailure", "Err : ${response.code()}")
                when (response.code()) {
                    in 200..299 -> {
                        response.body()?.let { itBody ->
                            var dataString = itBody.data.reported
                            val dataclass = Gson().fromJson(dataString, LoginFragment.Datpass::class.java)
                            if (passwordOld == dataclass.password && passwordNew.length >= 8) {
                                Reset(id, passwordNew)
                            }
                            else {
                                if (passwordOld != dataclass.password) {
                                    Toast.makeText(
                                        context,
                                        "Nhập sai mật khẩu cũ vui lòng thử lại!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                if (passwordNew.length < 8) {
                                    Toast.makeText(
                                        context,
                                        "Vui lòng đặt mật khẩu có ít nhất 8 ký tự!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    in 300..399 -> {
                        Log.d("Response Code", " Redirection messages : ${response.code()}")
                        NetworkError()
                    }
                    in 400..499 -> {
                        Log.d("Response Code", " Client error responses : ${response.code()}")
                        NetworkError()
                    }
                    in 500..599 -> {
                        Log.d("Response Code", " Server error responses : ${response.code()}")
                        ServerError()
                    }
                }
            }

            override fun onFailure(call: Call<dataP>, t: Throwable) {
                Log.e("onFailure", "Err : ${t.message}")
                NetworkError()
            }
        })
    }
    private fun Reset(id : String, password : String) {
        Constants.Password = "{\"password\": \"$password\"}"
        val putApi = reset.controlDt(id)
        putApi.enqueue(object : Callback<dataP> {
            override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                Log.e("onFailure", "Err : ${response.code()}")
                Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<dataP>, t: Throwable) {
                Log.e("onFailure", "Err : ${t.message}")
                NetworkError()
            }
        })
    }
    private fun HelpCenter() {
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.helpcenter)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var i = 0
        var j = 0
        val close = dialog.findViewById<ImageButton>(R.id.close_help)
        val next = dialog.findViewById<ImageButton>(R.id.next)
        val back = dialog.findViewById<ImageButton>(R.id.back)
        val page = dialog.findViewById<ImageView>(R.id.image_help)
        when (i) {
            0 -> {
                back.visibility = View.INVISIBLE
                next.visibility = View.VISIBLE
            }
            5 -> {
                back.visibility = View.VISIBLE
                next.visibility = View.INVISIBLE
            }
            else -> {
                back.visibility = View.VISIBLE
                next.visibility = View.VISIBLE
            }
        }
        i = i.coerceIn(1, 5)
        next.setOnClickListener() {
            i += 1
            page.setImageResource(Constants.helpPage[i])
            when (i) {
                0 -> {
                    back.visibility = View.INVISIBLE
                    next.visibility = View.VISIBLE
                }
                5 -> {
                    back.visibility = View.VISIBLE
                    next.visibility = View.INVISIBLE
                }
                else -> {
                    back.visibility = View.VISIBLE
                    next.visibility = View.VISIBLE
                }
            }
        }
        back.setOnClickListener() {
            i -= 1
            page.setImageResource(Constants.helpPage[i])
            when (i) {
                0 -> {
                    back.visibility = View.INVISIBLE
                    next.visibility = View.VISIBLE
                }
                5 -> {
                    back.visibility = View.VISIBLE
                    next.visibility = View.INVISIBLE
                }
                else -> {
                    back.visibility = View.VISIBLE
                    next.visibility = View.VISIBLE
                }
            }
        }
        close.setOnClickListener() {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun NetworkError() {
        Toast.makeText(context, "Lỗi mạng vui lòng thử lại sau!", Toast.LENGTH_SHORT).show()
    }
    private fun ServerError() {
        Toast.makeText(context, "Server đang lỗi vui lòng thử lại sau!", Toast.LENGTH_SHORT).show()
    }
}
