package com.example.appac

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.appac.API.PostClient
import com.example.appac.HomeFragment.Companion.ON_KEY
import com.example.appac.database.dataP
import com.example.appac.databinding.AcTypeBinding
import com.example.movieapp.API.ApiServices
import com.example.movieapp.utils.Constants.API_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ACtype : DialogFragment() {
    private lateinit var binding : AcTypeBinding
    private val put: ApiServices by lazy {
        PostClient().getClient().create(ApiServices::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AcTypeBinding.inflate(layoutInflater)
        val view : View = binding.getRoot()
        binding.buttonCancel.setOnClickListener() {
            dismiss()
        }
        binding.buttonSave.setOnClickListener() {
            try {
                var selectID = binding.radioGroup.checkedRadioButtonId
                val radio = view.findViewById<RadioButton>(selectID)
                var typeac = radio.text.toString()
                val editor = LoginAct.shared.edit()
                ON_KEY = "{\"type\": \"$typeac\",\"enable\": \"${HomeFragment.onoff}\"}"
                val putApi = put.controlDt(API_KEY.toString())
                putApi.enqueue(object : Callback<dataP> {
                    override fun onResponse(call: Call<dataP>, response: Response<dataP>) {
                        Log.e("onFailure", "Err : ${response.code()}")
                    }

                    override fun onFailure(call: Call<dataP>, t: Throwable) {
                        Log.e("onFailure", "Err : ${t.message}")
                    }

                })
                editor.clear()
                editor.commit()
                editor.putString("type", typeac)
                editor.apply()
                editor.commit()
                SettingFragment.binding.actype.setText(LoginAct.shared.getString("type", "..."))
            }
            catch (e:Exception) {
                Toast.makeText(context, "Vui lòng chọn loại điều hòa", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
        return view
    }
}