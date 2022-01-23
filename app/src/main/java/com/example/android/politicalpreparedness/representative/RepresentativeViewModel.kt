package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(applicationContext: Context) : ViewModel() {

    private val repository = Repository(applicationContext)

    private var _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

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

}
