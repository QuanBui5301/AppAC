package com.example.appac

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginAct : AppCompatActivity() {
    companion object {
        lateinit var shared : SharedPreferences
    }
    private lateinit var dialog: AlertDialog
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottomLogin)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentLoginView) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        shared = getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.loginn -> {
                navController.navigate(R.id.loginn)
                return true
            }
            R.id.registerr -> {
                navController.navigate(R.id.registerr)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    open fun requestFloatingIcon(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Camera Permission Needed")
        builder.setMessage("Enable 'Camera' from settings")
        builder.setPositiveButton("Open Settings", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, RESULT_OK)
        })
        dialog = builder.create()
        dialog.show()
    }
}