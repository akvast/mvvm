package com.github.akvast.mvvm.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
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

    override fun onResume() {
        super.onResume()
        MainAdapter.reload()
        NavigationAdapter.reload()
    }

}