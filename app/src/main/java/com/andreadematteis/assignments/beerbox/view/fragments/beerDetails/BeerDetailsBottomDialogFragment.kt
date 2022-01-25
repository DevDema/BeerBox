package com.andreadematteis.assignments.beerbox.view.fragments.beerDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.andreadematteis.assignments.beerbox.databinding.BeerBottomLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BeerDetailsBottomDialogFragment: BottomSheetDialogFragment() {
    private lateinit var binding: BeerBottomLayoutBinding
    private val args: BeerDetailsBottomDialogFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = BeerBottomLayoutBinding.inflate(layoutInflater, container, false)
        binding.beer = args.beer

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.image.setImageBitmap(args.image)
    }
}