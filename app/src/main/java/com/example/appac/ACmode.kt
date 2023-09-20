package com.example.appac

import android.app.Service
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.example.appac.API.PostClient
import com.example.appac.HomeFragment.Companion.ON_KEY
import com.example.appac.database.dataP
import com.example.appac.databinding.AcSettingBinding
import com.example.appac.databinding.AcTypeBinding
import com.example.movieapp.API.ApiServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ACmode : DialogFragment() {
    private lateinit var binding : AcSettingBinding
    private val put: ApiServices by lazy {
        PostClient().getClient().create(ApiServices::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AcSettingBinding.inflate(layoutInflater)
        val view : View = binding.getRoot()
        var curtem = 24

        binding.uptem.setOnClickListener() {
            if (curtem > 30) {
                curtem = 30
            }
            if (curtem < 16) {
                curtem = 16
            }
            if (curtem >= 16 && curtem < 30) {
                curtem = curtem + 1
            }
            binding.temcur.setText("$curtem℃")
        }
        binding.downtem.setOnClickListener() {
            if (curtem < 16) {
                curtem = 16
            }
            if (curtem > 16 && curtem <= 30) {
                curtem = curtem - 1
            }
            binding.temcur.setText("$curtem℃")
        }
        binding.buttonCancel.setOnClickListener() {
            dismiss()
        }
        binding.buttonSave.setOnClickListener() {
            dismiss()
        }
        return view
    }
}