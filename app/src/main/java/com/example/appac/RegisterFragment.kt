package com.example.appac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.appac.API.ConfirmClient
import com.example.appac.API.PostClient
import com.example.appac.API.RegisterClient
import com.example.appac.database.dataP
import com.example.appac.databinding.FragmentLoginBinding
import com.example.appac.databinding.FragmentRegisterBinding
import com.example.movieapp.API.ApiServices
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Constants.API_KEY
import com.example.movieapp.utils.Constants.Password
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val put: ApiServices by lazy {
        RegisterClient().getClient().create(ApiServices::class.java)
    }
    private val get: ApiServices by lazy {
        ConfirmClient().getClient().create(ApiServices::class.java)
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
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        val view : View = binding.getRoot()
        binding.idLogin.text.clear()
        binding.idPassword.text.clear()
        binding.idPasswordAgain.text.clear()
        binding.progressbar.visibility = View.GONE
        var password1 = 0
        var password2 = 0
        binding.hiddeneye1.setOnClickListener() {
            Hidden(binding.idPassword)
        }
        binding.hiddeneye2.setOnClickListener() {
           Hidden(binding.idPasswordAgain)
        }
        binding.register.setOnClickListener() {
            binding.progressbar.visibility = View.VISIBLE
            val getApi = get.confirm(binding.idLogin.text.toString())
            getApi.enqueue(object : Callback<dataP> {
                override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                    Log.e("onFailure", "Err : ${response.code()}")
                    when (response.code()) {
                        in 200..299 -> {
                            response.body()?.let { itBody ->
                                if (itBody.data.reported == "{}") {
                                    Register()
                                    binding.progressbar.visibility = View.GONE
                                }
                                else {
                                    Toast.makeText(context, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show()
                                    binding.progressbar.visibility = View.GONE
                                }
                            }
                        }
                        in 300..399 -> {
                            ErrorRe()
                            Log.d("Response Code", " Redirection messages : ${response.code()}")
                        }
                        in 400..499 -> {
                            ErrorR()
                            Log.d("Response Code", " Client error responses : ${response.code()}")
                        }
                        in 500..599 -> {
                            ErrorRe()
                            Log.d("Response Code", " Server error responses : ${response.code()}")
                        }
                    }
                    Clear()
                }

                override fun onFailure(call: Call<dataP>, t: Throwable) {
                    Log.e("onFailure", "Err : ${t.message}")
                    ErrorRe()
                    Clear()
                }
            })

        }
        binding.qrRegister.setOnClickListener() {
            var intent : Intent = Intent(context, QrScanner::class.java)
            startActivityForResult(intent, 1)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                var result : String = data!!.getStringExtra("result")!!
                binding.idLogin.setText(result)
            }
        }
    }

    private fun Register() {
        if (binding.idPassword.text.toString() == binding.idPasswordAgain.text.toString() && binding.idPassword.text.length >= 8) {
            Password = "{\"password\": \"${binding.idPassword.text}\"}"
            val putApi = put.controlDt(binding.idLogin.text.toString())
            putApi.enqueue(object : Callback<dataP> {
                override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                    Log.e("onFailure", "Err : ${response.code()}")
                    binding.progressbar.visibility = View.GONE
                }

                override fun onFailure(call: Call<dataP>, t: Throwable) {
                    Log.e("onFailure", "Err : ${t.message}")
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(context, "Lỗi mạng vui lòng thử lại sau!", Toast.LENGTH_SHORT).show()
                }
            })
        }
        else {
            binding.progressbar.visibility = View.GONE
            if (binding.idPassword.text.length < 8) {
                Toast.makeText(context, "Vui lòng tạo mật khẩu từ 8 ký tự trở lên!", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Vui lòng nhập đúng yêu cầu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun ErrorRe() {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(context, "Lỗi mạng vui lòng thử lại sau!", Toast.LENGTH_SHORT).show()
    }
    private fun ErrorR() {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(context, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show()
    }
    private fun Clear() {
        binding.idLogin.text.clear()
        binding.idPassword.text.clear()
        binding.idPasswordAgain.text.clear()
    }
    companion object {
        fun Hidden(password : EditText) {
            if (password.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                password.inputType = InputType.TYPE_TEXT_VARIATION_NORMAL
            }
            else {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

}