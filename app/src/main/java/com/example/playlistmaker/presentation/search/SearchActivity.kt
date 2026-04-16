package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.network.Track

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }

    private var searchText: String = ""

    private lateinit var toolbar: Toolbar
    private lateinit var searchField: EditText
    private lateinit var clearButton: ImageView
    private lateinit var tracksRecyclerView: RecyclerView

    private var trackList = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            windowInsets
        }

        toolbar = findViewById(R.id.search_toolbar)
        searchField = findViewById(R.id.search_field)
        clearButton = findViewById(R.id.search_field_clear)
        tracksRecyclerView = findViewById(R.id.search_recycler_view)

        setupToolBar()
        setupSearch()
        setupClear()
        setupTracksRecycler()
    }

    private fun setupToolBar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSearch() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        searchField.addTextChangedListener(searchTextWatcher)
    }

    private fun setupClear() {
        clearButton.setOnClickListener {
            searchField.text.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchField.windowToken, 0)
            searchField.clearFocus()
        }
    }

    private fun setupTracksRecycler() {
        tracksRecyclerView.adapter = TrackAdapter(trackList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")

        searchField.setText(searchText)
    }

}