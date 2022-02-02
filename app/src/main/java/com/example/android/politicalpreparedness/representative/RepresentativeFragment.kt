package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.ElectionsNetworkManager
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 334
    }

    private lateinit var fusedLocationServices: FusedLocationProviderClient

    private lateinit var representativeViewModel: RepresentativeViewModel

    private lateinit var binding: FragmentRepresentativeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)

        val database = ElectionDatabase.getDatabase(requireActivity().applicationContext)
        val repository = Repository(database)
        val representativeViewModelFactory =
            RepresentativeViewModelFactory(repository, this)

        representativeViewModel = ViewModelProvider(
            this,
            representativeViewModelFactory
        ).get(RepresentativeViewModel::class.java)

        fusedLocationServices = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.apply {
            lifecycleOwner = this@DetailFragment
            viewModel = representativeViewModel
        }

        val representativeAdapter = RepresentativeListAdapter()

        binding.representativesContainer.adapter = representativeAdapter

        binding.buttonSearch.setOnClickListener {
            checkNetworkAvailable()
            val address = checkAddressFieldsNotEmpty()
            if (address != null) {
                representativeViewModel.getRepresentatives(address)
                hideKeyboard()
            }
        }

        binding.addressLine1.doOnTextChanged { _, _, _, _ ->
            binding.addressLine1Container.error = null
        }

        binding.city.doOnTextChanged { _, _, _, _ ->
            binding.cityContainer.error = null
        }

        binding.zip.doOnTextChanged { _, _, _, _ ->
            binding.zipContainer.error = null
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
        }

        representativeViewModel.representatives.observe(viewLifecycleOwner) {
            hideKeyboard()
            representativeAdapter.submitList(it)
            representativeViewModel.setListShowing(true)
        }

        representativeViewModel.motionTransition.observe(viewLifecycleOwner) { id ->
            if (id != null) {
                binding.constraintContainer.transitionToState(id)
            }
        }

        representativeViewModel.networkException.observe(this.viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        representativeViewModel.setMotionTransitionId(binding.constraintContainer.currentState)
    }

    private fun checkNetworkAvailable() {
        ElectionsNetworkManager.getInstance(requireActivity().applicationContext).connectedToNetwork.observe(
            this.viewLifecycleOwner
        ) { isNetworkAvailable ->
            Timber.d("isNetworkAvailable: $isNetworkAvailable")
            if (!isNetworkAvailable) {
                Toast.makeText(requireContext(), "No Network Available", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun checkAddressFieldsNotEmpty(): String? {
        var address: Address? = null
        when {
            binding.addressLine1.text?.isEmpty() == true -> {
                binding.addressLine1Container.error = "Field shouldn't be empty"
            }
            binding.city.text?.isEmpty() == true -> {
                binding.cityContainer.error = "Please enter the city name"
            }
            binding.zip.text?.isEmpty() == true -> {
                binding.zipContainer.error = "Enter Zip Code"
            }
            else -> {
                val line1 = binding.addressLine1.text.toString().trim()
                val line2 = binding.addressLine2.text.toString().trim()
                val city = binding.city.text.toString().trim()
                val state = binding.state.selectedItem.toString().trim()
                val zip = binding.zip.text.toString()
                address = Address(line1, line2, city, state, zip)
            }
        }
        return address?.toFormattedString()
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
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationServices.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            Timber.d("location= $location")
            if (location != null) {
                val address = geoCodeLocation(location)
                representativeViewModel.setAddressFields(address)
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
        val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        requireActivity().requestPermissions(permissionsArray, resultCode)
    }

}