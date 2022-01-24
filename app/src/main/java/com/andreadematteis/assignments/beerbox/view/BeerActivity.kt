package com.andreadematteis.assignments.beerbox.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andreadematteis.assignments.beerbox.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BeerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}