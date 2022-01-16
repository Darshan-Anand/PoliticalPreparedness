package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 333
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 334
    }

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private lateinit var fusedLocationServices: FusedLocationProviderClient

    private lateinit var viewModel: RepresentativeViewModel

    private lateinit var binding: FragmentRepresentativeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)
        fusedLocationServices = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel = RepresentativeViewModel(requireActivity().applicationContext)

        val representativeAdapter = RepresentativeListAdapter()

        binding.representativesContainer.adapter = representativeAdapter

        binding.buttonSearch.setOnClickListener {
            val address = checkAddressFieldsNotEmpty()
            if (address != null) {
                viewModel.getRepresentatives(address.toString())
                hideKeyboard()
            }
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
        }

        viewModel.representatives.observe(requireActivity(), {
            representativeAdapter.submitList(it)
        })

        return binding.root
    }

    private fun checkAddressFieldsNotEmpty(): String? {
        var address: String? = null
        when {
            binding.addressLine1.text.isEmpty() -> {
                binding.addressLine1.error = "Field shouldn't be empty"
            }
            binding.city.text.isEmpty() -> {
                binding.city.error = "Please enter the city name"
            }
            binding.zip.text.isEmpty() -> {
                binding.zip.error = "Enter Zip Code"
            }
            else -> {
                address = binding.addressLine1.text.toString().trim().plus(",")
                    .plus(binding.addressLine2.text.toString().trim()).plus(",")
                    .plus(binding.city.text.toString().trim()).plus(",")
                    .plus(binding.state.selectedItem.toString().trim()).plus(",")
                    .plus(binding.zip.text.toString().trim())
            }
        }
        return address
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (
            grantResults.isEmpty() ||
            grantResults[0] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[1] ==
                    PackageManager.PERMISSION_DENIED) ||
            (requestCode == REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE &&
                    grantResults[1] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            Timber.d("Permission denied by user, show toast")
            Toast.makeText(
                requireContext(),
                "Please to grant permission to fetch address from device location",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Timber.d("Permission granted by user, check location enabled")
            getLocation()
        }
    }

    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            getLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ))
        val backgroundPermissionApproved = if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            true
        }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationServices.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            Timber.d("location= $location")
            if (location != null) {
                val address = geoCodeLocation(location)
                binding.address = address
                Timber.d("address= $address")
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun requestLocationPermission() {
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        requireActivity().requestPermissions(permissionsArray, resultCode)
    }

}