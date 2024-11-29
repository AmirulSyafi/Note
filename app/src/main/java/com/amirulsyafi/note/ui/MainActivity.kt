package com.amirulsyafi.note.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.amirulsyafi.note.R
import com.amirulsyafi.note.databinding.ActivityMainBinding
import com.amirulsyafi.note.ui.note.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var noteViewModel: NoteViewModel
    private var mainBinding: ActivityMainBinding? = null
    private val binding get() = mainBinding!!

    private var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null
    private var navHostFragment: NavHostFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustDisplay(this)
        enableEdgeToEdge()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration.Builder(R.id.noteFragment).build()
        navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment?
        if (navHostFragment == null) return
        navController = navHostFragment!!.navController
        setupActionBarWithNavController(this, navController!!, appBarConfiguration!!)

        navController!!.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.noteDetailFragment) {
                supportActionBar!!.subtitle = null
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navController!!, appBarConfiguration!!) || super.onSupportNavigateUp()
    }

    fun adjustDisplay(activity: Activity) {
        val model = Build.MODEL
        if (model == "GABLET") {
            try {
                val metrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(metrics)
                metrics.density = 1.2f
                metrics.scaledDensity = 1.2f
                activity.resources.displayMetrics.setTo(metrics)
                //need delay for ui to scale properly
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
    }
}