package com.andreadematteis.assignments.beerbox.view.fragments.moreFilters

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.andreadematteis.assignments.beerbox.R
import com.andreadematteis.assignments.beerbox.databinding.FragmentBeerFiltersBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class MoreFilterSheetDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: FilterViewModel by activityViewModels()
    private lateinit var binding: FragmentBeerFiltersBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentBeerFiltersBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.brewingTimeText.inputType = InputType.TYPE_NULL
        binding.brewingTimeText.keyListener = null
        binding.brewingTimeText.setOnClickListener { openDatePicker() }
        binding.cancelLabel.setOnClickListener {
            dismiss()
        }

        binding.filterLabel.setOnClickListener {
            dismiss()
        }

        binding.resetLabel.setOnClickListener {
            dismiss()
        }

        binding.filterByNameLabel.setOnCheckedChangeListener { _, isChecked ->
            binding.searchTextInputLayout.isEnabled = isChecked
            binding.searchText.isEnabled = isChecked

            if(isChecked) {
                binding.byBrewingTimeLabel.isChecked = false
            } else {
                binding.searchText.setText("")
            }
        }

        binding.byBrewingTimeLabel.setOnCheckedChangeListener { _, isChecked ->
            binding.brewingTimeTextInputLayout.isEnabled = isChecked
            binding.brewingTimeText.isEnabled = isChecked

            if(isChecked) {
                binding.filterByNameLabel.isChecked = false
            } else {
                binding.brewingTimeText.setText("")
            }
        }

        binding.brewingTimeTextInputLayout.isEnabled = false
        binding.brewingTimeText.isEnabled = false

        binding.searchTextInputLayout.isEnabled = false
        binding.searchText.isEnabled = false

        viewModel.datesFilter.observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()) {
                binding.byBrewingTimeLabel.isChecked = true
                binding.brewingTimeText.setText(it)
                binding.filterLabel.isEnabled = binding.byBrewingTimeLabel.isChecked
            } else {
                binding.filterLabel.isEnabled = false
            }
        }

        viewModel.nameFilter.observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()) {
                binding.filterByNameLabel.isChecked = true
                binding.filterLabel.isEnabled = binding.filterByNameLabel.isChecked
            } else {
                binding.filterLabel.isEnabled = false
            }
        }
    }

    private fun openDatePicker() {
        val today = Calendar.getInstance()

        val dateRange = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_brewing_timeframe_label))
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setOpenAt(today.timeInMillis)
                    .setEnd(today.timeInMillis)
                    .setValidator(DateValidatorPointBackward.now())
                    .build()
            )
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)
                    val startDate = Calendar.getInstance().apply {
                        timeInMillis = it.first
                    }.time

                    val endDate = Calendar.getInstance().apply {
                        timeInMillis = it.second
                    }.time


                    viewModel.datesFilter.value = getString(
                        R.string.two_strings_trattino_spaced,
                        dateFormat.format(startDate),
                        dateFormat.format(endDate)
                    )
                }
            }

        dateRange.show(parentFragmentManager, TAG_DATE_PICKER)
    }

    companion object {
        private const val TAG_DATE_PICKER = "DATE_PICKER_BREWING"

        fun newInstance() = MoreFilterSheetDialogFragment().apply {
            arguments = Bundle()
        }
    }
}