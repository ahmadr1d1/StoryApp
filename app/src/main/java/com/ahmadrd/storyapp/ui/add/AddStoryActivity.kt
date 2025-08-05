package com.ahmadrd.storyapp.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ahmadrd.storyapp.R
import com.ahmadrd.storyapp.databinding.ActivityAddStoryBinding
import com.ahmadrd.storyapp.di.ViewModelFactory
import com.ahmadrd.storyapp.ui.main.StoryActivity
import com.ahmadrd.storyapp.utils.ErrorType
import com.ahmadrd.storyapp.utils.ResultState
import com.ahmadrd.storyapp.utils.UploadStory.getImageUri
import com.ahmadrd.storyapp.utils.UploadStory.reduceFileImage
import com.ahmadrd.storyapp.utils.UploadStory.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    // Tidak ada izin yang diberikan. Matikan kembali switch dan beri tahu user.
                    binding.switchLocation.isChecked = false
                    showToast(getString(R.string.location_permission_denied))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addStory)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        observeViewModel()
        with(binding) {
            toolbarHome.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            buttonCamera.setOnClickListener { checkCameraPermissionAndOpenCamera() }
            buttonGallery.setOnClickListener { startGallery() }
            buttonUpload.setOnClickListener { uploadImage() }
            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLastLocation()
                } else {
                    // Saat switch dinonaktifkan, hapus data lokasi
                    currentLatitude = null
                    currentLongitude = null
                }
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast(getString(R.string.no_media))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            binding.previewImageView.setImageResource(R.drawable.baseline_image_24)
            currentImageUri = null
        }
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                // User belum memberikan izin, tampilkan penjelasan
                AlertDialog.Builder(this)
                    .setTitle(R.string.title_permission_camera)
                    .setMessage(R.string.message_permission_camera)
                    .setPositiveButton(R.string.allow) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            } else {
                // User pilih "Don't ask again" atau pertama kali tapi tidak mengizinkan
                AlertDialog.Builder(this)
                    .setTitle(R.string.title_permission_camera)
                    .setMessage(R.string.message_permission_camera_settings)
                    .setPositiveButton(R.string.open_settings) { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        }
    }


    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDesc.text.toString()
            viewModel.uploadImage(imageFile, description, currentLatitude, currentLongitude)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun observeViewModel() {
        viewModel.uploadStory.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showToast(getString(R.string.success_add_story))
                        val intent = Intent(this, StoryActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        showLoading(false)
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        when (val errorType = result.error) {
                            is ErrorType.ApiError -> {
                                // Jika error dari API, tampilkan pesannya langsung
                                showToast(errorType.message)
                            }

                            is ErrorType.ResourceError -> {
                                // Jika error dari resource, gunakan getString untuk menerjemahkannya
                                showToast(getString(errorType.resId))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.currentLatitude = location.latitude
                    this.currentLongitude = location.longitude
                    showToast(getString(R.string.location_found))
                } else {
                    showToast(getString(R.string.location_not_found))
                    // Matikan switch jika lokasi null
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}