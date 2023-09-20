package com.example.appac

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appac.LoginAct.Companion.shared
import com.example.appac.databinding.NotificationLayoutBinding
import com.example.movieapp.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

import java.util.*

class MainActivity2 : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var btnStart: ImageButton
    private lateinit var dialog: AlertDialog
    companion object {
        var error = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Constants.API_KEY = shared.getString("id", "12")
        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottomNV)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        btnStart = findViewById(R.id.btnStart)
        btnStart.setOnClickListener() {
            if(checkOverlayPermission()) {
                startService(Intent(this@MainActivity2, FloatingIcon::class.java))
                finish()
            }else {
                requestFloatingIcon()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.home -> {
                navController.navigate(R.id.home)
                return true
            }
            R.id.graph -> {
                navController.navigate(R.id.graph)
                return true
            }
            R.id.settings -> {
                navController.navigate(R.id.settings)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun isServiceRunning(): Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (FloatingIcon::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }
    private fun requestFloatingIcon(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Screen Overlay Permission Needed")
        builder.setMessage("Enable 'Display over the App' from settings")
        builder.setPositiveButton("Open Settings", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, RESULT_OK)
        })
        if ("xiaomi" == Build.MANUFACTURER.toLowerCase(Locale.ROOT)) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity")
            intent.putExtra("extra_pkgname", getPackageName())
            startActivity(intent)
        }
        dialog = builder.create()
        dialog.show()
    }
    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        }
        else return true
    }
}