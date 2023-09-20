package com.example.appac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.appac.API.ConfirmClient
import com.example.appac.LoginAct.Companion.shared
import com.example.appac.database.dataP
import com.example.appac.databinding.FragmentLoginBinding
import com.example.movieapp.API.ApiServices
import com.example.movieapp.utils.Constants
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
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
        binding = FragmentLoginBinding.inflate(layoutInflater)
        val view : View = binding.root
        binding.progressbar.visibility = View.GONE
        if (Constants.autoLogin == 1) {
            val intent = Intent(context, MainActivity2::class.java)
            startActivity(intent)
            activity?.finish()
        }
        binding.login.setOnClickListener {
            Login()
        }
        binding.hidden.setOnClickListener {
            RegisterFragment.Hidden(binding.idPassword)
        }
        binding.qrLogin.setOnClickListener {
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

    private fun ErrorId() {
        Toast.makeText(
            context,
            "Tài khoản không tồn tại!",
            Toast.LENGTH_SHORT
        ).show()
        binding.progressbar.visibility = View.GONE
    }
    inner class Datpass {
        var password: String? = null

        override fun toString(): String {
            return "Data(password=$password)"
        }
    }
    private fun Login() {
            binding.progressbar.visibility = View.VISIBLE
            val getApi = get.confirm(binding.idLogin.text.toString())
            getApi.enqueue(object : Callback<dataP> {
                override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                    Log.e("onFailure", "Err : ${response.code()}")
                    when (response.code()) {
                        in 200..299 -> {
                            response.body()?.let { itBody ->
                                if (itBody.data.reported == "{}") {
                                    Toast.makeText(
                                        context,
                                        "Tài khoản chưa được đăng ký!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.progressbar.visibility = View.GONE
                                } else {
                                    var dataString = itBody.data.reported
                                    val dataclass = Gson().fromJson(dataString, Datpass::class.java)
                                    if (binding.idPassword.text.toString() == dataclass.password) {
                                        val intent = Intent(context, MainActivity2::class.java)
                                        val editor = shared.edit()
                                        editor.putString("id", binding.idLogin.text.toString())
                                        editor.apply()
                                        editor.commit()
                                        startActivity(intent)
                                        activity?.finish()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Sai mật khẩu vui lòng thử lại!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    binding.progressbar.visibility = View.GONE
                                }
                            }
                        }
                        in 300..399 -> {
                            ErrorId()
                            Log.d("Response Code", " Redirection messages : ${response.code()}")
                        }
                        in 400..499 -> {
                            ErrorId()
                            Log.d("Response Code", " Client error responses : ${response.code()}")
                        }
                        in 500..599 -> {
                            ErrorId()
                            Log.d("Response Code", " Server error responses : ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<dataP>, t: Throwable) {
                    ErrorId()
                    Log.e("onFailure", "Err : ${t.message}")
                }
            })
        Check()
    }
    private fun Check() {
        if (binding.checkBox.isChecked) {
            Constants.save = 1
        }
        else {
            Constants.save = 0
        }
        val editor =shared.edit()
        editor.putInt("save", Constants.save)
        editor.apply()
        editor.commit()
    }

}