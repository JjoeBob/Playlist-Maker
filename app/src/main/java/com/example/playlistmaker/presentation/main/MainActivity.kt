package com.example.playlistmaker.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.library.LibraryActivity
import com.example.playlistmaker.presentation.search.SearchActivity
import com.example.playlistmaker.presentation.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            windowInsets
        }

        val searchButton = findViewById<Button>(R.id.main_search_button)
        val libraryButton = findViewById<Button>(R.id.main_library_button)
        val settingsButton = findViewById<Button>(R.id.main_settings_button)

        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        val libraryButtonOnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val libraryIntent = Intent(this@MainActivity, LibraryActivity::class.java)
                startActivity(libraryIntent)
            }
        }
        libraryButton.setOnClickListener(libraryButtonOnClickListener)

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}