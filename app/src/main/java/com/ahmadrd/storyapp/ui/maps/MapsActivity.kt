package com.ahmadrd.storyapp.ui.maps

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ahmadrd.storyapp.R
import com.ahmadrd.storyapp.databinding.ActivityMapsBinding
import com.ahmadrd.storyapp.di.ViewModelFactory
import com.ahmadrd.storyapp.utils.ErrorType
import com.ahmadrd.storyapp.utils.ResultState
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.getValue

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapTypeBottomSheet.MapTypeListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set listener untuk Floating Action Button
        binding.fabMapType.setOnClickListener {
            val bottomSheet = MapTypeBottomSheet(this)
            bottomSheet.show(supportFragmentManager, TAG)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker
        observeViewModel()

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    override fun onMapTypeSelected(mapType: Int) {
        mMap.mapType = mapType
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    val storyList = result.data
                    storyList?.forEach { story ->
                        val lat = story?.lat
                        val lon = story?.lon
                        if (lat != null && lon != null) {
                            val storyLatLng = LatLng(lat, lon)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(storyLatLng)
                                    .title(story.name)
                                    .snippet(story.description)
                            )
                        }
                    }
                }

                is ResultState.Error -> {
                    showLoading(false)
                    when (val errorType = result.error) {
                        is ErrorType.ApiError -> {
                            // Jika error dari API, tampilkan pesannya langsung
                            Toast.makeText(
                                this, errorType.message, Toast.LENGTH_LONG
                            ).show()
                        }

                        is ErrorType.ResourceError -> {
                            // Jika error dari resource, gunakan getString untuk menerjemahkannya
                            Toast.makeText(
                                this, getString(errorType.resId), Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarMaps.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val TAG = "MapTypeBottomSheet"
    }
}