package com.github.akvast.mvvm.ui

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.github.akvast.mvvm.R
import com.github.akvast.mvvm.databinding.ActivityMainBinding
import com.github.akvast.mvvm.ui.adapter.MainAdapter
import com.github.akvast.mvvm.ui.adapter.NavigationAdapter

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val drawerToggle = ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.mainAdapter = MainAdapter
        binding.navigationAdapter = NavigationAdapter
    }

}