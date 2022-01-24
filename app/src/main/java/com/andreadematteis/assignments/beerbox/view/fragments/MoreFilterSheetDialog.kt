package com.andreadematteis.assignments.beerbox.view.fragments

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.andreadematteis.assignments.beerbox.R
import com.andreadematteis.assignments.beerbox.databinding.FragmentBeerFiltersBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class MoreFilterSheetDialog(
    private val activity: FragmentActivity,
    private val viewModel: FilterViewModel,
    layoutInflater: LayoutInflater,
    rootView: ViewGroup,
    attachToRoot: Boolean
) : BottomSheetDialog(activity) {

    private val binding: FragmentBeerFiltersBinding =
        FragmentBeerFiltersBinding.inflate(layoutInflater, rootView, attachToRoot)

    init {
        setContentView(binding.root)

        binding.brewingTimeText.inputType = InputType.TYPE_NULL
        binding.brewingTimeText.keyListener = null
        binding.brewingTimeText.setOnClickListener { openDatePicker() }
        binding.cancelLabel.setOnClickListener {
            viewModel.setMoreFiltersOpened(false)
            cancel()
        }
        binding.filterLabel.setOnClickListener {
            viewModel.setMoreFiltersOpened(false)
            cancel()
        }
    }

    private fun openDatePicker() {
        val today = Calendar.getInstance()

        val dateRange = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(activity.getString(R.string.select_brewing_timeframe_label))
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


                    binding.brewingTimeText.setText(
                        getString(
                            R.string.two_strings_trattino_spaced,
                            dateFormat.format(startDate),
                            dateFormat.format(endDate)
                        )
                    )
                }
            }

        dateRange.show(activity.supportFragmentManager, TAG_DATE_PICKER)
    }


    companion object {
        private const val TAG_DATE_PICKER = "DATE_PICKER_BREWING"
    }
}