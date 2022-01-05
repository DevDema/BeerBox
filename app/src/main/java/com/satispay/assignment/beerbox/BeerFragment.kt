package com.satispay.assignment.beerbox

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.satispay.assignment.beerbox.databinding.BeerBottomLayoutBinding
import com.satispay.assignment.beerbox.databinding.FragmentBeerBinding
import com.satispay.assignment.beerbox.model.Beer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BeerFragment: Fragment() {

    private val viewModel: BeerViewModel by activityViewModels()
    private lateinit var binding: FragmentBeerBinding
    private lateinit var adapter: BeerAdapter
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

        binding.recyclerViewBeer.layoutManager = LinearLayoutManager(requireContext())

        viewModel.beers.observe(this, {
            adapter = BeerAdapter(requireContext().applicationContext, viewModel, it)
            binding.recyclerViewBeer.adapter = adapter
        })

        viewModel.beerImage.observe(this, {
            adapter.onImageLoaded(it.first, it.second)
        })

        viewModel.beerDetailed.observe(this, { showDetails(it.first, it.second, view as ViewGroup) })

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

    private fun showDetails(beer: Beer, bitmap: Bitmap?, rootView: ViewGroup) {
        val binding = BeerBottomLayoutBinding.inflate(layoutInflater, rootView, false)

        BottomSheetDialog(requireContext()).apply {
            setContentView(binding.root)

            binding.beer = beer
            binding.image.setImageBitmap(bitmap)
        }.show()
    }

    private fun clearAllActivated() {
        binding.buttonToggleLayout.forEach {
            (it as? Button)?.isActivated = false
        }
    }
}