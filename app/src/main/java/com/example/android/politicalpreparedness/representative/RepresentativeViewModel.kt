package com.example.android.politicalpreparedness.representative

import android.content.Context
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(
    applicationContext: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val line1Key = "line1"
    private val line2Key = "line2"
    private val cityKey = "city"
    private val stateKey = "state"
    private val zipKey = "zip"
    private val showListKey = "list"

    private val repository = Repository(applicationContext)

    private var _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    var addressLine1 = MutableLiveData<String>()
        set(value) {
            field = value
            savedStateHandle.set(line1Key, value.value)
        }

    var addressLine2 = MutableLiveData<String>()
        set(value) {
            field = value
            savedStateHandle.set(line2Key, value.value)
        }
    var addressCity = MutableLiveData<String>()
        set(value) {
            field = value
            savedStateHandle.set(cityKey, value.value)
        }
    var addressState = MutableLiveData<String>()
        set(value) {
            field = value
            savedStateHandle.set(stateKey, value.value)
        }

    var addressZip = MutableLiveData<String>()
        set(value) {
            field = value
            savedStateHandle.set(zipKey, value.value)
        }

    private var _isListShowing = MutableLiveData<Boolean>()
        set(value) {
            field = value
            savedStateHandle.set(showListKey, value.value)
        }

    private var _motionTransition = MutableLiveData<Int>()
        set(value) {
            field = value
            savedStateHandle.set("motion", value.value)
        }
    val motionTransition: LiveData<Int>
        get() = _motionTransition

    private var _networkException = MutableLiveData<String>()
    val networkException: MutableLiveData<String>
        get() = _networkException

    init {
        restoreAddressFields()
        Timber.d("isListShow= ${_isListShowing.value}")
        val address: String? = getAddressFromFields()
        Timber.d("restoredAddress = ${getAddressFromFields()}")
        if (address != null) {
            getRepresentatives(address)
        }
    }

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

    private fun restoreAddressFields() {
        addressLine1 = savedStateHandle.getLiveData(line1Key)
        addressLine2 = savedStateHandle.getLiveData(line2Key)
        addressCity = savedStateHandle.getLiveData(cityKey)
        addressState = savedStateHandle.getLiveData(stateKey)
        addressZip = savedStateHandle.getLiveData(zipKey)
        _isListShowing = savedStateHandle.getLiveData(showListKey)
        _motionTransition = savedStateHandle.getLiveData("motion")
    }

    private fun checkAddressFieldNotEmpty(): Boolean {
        return (addressLine1.value?.isNotBlank() == true &&
                addressCity.value?.isNotBlank() == true &&
                addressState.value?.isNotBlank() == true &&
                addressZip.value?.isNotBlank() == true &&
                _isListShowing.value == true)
    }

    private fun getAddressFromFields(): String? {
        if (checkAddressFieldNotEmpty()) {
            return Address(
                addressLine1.value!!,
                addressLine2.value,
                addressCity.value!!,
                addressState.value!!,
                addressZip.value!!
            ).toFormattedString()
        }
        return null
    }

    fun setListShowing(boolean: Boolean) {
        _isListShowing.postValue(boolean)
    }

    fun setMotionTransitionId(id: Int) {
        _motionTransition.postValue(id)
    }
}
