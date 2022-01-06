package com.satispay.assignment.beerbox

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.satispay.assignment.beerbox.databinding.BeerBottomLayoutBinding
import com.satispay.assignment.beerbox.databinding.FragmentBeerBinding
import com.satispay.assignment.beerbox.model.Beer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BeerFragment : Fragment() {

    private val viewModel: BeerViewModel by activityViewModels()
    private lateinit var binding: FragmentBeerBinding
    private lateinit var adapter: BeerAdapter
    private var currentBottomSheet: BottomSheetDialog? = null
    private val recyclerViewScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!recyclerView.canScrollVertically(1) &&
                !adapter.isContentLoading) {

                CoroutineScope(Dispatchers.Main).launch {
                    adapter.showContentLoading()
                    viewModel.loadNextPage()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadBeers()
        viewModel.defaultImage = BitmapFactory.decodeResource(resources, R.mipmap.ic_missing_image)
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
        adapter = BeerAdapter(
            requireContext().applicationContext,
            viewModel,
            emptyList<BeerAdapterItem>().toMutableList()
        )

        binding.progressCircular.visibility = View.VISIBLE
        binding.recyclerViewBeer.adapter = adapter
        binding.recyclerViewBeer.addOnScrollListener(recyclerViewScrollListener)

        viewModel.beersAdapterItems.observe(this, { beerAdapterItems ->
            if(viewModel.currentPage == 1) {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerViewBeer.visibility = View.VISIBLE
            }

            if(viewModel.currentPage > 1 && adapter.dataSet.isNotEmpty()) {
                adapter.hideContentLoading()
            }

            beerAdapterItems.forEach { item ->
                val beerInAdapter =
                    adapter.dataSet.firstOrNull { it.beer.id == item.beer.id }
                val position = item.beer.id - 1

                if (beerInAdapter == null) {
                    adapter.dataSet.add(position, item)
                    adapter.notifyItemInserted(position)
                } else if (item != beerInAdapter) {
                    adapter.dataSet[position] = item
                    adapter.notifyItemChanged(position)
                }
            }
        })

        viewModel.beerDetailed.observe(
            this,
            {
                it.first?.let { beer ->
                    showDetails(beer, it.second, view as ViewGroup)
                }
            })

        viewModel.toastMessage.observe(this, {
            Toast.makeText(
                this@BeerFragment.requireContext().applicationContext,
                it,
                Toast.LENGTH_SHORT
            ).show()
        })

        viewModel.filteredBeersAdapterItems.observe(this, {
            adapter.onFiltered(it)
        })

        viewModel.nothingMatchesVisibilityText.observe(this, {
            binding.recyclerViewBeer.visibility = if(it) View.INVISIBLE else View.VISIBLE
            binding.nothingMatchesText.visibility = if(it) View.VISIBLE else View.GONE
        })

        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val selectedToggle = binding.buttonToggleLayout.children
                    .filterIsInstance(Button::class.java)
                    .firstOrNull { it.text.toString().lowercase() == s.toString().lowercase() }

                clearAllActivatedTogglesExcept(selectedToggle)
                selectedToggle?.isActivated = true

                if(s.isNullOrBlank()) {
                    binding.recyclerViewBeer.addOnScrollListener(recyclerViewScrollListener)

                } else {
                    binding.recyclerViewBeer.removeOnScrollListener(recyclerViewScrollListener)
                }

                if(s.isNullOrBlank() || s.length > 3) {
                    adapter.filter.filter(s)
                }
            }
        })

        binding.buttonToggleLayout.forEach { childView ->
            (childView as? Button)?.setOnClickListener {
                if(!it.isActivated) {
                    binding.searchText.setText((it as Button).text)
                } else {
                    binding.searchText.setText("")
                }
            }
        }

        binding.scrollviewFilterButtons.isHorizontalScrollBarEnabled = false
    }

    private fun showDetails(beer: Beer, bitmap: Bitmap?, rootView: ViewGroup) {
        val binding = BeerBottomLayoutBinding.inflate(layoutInflater, rootView, false)

        currentBottomSheet = BottomSheetDialog(requireContext())
        currentBottomSheet?.apply {
            setContentView(binding.root)
            setOnCancelListener {
                currentBottomSheet = null
                viewModel.hideDetails()
            }
            binding.beer = beer
            binding.image.setImageBitmap(bitmap)
        }?.show()
    }

    private fun clearAllActivatedTogglesExcept(exceptView: View?) {
        binding.buttonToggleLayout.forEach {
            if(it !== exceptView) {
                (it as? Button)?.isActivated = false
            }
        }

        adapter.filter.filter("")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        currentBottomSheet?.cancel()
    }
}