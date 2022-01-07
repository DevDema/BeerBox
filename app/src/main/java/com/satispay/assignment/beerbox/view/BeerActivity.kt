package com.satispay.assignment.beerbox.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satispay.assignment.beerbox.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BeerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}