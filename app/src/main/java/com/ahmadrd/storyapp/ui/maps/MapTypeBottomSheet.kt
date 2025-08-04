package com.ahmadrd.storyapp.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahmadrd.storyapp.databinding.BottomSheetMapTypeBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MapTypeBottomSheet(private val listener: MapTypeListener) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetMapTypeBinding

    // Interface untuk mengirim data kembali ke MapsActivity
    interface MapTypeListener {
        fun onMapTypeSelected(mapType: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMapTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set OnClickListener untuk setiap pilihan
        binding.layoutDefault.setOnClickListener {
            listener.onMapTypeSelected(GoogleMap.MAP_TYPE_NORMAL)
            dismiss() // Tutup Bottom Sheet setelah dipilih
        }

        binding.layoutSatellite.setOnClickListener {
            listener.onMapTypeSelected(GoogleMap.MAP_TYPE_HYBRID)
            dismiss()
        }

        binding.layoutTerrain.setOnClickListener {
            listener.onMapTypeSelected(GoogleMap.MAP_TYPE_TERRAIN)
            dismiss()
        }
    }
}