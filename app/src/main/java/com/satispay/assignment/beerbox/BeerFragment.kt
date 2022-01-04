package com.satispay.assignment.beerbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.satispay.assignment.beerbox.databinding.FragmentBeerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BeerFragment: Fragment() {

    private val viewModel: BeerViewModel by activityViewModels()
    private lateinit var binding: FragmentBeerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadBeers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentBeerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.beers.observe(this, {

        })

        viewModel.toastMessage.observe(this, {
            Toast.makeText(this@BeerFragment.requireContext().applicationContext,
                it,
                Toast.LENGTH_SHORT
            ).show()
        })

        binding.buttonToggleLayout.forEach { childView ->
            (childView as? Button)?.setOnClickListener {
                clearAllActivated()
                it.isActivated = true
                // Add more logic here.
            }
        }

        binding.scrollviewFilterButtons.isHorizontalScrollBarEnabled = false
    }

    private fun clearAllActivated() {
        binding.buttonToggleLayout.forEach {
            (it as? Button)?.isActivated = false
        }
    }
}