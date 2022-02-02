package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber

class ElectionsViewModel(private val repository: Repository) : ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    fun getUpcomingElections() {
        try {
            viewModelScope.launch() {
                _upcomingElections.value = repository.getUpcomingElectionsList()
            }
        } catch (exception: Exception) {
            Timber.d("getUpcomingElections exception: ${exception.message}")
        }
    }

    fun getSavedElections() {
        try {
            viewModelScope.launch {
                _savedElections.value = repository.getSavedElectionsList()
            }
        } catch (exception: Exception) {
            Timber.d("getSavedElection exception: ${exception.message}")
        }
    }

    fun refreshElections() {
        getSavedElections()
        getUpcomingElections()
    }


}