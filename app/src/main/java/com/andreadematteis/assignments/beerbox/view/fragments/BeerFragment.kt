package com.andreadematteis.assignments.beerbox.view.fragments

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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andreadematteis.assignments.beerbox.R
import com.andreadematteis.assignments.beerbox.databinding.FragmentBeerBinding
import com.andreadematteis.assignments.beerbox.model.Beer
import com.andreadematteis.assignments.beerbox.view.fragments.moreFilters.FilterType
import com.andreadematteis.assignments.beerbox.view.fragments.moreFilters.MoreFilterSheetDialogFragment.Companion.SAVED_STATE_HANDLE_FILTER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class BeerFragment : Fragment() {

    private val viewModel: BeerViewModel by viewModels()
    private lateinit var binding: FragmentBeerBinding
    private lateinit var adapter: BeerAdapter
    private val recyclerViewScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!recyclerView.canScrollVertically(1)) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.loadNextPage()
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (s.isNullOrBlank()) {
                binding.moreFilters.text = getString(R.string.all_filters_label)

                adapter.filterType = FilterType.NAME
                adapter.filter.filter(s)
            } else if (s.length > 3) {
                binding.moreFilters.text = getString(R.string.all_filters_number_label, 1)

                adapter.filterType = FilterType.NAME
                adapter.filter.filter(s)
            }
        }
    }

    private fun selectToggle(s: String) {
        val selectedToggle = binding.buttonToggleLayout.children
            .filterIsInstance(Button::class.java)
            .firstOrNull { it.text.toString().lowercase() == s.lowercase() }

        clearAllActivatedTogglesExcept(selectedToggle)
        selectedToggle?.isActivated = true
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
        binding.nothingRetryButton.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            binding.recyclerViewBeer.visibility = View.INVISIBLE
            binding.nothingMatchesText.visibility = View.GONE
            binding.nothingRetryButton.visibility = View.GONE

            viewModel.loadBeers()
        }

        binding.buttonToggleLayout.forEach { childView ->
            (childView as? Button)?.setOnClickListener {
                if (!it.isActivated) {
                    binding.searchText.setText((it as Button).text)
                } else {
                    binding.searchText.setText("")
                }

                binding.searchText.clearFocus()
            }
        }

        binding.scrollviewFilterButtons.isHorizontalScrollBarEnabled = false

        viewModel.beersAdapterItems.observe(viewLifecycleOwner, { pair ->
            if (pair.first.isEmpty()) {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerViewBeer.visibility = View.VISIBLE
                binding.nothingMatchesText.visibility = View.VISIBLE
                binding.nothingRetryButton.visibility = View.VISIBLE

                binding.nothingMatchesText.text = getString(R.string.request_failed_beers)
                return@observe
            }

            binding.nothingRetryButton.visibility = View.GONE
            binding.progressCircular.visibility = View.GONE
            binding.recyclerViewBeer.visibility =
                if (pair.second.isEmpty()) View.INVISIBLE else View.VISIBLE
            binding.nothingMatchesText.visibility =
                if (pair.second.isEmpty()) View.VISIBLE else View.GONE
            binding.nothingMatchesText.text = getString(R.string.no_results_label)

            binding.recyclerViewBeer.visibility = View.VISIBLE


            adapter.dataSet = pair.first
            adapter.onFiltered(pair.second)

            selectToggle(binding.searchText.text.toString())

            binding.searchText.removeTextChangedListener(textWatcher)
            binding.searchText.addTextChangedListener(textWatcher)

            binding.recyclerViewBeer.removeOnScrollListener(recyclerViewScrollListener)

            if (pair.first.size == pair.second.size) {
                binding.recyclerViewBeer.addOnScrollListener(recyclerViewScrollListener)
            }
        })

        viewModel.beerDetailed.observe(
            this,
            {
                binding.searchText.clearFocus()

                it.first?.let { beer ->
                    showDetails(beer, it.second)
                }
            })

        viewModel.toastMessage.observe(viewLifecycleOwner, { string ->
            string.takeUnless { it.isEmpty() }?.let {
                Toast.makeText(
                    this@BeerFragment.requireContext().applicationContext,
                    it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.moreFilters.setOnClickListener {
            binding.searchText.clearFocus()
            openMoreFiltersDialog()
        }

        // Listen to events from bottom fragment destination

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Pair<FilterType, String>>(SAVED_STATE_HANDLE_FILTER)
            ?.observe(viewLifecycleOwner) { filterPair ->
                handleAllFiltersFeedback(filterPair)
            }
    }

    private fun handleAllFiltersFeedback(filterPair: Pair<FilterType, String>) =
        when (filterPair.first) {
            FilterType.NONE -> {
                binding.searchText.setText("")
                binding.searchTextInputLayout.isEnabled = true
                binding.buttonToggleLayout.forEach { it.isEnabled = true }
            }

            FilterType.NAME -> {
                binding.searchText.setText(filterPair.second)
                binding.searchTextInputLayout.isEnabled = true
                binding.buttonToggleLayout.forEach { it.isEnabled = true }

            }

            FilterType.TIMEFRAME -> {
                binding.searchText.setText("")
                binding.searchTextInputLayout.isEnabled = false
                binding.buttonToggleLayout.forEach { it.isEnabled = false }

                binding.moreFilters.text = getString(R.string.all_filters_number_label, 1)

                adapter.filterType = FilterType.TIMEFRAME
                adapter.filter.filter(filterPair.second)
            }
        }

    private fun openMoreFiltersDialog() {
        findNavController().navigate(
            BeerFragmentDirections
                .actionBeerFragmentToBottomSheet(binding.searchText.text.toString())
        )
    }


    private fun showDetails(beer: Beer, bitmap: Bitmap?) {
        findNavController().navigate(
            BeerFragmentDirections
                .actionBeerFragmentToBottomSheetBeers(
                    beer,
                    bitmap ?: BitmapFactory.decodeResource(resources, R.mipmap.ic_missing_image)
                )
        )
    }

    private fun clearAllActivatedTogglesExcept(exceptView: View?) {
        binding.buttonToggleLayout.forEach {
            if (it !== exceptView) {
                (it as? Button)?.isActivated = false
            }
        }
    }
}