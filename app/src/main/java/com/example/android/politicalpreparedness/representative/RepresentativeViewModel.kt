package com.example.android.politicalpreparedness.representative

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(applicationContext: Context) : ViewModel() {

    private val repository = Repository(applicationContext)

    private var _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    val addressLine1 = MutableLiveData<String>()
    val addressLine2 = MutableLiveData<String>()
    val addressCity = MutableLiveData<String>()
    val addressState = MutableLiveData<String>()
    val addressZip = MutableLiveData<String>()

    private var _networkException = MutableLiveData<String>()
    val networkException: MutableLiveData<String>
        get() = _networkException

    fun getRepresentatives(address: String) {
        viewModelScope.launch {
            try {
                val (offices, officials) = repository.getRepresentativeInfoByAddress(address)
                _representatives.value = offices.flatMap {
                    it.getRepresentatives(officials)
                }
            } catch (exception: Exception) {
                Timber.d("Representative Exception: ${exception.message}, cause: ${exception.cause}")
                _networkException.postValue(exception.message)
            }
        }
    }

    fun setAddressFields(address: Address) {
        addressLine1.value = address.line1
        addressLine2.value = address.line2
        addressCity.postValue(address.city)
        addressState.postValue(address.state)
        addressZip.postValue(address.zip)
    }

}
